package com.maslonka.reservation.errorutils.validation.api;

import com.maslonka.reservation.errorutils.validation.pipeline.ObjectValidator;
import com.maslonka.reservation.errorutils.validation.pipeline.ValidationChain;

/**
 * Entry point for building fluent validators for arbitrary target objects.
 *
 * <p>This is the usual starting point for validation flows. It creates an
 * {@link ObjectValidator} bound to a specific target and backed by a fresh
 * {@link ValidationChain} in collect-all mode.</p>
 *
 * <p>Typical flow:</p>
 *
 * <pre>{@code
 * ValidationResult result = Validator.forObject(command)
 *     .collectAll()
 *     .step(basicValidator)
 *     .step((collector) -> collector.check(
 *         featureEnabled,
 *         ValidationFailure.of(UserErrorCode.FEATURE_DISABLED, "Feature is disabled")
 *     ))
 *     .toResult();
 * }</pre>
 *
 * @see ObjectValidator
 * @see ValidationStep
 * @see com.maslonka.reservation.errorutils.validation.model.ValidationResult
 */
public final class Validator {

    private Validator() {
    }

    /**
     * Starts validation for the given target in collect-all mode.
     *
     * @param target validated object
     * @param <T>    target type
     * @return fluent validator bound to the supplied target
     */
    public static <T> ObjectValidator<T> forObject(T target) {
        return new ObjectValidator<>(target, ValidationChain.start());
    }
}
