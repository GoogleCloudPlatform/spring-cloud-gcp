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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static com.google.cloud.spring.storage.lock.GcsLock.LOCK_TTL_EPOCH_MS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

public class GcsLockIntegrationTest {

	private static GcsLockConfig configuration;

	private GcsLockListener lifecycleListener;
	private ArgumentCaptor<Exception> exceptionArgumentCaptor;
	private Storage storage = StorageOptions.getDefaultInstance().getService();

	@Before
	public void setUp() {
		lifecycleListener = mock(GcsLockListener.class);
		exceptionArgumentCaptor = forClass(Exception.class);

		configuration = GcsLockConfig.newBuilder()
				.setGcsBucketName(System.getenv("GCS_BUCKET_NAME_FOR_GCS_LOCK_IT"))
				.setGcsLockFilename("test-gcs-lock")
				.setRefreshIntervalInSeconds(10)
				.setLifeExtensionInSeconds(60)
				.build();
	}

	/**
	 * Test the GCS lock file is kept alive while it is not released.
	 */
	@Test
	public void testKeepAliveLongLivingLock() {
		GcsLock gcsLock = new GcsLock(configuration);
		gcsLock.addLockListener(lifecycleListener);

		if (gcsLock.tryLock()) {
			try {
				assertThat(gcsLock.isLocked()).isTrue();

				verify(lifecycleListener,
						after((long) (configuration.getRefreshIntervalInSeconds() * 1000 * 4.5)).never())
						.keepLockAliveException(exceptionArgumentCaptor.capture());

				assertThat(doesLockExist()).isTrue();

				assertThat(isLockAlive()).isTrue();
			}
			finally {
				gcsLock.unlock();
			}
		}

		assertThat(!doesLockExist()).isTrue();
	}

	/**
	 * Test the GcsLock.KeepAliveJob will terminate normally when the GCS lock file is deleted, which
	 * could happen when <br/> 1. The leadership is released. <br/> 2. The KeepAliveJob has a long
	 * pause in keeping the leadership alive, during which period it is considered expired by some
	 * CleanupDeadLock <br/>
	 */
	@Test
	public void testKeepAliveDeletedLock() {
		GcsLock gcsLock = new GcsLock(configuration);
		gcsLock.addLockListener(lifecycleListener);

		if (gcsLock.tryLock()) {
			try {
				assertThat(gcsLock.isLocked()).isTrue();

				verify(lifecycleListener,
						after((long) (configuration.getRefreshIntervalInSeconds() * 1000 * 4.5)).never())
						.keepLockAliveException(exceptionArgumentCaptor.capture());

				deleteLock();

				verify(lifecycleListener,
						after((long) (configuration.getRefreshIntervalInSeconds() * 1000 * 4.5)).never())
						.keepLockAliveException(exceptionArgumentCaptor.capture());
			}
			finally {
				gcsLock.unlock();
			}
		}

		assertThat(!doesLockExist()).isTrue();
	}

	/**
	 * Test the KeepAliveJob could properly recognize the GCS lock file has been re-created and
	 * release its leadership would not delete the GCS lock file <br/> Such scenario could happen when
	 * <br/> 1. The keepAliveJob had a long pause of keep the leadership alive <br/> 2. Some
	 * CleanupDeadLock deleted it as an expired leadership <br/> 3. A third process acquired the
	 * leadership
	 */
	@Test
	public void testKeepAliveRecreatedLock() {
		GcsLock gcsLock = new GcsLock(configuration);
		gcsLock.addLockListener(lifecycleListener);

		if (gcsLock.tryLock()) {
			try {
				assertThat(gcsLock.isLocked()).isTrue();

				verify(lifecycleListener,
						after((long) (configuration.getRefreshIntervalInSeconds() * 1000 * 4.5)).never())
						.keepLockAliveException(exceptionArgumentCaptor.capture());

				assertThat(isLockAlive()).isTrue();

				recreateLock();

				verify(lifecycleListener,
						timeout((long) (configuration.getRefreshIntervalInSeconds() * 1000 * 4.5)).times(1))
						.keepLockAliveException(exceptionArgumentCaptor.capture());

				assertThat(null != exceptionArgumentCaptor.getValue()).isTrue();
				Exception ex = exceptionArgumentCaptor.getValue();
				assertThat(ex instanceof StorageException).isTrue();
				assertThat(412 == ((StorageException) ex).getCode()).isTrue();
			}
			finally {
				gcsLock.unlock();
			}
		}

		assertThat(doesLockExist()).isTrue();

		// delete dangling lock
		deleteLock();

		assertThat(!doesLockExist()).isTrue();
	}

	/**
	 * Test the GcsLock instance can be reused in a sequentially.
	 */
	@Test
	public void testReuseLock() {
		GcsLock gcsLock = new GcsLock(configuration);

		executeCriticalSection(gcsLock);

		assertThat(!doesLockExist()).isTrue();

		executeCriticalSection(gcsLock);

		assertThat(!doesLockExist()).isTrue();

		verify(lifecycleListener, never()).cleanupDeadLockException(exceptionArgumentCaptor.capture());
	}

	/**
	 * Test the GcsLock instance is thread-safe.
	 */
	@Test
	public void testReuseLockInMultipleThreads() {
		GcsLock gcsLock = new GcsLock(configuration);
		gcsLock.addLockListener(lifecycleListener);

		final int threads = 5;
		ExecutorService service = Executors.newFixedThreadPool(threads);

		try {
			List<Future> futures = new ArrayList<>(threads);

			for (int i = 0; i < threads; i++) {
				futures.add(service.submit(competeForLock(gcsLock)));
			}

			for (Future f : futures) {
				f.get();
			}
		}
		catch (ExecutionException e) {
			e.printStackTrace();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally {
			service.shutdown();
		}

		assertThat(!doesLockExist()).isTrue();
	}

	/**
	 * Test GcsLock#isHeldByCurrentThread() returns true only if the calling thread obtained the leadership earlier.
	 */
	@Test
	public void testIsHeldByCurrentThread() throws ExecutionException, InterruptedException {
		final int threads = 10;
		ExecutorService executorService = Executors.newFixedThreadPool(threads);

		GcsLock gcsLock = new GcsLock(configuration);
		gcsLock.addLockListener(lifecycleListener);

		if (gcsLock.tryLock()) {
			try {

				assertThat(gcsLock.isLocked()).isTrue();
				assertThat(gcsLock.isHeldByCurrentThread()).isTrue();

				List<Future> futures = IntStream.range(0, threads)
						.mapToObj(intValue -> executorService.submit(() -> {
							assertThat(gcsLock.isLocked()).isTrue();
							assertThat(!gcsLock.isHeldByCurrentThread()).isTrue();
						}))
						.collect(Collectors.toList());

				for (Future future : futures) {
					future.get();
				}
			}
			finally {
				executorService.shutdown();
				gcsLock.unlock();
			}
		}
	}

	/**
	 * Test GcsLock.CleanupDeadLock does not remove live GCS lock file.
	 */
	@Test
	public void testCleanupLongLivingLock() {
		createLongLivingLock();

		GcsLock gcsLock = new GcsLock(configuration);
		gcsLock.addLockListener(lifecycleListener);

		if (gcsLock.tryLock()) {
			try {
				assertThat(!gcsLock.isLocked()).isTrue();

				verify(lifecycleListener,
						after((long) (configuration.getRefreshIntervalInSeconds() * 4000 * 1.5)).never())
						.cleanupDeadLockException(exceptionArgumentCaptor.capture());
			}
			finally {
				gcsLock.unlock();
			}
		}

		assertThat(doesLockExist()).isTrue();

		deleteLock();

		assertThat(!doesLockExist()).isTrue();
	}

	/**
	 * Test expired lock (GcsLock#LOCK_TTL_EPOCH_MS metadata is in the past) is removed.
	 */
	@Test
	public void testCleanupExpiredLock() {
		createExpiredLock();

		GcsLock gcsLock = new GcsLock(configuration);
		gcsLock.addLockListener(lifecycleListener);

		assertThat(!gcsLock.tryLock()).isTrue();
		assertThat(!gcsLock.isLocked()).isTrue();

		verify(lifecycleListener,
				after((long) (configuration.getRefreshIntervalInSeconds() * 1000 * 1.5)).never())
				.cleanupDeadLockException(exceptionArgumentCaptor.capture());

		assertThat(!doesLockExist()).isTrue();
	}

	/**
	 * Test GcsLock.CleanupDeadLock finishes normally if the GCS lock file has been removed somewhere else <br/> either through leadership release or removed by other CleanupDeadLock.
	 */
	@Test
	public void testCleanupGoneLock() {
		createLongLivingLock();

		GcsLock gcsLock = new GcsLock(configuration);
		gcsLock.addLockListener(lifecycleListener);

		assertThat(!gcsLock.tryLock()).isTrue();

		assertThat(!gcsLock.isLocked()).isTrue();

		deleteLock();

		verify(lifecycleListener,
				after((long) (configuration.getRefreshIntervalInSeconds() * 1000 * 1.5)).never())
				.cleanupDeadLockException(exceptionArgumentCaptor.capture());

		assertThat(!doesLockExist()).isTrue();
	}

	/**
	 * test GcsLock.lock().
	 */
	@Test
	public void testLock() throws InterruptedException {
		GcsLock gcsLock = new GcsLock(configuration);

		gcsLock.tryLock();

		assertThat(gcsLock.isLocked() && gcsLock.isHeldByCurrentThread()).isTrue();

		new Thread(() -> {
			gcsLock.lock();
			assertThat(gcsLock.isLocked() && gcsLock.isHeldByCurrentThread()).isTrue();
			LockSupport.parkNanos((long) 1E9);
			gcsLock.unlock();

			synchronized (gcsLock) {
				gcsLock.notifyAll();
			}
		}).start();

		LockSupport.parkNanos((long) (configuration.getLifeExtensionInSeconds() * 1E9));

		gcsLock.unlock();

		synchronized (gcsLock) {
			gcsLock.wait();
		}

		assertThat(!doesLockExist()).isTrue();
	}

	/**
	 * test GcsLock.lock() by multiple thread concurrently.
	 */
	@Test
	public void testLockMultipleThreads() throws InterruptedException {
		GcsLock gcsLock = new GcsLock(configuration);

		gcsLock.tryLock();
		assertThat(gcsLock.isLocked() && gcsLock.isHeldByCurrentThread()).isTrue();

		final int threads = 10;
		ExecutorService service = Executors.newFixedThreadPool(threads);
		List<Future> futures = new ArrayList<>(threads);
		Runnable runnableTask = () -> {
			gcsLock.lock();
			assertThat(gcsLock.isLocked() && gcsLock.isHeldByCurrentThread()).isTrue();
			LockSupport.parkNanos((long) 1E9);
			gcsLock.unlock();
		};

		for (int i = 0; i < threads; i++) {
			futures.add(service.submit(runnableTask));
		}

		LockSupport.parkNanos((long) (configuration.getLifeExtensionInSeconds() * 1E9));
		gcsLock.unlock();

		try {
			for (Future f : futures) {
				f.get();
			}
		}
		catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}
		finally {
			service.shutdown();
		}

		assertThat(!doesLockExist()).isTrue();
	}

	@Test
	public void testTimedTryLockSuccess() throws InterruptedException {
		GcsLock gcsLock1 = new GcsLock(configuration);
		gcsLock1.lock();
		assertThat(gcsLock1.isLocked() && gcsLock1.isHeldByCurrentThread()).isTrue();

		new Thread(() -> {
			GcsLock gcsLock2 = new GcsLock(configuration);
			try {
				gcsLock2.tryLock(configuration.getRefreshIntervalInSeconds() * 2L, TimeUnit.SECONDS);
			}
			catch (InterruptedException e) {
				assertThat(false).isTrue();
			}
			assertThat(gcsLock2.isLocked() && gcsLock2.isHeldByCurrentThread()).isTrue();

			gcsLock2.unlock();

			synchronized (gcsLock1) {
				gcsLock1.notifyAll();
			}
		}).start();

		LockSupport.parkUntil(
				System.currentTimeMillis() + configuration.getRefreshIntervalInSeconds() * 1000);

		gcsLock1.unlock();

		synchronized (gcsLock1) {
			gcsLock1.wait();
		}

		assertThat(!doesLockExist()).isTrue();
	}

	@Test
	public void testTimedTryLockTimeout() throws InterruptedException {
		GcsLock gcsLock1 = new GcsLock(configuration);
		gcsLock1.lock();
		assertThat(gcsLock1.isLocked() && gcsLock1.isHeldByCurrentThread()).isTrue();

		new Thread(() -> {
			GcsLock gcsLock2 = new GcsLock(configuration);
			try {
				gcsLock2.tryLock(configuration.getRefreshIntervalInSeconds(), TimeUnit.SECONDS);
			}
			catch (InterruptedException e) {
				assertThat(false).isTrue();
			}
			assertThat(!gcsLock2.isLocked()).isTrue();
			assertThat(!gcsLock2.isHeldByCurrentThread()).isTrue();
		}).start();

		LockSupport.parkUntil(
				System.currentTimeMillis() + configuration.getRefreshIntervalInSeconds() * 2 * 1000);

		assertThat(gcsLock1.isLocked() && gcsLock1.isHeldByCurrentThread()).isTrue();

		gcsLock1.unlock();

		assertThat(!doesLockExist()).isTrue();
	}

	@Test
	public void testTimedTryLockInterrupted() throws InterruptedException {
		GcsLock gcsLock1 = new GcsLock(configuration);
		gcsLock1.lock();
		assertThat(gcsLock1.isLocked() && gcsLock1.isHeldByCurrentThread()).isTrue();

		Thread gcsLock2Thread = new Thread(() -> {
			GcsLock gcsLock2 = new GcsLock(configuration);
			try {
				gcsLock2.tryLock(configuration.getRefreshIntervalInSeconds() * 2, TimeUnit.SECONDS);
			}
			catch (InterruptedException e) {
				assertThat(Objects.nonNull(e)).isTrue();
				assertThat(!gcsLock2.isLocked()).isTrue();
				assertThat(!gcsLock2.isHeldByCurrentThread()).isTrue();

				synchronized (gcsLock1) {
					gcsLock1.notifyAll();
				}

				return;
			}

			assertThat(false).isTrue();
		});
		gcsLock2Thread.start();

		LockSupport.parkUntil(
				System.currentTimeMillis() + configuration.getRefreshIntervalInSeconds() * 1000);

		gcsLock2Thread.interrupt();

		synchronized (gcsLock1) {
			gcsLock1.wait();
		}

		assertThat(gcsLock1.isLocked() && gcsLock1.isHeldByCurrentThread()).isTrue();

		assertThat(!gcsLock2Thread.isInterrupted()).isTrue();

		gcsLock1.unlock();

		assertThat(!doesLockExist()).isTrue();
	}

	@Test
	public void testLockInterruptibly() throws InterruptedException {
		GcsLock gcsLock1 = new GcsLock(configuration);
		gcsLock1.lock();
		assertThat(gcsLock1.isLocked() && gcsLock1.isHeldByCurrentThread()).isTrue();

		Thread gcsLock2Thread = new Thread(() -> {
			GcsLock gcsLock2 = new GcsLock(configuration);
			try {
				gcsLock2.lockInterruptibly();
			}
			catch (InterruptedException e) {
				assertThat(Objects.nonNull(e)).isTrue();
				assertThat(!gcsLock2.isLocked()).isTrue();
				assertThat(!gcsLock2.isHeldByCurrentThread()).isTrue();

				synchronized (gcsLock1) {
					gcsLock1.notifyAll();
				}

				return;
			}

			assertThat(false).isTrue();
		});
		gcsLock2Thread.start();

		LockSupport.parkUntil(
				System.currentTimeMillis() + configuration.getRefreshIntervalInSeconds() * 1000);

		gcsLock2Thread.interrupt();

		synchronized (gcsLock1) {
			gcsLock1.wait();
		}

		assertThat(gcsLock1.isLocked() && gcsLock1.isHeldByCurrentThread()).isTrue();

		assertThat(!gcsLock2Thread.isInterrupted()).isTrue();

		gcsLock1.unlock();

		assertThat(!doesLockExist()).isTrue();

	}

	@Test
	public void testReentrant() {
		GcsLock gcsLock = new GcsLock(configuration);
		gcsLock.lock();
		assertThat(gcsLock.tryLock()).isTrue();
		gcsLock.unlock();
		assertThat(!doesLockExist()).isTrue();
	}

	private void deleteLock() {
		try {
			storage.delete(configuration.getGcsBucketName(), configuration.getGcsLockFilename());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void recreateLock() {
		try {
			BlobId blobId = BlobId.of(configuration.getGcsBucketName(),
					configuration.getGcsLockFilename());
			BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
			storage.create(blobInfo);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean doesLockExist() {
		boolean ret = false;
		try {
			ret = Objects.nonNull(
					storage.get(configuration.getGcsBucketName(), configuration.getGcsLockFilename()));
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	private void createExpiredLock() {
		try {
			long keepAliveToUnitMillis = System.currentTimeMillis() - 1000L;
			Map<String, String> metaData = new HashMap<>();
			metaData.put(LOCK_TTL_EPOCH_MS, String.valueOf(keepAliveToUnitMillis));

			BlobId blobId = BlobId.of(configuration.getGcsBucketName(),
					configuration.getGcsLockFilename());
			BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setMetadata(metaData).build();
			storage.create(blobInfo);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createLongLivingLock() {
		try {
			long keepAliveToUnitMillis = Long.MAX_VALUE;
			Map<String, String> metaData = new HashMap<>();
			metaData.put(LOCK_TTL_EPOCH_MS, String.valueOf(keepAliveToUnitMillis));

			BlobId blobId = BlobId.of(configuration.getGcsBucketName(),
					configuration.getGcsLockFilename());
			BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setMetadata(metaData).build();
			storage.create(blobInfo);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void executeCriticalSection(GcsLock gcsLock) {
		gcsLock.tryLock();
		assertThat(gcsLock.isLocked()).isTrue();
		if (gcsLock.isLocked()) {
			try {
				LockSupport.parkUntil(
						System.currentTimeMillis() + configuration.getRefreshIntervalInSeconds() * 1000 * 2);
			}
			finally {
				gcsLock.unlock();
			}
		}
	}

	private static Runnable competeForLock(GcsLock gcsLock) {
		return () -> {
			while (!gcsLock.tryLock()) {
				LockSupport.parkUntil(System.currentTimeMillis() + 1000);
			}

			LockSupport.parkUntil(
					System.currentTimeMillis() + configuration.getRefreshIntervalInSeconds() * 1000);

			gcsLock.unlock();
		};
	}

	private boolean isLockAlive() {
		Blob blob = storage.get(configuration.getGcsBucketName(), configuration.getGcsLockFilename());
		Map<String, String> metaData = blob.getMetadata();
		String lockTtl = metaData.get(LOCK_TTL_EPOCH_MS);
		return Long.parseLong(lockTtl) >= System.currentTimeMillis();
	}
}
