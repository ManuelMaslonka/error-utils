package com.maslonka.reservation.errorutils.spring.web.factory;

import com.maslonka.reservation.errorutils.core.api.ApiError;
import com.maslonka.reservation.errorutils.core.api.ApiErrorAssembler;
import com.maslonka.reservation.errorutils.core.api.ApiErrorInput;
import com.maslonka.reservation.errorutils.core.api.ErrorCode;
import com.maslonka.reservation.errorutils.core.api.FieldViolation;
import com.maslonka.reservation.errorutils.core.exception.BusinessException;
import com.maslonka.reservation.errorutils.core.exception.InternalException;
import com.maslonka.reservation.errorutils.core.spi.ErrorMetadataSanitizer;
import com.maslonka.reservation.errorutils.spring.web.ErrorResponseCustomizer;
import com.maslonka.reservation.errorutils.spring.web.ErrorUtilsProperties;
import com.maslonka.reservation.errorutils.spring.web.StandardErrorCode;
import com.maslonka.reservation.errorutils.spring.web.advice.ErrorResponseContext;
import com.maslonka.reservation.errorutils.spring.web.trace.TraceContext;
import com.maslonka.reservation.errorutils.spring.web.trace.TraceContextResolver;
import jakarta.servlet.http.HttpServletRequest;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class WebApiErrorFactory {

    private final Clock clock;
    private final ApiErrorAssembler apiErrorAssembler;
    private final TraceContextResolver traceContextResolver;
    private final ErrorMetadataSanitizer metadataSanitizer;
    private final ErrorUtilsProperties properties;
    private final List<ErrorResponseCustomizer> errorResponseCustomizers;

    public WebApiErrorFactory(Clock clock,
                              ApiErrorAssembler apiErrorAssembler,
                              TraceContextResolver traceContextResolver,
                              ErrorMetadataSanitizer metadataSanitizer,
                              ErrorUtilsProperties properties,
                              List<ErrorResponseCustomizer> errorResponseCustomizers) {
        this.clock = clock;
        this.apiErrorAssembler = apiErrorAssembler;
        this.traceContextResolver = traceContextResolver;
        this.metadataSanitizer = metadataSanitizer;
        this.properties = properties;
        this.errorResponseCustomizers = errorResponseCustomizers == null ?
                                        List.of() :
                                        List.copyOf(errorResponseCustomizers);
    }

    public ApiError fromBusinessException(BusinessException ex, HttpServletRequest request) {
        return from(ex.errorCode(), ex.getMessage(), request, ex.violations(), ex.metadata(), ex);
    }

    public ApiError fromInternalException(InternalException ex, HttpServletRequest request) {
        return from(ex.errorCode(), safeTechnicalMessage(ex.getMessage()), request, List.of(), Map.of(), ex);
    }

    public ApiError from(ErrorCode errorCode, String detail, HttpServletRequest request, List<FieldViolation> violations, Map<String, Object> metadata) {
        return from(errorCode, detail, request, violations, metadata, null);
    }

    public ApiError from(ErrorCode errorCode,
                         String detail,
                         HttpServletRequest request,
                         List<FieldViolation> violations,
                         Map<String, Object> metadata,
                         Throwable throwable) {
        List<FieldViolation> normalizedViolations = violations == null ?
                                                    List.of() :
                                                    List.copyOf(violations);
        TraceContext traceContext = traceContextResolver.resolve(request);

        ApiError apiError = apiErrorAssembler.assemble(new ApiErrorInput(Instant.now(clock),
                                                                         errorCode,
                                                                         detail,
                                                                         request == null ?
                                                                         null :
                                                                         request.getRequestURI(),
                                                                         properties.isIncludeCorrelationId() ?
                                                                         traceContext.correlationId() :
                                                                         null,
                                                                         properties.isIncludeTraceId() ?
                                                                         traceContext.traceId() :
                                                                         null,
                                                                         normalizedViolations,
                                                                         metadataSanitizer.sanitize(metadata)));

        Object rejectedValue = normalizedViolations.stream().map(FieldViolation::rejectedValue).filter(Objects::nonNull).findFirst().orElse(null);

        ErrorResponseContext context = new ErrorResponseContext(throwable, request, normalizedViolations, rejectedValue);
        return applyCustomizers(apiError, context);
    }

    public ApiError fromUnhandled(Throwable ex, HttpServletRequest request) {
        return from(StandardErrorCode.TECHNICAL_ERROR, safeTechnicalMessage(ex.getMessage()), request, List.of(), Map.of(), ex);
    }

    private ApiError applyCustomizers(ApiError apiError, ErrorResponseContext context) {
        ApiError current = apiError;
        for (ErrorResponseCustomizer customizer : errorResponseCustomizers) {
            ApiError customized = customizer.customize(current, context);
            if (customized != null) {
                current = customized;
            }
        }
        return current;
    }

    private String safeTechnicalMessage(String detail) {
        if (properties.isIncludeExceptionMessage() && detail != null && !detail.isBlank()) {
            return detail;
        }
        return properties.getInternalErrorMessage();
    }
}
