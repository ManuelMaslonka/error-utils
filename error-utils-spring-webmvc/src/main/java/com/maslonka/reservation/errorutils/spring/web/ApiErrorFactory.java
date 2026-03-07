package com.maslonka.reservation.errorutils.spring.web;

import com.maslonka.reservation.errorutils.core.api.ApiErrorAssembler;
import com.maslonka.reservation.errorutils.core.spi.ErrorMetadataSanitizer;
import com.maslonka.reservation.errorutils.spring.web.factory.WebApiErrorFactory;
import com.maslonka.reservation.errorutils.spring.web.trace.TraceContextResolver;

import java.time.Clock;
import java.util.List;

public class ApiErrorFactory extends WebApiErrorFactory {

    public ApiErrorFactory(Clock clock, ApiErrorAssembler apiErrorAssembler,
        TraceContextResolver traceContextResolver,
        ErrorMetadataSanitizer metadataSanitizer,
        ErrorUtilsProperties properties,
        List<ErrorResponseCustomizer> errorResponseCustomizers
    ) {
        super(clock, apiErrorAssembler, traceContextResolver, metadataSanitizer, properties, errorResponseCustomizers);
    }
}
