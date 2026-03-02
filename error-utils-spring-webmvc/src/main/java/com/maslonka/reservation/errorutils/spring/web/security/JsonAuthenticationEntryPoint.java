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

public class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ApiErrorFactory apiErrorFactory;
    private final ObjectMapper objectMapper;

    public JsonAuthenticationEntryPoint(ApiErrorFactory apiErrorFactory, ObjectMapper objectMapper) {
        this.apiErrorFactory = apiErrorFactory;
        this.objectMapper = objectMapper;
    }

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
