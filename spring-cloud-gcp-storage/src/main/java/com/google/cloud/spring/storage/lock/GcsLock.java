/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.spring.storage.lock;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobGetOption;
import com.google.cloud.storage.Storage.BlobSourceOption;
import com.google.cloud.storage.Storage.BlobTargetOption;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;

/**
 * Adapted from https://github.com/org-openbites/distributed-lock.
 *
 * @author Williams Simon (openbites.org@gmail.com)
 */
public class GcsLock implements Lock, Serializable {

	private static final long serialVersionUID = 5184201915922962120L;

	private static final String LOCK_FILE_CONTENT = "_lock";
	private static final String MIME_TYPE_TEXT_PLAIN = "text/plain";
	private static final String CREATING_HOST = "CREATING_HOST";
	private static final String TTL_EXTENSION_SECONDS = "TTL_EXTENSION_SECONDS";
	private static final String REFRESH_SECONDS = "REFRESH_SECONDS";
	private static final String HOST_NAME = getHostName();

	static final String LOCK_TTL_EPOCH_MS = "LOCK_TTL_EPOCH_MS";
	static final int GCS_PRECONDITION_FAILED = 412;

	private final GcsLockConfig lockConfig;
	private final long intervalNanos;
	private final Storage storage;
	private final Set<GcsLockListener> lockListeners = new HashSet<>();
	private final ReentrantLock lock = new ReentrantLock();

	private final KeepLockAlive keepLockAlive = new KeepLockAlive();
	private final CleanupDeadLock cleanupDeadLock = new CleanupDeadLock();

	private transient volatile Optional<Blob> acquired = Optional.empty();
	private transient volatile Thread exclusiveOwnerThread;

	private transient Collection<Thread> waitingThreads = new ConcurrentLinkedQueue<>();

	public GcsLock(GcsLockConfig lockConfig) {
		if (Objects.isNull(lockConfig)) {
			throw new NullPointerException("Null GcsLockConfig");
		}
		this.lockConfig = lockConfig;
		this.storage = StorageOptions.getDefaultInstance().getService();
		intervalNanos = (long) (lockConfig.getRefreshIntervalInSeconds() * 1E9);
	}

	/**
	 * Acquires the lock only if it is free at the time of invocation.
	 *
	 * <p>Acquires the lock if it is available and returns immediately
	 * with the value {@code true}. If the lock is not available then this method will return
	 * immediately with the value {@code false}.
	 *
	 * <p>Any exception would be returned as the parameter in the callback {@link
	 * GcsLockListener#acquireLockException(Exception) GcsLockListener#acquireLockException(java.lang.Exception)}
	 *
	 * @return {@code true} if the lock was acquired and {@code false} otherwise
	 */
	@Override
	public boolean tryLock() {
		if (isLocked() && isHeldByCurrentThread()) {
			return true;
		}

		try {
			Map<String, String> metadata = computeMetaData();
			BlobId blobId = BlobId.of(lockConfig.getGcsBucketName(), lockConfig.getGcsLockFilename());
			BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
					.setMetadata(metadata)
					.setContentType(MIME_TYPE_TEXT_PLAIN)
					.build();
			BlobTargetOption blobOption = BlobTargetOption.doesNotExist();
			Blob blob = storage.create(blobInfo, LOCK_FILE_CONTENT.getBytes(), blobOption);

			acquired = Optional.of(blob);
			exclusiveOwnerThread = Thread.currentThread();
			keepLockAlive.start();
			return true;
		}
		catch (Exception exception) {
			if ((exception instanceof StorageException)
					&& ((StorageException) exception).getCode() == GCS_PRECONDITION_FAILED) {
				cleanupDeadLock.start();
				return false;
			}

			notifyAcquireLockListeners(exception);

			return false;
		}
	}

	/**
	 * Acquires the lock if it is free within the given waiting time and the current thread has not
	 * been {@linkplain Thread#interrupt interrupted}.
	 *
	 * <p>If the lock is available this method returns immediately
	 * with the value {@code true}. If the lock is not available then the current thread becomes
	 * disabled for thread scheduling purposes and lies dormant until one of three things happens:
	 * <ul>
	 * <li>The lock is acquired by the current thread; or
	 * <li>Some other thread {@linkplain Thread#interrupt interrupts} the
	 * current thread, and interruption of lock acquisition is supported; or
	 * <li>The specified waiting time elapses
	 * </ul>
	 *
	 * <p>If the lock is acquired then the value {@code true} is returned.
	 *
	 * <p>If the current thread:
	 * <ul>
	 * <li>has its interrupted status set on entry to this method; or
	 * <li>is {@linkplain Thread#interrupt interrupted} while acquiring
	 * the lock, and interruption of lock acquisition is supported,
	 * </ul>
	 * then {@link InterruptedException} is thrown and the current thread's
	 * interrupted status is cleared.
	 *
	 * <p>If the specified waiting time elapses then the value {@code false}
	 * is returned.
	 * If the time is
	 * less than or equal to zero, the method will not wait at all.
	 *
	 * @param time     the maximum time to wait for the lock
	 * @param timeUnit the time unit of the {@code time} argument
	 * @return {@code true} if the lock was acquired and {@code false} if the waiting time elapsed
	 * elapsed before the lock was acquired
	 * @throws InterruptedException if the current thread is interrupted while acquiring the lock (and
	 *                              interruption of lock acquisition is supported)
	 */
	@Override
	public boolean tryLock(long time, TimeUnit timeUnit) throws InterruptedException {
		if (time <= 0) {
			return false;
		}

		long deadline = TimeUnit.MILLISECONDS.convert(time, timeUnit) + System.currentTimeMillis();
		try {
			while (System.currentTimeMillis() <= deadline && !tryLock()) {
				waitingThreads.add(Thread.currentThread());
				LockSupport.parkUntil(deadline);
				waitingThreads.remove(Thread.currentThread());

				if (Thread.currentThread().isInterrupted()) {
					throw new InterruptedException();
				}
			}
		}
		catch (Exception exception) {
			notifyAcquireLockListeners(exception);

			if (exception instanceof InterruptedException) {
				Thread.interrupted();
				throw (InterruptedException) exception;
			}
		}
		return isLocked() && isHeldByCurrentThread();
	}

	/**
	 * Acquires the lock.
	 *
	 * <p>If the lock is not available then the current thread becomes
	 * disabled for thread scheduling purposes and lies dormant until the lock has been acquired.
	 *
	 * <p>Any exception would be returned as the parameter in the callback {@link
	 * GcsLockListener#acquireLockException(Exception) GcsLockListener#acquireLockException(java.lang.Exception)}
	 */
	@Override
	public void lock() {
		try {
			while (!tryLock()) {
				waitingThreads.add(Thread.currentThread());
				LockSupport.park();
				waitingThreads.remove(Thread.currentThread());
			}
		}
		catch (Exception exception) {
			notifyAcquireLockListeners(exception);
		}
	}

	/**
	 * Acquires the lock unless the current thread is {@linkplain Thread#interrupt interrupted}.
	 *
	 * <p>Acquires the lock if it is available and returns immediately.
	 *
	 * <p>If the lock is not available then the current thread becomes
	 * disabled for thread scheduling purposes and lies dormant until one of two things happens:
	 *
	 * <ul>
	 * <li>The lock is acquired by the current thread; or
	 * <li>Some other thread {@linkplain Thread#interrupt interrupts} the
	 * current thread, and interruption of lock acquisition is supported.
	 * </ul>
	 *
	 * <p>If the current thread:
	 * <ul>
	 * <li>has its interrupted status set on entry to this method; or
	 * <li>is {@linkplain Thread#interrupt interrupted} while acquiring the
	 * lock, and interruption of lock acquisition is supported,
	 * </ul>
	 * then {@link InterruptedException} is thrown and the current thread's
	 * interrupted status is cleared.
	 *
	 * @throws InterruptedException if the current thread is interrupted while acquiring the lock (and
	 *                              interruption of lock acquisition is supported)
	 */
	@Override
	public void lockInterruptibly() throws InterruptedException {
		try {
			while (!tryLock()) {
				waitingThreads.add(Thread.currentThread());
				LockSupport.park();
				waitingThreads.remove(Thread.currentThread());

				if (Thread.currentThread().isInterrupted()) {
					throw new InterruptedException();
				}
			}
		}
		catch (Exception exception) {
			notifyAcquireLockListeners(exception);

			if (exception instanceof InterruptedException) {
				Thread.interrupted();
				throw (InterruptedException) exception;
			}
		}
	}

	/**
	 * Releases the lock.
	 *
	 * <p>While used in multi-thread environment only the thread that originally acquired the lock is
	 * able to release the lock.</p>
	 *
	 * <p>Any exception would be returned as the parameter in the callback {@link
	 * GcsLockListener#releaseLockException(Exception) GcsLockListener#releaseLockException(java.lang.Exception)}</p>
	 */
	@Override
	public void unlock() {
		lock.lock();
		try {
			if (isHeldByCurrentThread()) {
				acquired.ifPresent(blob -> {
					acquired = Optional.empty();
					exclusiveOwnerThread = null;
					deleteLock(blob);
				});
			}
		}
		catch (Exception exception) {
			notifyReleaseLockListeners(exception);
		}
		finally {
			lock.unlock();
		}
	}

	@Override
	public Condition newCondition() {
		throw new UnsupportedOperationException("Operation Not Supported");
	}

	/**
	 * @return {@code true} if the lock has been acquired and {@code false} otherwise
	 */
	public boolean isLocked() {
		return acquired.isPresent();
	}

	/**
	 * @return {@code true} if the lock was acquired by the current thread {@code false} otherwise
	 */
	public boolean isHeldByCurrentThread() {
		return exclusiveOwnerThread == Thread.currentThread();
	}

	/**
	 * Add a lock listener as the callback for state change.
	 *
	 * @param listener the lock listener
	 */
	public void addLockListener(GcsLockListener listener) {
		lockListeners.add(listener);
	}

	/**
	 * Remove a lock listener as the callback for state change.
	 *
	 * @param listener the lock listener
	 */
	public void removeLockListener(GcsLockListener listener) {
		lockListeners.remove(listener);
	}

	private Map<String, String> computeMetaData() {
		long keepAliveToUnitMillis =
				System.currentTimeMillis() + lockConfig.getLifeExtensionInSeconds().intValue() * 1000L;
		Map<String, String> metaData = new HashMap<>();
		metaData.put(LOCK_TTL_EPOCH_MS, String.valueOf(keepAliveToUnitMillis));
		metaData.put(CREATING_HOST, HOST_NAME);
		metaData.put(TTL_EXTENSION_SECONDS, String.valueOf(lockConfig.getLifeExtensionInSeconds()));
		metaData.put(REFRESH_SECONDS, String.valueOf(lockConfig.getRefreshIntervalInSeconds()));
		return metaData;
	}

	private void notifyAcquireLockListeners(Exception exception) {
		lockListeners.forEach(listener -> {
			try {
				listener.acquireLockException(exception);
			}
			catch (Exception e) {
				// Don't care if listener throws any exception
			}
		});
	}

	private void notifyReleaseLockListeners(Exception exception) {
		lockListeners.forEach(listener -> {
			try {
				listener.releaseLockException(exception);
			}
			catch (Exception e) {
				// Don't care if listener throws any exception
			}
		});
	}

	private void notifyKeepLockAliveListeners(Exception exception) {
		lockListeners.forEach(listener -> {
			try {
				listener.keepLockAliveException(exception);
			}
			catch (Exception e) {
				// Don't care if listener throws any exception
			}
		});
	}

	private void notifyCleanupDeadLockListeners(Exception exception) {
		lockListeners.forEach(listener -> {
			try {
				listener.cleanupDeadLockException(exception);
			}
			catch (Exception e) {
				// Don't care if listener throws any exception
			}
		});
	}

	private void deleteLock(Blob blob) {
		storage.delete(blob.getBlobId(),
				BlobSourceOption.generationMatch(blob.getGeneration()),
				BlobSourceOption.metagenerationMatch(blob.getMetageneration()));
	}

	private static String getHostName() {
		String hostName = "NONE";
		try {
			hostName = InetAddress.getLocalHost().getHostName();
		}
		catch (UnknownHostException e) {
		}

		return hostName;
	}

	private abstract class SingleIntervalExecution implements Runnable {

		Thread executingThread;

		void start() {
			lock.lock();
			try {
				if (executingThread == null) {
					Thread thread = new Thread(this);
					thread.setName(String.format("%s-%s-%s",
							this.getClass().getSimpleName(),
							lockConfig.getGcsBucketName(),
							lockConfig.getGcsLockFilename()));
					thread.setDaemon(true);
					thread.start();
					executingThread = thread;
				}
			}
			finally {
				lock.unlock();
			}
		}

		void finish() {
			executingThread = null;
		}
	}

	private class KeepLockAlive extends SingleIntervalExecution {

		@Override
		public void run() {
			while (true) {
				lock.lock();
				try {
					Blob updatedBlob;
					updatedBlob = storage.get(lockConfig.getGcsBucketName(), lockConfig.getGcsLockFilename(),
							BlobGetOption.generationMatch(acquired.get().getGeneration()),
							BlobGetOption.metagenerationMatch(acquired.get().getMetageneration()));
					if (!acquired.isPresent() || Objects.isNull(updatedBlob)) {
						finish();
						return;
					}
					else {
						updatedBlob = updatedBlob.toBuilder().setMetadata(computeMetaData()).build();
						updatedBlob = storage.update(updatedBlob, BlobTargetOption.generationMatch(),
								BlobTargetOption.metagenerationMatch());
						acquired = Optional.of(updatedBlob);
					}
				}
				catch (Exception exception) {
					notifyKeepLockAliveListeners(exception);
					finish();
					return;
				}
				finally {
					lock.unlock();
				}

				LockSupport.parkNanos(intervalNanos);
			}
		}
	}

	private class CleanupDeadLock extends SingleIntervalExecution {

		@Override
		public void run() {
			while (true) {
				LockSupport.parkNanos(intervalNanos);

				Blob blob = storage.get(lockConfig.getGcsBucketName(), lockConfig.getGcsLockFilename());

				if (Objects.isNull(blob)) {
					finish();
					return;
				}

				Map<String, String> metaData = blob.getMetadata();
				long ttl = Optional.ofNullable(metaData)
						.map(metadata -> metaData.get(LOCK_TTL_EPOCH_MS))
						.map(Long::valueOf)
						.orElse(Long.valueOf(Long.MAX_VALUE))
						.longValue();

				if (ttl <= System.currentTimeMillis()) {
					try {
						deleteLock(blob);
					}
					catch (Exception exception) {
						notifyCleanupDeadLockListeners(exception);
					}
					finally {
						finish();
					}
					return;
				}
			}
		}

		@Override
		void finish() {
			super.finish();

			Optional<Thread> thread = waitingThreads.stream().findAny();
			thread.ifPresent(LockSupport::unpark);

			if (waitingThreads.size() > 0) {
				start();
			}
		}
	}
}
