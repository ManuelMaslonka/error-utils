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

/**
 * Core HTTP error factory that assembles standardized {@link ApiError} payloads.
 *
 * <p>The factory is responsible for resolving trace information, sanitizing metadata, normalizing
 * field violations, and applying registered {@link ErrorResponseCustomizer customizers}.</p>
 *
 * <p>Relationship to other types:</p>
 *
 * <ul>
 *     <li>Consumes {@link ErrorCode}, {@link BusinessException}, and {@link InternalException}</li>
 *     <li>Uses {@link TraceContextResolver} to resolve {@link TraceContext}</li>
 *     <li>Uses {@link ErrorMetadataSanitizer} to filter response metadata</li>
 *     <li>Delegates payload creation to {@link ApiErrorAssembler}</li>
 *     <li>Feeds {@link ErrorResponseContext} into each {@link ErrorResponseCustomizer}</li>
 * </ul>
 *
 * <p>In a standard Spring MVC setup this class sits behind
 * {@link com.maslonka.reservation.errorutils.spring.web.ApiErrorFactory} and is called by
 * {@link com.maslonka.reservation.errorutils.spring.web.advice.GlobalApiExceptionHandler}.</p>
 *
 * <p>Typical customization path:</p>
 *
 * <pre>{@code
 * @Bean
 * ErrorResponseCustomizer tenantMetadataCustomizer() {
 *     return (apiError, context) -> new ApiError(
 *         apiError.timestamp(),
 *         apiError.status(),
 *         apiError.error(),
 *         apiError.code(),
 *         apiError.message(),
 *         apiError.path(),
 *         apiError.correlationId(),
 *         apiError.traceId(),
 *         apiError.violations(),
 *         Map.of("tenant", context.request().getHeader("X-Tenant-Id"))
 *     );
 * }
 * }</pre>
 */
public class WebApiErrorFactory {

    private final Clock clock;
    private final ApiErrorAssembler apiErrorAssembler;
    private final TraceContextResolver traceContextResolver;
    private final ErrorMetadataSanitizer metadataSanitizer;
    private final ErrorUtilsProperties properties;
    private final List<ErrorResponseCustomizer> errorResponseCustomizers;

    /**
     * Creates a new factory instance.
     *
     * @param clock                    clock used to timestamp responses
     * @param apiErrorAssembler        assembler that creates the final payload
     * @param traceContextResolver     resolver used to obtain correlation and trace identifiers
     * @param metadataSanitizer        sanitizer applied to custom metadata
     * @param properties               runtime rendering properties
     * @param errorResponseCustomizers ordered payload customizers
     */
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

    /**
     * Builds an API error from a domain-level business exception.
     *
     * @param ex      thrown business exception
     * @param request current HTTP request
     * @return assembled error payload
     */
    public ApiError fromBusinessException(BusinessException ex, HttpServletRequest request) {
        return from(ex.errorCode(), ex.getMessage(), request, ex.violations(), ex.metadata(), ex);
    }

    /**
     * Builds an API error from a controlled internal exception.
     *
     * @param ex thrown internal exception
     * @param request current HTTP request
     * @return assembled error payload
     */
    public ApiError fromInternalException(InternalException ex, HttpServletRequest request) {
        return from(ex.errorCode(), safeTechnicalMessage(ex.getMessage()), request, List.of(), Map.of(), ex);
    }

    /**
     * Builds an API error from the supplied error information.
     *
     * @param errorCode domain error code to render
     * @param detail detail message to include
     * @param request current HTTP request
     * @param violations field violations to include
     * @param metadata metadata to sanitize and serialize
     * @return assembled error payload
     */
    public ApiError from(ErrorCode errorCode, String detail, HttpServletRequest request, List<FieldViolation> violations, Map<String, Object> metadata) {
        return from(errorCode, detail, request, violations, metadata, null);
    }

    /**
     * Builds an API error from the supplied error information and original throwable.
     *
     * @param errorCode domain error code to render
     * @param detail detail message to include
     * @param request current HTTP request
     * @param violations field violations to include
     * @param metadata metadata to sanitize and serialize
     * @param throwable original exception associated with the response
     * @return assembled error payload after all customizers have been applied
     */
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

    /**
     * Builds a generic technical error response for an unhandled exception.
     *
     * @param ex unhandled exception
     * @param request current HTTP request
     * @return generic technical error payload
     */
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
