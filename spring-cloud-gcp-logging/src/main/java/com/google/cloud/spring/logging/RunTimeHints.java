package com.google.cloud.spring.logging;

import com.google.cloud.spring.logging.extractors.CloudTraceIdExtractor;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;

import java.util.Arrays;

public class RunTimeHints implements RuntimeHintsRegistrar {
    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.resources().registerPattern("com/google/cloud/spring/logging/.*.xml");

        hints.reflection().registerTypes(Arrays.asList(
                        TypeReference.of(LoggingWebMvcConfigurer.class),
                        TypeReference.of(LoggingAppender.class),
                        TypeReference.of(TraceIdLoggingEnhancer.class),
                        TypeReference.of(TraceIdLoggingWebMvcInterceptor.class),
                        TypeReference.of(StackdriverJsonLayout.class),
                        TypeReference.of(StackdriverErrorReportingServiceContext.class),
                        TypeReference.of(CloudTraceIdExtractor.class)
                ),
                hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                        MemberCategory.INVOKE_PUBLIC_METHODS));
    }
}
