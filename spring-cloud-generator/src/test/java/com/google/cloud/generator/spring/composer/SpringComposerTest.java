/*
 * Copyright 2023 Google LLC
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

package com.google.cloud.generator.spring.composer;

import com.google.api.generator.engine.writer.JavaWriterVisitor;
import com.google.api.generator.gapic.model.GapicClass;
import com.google.api.generator.gapic.model.GapicContext;
import com.google.api.generator.gapic.model.GapicPackageInfo;
import com.google.api.generator.test.framework.Assert;
import com.google.api.generator.test.framework.GoldenFileWriter;
import com.google.api.generator.test.protoloader.TestProtoLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class SpringComposerTest {
  private GapicContext context;

  @Before
  public void setUp() {
    this.context = TestProtoLoader.instance().parseShowcaseEcho();
  }

  @Test
  public void spring_composer_test() {

    List<GapicClass> gapicClasses = SpringComposer.composeServiceAutoConfigClasses(context);
    GapicPackageInfo packageInfo = SpringComposer.composePackageInfo(context);

    // write to verify result for now
    for (GapicClass gapicClazz : gapicClasses) {
      String fileName = gapicClazz.classDefinition().classIdentifier() + "Full.golden";
      Assert.assertGoldenClass(this.getClass(), gapicClazz, fileName);
    }

    String packageInfoFileName = "SpringPackageInfoFull.golden";
    JavaWriterVisitor visitor = new JavaWriterVisitor();
    packageInfo.packageInfo().accept(visitor);
    GoldenFileWriter.saveCodegenToFile(this.getClass(), packageInfoFileName, visitor.write());
    Path packageInfoGoldenFilePath =
        Paths.get(GoldenFileWriter.getGoldenDir(this.getClass()), packageInfoFileName);
    Assert.assertCodeEquals(packageInfoGoldenFilePath, visitor.write());
  }
}
