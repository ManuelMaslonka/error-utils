package com.maslonka.reservation.errorutils.spring.web.security;

import com.maslonka.reservation.errorutils.core.api.ApiError;
import com.maslonka.reservation.errorutils.spring.web.ApiErrorFactory;
import com.maslonka.reservation.errorutils.spring.web.StandardErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalSecurityExceptionHandler {

    private final ApiErrorFactory apiErrorFactory;

    public GlobalSecurityExceptionHandler(ApiErrorFactory apiErrorFactory) {
        this.apiErrorFactory = apiErrorFactory;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        ApiError body = apiErrorFactory.from(StandardErrorCode.ACCESS_DENIED, "Access denied", request, List.of(), Map.of(), ex);

        return ResponseEntity.status(body.status()).body(body);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthentication(AuthenticationException ex, HttpServletRequest request) {
        ApiError body = apiErrorFactory.from(StandardErrorCode.UNAUTHORIZED, "Unauthorized", request, List.of(), Map.of(), ex);

        return ResponseEntity.status(body.status()).body(body);
    }
}
