package com.maslonka.reservation.errorutils.spring.web.advice;

import com.maslonka.reservation.errorutils.core.api.ApiError;
import com.maslonka.reservation.errorutils.core.api.FieldViolation;
import com.maslonka.reservation.errorutils.core.exception.BusinessException;
import com.maslonka.reservation.errorutils.core.exception.InternalException;
import com.maslonka.reservation.errorutils.spring.web.ApiErrorFactory;
import com.maslonka.reservation.errorutils.spring.web.StandardErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

/**
 * Maps common application and framework exceptions to a standardized {@link ApiError} response.
 *
 * <p>This advice is the default bridge between thrown exceptions and the HTTP contract exposed by
 * the library. It delegates actual payload creation to {@link ApiErrorFactory}, which means that
 * customizers such as {@link com.maslonka.reservation.errorutils.spring.web.ErrorResponseCustomizer}
 * still apply even when exceptions are handled here.</p>
 *
 * <p>Related components:</p>
 *
 * <ul>
 *     <li>{@link ApiErrorFactory} for payload creation</li>
 *     <li>{@link com.maslonka.reservation.errorutils.spring.web.security.GlobalSecurityExceptionHandler}
 *     for security-specific exceptions</li>
 *     <li>{@link StandardErrorCode} for framework-level default error codes</li>
 * </ul>
 *
 * <p>If your service needs a different exception-to-status mapping, replace this advice with a
 * custom bean of the same type or provide an alternative controller advice.</p>
 */
@RestControllerAdvice
public class GlobalApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalApiExceptionHandler.class);

    private final ApiErrorFactory apiErrorFactory;

    /**
     * Creates a new handler using the shared API error factory.
     *
     * @param apiErrorFactory factory used to build response payloads
     */
    public GlobalApiExceptionHandler(ApiErrorFactory apiErrorFactory) {
        this.apiErrorFactory = apiErrorFactory;
    }

    /**
     * Converts bean validation errors from request binding into a standard validation response.
     *
     * @param ex      thrown validation exception
     * @param request current HTTP request
     * @return response containing normalized field violations
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<FieldViolation> violations = ex.getBindingResult().getFieldErrors().stream().map(this::toFieldViolation).toList();

        ApiError body = apiErrorFactory.from(StandardErrorCode.REQUEST_VALIDATION_FAILED, "Validation failed", request, violations, Map.of(), ex);

        return ResponseEntity.status(body.status()).body(body);
    }

    /**
     * Converts constraint violations raised outside object binding into a standard validation response.
     *
     * @param ex      thrown constraint violation exception
     * @param request current HTTP request
     * @return response containing normalized field violations
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        List<FieldViolation> violations = ex.getConstraintViolations().stream().map(this::toFieldViolation).toList();

        ApiError body = apiErrorFactory.from(StandardErrorCode.REQUEST_VALIDATION_FAILED, "Validation failed", request, violations, Map.of(), ex);

        return ResponseEntity.status(body.status()).body(body);
    }

    /**
     * Converts malformed request body errors into a standardized response.
     *
     * @param ex thrown parsing exception
     * @param request current HTTP request
     * @return response describing the malformed body
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        ApiError body = apiErrorFactory.from(StandardErrorCode.MALFORMED_REQUEST_BODY, "Malformed request body", request, List.of(), Map.of(), ex);

        return ResponseEntity.status(body.status()).body(body);
    }

    /**
     * Converts expected domain exceptions into a standardized response.
     *
     * @param ex thrown business exception
     * @param request current HTTP request
     * @return response based on the embedded domain error code
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusiness(BusinessException ex, HttpServletRequest request) {
        ApiError body = apiErrorFactory.fromBusinessException(ex, request);
        return ResponseEntity.status(body.status()).body(body);
    }

    /**
     * Converts controlled technical exceptions into a standardized response and logs the failure.
     *
     * @param ex thrown internal exception
     * @param request current HTTP request
     * @return response based on the embedded domain error code
     */
    @ExceptionHandler(InternalException.class)
    public ResponseEntity<ApiError> handleInternal(InternalException ex, HttpServletRequest request) {
        log.error("InternalException handled", ex);
        ApiError body = apiErrorFactory.fromInternalException(ex, request);
        return ResponseEntity.status(body.status()).body(body);
    }

    /**
     * Converts any unhandled exception into a generic technical error response.
     *
     * @param ex unhandled exception
     * @param request current HTTP request
     * @return generic technical error response
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiError> handleUnhandled(Throwable ex, HttpServletRequest request) {
        log.error("Unhandled exception", ex);
        ApiError body = apiErrorFactory.fromUnhandled(ex, request);
        return ResponseEntity.status(body.status()).body(body);
    }

    private FieldViolation toFieldViolation(FieldError fieldError) {
        return new FieldViolation(fieldError.getField(), fieldError.getRejectedValue(), fieldError.getDefaultMessage(), fieldError.getCode());
    }

    private FieldViolation toFieldViolation(ConstraintViolation<?> violation) {
        return new FieldViolation(violation.getPropertyPath().toString(),
                                  violation.getInvalidValue(),
                                  violation.getMessage(),
                                  violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName());
    }
}
