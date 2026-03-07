package com.maslonka.reservation.errorutils.core.api;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Normalized input used to create an {@link ApiError}.
 *
 * @param timestamp     response creation time
 * @param errorCode     domain error code driving status and machine-readable code
 * @param message       detail message intended for the client
 * @param path          request URI, when available
 * @param correlationId correlation identifier to expose, when available
 * @param traceId       trace identifier to expose, when available
 * @param violations    field-level validation details
 * @param metadata      additional structured error metadata
 */
public record ApiErrorInput(Instant timestamp, ErrorCode errorCode, String message, String path, String correlationId, String traceId,
                            List<FieldViolation> violations, Map<String, Object> metadata) {}
