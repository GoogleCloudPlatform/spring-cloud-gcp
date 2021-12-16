package com.google.cloud.spring.trace.brave.sender.internal;


import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import java.io.IOException;
import zipkin2.Call;
import zipkin2.Callback;

public abstract class UnaryClientCall<ReqT, RespT> extends Call.Base<RespT> {
	public static final int DEFAULT_SERVER_TIMEOUT_MS = 5000;
	final ClientCall<ReqT, RespT> call;
	final ReqT request;
	final long serverTimeoutMs;

	protected UnaryClientCall(
			Channel channel,
			MethodDescriptor<ReqT, RespT> descriptor,
			CallOptions callOptions,
			ReqT request,
			long serverTimeoutMs) {
		this.call = channel.newCall(descriptor, callOptions);
		this.request = request;
		this.serverTimeoutMs = serverTimeoutMs;
	}

	protected final ReqT request() {
		return request;
	}

	@Override
	protected final RespT doExecute() throws IOException {
		AwaitableUnaryClientCallListener<RespT> listener = new AwaitableUnaryClientCallListener<>(this.serverTimeoutMs);
		beginUnaryCall(listener);
		return listener.await();
	}

	@Override
	protected final void doEnqueue(Callback<RespT> callback) {
		ClientCall.Listener<RespT> listener = new CallbackToUnaryClientCallListener<>(callback);
		try {
			beginUnaryCall(listener);
		} catch (RuntimeException | Error t) {
			callback.onError(t);
			throw t;
		}
	}

	void beginUnaryCall(ClientCall.Listener<RespT> listener) {
		try {
			call.start(listener, new Metadata());
			call.request(1);
			call.sendMessage(request);
			call.halfClose();
		} catch (RuntimeException | Error t) {
			call.cancel(null, t);
			throw t;
		}
	}

	@Override
	protected final void doCancel() {
		call.cancel(null, null);
	}
}

