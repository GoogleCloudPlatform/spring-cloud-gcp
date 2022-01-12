package com.google.cloud.spring.trace.brave.propagation;

import static brave.internal.codec.HexCodec.lenientLowerHexToUnsignedLong;

import brave.propagation.Propagation;
import brave.propagation.Propagation.Getter;
import brave.propagation.TraceContext;
import brave.propagation.TraceContext.Extractor;
import brave.propagation.TraceContextOrSamplingFlags;
import brave.propagation.TraceIdContext;
import java.util.logging.Level;
import java.util.logging.Logger;

final class CloudTraceContextExtractor<R> implements Extractor<R> {

  static final Logger LOG = Logger.getLogger(CloudTraceContextExtractor.class.getName());

  final Extractor<R> primary;
  final Getter<R, String> getter;

  CloudTraceContextExtractor(Propagation<String> primary, Getter<R, String> getter) {
    this.primary = primary.extractor(getter);
    this.getter = getter;
  }

  /**
   * Creates a tracing context if the extracted string follows the "x-cloud-trace-context: TRACE_ID"
   * or "x-cloud-trace-context: TRACE_ID/SPAN_ID" format; or the "x-cloud-trace-context:
   * TRACE_ID/SPAN_ID;o=TRACE_TRUE" format and {@code TRACE_TRUE}'s value is {@code 1}.
   */
  @Override
  public TraceContextOrSamplingFlags extract(R request) {
    if (request == null) {
      throw new NullPointerException("request == null");
    }
    TraceContextOrSamplingFlags context = primary.extract(request);
    if (context != TraceContextOrSamplingFlags.EMPTY) {
      return context;
    }

    TraceContextOrSamplingFlags result = TraceContextOrSamplingFlags.EMPTY;

    String cloudTraceContext = getter.get(request, StackdriverTracePropagation.TRACE_ID_NAME);

    if (cloudTraceContext != null) {
      String[] tokens = cloudTraceContext.split("/");

      long[] traceId = convertHexTraceIdToLong(tokens[0]);

      // traceId is null if invalid
      if (traceId != null) {
        long spanId = 0; // 0 indicates no span ID is set by the user
        Boolean traceTrue = null; // null means to defer trace decision to sampler

        // A span ID exists. A TRACE_TRUE flag also possibly exists.
        if (tokens.length >= 2) {
          String[] traceOptionTokens = tokens[1].split(";");

          if (traceOptionTokens.length >= 1 && !traceOptionTokens[0].isEmpty()) {
            spanId = parseUnsignedLong(traceOptionTokens[0]);
          }

          if (traceOptionTokens.length >= 2) {
            traceTrue = extractTraceTrueFromToken(traceOptionTokens[1]);
          }
        }

        if (spanId == 0) {
          result =
              TraceContextOrSamplingFlags.create(
                  TraceIdContext.newBuilder()
                      .traceIdHigh(traceId[0])
                      .traceId(traceId[1])
                      .sampled(traceTrue)
                      .build());
        } else {
          result =
              TraceContextOrSamplingFlags.create(
                  TraceContext.newBuilder()
                      .traceIdHigh(traceId[0])
                      .traceId(traceId[1])
                      .spanId(spanId)
                      .sampled(traceTrue)
                      .build());
        }
      }
    }

    return result;
  }

  private static long[] convertHexTraceIdToLong(String hexTraceId) {
    long[] result = new long[2];
    int length = hexTraceId.length();

    if (length != 32) {
      return null;
    }

    // left-most characters, if any, are the high bits
    int traceIdIndex = Math.max(0, length - 16);

    result[0] = lenientLowerHexToUnsignedLong(hexTraceId, 0, traceIdIndex);
    if (result[0] == 0) {
      if (LOG.isLoggable(Level.FINE)) {
        LOG.fine(hexTraceId + " is not a lower hex string.");
      }
      return null;
    }

    // right-most up to 16 characters are the low bits
    result[1] = lenientLowerHexToUnsignedLong(hexTraceId, traceIdIndex, length);
    if (result[1] == 0) {
      if (LOG.isLoggable(Level.FINE)) {
        LOG.fine(hexTraceId + " is not a lower hex string.");
      }
      return null;
    }
    return result;
  }

  /** Strictly parses unsigned numbers without a java 8 dependency. */
  static long parseUnsignedLong(String input) throws NumberFormatException {
    if (input == null) {
      throw new NumberFormatException("input == null");
    }
    int len = input.length();
    if (len == 0) {
      throw new NumberFormatException("empty input");
    }
    if (len > 20) {
      throw new NumberFormatException("too long for uint64: " + input);
    }

    // Bear in mind the following:
    // * maximum int64  is  9223372036854775807. Note it is 19 characters
    // * maximum uint64 is 18446744073709551615. Note it is 20 characters

    // It is safe to use defaults to parse <= 18 characters.
    if (len <= 18) {
      return Long.parseLong(input);
    }

    // we now know it is 19 or 20 characters: safely parse the left 18 characters
    long left = Long.parseLong(input.substring(0, 18));

    int digit19 = digitAt(input, 18);
    int rightDigits = 20 - len;
    if (rightDigits == 1) {
      return left * 10 + digit19; // even 19 9's fit safely in a uint64
    }

    int digit20 = digitAt(input, 19);
    int right = digit19 * 10 + digit20;
    // we can run into trouble if the 18 character prefix is greater than the prefix of the
    // maximum uint64, or the remaining two digits will make the number overflow
    // Reminder, largest uint64 is 18446744073709551615
    if (left > 184467440737095516L || (left == 184467440737095516L && right > 15)) {
      throw new NumberFormatException("out of range for uint64: " + input);
    }
    return left * 100 + right; // we are safe!
  }

  /**
   * Parses the TRACE_TRUE from the header token substring in the form: 'o=TRACE_TRUE'.
   *
   * @return Optional containing the Span ID if present.
   */
  private static Boolean extractTraceTrueFromToken(String traceTrueToken) {
    int optionIndex = traceTrueToken.indexOf("o=");

    Boolean result = null;

    if (optionIndex != -1 && optionIndex + 2 < traceTrueToken.length()) {
      char traceTrueOption = traceTrueToken.charAt(optionIndex + 2);

      if (traceTrueOption == '1') {
        result = true;
      } else if (traceTrueOption == '0') {
        result = false;
      }
    }

    return result;
  }

  private static int digitAt(String input, int position) {
    if (input.length() <= position) {
      throw new NumberFormatException("position out of bounds");
    }

    switch (input.charAt(position)) {
      case '0':
        return 0;
      case '1':
        return 1;
      case '2':
        return 2;
      case '3':
        return 3;
      case '4':
        return 4;
      case '5':
        return 5;
      case '6':
        return 6;
      case '7':
        return 7;
      case '8':
        return 8;
      case '9':
        return 9;
      default:
        throw new NumberFormatException(
            "char at position " + position + "(" + input.charAt(position) + ") isn't a number");
    }
  }
}
