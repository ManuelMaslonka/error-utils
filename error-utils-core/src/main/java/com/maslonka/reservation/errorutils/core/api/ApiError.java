package com.maslonka.reservation.errorutils.core.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Immutable API error payload returned to clients.
 *
 * @param timestamp     response creation time
 * @param status        numeric HTTP status
 * @param error         symbolic HTTP status name
 * @param code          machine-readable application error code
 * @param message       detail message intended for the client
 * @param path          request URI, or {@code null} when no request is available
 * @param correlationId correlation identifier exposed to the client, or {@code null} when disabled or unresolved
 * @param traceId       trace identifier exposed to the client, or {@code null} when disabled or unresolved
 * @param violations    field-level validation details
 * @param metadata      additional structured error metadata
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
    Instant timestamp,
    int status,
    String error,
    String code,
    String message,
    @Nullable String path,
    @Nullable String correlationId,
    @Nullable String traceId,
    List<FieldViolation> violations,
    Map<String, Object> metadata
) {
}
