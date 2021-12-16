package com.google.cloud.spring.trace.brave.sender.internal;

import io.grpc.ClientCall;
import io.grpc.Metadata;
import io.grpc.Status;
import zipkin2.Callback;

final class CallbackToUnaryClientCallListener<RespT> extends ClientCall.Listener<RespT> {
	private final Callback<RespT> callback;
	/** this differentiates between not yet set and null */
	boolean valueSet; // guarded by this

	RespT value; // guarded by this

	CallbackToUnaryClientCallListener(Callback<RespT> callback) {
		this.callback = callback;
	}

	@Override
	public void onHeaders(Metadata headers) {}

	@Override
	public synchronized void onMessage(RespT value) {
		if (valueSet) {
			throw Status.INTERNAL
					.withDescription("More than one value received for unary call")
					.asRuntimeException();
		}
		valueSet = true;
		this.value = value;
	}

	@Override
	public synchronized void onClose(Status status, Metadata trailers) {
		if (status.isOk()) {
			if (!valueSet) {
				callback.onError(
						Status.INTERNAL
								.withDescription("No value received for unary call")
								.asRuntimeException(trailers));
			}
			callback.onSuccess(value);
		} else {
			callback.onError(status.asRuntimeException(trailers));
		}
	}
}
