package com.maslonka.reservation.errorutils.spring.web.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maslonka.reservation.errorutils.core.api.ApiError;
import com.maslonka.reservation.errorutils.spring.web.ApiErrorFactory;
import com.maslonka.reservation.errorutils.spring.web.StandardErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * {@link AuthenticationEntryPoint} that serializes authentication failures as JSON
 * {@link ApiError} payloads.
 */
public class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ApiErrorFactory apiErrorFactory;
    private final ObjectMapper objectMapper;

    /**
     * Creates a new entry point.
     *
     * @param apiErrorFactory factory used to build response payloads
     * @param objectMapper    mapper used to serialize the payload
     */
    public JsonAuthenticationEntryPoint(ApiErrorFactory apiErrorFactory, ObjectMapper objectMapper) {
        this.apiErrorFactory = apiErrorFactory;
        this.objectMapper = objectMapper;
    }

    /**
     * Writes a standardized JSON response for an authentication failure.
     *
     * @param request       current HTTP request
     * @param response      current HTTP response
     * @param authException thrown authentication exception
     * @throws IOException      when the response cannot be written
     * @throws ServletException when servlet processing fails
     */
    @Override
    public void commence(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException authException
    ) throws IOException, ServletException {
        ApiError body = apiErrorFactory.from(
            StandardErrorCode.UNAUTHORIZED,
            "Unauthorized",
            request,
            List.of(),
            Map.of(),
            authException
        );

        response.setStatus(body.status());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
