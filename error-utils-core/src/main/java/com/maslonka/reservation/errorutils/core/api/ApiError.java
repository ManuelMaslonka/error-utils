package com.maslonka.reservation.errorutils.core.api;

import com.fasterxml.jackson.annotation.JsonInclude;

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
 * @param path          request URI, when available
 * @param correlationId correlation identifier exposed to the client, when enabled
 * @param traceId       trace identifier exposed to the client, when enabled
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
    String path,
    String correlationId,
    String traceId,
    List<FieldViolation> violations,
    Map<String, Object> metadata
) {
}
