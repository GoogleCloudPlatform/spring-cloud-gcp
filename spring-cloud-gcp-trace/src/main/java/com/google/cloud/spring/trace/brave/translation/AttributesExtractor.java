package com.google.cloud.spring.trace.brave.translation;

import com.google.devtools.cloudtrace.v2.AttributeValue;
import com.google.devtools.cloudtrace.v2.Span;

import java.util.Map;

import static com.google.cloud.spring.trace.brave.translation.SpanUtil.toTruncatableString;

/**
 * LabelExtractor extracts the set of Stackdriver Span labels equivalent to the annotations in a
 * given Zipkin Span.
 *
 * <p>Zipkin annotations are converted to Stackdriver Span labels by using annotation.value as the
 * key and annotation.timestamp as the value.
 *
 * <p>Zipkin tags are converted to Stackdriver Span labels by using annotation.key as the key and
 * the String value of annotation.value as the value.
 *
 * <p>Zipkin annotations with equivalent Stackdriver labels will be renamed to the Stackdriver
 * Trace
 * name. Any Zipkin annotations without a Stackdriver label equivalent are renamed to
 * zipkin.io/[key_name]
 */
final class AttributesExtractor {

	private static final String kAgentLabelKey = "/agent";
	private static final String kComponentLabelKey = "/component";
	private static final String kKindLabelKey = "/kind";

	private final Map<String, String> renamedLabels;

	AttributesExtractor(Map<String, String> renamedLabels) {
		this.renamedLabels = renamedLabels;
	}

	/**
	 * Extracts the Stackdriver span labels that are equivalent to the Zipkin Span tags.
	 *
	 * @param zipkinSpan The Zipkin Span
	 * @return {@link Span.Attributes} with the Stackdriver span labels equivalent to the Zipkin tags.
	 */
	Span.Attributes extract(zipkin2.Span zipkinSpan) {
		Span.Attributes.Builder attributes = Span.Attributes.newBuilder();

		// Add Kind as a tag for now since there is no structured way of sending it with Stackdriver
		// Trace API V2
		if (zipkinSpan.kind() != null) {
			attributes.putAttributeMap(kKindLabelKey, toAttributeValue(kindLabel(zipkinSpan.kind())));
		}

		for (Map.Entry<String, String> tag : zipkinSpan.tags().entrySet()) {
			attributes.putAttributeMap(getLabelName(tag.getKey()), toAttributeValue(tag.getValue()));
		}

		// Only use server receive spans to extract endpoint data as spans
		// will be rewritten into multiple single-host Stackdriver spans. A client send
		// trace might not show the final destination.
		if (zipkinSpan.localEndpoint() != null && zipkinSpan.kind() == zipkin2.Span.Kind.SERVER) {
			if (zipkinSpan.localEndpoint().ipv4() != null) {
				attributes.putAttributeMap(
						getLabelName("endpoint.ipv4"), toAttributeValue(zipkinSpan.localEndpoint().ipv4()));
			}
			if (zipkinSpan.localEndpoint().ipv6() != null) {
				attributes.putAttributeMap(
						getLabelName("endpoint.ipv6"), toAttributeValue(zipkinSpan.localEndpoint().ipv6()));
			}
		}

		if (zipkinSpan.localEndpoint() != null &&
				zipkinSpan.localEndpoint().serviceName() != null &&
				!zipkinSpan.localEndpoint().serviceName().isEmpty()) {
			attributes.putAttributeMap(
					kComponentLabelKey, toAttributeValue(zipkinSpan.localEndpoint().serviceName()));
		}

		if (zipkinSpan.parentId() == null) {
			String agentName = System.getProperty("stackdriver.trace.zipkin.agent", "zipkin-java");
			attributes.putAttributeMap(kAgentLabelKey, toAttributeValue(agentName));
		}

		return attributes.build();
	}

	static AttributeValue toAttributeValue(String text) {
		return AttributeValue.newBuilder()
				.setStringValue(toTruncatableString(text))
				.build();
	}

	private String getLabelName(String zipkinName) {
		String renamed = renamedLabels.get(zipkinName);
		return renamed != null ? renamed : zipkinName;
	}

	private String kindLabel(zipkin2.Span.Kind kind) {
		switch (kind) {
			case CLIENT:
				return "client";
			case SERVER:
				return "server";
			case PRODUCER:
				return "producer";
			case CONSUMER:
				return "consumer";
			default:
				return kind.name().toLowerCase();
		}
	}
}

