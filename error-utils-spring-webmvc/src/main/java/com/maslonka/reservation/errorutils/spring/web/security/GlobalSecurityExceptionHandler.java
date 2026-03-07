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

/**
 * Maps Spring Security exceptions to the standardized {@link ApiError} response format.
 */
@RestControllerAdvice
public class GlobalSecurityExceptionHandler {

    private final ApiErrorFactory apiErrorFactory;

    /**
     * Creates a new handler using the shared API error factory.
     *
     * @param apiErrorFactory factory used to build response payloads
     */
    public GlobalSecurityExceptionHandler(ApiErrorFactory apiErrorFactory) {
        this.apiErrorFactory = apiErrorFactory;
    }

    /**
     * Converts an authorization failure into a standardized access denied response.
     *
     * @param ex      thrown access denied exception
     * @param request current HTTP request
     * @return response describing the authorization failure
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        ApiError body = apiErrorFactory.from(StandardErrorCode.ACCESS_DENIED, "Access denied", request, List.of(), Map.of(), ex);

        return ResponseEntity.status(body.status()).body(body);
    }

    /**
     * Converts an authentication failure into a standardized unauthorized response.
     *
     * @param ex      thrown authentication exception
     * @param request current HTTP request
     * @return response describing the authentication failure
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthentication(AuthenticationException ex, HttpServletRequest request) {
        ApiError body = apiErrorFactory.from(StandardErrorCode.UNAUTHORIZED, "Unauthorized", request, List.of(), Map.of(), ex);

        return ResponseEntity.status(body.status()).body(body);
    }
}
