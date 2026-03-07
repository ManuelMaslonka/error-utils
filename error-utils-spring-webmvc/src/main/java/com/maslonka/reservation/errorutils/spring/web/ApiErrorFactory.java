package com.maslonka.reservation.errorutils.spring.web;

import com.maslonka.reservation.errorutils.core.api.ApiErrorAssembler;
import com.maslonka.reservation.errorutils.core.spi.ErrorMetadataSanitizer;
import com.maslonka.reservation.errorutils.spring.web.factory.WebApiErrorFactory;
import com.maslonka.reservation.errorutils.spring.web.trace.TraceContextResolver;

import java.time.Clock;
import java.util.List;

/**
 * Public Spring-facing facade for building {@link com.maslonka.reservation.errorutils.core.api.ApiError}
 * responses.
 *
 * <p>The class exposes the {@link WebApiErrorFactory} behavior as a concrete bean type that can be
 * overridden by applications when they need custom response assembly logic.</p>
 */
public class ApiErrorFactory extends WebApiErrorFactory {

    /**
     * Creates a new factory instance using the collaborators required for HTTP error assembly.
     *
     * @param clock                    clock used to timestamp responses
     * @param apiErrorAssembler        assembler that turns normalized input into {@code ApiError}
     * @param traceContextResolver     resolver used to obtain correlation and trace identifiers
     * @param metadataSanitizer        sanitizer applied to metadata before serialization
     * @param properties               runtime configuration for error rendering
     * @param errorResponseCustomizers ordered response customizers
     */
    public ApiErrorFactory(Clock clock, ApiErrorAssembler apiErrorAssembler,
        TraceContextResolver traceContextResolver,
        ErrorMetadataSanitizer metadataSanitizer,
        ErrorUtilsProperties properties,
        List<ErrorResponseCustomizer> errorResponseCustomizers
    ) {
        super(clock, apiErrorAssembler, traceContextResolver, metadataSanitizer, properties, errorResponseCustomizers);
    }
}
