/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.spanner.r2dbc.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.google.spanner.v1.CreateSessionRequest;
import com.google.spanner.v1.ExecuteSqlRequest;
import com.google.spanner.v1.PartialResultSet;
import com.google.spanner.v1.Session;
import com.google.spanner.v1.SpannerGrpc;
import com.google.spanner.v1.SpannerGrpc.SpannerImplBase;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.function.Consumer;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Flux;

/**
 * Test for {@link GrpcClient}.
 */
public class GrpcClientTest {


  @Test
  public void testCreateSession() throws IOException {

    SpannerImplBase spannerSpy = doTest(new SpannerImplBase() {
          @Override
          public void createSession(CreateSessionRequest request,
              StreamObserver<Session> responseObserver) {
            responseObserver.onNext(Session.newBuilder().build());
            responseObserver.onCompleted();
          }
        },
        // call the method under test
        grpcClient -> grpcClient.createSession("testDb").block()
    );

    // verify the service was called correctly
    ArgumentCaptor<CreateSessionRequest> requestCaptor = ArgumentCaptor
        .forClass(CreateSessionRequest.class);
    verify(spannerSpy).createSession(requestCaptor.capture(), any());
    assertEquals("testDb", requestCaptor.getValue().getDatabase());
  }

  @Test
  public void testExecuteStreamingSql() throws IOException {

    ExecuteSqlRequest request = ExecuteSqlRequest.newBuilder().build();

    SpannerImplBase spannerSpy = doTest(new SpannerImplBase() {
          @Override
          public void executeStreamingSql(ExecuteSqlRequest request,
              StreamObserver<PartialResultSet> responseObserver) {
            responseObserver.onNext(PartialResultSet.newBuilder().build());
            responseObserver.onCompleted();
          }
        },
        // call the method under test
        grpcClient -> Flux.from(grpcClient.executeStreamingSql(request)).blockFirst()
    );

    // verify the service was called correctly
    ArgumentCaptor<ExecuteSqlRequest> requestCaptor = ArgumentCaptor
        .forClass(ExecuteSqlRequest.class);
    verify(spannerSpy).executeStreamingSql(requestCaptor.capture(), any());
    assertSame(request, requestCaptor.getValue());
  }

  /**
   * Starts and shuts down an in-process gRPC service based on the {@code serviceImpl} provided,
   * while allowing a test to execute using the {@link GrpcClient}.
   *
   * @param serviceImpl implementation of the Spanner service. Typically, just the methods needed to
   *     execute the test.
   * @param clientConsumer consumer of the {@link GrpcClient} - the class under test.
   * @return a Mockito spy for the gRPC service for verification.
   */
  private SpannerImplBase doTest(SpannerGrpc.SpannerImplBase serviceImpl,
      Consumer<GrpcClient> clientConsumer)
      throws IOException {
    SpannerGrpc.SpannerImplBase serviceImplSpy = spy(serviceImpl);

    String serverName = InProcessServerBuilder.generateName();

    Server server = InProcessServerBuilder
        .forName(serverName).directExecutor().addService(serviceImplSpy).build().start();

    ManagedChannel channel =
        InProcessChannelBuilder.forName(serverName).directExecutor().build();

    clientConsumer.accept(new GrpcClient(SpannerGrpc.newStub(channel)));

    channel.shutdown();
    server.shutdown();

    return serviceImplSpy;
  }
}
