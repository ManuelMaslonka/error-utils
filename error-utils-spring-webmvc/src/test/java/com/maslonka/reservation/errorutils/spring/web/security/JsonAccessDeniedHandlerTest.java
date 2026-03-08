package com.maslonka.reservation.errorutils.spring.web.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maslonka.reservation.errorutils.spring.web.TestWebMvcSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonAccessDeniedHandlerTest {

    private final ObjectMapper objectMapper = TestWebMvcSupport.createObjectMapper();

    @Test
    @DisplayName("Should write forbidden JSON payload when JsonAccessDeniedHandler handles authorization failure")
    void shouldWriteForbiddenJsonPayloadWhenJsonAccessDeniedHandlerHandlesAuthorizationFailure() throws Exception {
        JsonAccessDeniedHandler handler = new JsonAccessDeniedHandler(TestWebMvcSupport.createFactory(false, true, true), objectMapper);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/secured/access");
        request.addHeader("X-Correlation-Id", "corr-123");
        request.addHeader("X-Trace-Id", "trace-456");
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.handle(request, response, new AccessDeniedException("Missing role"));

        JsonNode body = objectMapper.readTree(response.getContentAsByteArray());
        assertEquals(403, response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
        assertEquals("ACCESS_DENIED", body.get("code").asText());
        assertEquals("Access denied", body.get("message").asText());
        assertEquals("/secured/access", body.get("path").asText());
        assertEquals("corr-123", body.get("correlationId").asText());
        assertEquals("trace-456", body.get("traceId").asText());
    }
}
