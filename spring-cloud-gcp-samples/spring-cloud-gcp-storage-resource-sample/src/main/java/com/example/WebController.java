/*
 * Copyright 2017-2018 the original author or authors.
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

package com.example;

import com.google.cloud.spring.storage.GoogleStorageResource;
import com.google.cloud.storage.Storage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * A REST Controller that exposes read and write operations on a Google Cloud Storage file accessed
 * using the Spring Resource Abstraction.
 */
@RestController
public class WebController {

  @Value("${gcs-resource-test-bucket}")
  private String bucketName;

  @Value("gs://${gcs-resource-test-bucket}/my-file.txt")
  private Resource gcsFile;

  private Storage storage;

  private WebController(Storage storage) {
    this.storage = storage;
  }

  @GetMapping(value = "/")
  public String readGcsFile(@RequestParam("filename") Optional<String> filename)
      throws IOException {
    if (filename.isPresent()) {
      GoogleStorageResource resource = fetchResource(filename);
      return StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset()) + "\n";
    }
    return StreamUtils.copyToString(this.gcsFile.getInputStream(), Charset.defaultCharset()) + "\n";
  }

  @PostMapping(value = "/")
  public String writeGcs(
      @RequestBody String data, @RequestParam("filename") Optional<String> filename)
      throws IOException {
    if (filename.isPresent()) {
      GoogleStorageResource resource = fetchResource(filename);
      return updateResource(resource, data);
    }
    return updateResource(this.gcsFile, data);
  }

  private String updateResource(Resource resource, String data) throws IOException {
    try (OutputStream os = ((WritableResource) resource).getOutputStream()) {
      os.write(data.getBytes());
    }
    return "file was updated\n";
  }

  private GoogleStorageResource fetchResource(Optional<String> filename) {
    return new GoogleStorageResource(
        this.storage, String.format("gs://%s/%s", this.bucketName, filename.get()));
  }
}
