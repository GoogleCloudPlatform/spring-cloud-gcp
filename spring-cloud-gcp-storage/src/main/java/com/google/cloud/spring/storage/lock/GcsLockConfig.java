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
final public class GcsLockConfig {

	private String gcsBucketName;
	private String gcsLockFilename;
	private Integer refreshIntervalInSeconds;
	private Integer lifeExtensionInSeconds;

	private GcsLockConfig() {
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	/**
	 * @return The GCS bucket name where the lock object is stored
	 */
	public String getGcsBucketName() {
		return gcsBucketName;
	}

	/**
	 * @return The name of the lock object in the GCS bucket returned by getGcsBucketName()
	 */
	public String getGcsLockFilename() {
		return gcsLockFilename;
	}

	/**
	 * @return The interval in seconds when the lock is refreshed by the lock owner or a cleanup is
	 * attempted by the other processes that couldn't obtain the lock.
	 */
	public Integer getRefreshIntervalInSeconds() {
		return refreshIntervalInSeconds;
	}

	/**
	 * @return The length in seconds the lock's expiration is extended by the lock owner.
	 */
	public Integer getLifeExtensionInSeconds() {
		return lifeExtensionInSeconds;
	}

	@Override
	public String toString() {
		return String.format("[gcsBucketName=%s, gcsLockFilename=%s]", gcsBucketName, gcsLockFilename);
	}

	static final class Builder {

		private String gcsBucketName;
		private String gcsLockFilename;
		private Integer refreshIntervalInSeconds = 10;
		private Integer lifeExtensionInSeconds = 60;

		private Builder() {
		}

		/**
		 * @param gcsBucketName The GCS bucket name where the lock object is stored
		 * @return
		 */
		public Builder setGcsBucketName(String gcsBucketName) {
			if (gcsBucketName == null) {
				throw new NullPointerException("Null gcsBucketName");
			}
			this.gcsBucketName = gcsBucketName;
			return this;
		}

		/**
		 * @param gcsLockFilename The name of the lock object in the GCS bucket returned by
		 *                         GcsLockConfig#getGcsBucketName()
		 * @return
		 */
		public Builder setGcsLockFilename(String gcsLockFilename) {
			if (gcsLockFilename == null) {
				throw new NullPointerException("Null gcsLockFilename");
			}
			this.gcsLockFilename = gcsLockFilename;
			return this;
		}

		/**
		 * @param refreshIntervalInSeconds The interval in seconds when the lock is refreshed by the
		 *                                  lock owner or a cleanup is attempted by the other processes
		 *                                  that couldn't obtain the lock.
		 * @return
		 */
		public Builder setRefreshIntervalInSeconds(Integer refreshIntervalInSeconds) {
			if (refreshIntervalInSeconds == null) {
				throw new NullPointerException("Null refreshIntervalInSeconds");
			}
			this.refreshIntervalInSeconds = refreshIntervalInSeconds;
			return this;
		}

		/**
		 * @param iifeExtensionInSeconds The length in seconds the lock's expiration is extended by the
		 *                                lock owner.
		 * @return
		 */
		public Builder setLifeExtensionInSeconds(Integer iifeExtensionInSeconds) {
			if (iifeExtensionInSeconds == null) {
				throw new NullPointerException("Null iifeExtensionInSeconds");
			}
			this.lifeExtensionInSeconds = iifeExtensionInSeconds;
			return this;
		}

		public GcsLockConfig build() {
			if (this.gcsBucketName == null
					|| this.gcsLockFilename == null) {
				StringBuilder missing = new StringBuilder();
				if (this.gcsBucketName == null) {
					missing.append(" gcsBucketName");
				}
				if (this.gcsLockFilename == null) {
					missing.append(" gcsLockFilename");
				}
				throw new IllegalStateException("Missing required properties:" + missing);
			}

			GcsLockConfig configuration = new GcsLockConfig();

			configuration.gcsBucketName = this.gcsBucketName;
			configuration.gcsLockFilename = this.gcsLockFilename;
			configuration.refreshIntervalInSeconds = this.refreshIntervalInSeconds;
			configuration.lifeExtensionInSeconds = this.lifeExtensionInSeconds;

			return configuration;
		}
	}
}
