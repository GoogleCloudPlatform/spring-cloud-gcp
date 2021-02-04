package com.google.cloud.spring.autoconfigure.spanner;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import com.google.protobuf.Value;
import com.google.spanner.v1.BatchCreateSessionsResponse;
import com.google.spanner.v1.CommitResponse;
import com.google.spanner.v1.PartialResultSet;
import com.google.spanner.v1.ResultSetMetadata;
import com.google.spanner.v1.Session;
import com.google.spanner.v1.StructType;
import com.google.spanner.v1.Transaction;
import com.google.spanner.v1.Type;
import com.google.spanner.v1.TypeCode;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;

/**
 * gRPC interceptor that stops the chain and returns a pre-set response object.
 */
class ReplacingGrpcCallInterceptor implements ClientInterceptor {

	private Map<String, Object> cannedResponses;

	/**
	 *
	 * @param cannedResponses short method names mapped to hardcoded responses. Pass Status.Code to represent an error.
	 */
	ReplacingGrpcCallInterceptor(Map<String, Object> cannedResponses) {
		this.cannedResponses = cannedResponses;
	}

	@Override
	public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
			MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
		System.out.println("*** Received request: " + method + " with call options " + callOptions);

		Object response = this.cannedResponses.get(method.getBareMethodName());
		if (response == null) {
			throw new IllegalStateException("method " + method.getBareMethodName() + " not supported");
		}
		return new ReplacingClientCall(response);

	}

	/**
	 * Client call returning a single pre-set response and completing.
	 */
	static class ReplacingClientCall extends ClientCall {
		Object response;

		public ReplacingClientCall(Object response) {
			this.response = response;
		}
		@Override
		public void start(Listener listener, Metadata metadata) {
			if (response instanceof Status.Code) {
				listener.onClose(Status.fromCode((Status.Code) response), new Metadata());
			} else {
				listener.onMessage(response);
				listener.onClose(Status.OK, new Metadata());
			}
		}

		@Override
		public void request(int i) {}

		@Override
		public void cancel(String s, Throwable throwable) {}

		@Override
		public void halfClose() {}

		@Override
		public void sendMessage(Object o) {}
	}

	static Map<String, Object> fakeSpannerResponses() {
		Random rand = new Random();
		Map<String, Object> responses = new HashMap<>();
		responses.put("BatchCreateSessions", BatchCreateSessionsResponse.newBuilder()
				.addSession(Session.newBuilder()
								.setName("projects/p/instances/i/databases/d/sessions/fakesession_" + rand.nextInt())
				).build());

		responses.put("BeginTransaction", Transaction.newBuilder()
				.setId(ByteString.copyFromUtf8("txn" + rand.nextInt())).build());

		responses.put("StreamingRead", PartialResultSet.newBuilder()
						.setMetadata(ResultSetMetadata.newBuilder().setRowType(StructType.newBuilder()
								.addFields(
										StructType.Field.newBuilder().setName("id")
												.setType(Type.newBuilder().setCode(TypeCode.STRING)))
								.addFields(
										StructType.Field.newBuilder().setName("num")
												.setType(Type.newBuilder().setCode(TypeCode.INT64)))))
						.addValues(Value.newBuilder().setStringValue("fake_id"))
						.addValues(Value.newBuilder().setStringValue("1234"))
						.build());

		responses.put("Commit", CommitResponse.newBuilder()
				.setCommitTimestamp(Timestamp.getDefaultInstance()).build());
		return responses;
	}
}

