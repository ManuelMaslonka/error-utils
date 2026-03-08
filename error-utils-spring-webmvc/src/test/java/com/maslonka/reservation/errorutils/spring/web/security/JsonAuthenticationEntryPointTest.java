package com.maslonka.reservation.errorutils.spring.web.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maslonka.reservation.errorutils.spring.web.TestWebMvcSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonAuthenticationEntryPointTest {

    private final ObjectMapper objectMapper = TestWebMvcSupport.createObjectMapper();

    @Test
    @DisplayName("Should write unauthorized JSON payload when JsonAuthenticationEntryPoint commences authentication failure")
    void shouldWriteUnauthorizedJsonPayloadWhenJsonAuthenticationEntryPointCommencesAuthenticationFailure() throws Exception {
        JsonAuthenticationEntryPoint entryPoint = new JsonAuthenticationEntryPoint(TestWebMvcSupport.createFactory(false, true, true), objectMapper);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/secured/auth");
        request.addHeader("X-Correlation-Id", "corr-123");
        request.addHeader("X-Trace-Id", "trace-456");
        MockHttpServletResponse response = new MockHttpServletResponse();

        entryPoint.commence(request, response, new TestAuthenticationException("Token expired"));

        JsonNode body = objectMapper.readTree(response.getContentAsByteArray());
        assertEquals(401, response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
        assertEquals("UNAUTHORIZED", body.get("code").asText());
        assertEquals("Unauthorized", body.get("message").asText());
        assertEquals("/secured/auth", body.get("path").asText());
        assertEquals("corr-123", body.get("correlationId").asText());
        assertEquals("trace-456", body.get("traceId").asText());
    }

    private static final class TestAuthenticationException extends AuthenticationException {

        private TestAuthenticationException(String message) {
            super(message);
        }
    }
}
