package com.maslonka.reservation.errorutils.spring.web;

import com.maslonka.reservation.errorutils.core.api.FieldViolation;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public record ErrorResponseContext(
    Throwable throwable,
    HttpServletRequest request,
    List<FieldViolation> violations,
    Object rejectedValue
) {
}
