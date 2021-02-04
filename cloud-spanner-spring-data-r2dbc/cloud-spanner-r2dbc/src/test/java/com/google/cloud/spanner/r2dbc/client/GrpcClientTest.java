/*
 * Copyright 2019-2020 Google LLC
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.gax.rpc.StatusCode.Code;
import com.google.cloud.NoCredentials;
import com.google.cloud.spanner.r2dbc.StatementExecutionContext;
import com.google.protobuf.ByteString;
import com.google.rpc.Status;
import com.google.spanner.v1.CreateSessionRequest;
import com.google.spanner.v1.ExecuteBatchDmlRequest;
import com.google.spanner.v1.ExecuteBatchDmlResponse;
import com.google.spanner.v1.ExecuteSqlRequest;
import com.google.spanner.v1.PartialResultSet;
import com.google.spanner.v1.ResultSet;
import com.google.spanner.v1.ResultSetStats;
import com.google.spanner.v1.Session;
import com.google.spanner.v1.SpannerGrpc;
import com.google.spanner.v1.SpannerGrpc.SpannerImplBase;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.test.StepVerifier;

/**
 * Test for {@link GrpcClient}.
 */
public class GrpcClientTest {

  static String SESSION_NAME = "/session/1234";

  static ByteString TRANSACTION_ID = ByteString.copyFrom("/transaction/abc".getBytes());

  StatementExecutionContext mockContext;

  /**
   * Sets up execution context mock.
   */
  @BeforeEach
  public void setUp() {
    this.mockContext = mock(StatementExecutionContext.class);
    when(this.mockContext.getSessionName()).thenReturn(SESSION_NAME);
    when(this.mockContext.getTransactionId()).thenReturn(TRANSACTION_ID);
  }

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

    String sql = "select book from library";
    SpannerImplBase spannerSpy = doTest(new SpannerImplBase() {
          @Override
          public void executeStreamingSql(ExecuteSqlRequest request,
              StreamObserver<PartialResultSet> responseObserver) {
            responseObserver.onNext(PartialResultSet.newBuilder().build());
            responseObserver.onCompleted();
          }
        },
        // call the method under test
        grpcClient -> grpcClient.executeStreamingSql(this.mockContext, sql).blockFirst());

    // verify the service was called correctly
    ArgumentCaptor<ExecuteSqlRequest> requestCaptor = ArgumentCaptor
        .forClass(ExecuteSqlRequest.class);
    verify(spannerSpy).executeStreamingSql(requestCaptor.capture(), any());
    assertEquals(sql, requestCaptor.getValue().getSql());
    assertEquals(SESSION_NAME, requestCaptor.getValue().getSession());
    assertEquals(TRANSACTION_ID, requestCaptor.getValue().getTransaction().getId());
  }


  @Test
  public void testBatchDmlErrorPropagation() throws IOException {
    ResultSet expectedResultSet =
        ResultSet.newBuilder()
            .setStats(ResultSetStats.newBuilder().setRowCountExact(20))
            .build();

    doTest(new SpannerImplBase() {
          @Override
          public void executeBatchDml(
              ExecuteBatchDmlRequest request,
              StreamObserver<ExecuteBatchDmlResponse> responseObserver) {
            ExecuteBatchDmlResponse response =
                ExecuteBatchDmlResponse.newBuilder()
                    .setStatus(
                        Status.newBuilder()
                            .setCode(Code.ABORTED.getHttpStatusCode())
                            .setMessage("error message"))
                    .addResultSets(expectedResultSet)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
          }
        },
        grpcClient ->
          StepVerifier
              .create(grpcClient.executeBatchDml(this.mockContext, new ArrayList<>()))
              .expectNext(expectedResultSet)
              .expectErrorMessage("error message")
              .verify()
    );
  }

  @Test
  public void testHostPortConfig() {
    assertEquals("spanner.googleapis.com:443",
        new GrpcClient(NoCredentials.getInstance()).getSpanner().getChannel()
            .authority());
  }

  @Test
  public void testUserAgentConfig()
      throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    GrpcClient grpcClient = new GrpcClient(NoCredentials.getInstance());
    Channel channel = grpcClient.getSpanner().getChannel();
    Class innerChannelWrapperClass = Class.forName("io.grpc.internal.ForwardingManagedChannel");
    Class channelImplClass = Class.forName("io.grpc.internal.ManagedChannelImpl");

    Field deletegateField = innerChannelWrapperClass.getDeclaredField("delegate");
    deletegateField.setAccessible(true);
    Field userAgentField = channelImplClass.getDeclaredField("userAgent");
    userAgentField.setAccessible(true);

    assertTrue(Pattern.matches("cloud-spanner-r2dbc/[0-9A-Za-z._-]+",
        (String) userAgentField.get(deletegateField.get(channel))));
  }

  @Test
  public void testHealthcheck() throws IOException {
    String sql = "SELECT 1";
    SpannerImplBase spannerSpy = doTest(new SpannerImplBase() {
      @Override
      public void executeSql(ExecuteSqlRequest request,
          StreamObserver<ResultSet> responseObserver) {
        responseObserver.onNext(ResultSet.newBuilder().build());
        responseObserver.onCompleted();
      }
      },
        // call the method under test
        grpcClient -> grpcClient.healthcheck(this.mockContext).block());

    // verify the service was called correctly
    ArgumentCaptor<ExecuteSqlRequest> requestCaptor = ArgumentCaptor
        .forClass(ExecuteSqlRequest.class);
    verify(spannerSpy).executeSql(requestCaptor.capture(), any());
    assertEquals(sql, requestCaptor.getValue().getSql());
    assertEquals(SESSION_NAME, requestCaptor.getValue().getSession());
    assertEquals(ByteString.EMPTY, requestCaptor.getValue().getTransaction().getId());

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

    clientConsumer.accept(new GrpcClient(SpannerGrpc.newStub(channel), null, null));

    channel.shutdown();
    server.shutdown();

    return serviceImplSpy;
  }
}
