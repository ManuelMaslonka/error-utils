package com.maslonka.reservation.errorutils.spring.web.security;

import com.maslonka.reservation.errorutils.core.api.ApiError;
import com.maslonka.reservation.errorutils.spring.web.TestWebMvcSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalSecurityExceptionHandlerTest {

    private final GlobalSecurityExceptionHandler handler = new GlobalSecurityExceptionHandler(TestWebMvcSupport.createFactory(false, true, true));

    @Test
    @DisplayName("Should render unauthorized ApiError when GlobalSecurityExceptionHandler handles authentication failures")
    void shouldRenderUnauthorizedApiErrorWhenGlobalSecurityExceptionHandlerHandlesAuthenticationFailures() {
        MockHttpServletRequest request = request("/secured/auth");

        ApiError body = handler.handleAuthentication(new TestAuthenticationException("Token expired"), request).getBody();

        assertNotNull(body);
        assertEquals(401, body.status());
        assertEquals("UNAUTHORIZED", body.code());
        assertEquals("Unauthorized", body.message());
        assertEquals("/secured/auth", body.path());
        assertEquals("corr-123", body.correlationId());
        assertEquals("trace-456", body.traceId());
    }

    @Test
    @DisplayName("Should render forbidden ApiError when GlobalSecurityExceptionHandler handles access denied failures")
    void shouldRenderForbiddenApiErrorWhenGlobalSecurityExceptionHandlerHandlesAccessDeniedFailures() {
        MockHttpServletRequest request = request("/secured/access");

        ApiError body = handler.handleAccessDenied(new AccessDeniedException("Missing role"), request).getBody();

        assertNotNull(body);
        assertEquals(403, body.status());
        assertEquals("ACCESS_DENIED", body.code());
        assertEquals("Access denied", body.message());
        assertEquals("/secured/access", body.path());
        assertEquals("corr-123", body.correlationId());
        assertEquals("trace-456", body.traceId());
    }

    private MockHttpServletRequest request(String path) {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", path);
        request.addHeader("X-Correlation-Id", "corr-123");
        request.addHeader("X-Trace-Id", "trace-456");
        return request;
    }

    private static final class TestAuthenticationException extends AuthenticationException {

        private TestAuthenticationException(String message) {
            super(message);
        }
    }
}
