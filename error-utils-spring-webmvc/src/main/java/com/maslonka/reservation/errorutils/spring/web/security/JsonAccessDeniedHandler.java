package com.maslonka.reservation.errorutils.spring.web.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maslonka.reservation.errorutils.core.api.ApiError;
import com.maslonka.reservation.errorutils.spring.web.ApiErrorFactory;
import com.maslonka.reservation.errorutils.spring.web.StandardErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * {@link AccessDeniedHandler} that serializes access denied responses as JSON {@link ApiError}
 * payloads.
 */
public class JsonAccessDeniedHandler implements AccessDeniedHandler {

    private final ApiErrorFactory apiErrorFactory;
    private final ObjectMapper objectMapper;

    /**
     * Creates a new handler.
     *
     * @param apiErrorFactory factory used to build response payloads
     * @param objectMapper    mapper used to serialize the payload
     */
    public JsonAccessDeniedHandler(ApiErrorFactory apiErrorFactory, ObjectMapper objectMapper) {
        this.apiErrorFactory = apiErrorFactory;
        this.objectMapper = objectMapper;
    }

    /**
     * Writes a standardized JSON response for an authorization failure.
     *
     * @param request               current HTTP request
     * @param response              current HTTP response
     * @param accessDeniedException thrown authorization exception
     * @throws IOException      when the response cannot be written
     * @throws ServletException when servlet processing fails
     */
    @Override
    public void handle(
        HttpServletRequest request,
        HttpServletResponse response,
        AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {
        ApiError body = apiErrorFactory.from(
            StandardErrorCode.ACCESS_DENIED,
            "Access denied",
            request,
            List.of(),
            Map.of(),
            accessDeniedException
        );

        response.setStatus(body.status());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
