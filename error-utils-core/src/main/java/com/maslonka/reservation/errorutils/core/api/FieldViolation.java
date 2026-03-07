package com.maslonka.reservation.errorutils.core.api;

/**
 * Field-level validation detail included in API error payloads.
 *
 * @param field         logical field path
 * @param rejectedValue offending value, when safe to expose
 * @param message       validation message intended for the client
 * @param code          validation rule or constraint code
 */
public record FieldViolation(
    String field,
    Object rejectedValue,
    String message,
    String code
) {
}
