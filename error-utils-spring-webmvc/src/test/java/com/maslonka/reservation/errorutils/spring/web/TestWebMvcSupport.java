package com.maslonka.reservation.errorutils.spring.web;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.maslonka.reservation.errorutils.core.api.ApiErrorAssembler;
import com.maslonka.reservation.errorutils.core.spi.ErrorMetadataSanitizer;
import com.maslonka.reservation.errorutils.spring.web.trace.DefaultTraceContextResolver;
import com.maslonka.reservation.errorutils.spring.web.trace.TraceContextResolver;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

public final class TestWebMvcSupport {

    public static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2026-03-06T10:15:30Z"), ZoneOffset.UTC);

    private TestWebMvcSupport() {
    }

    public static ApiErrorFactory createFactory(boolean includeExceptionMessage, boolean includeCorrelationId, boolean includeTraceId) {
        return createFactory(includeExceptionMessage,
                             includeCorrelationId,
                             includeTraceId,
                             metadata -> metadata == null ?
                                         Map.of() :
                                         Map.copyOf(metadata),
                             List.of());
    }

    public static ApiErrorFactory createFactory(boolean includeExceptionMessage,
                                                boolean includeCorrelationId,
                                                boolean includeTraceId,
                                                ErrorMetadataSanitizer sanitizer,
                                                List<ErrorResponseCustomizer> customizers) {
        ErrorUtilsProperties properties = new ErrorUtilsProperties();
        properties.setIncludeExceptionMessage(includeExceptionMessage);
        properties.setIncludeCorrelationId(includeCorrelationId);
        properties.setIncludeTraceId(includeTraceId);

        TraceContextResolver traceContextResolver = new DefaultTraceContextResolver(properties);
        return new ApiErrorFactory(FIXED_CLOCK, new ApiErrorAssembler(), traceContextResolver, sanitizer, properties, customizers);
    }

    public static ObjectMapper createObjectMapper() {
        SimpleModule instantModule = new SimpleModule();
        instantModule.addSerializer(Instant.class, new StdSerializer<>(Instant.class) {
            @Override
            public void serialize(Instant value, JsonGenerator generator, SerializerProvider provider) throws IOException {
                generator.writeString(value.toString());
            }
        });

        return JsonMapper.builder().addModule(instantModule).build();
    }
}
