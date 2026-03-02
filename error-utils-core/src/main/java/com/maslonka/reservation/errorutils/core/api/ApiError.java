package com.maslonka.reservation.errorutils.core.api;

import java.time.Instant;
import java.util.List;
import java.util.Map;

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
