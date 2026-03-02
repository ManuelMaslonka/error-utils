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

public class JsonAccessDeniedHandler implements AccessDeniedHandler {

    private final ApiErrorFactory apiErrorFactory;
    private final ObjectMapper objectMapper;

    public JsonAccessDeniedHandler(ApiErrorFactory apiErrorFactory, ObjectMapper objectMapper) {
        this.apiErrorFactory = apiErrorFactory;
        this.objectMapper = objectMapper;
    }

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
