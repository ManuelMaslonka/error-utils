package com.maslonka.reservation.errorutils.core.api;

public record FieldViolation(
    String field,
    Object rejectedValue,
    String message,
    String code
) {
}
