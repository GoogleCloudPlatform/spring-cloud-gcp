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

/**
 * Adapted from https://github.com/org-openbites/distributed-lock.
 *
 * @author Williams Simon (openbites.org@gmail.com)
 */
public interface GcsLockListener {

	/**
	 * Callback for when exception has been encountered during lock acquisition.
	 *
	 * @param exception The exception encountered during lock acquisition
	 */
	void acquireLockException(Exception exception);

	/**
	 * Callback for when exception has been encountered during lock release.
	 *
	 * @param exception The exception encountered during lock release
	 */
	void releaseLockException(Exception exception);

	/**
	 * Callback for when exception has been encountered while keeping the lock alive.
	 *
	 * @param exception The exception encountered while keeping the lock alive
	 */
	void keepLockAliveException(Exception exception);

	/**
	 * Callback for when exception has been encountered while cleaning up the expired lock.
	 *
	 * @param exception The exception encountered while cleaning up the expired lock
	 */
	void cleanupDeadLockException(Exception exception);
}
