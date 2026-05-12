package com.maslonka.reservation.errorutils.core.api;

import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Normalized input used to create an {@link ApiError}.
 *
 * @param timestamp     response creation time
 * @param errorCode     domain error code driving status and machine-readable code
 * @param message       detail message intended for the client
 * @param path          request URI, or {@code null} when no request is available
 * @param correlationId correlation identifier to expose, or {@code null} when disabled or unresolved
 * @param traceId       trace identifier to expose, or {@code null} when disabled or unresolved
 * @param violations    field-level validation details
 * @param metadata      additional structured error metadata
 */
public record ApiErrorInput(
    Instant timestamp,
    ErrorCode errorCode,
    String message,
    @Nullable String path,
    @Nullable String correlationId,
    @Nullable String traceId,
    List<FieldViolation> violations,
    Map<String, Object> metadata
) {}
