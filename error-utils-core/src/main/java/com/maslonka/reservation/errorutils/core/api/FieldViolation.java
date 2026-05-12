package com.maslonka.reservation.errorutils.core.api;

import org.jspecify.annotations.Nullable;

/**
 * Field-level validation detail included in API error payloads.
 *
 * @param field         logical field path, or {@code null} when the failure is not field-specific
 * @param rejectedValue offending value, when safe to expose; may be {@code null}
 * @param message       validation message intended for the client
 * @param code          validation rule or constraint code, or {@code null} when not applicable
 */
public record FieldViolation(
    @Nullable String field,
    @Nullable Object rejectedValue,
    String message,
    @Nullable String code
) {
}
