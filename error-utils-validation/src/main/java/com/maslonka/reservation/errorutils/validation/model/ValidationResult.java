package com.maslonka.reservation.errorutils.validation.model;

import com.maslonka.reservation.errorutils.core.api.FieldViolation;

import java.util.List;
import java.util.Map;

/**
 * Immutable snapshot of the current validation outcome.
 *
 * <p>This object is the read-model produced by the validation pipeline. It exposes both the raw
 * {@link ValidationFailure failures} and their API-oriented {@link FieldViolation} projection, so
 * callers can decide whether to build domain exceptions, enrich logs, or serialize field-level
 * details.</p>
 *
 * <p>It is typically obtained from {@link com.maslonka.reservation.errorutils.validation.api.Validator}
 * via {@code toResult()}.</p>
 *
 * @param mode       validation execution mode
 * @param failures   collected validation failures
 * @param violations field-level violations derived from the failures
 * @see ValidationFailure
 * @see com.maslonka.reservation.errorutils.validation.pipeline.ValidationChain
 */
public record ValidationResult(ValidationMode mode, List<ValidationFailure> failures, List<FieldViolation> violations) {

    /**
     * Creates a normalized validation result with immutable collections.
     */
    public ValidationResult {
        failures = failures == null ?
                   List.of() :
                   List.copyOf(failures);
        violations = violations == null ?
                     List.of() :
                     List.copyOf(violations);
    }

    /**
     * Indicates whether validation completed without failures.
     *
     * @return {@code true} when no failures were recorded
     */
    public boolean isValid() {
        return failures.isEmpty();
    }

    /**
     * Merges metadata from all collected failures.
     *
     * @return immutable merged metadata map
     */
    public Map<String, Object> metadata() {
        if (failures.isEmpty()) {
            return Map.of();
        }

        Map<String, Object> merged = new java.util.LinkedHashMap<>();
        for (ValidationFailure failure : failures) {
            merged.putAll(failure.metadata());
        }
        return Map.copyOf(merged);
    }

    /**
     * Returns the first collected failure.
     *
     * @return first failure or {@code null} when the result is valid
     */
    public ValidationFailure firstFailure() {
        return failures.isEmpty() ?
               null :
               failures.getFirst();
    }
}
