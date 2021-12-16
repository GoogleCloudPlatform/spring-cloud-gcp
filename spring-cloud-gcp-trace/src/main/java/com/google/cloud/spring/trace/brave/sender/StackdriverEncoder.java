package com.google.cloud.spring.trace.brave.sender;

import com.google.cloud.spring.trace.brave.translation.SpanTranslator;
import com.google.protobuf.CodedOutputStream;
import java.io.IOException;
import java.util.List;
import zipkin2.Span;
import zipkin2.codec.BytesEncoder;
import zipkin2.codec.Encoding;

@SuppressWarnings("ImmutableEnumChecker") // because span is immutable
public enum StackdriverEncoder implements BytesEncoder<Span> {
	V2 {
		@Override
		public Encoding encoding() {
			return Encoding.PROTO3;
		}

		@Override
		public int sizeInBytes(Span input) {
			return 32 + translate(input).getSerializedSize();
		}

		/** This encodes a TraceSpan message prefixed by a potentially padded 32 character trace ID */
		@Override
		public byte[] encode(Span span) {
			com.google.devtools.cloudtrace.v2.Span translated = translate(span);
			byte[] result = new byte[32 + translated.getSerializedSize()];

			// Zipkin trace ID is conditionally 16 or 32 characters, but Stackdriver needs 32
			String traceId = span.traceId();
			if (traceId.length() == 16) {
				for (int i = 0; i < 16; i++) result[i] = '0';
				for (int i = 0; i < 16; i++) result[i + 16] = (byte) traceId.charAt(i);
			} else {
				for (int i = 0; i < 32; i++) result[i] = (byte) traceId.charAt(i);
			}

			CodedOutputStream output = CodedOutputStream.newInstance(result, 32, result.length - 32);
			try {
				translated.writeTo(output);
			} catch (IOException e) {
				throw new AssertionError(e);
			}
			return result;
		}

		com.google.devtools.cloudtrace.v2.Span translate(Span span) {
			return SpanTranslator.translate(
					com.google.devtools.cloudtrace.v2.Span.newBuilder(), span).build();
		}

		@Override
		public byte[] encodeList(List<Span> spans) {
			throw new UnsupportedOperationException("used in rest api; unused in reporter and collector");
		}
	}
}
