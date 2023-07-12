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

import static com.google.common.truth.Truth.assertThat;

import com.google.api.generator.gapic.model.GapicClass;
import com.google.api.generator.gapic.model.GapicContext;
import com.google.api.generator.gapic.model.Service;
import com.google.api.generator.gapic.model.Transport;
import com.google.api.generator.test.framework.Assert;
import com.google.api.generator.test.protoloader.GrpcRestTestProtoLoader;
import com.google.api.generator.test.protoloader.TestProtoLoader;
import org.junit.Before;
import org.junit.Test;

public class SpringAutoConfigClassComposerTest {
  private GapicContext echoContext;
  private GapicContext echoGrpcRestContext;
  private GapicContext echoRestContext;
  private GapicContext wickedContext;

  private Service echoProtoService;
  private Service echoGrpcRestProtoService;
  private Service echoRestProtoService;
  private Service wickedProtoService;

  @Before
  public void setUp() {
    this.echoContext = TestProtoLoader.instance().parseShowcaseEcho();
    this.echoProtoService = this.echoContext.services().get(0);
    this.echoGrpcRestContext = this.echoContext.toBuilder().setTransport(Transport.GRPC_REST).build();
    this.echoGrpcRestProtoService = this.echoGrpcRestContext.services().get(0);
    this.echoRestContext = this.echoContext.toBuilder().setTransport(Transport.REST).build();
    this.echoRestProtoService = this.echoRestContext.services().get(0);
    this.wickedContext = GrpcRestTestProtoLoader.instance().parseShowcaseWicked();
    this.wickedProtoService = this.wickedContext.services().get(0);
  }

  @Test
  public void generateAutoConfigClazzGrpcTest() {
    assertThat(this.echoContext.transport()).isEqualTo(Transport.GRPC);
    GapicClass clazz =
        SpringAutoConfigClassComposer.instance().generate(this.echoContext, this.echoProtoService);
    String fileName = clazz.classDefinition().classIdentifier() + "Grpc.golden";
    Assert.assertGoldenClass(this.getClass(), clazz, fileName);
  }

  @Test
  public void generateAutoConfigClazzGrpcRestTest() {
    assertThat(this.echoGrpcRestContext.transport()).isEqualTo(Transport.GRPC_REST);
    GapicClass clazz =
        SpringAutoConfigClassComposer.instance()
            .generate(this.echoGrpcRestContext, this.echoGrpcRestProtoService);
    String fileName = clazz.classDefinition().classIdentifier() + "GrpcRest.golden";
    Assert.assertGoldenClass(this.getClass(), clazz, fileName);
  }

  @Test
  public void generateAutoConfigClazzNoRestRpcsTest() {
    assertThat(this.wickedContext.transport()).isEqualTo(Transport.GRPC_REST);
    GapicClass clazz =
        SpringAutoConfigClassComposer.instance()
            .generate(this.wickedContext, this.wickedProtoService);
    String fileName = clazz.classDefinition().classIdentifier() + "NoRestRpcs.golden";
    Assert.assertGoldenClass(this.getClass(), clazz, fileName);
  }

  @Test
  public void generateAutoConfigClazzRestTest() {
    assertThat(this.echoRestContext.transport()).isEqualTo(Transport.REST);
    GapicClass clazz =
        SpringAutoConfigClassComposer.instance()
            .generate(this.echoRestContext, this.echoRestProtoService);
    String fileName = clazz.classDefinition().classIdentifier() + "Rest.golden";
    Assert.assertGoldenClass(this.getClass(), clazz, fileName);
  }
}
