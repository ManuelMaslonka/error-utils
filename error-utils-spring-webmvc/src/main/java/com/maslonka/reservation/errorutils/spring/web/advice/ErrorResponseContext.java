package com.maslonka.reservation.errorutils.spring.web.advice;

import com.maslonka.reservation.errorutils.core.api.FieldViolation;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Context object passed to {@code ErrorResponseCustomizer} implementations.
 *
 * @param throwable     original exception, or {@code null} when not available
 * @param request       current HTTP request
 * @param violations    normalized field violations included in the payload
 * @param rejectedValue first non-null rejected value extracted from violations, or {@code null}
 */
public record ErrorResponseContext(
    @Nullable Throwable throwable,
    HttpServletRequest request,
    List<FieldViolation> violations,
    @Nullable Object rejectedValue
) {}
