package com.maslonka.reservation.errorutils.spring.web;

import com.maslonka.reservation.errorutils.core.api.ApiError;
import com.maslonka.reservation.errorutils.core.api.FieldViolation;
import com.maslonka.reservation.errorutils.core.exception.BusinessException;
import com.maslonka.reservation.errorutils.core.exception.InternalException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalApiExceptionHandler.class);

    private final ApiErrorFactory apiErrorFactory;

    public GlobalApiExceptionHandler(ApiErrorFactory apiErrorFactory) {
        this.apiErrorFactory = apiErrorFactory;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<FieldViolation> violations = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(this::toFieldViolation)
            .toList();

        ApiError body = apiErrorFactory.from(
            StandardErrorCode.REQUEST_VALIDATION_FAILED,
            "Validation failed",
            request,
            violations,
            Map.of(),
            ex
        );

        return ResponseEntity.status(body.status()).body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        List<FieldViolation> violations = ex.getConstraintViolations()
            .stream()
            .map(this::toFieldViolation)
            .toList();

        ApiError body = apiErrorFactory.from(
            StandardErrorCode.REQUEST_VALIDATION_FAILED,
            "Validation failed",
            request,
            violations,
            Map.of(),
            ex
        );

        return ResponseEntity.status(body.status()).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        ApiError body = apiErrorFactory.from(
            StandardErrorCode.MALFORMED_REQUEST_BODY,
            "Malformed request body",
            request,
            List.of(),
            Map.of(),
            ex
        );

        return ResponseEntity.status(body.status()).body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        ApiError body = apiErrorFactory.from(
            StandardErrorCode.ACCESS_DENIED,
            "Access denied",
            request,
            List.of(),
            Map.of(),
            ex
        );

        return ResponseEntity.status(body.status()).body(body);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthentication(AuthenticationException ex, HttpServletRequest request) {
        ApiError body = apiErrorFactory.from(
            StandardErrorCode.UNAUTHORIZED,
            "Unauthorized",
            request,
            List.of(),
            Map.of(),
            ex
        );

        return ResponseEntity.status(body.status()).body(body);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusiness(BusinessException ex, HttpServletRequest request) {
        ApiError body = apiErrorFactory.fromBusinessException(ex, request);
        return ResponseEntity.status(body.status()).body(body);
    }

    @ExceptionHandler(InternalException.class)
    public ResponseEntity<ApiError> handleInternal(InternalException ex, HttpServletRequest request) {
        log.error("InternalException handled", ex);
        ApiError body = apiErrorFactory.fromInternalException(ex, request);
        return ResponseEntity.status(body.status()).body(body);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiError> handleUnhandled(Throwable ex, HttpServletRequest request) {
        log.error("Unhandled exception", ex);
        ApiError body = apiErrorFactory.fromUnhandled(ex, request);
        return ResponseEntity.status(body.status()).body(body);
    }

    private FieldViolation toFieldViolation(FieldError fieldError) {
        return new FieldViolation(
            fieldError.getField(),
            fieldError.getRejectedValue(),
            fieldError.getDefaultMessage(),
            fieldError.getCode()
        );
    }

    private FieldViolation toFieldViolation(ConstraintViolation<?> violation) {
        return new FieldViolation(
            violation.getPropertyPath().toString(),
            violation.getInvalidValue(),
            violation.getMessage(),
            violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName()
        );
    }
}
