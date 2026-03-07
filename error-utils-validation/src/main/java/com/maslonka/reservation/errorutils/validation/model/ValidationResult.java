package com.maslonka.reservation.errorutils.validation.model;

import com.maslonka.reservation.errorutils.core.api.FieldViolation;

import java.util.List;
import java.util.Map;

public record ValidationResult(ValidationMode mode, List<ValidationFailure> failures, List<FieldViolation> violations) {

    public ValidationResult {
        failures = failures == null ?
                   List.of() :
                   List.copyOf(failures);
        violations = violations == null ?
                     List.of() :
                     List.copyOf(violations);
    }

    public boolean isValid() {
        return failures.isEmpty();
    }

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

    public ValidationFailure firstFailure() {
        return failures.isEmpty() ?
               null :
               failures.getFirst();
    }
}
