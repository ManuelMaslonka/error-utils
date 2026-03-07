package com.maslonka.reservation.errorutils.validation.pipeline;

import com.maslonka.reservation.errorutils.core.api.FieldViolation;
import com.maslonka.reservation.errorutils.core.exception.BusinessException;
import com.maslonka.reservation.errorutils.validation.model.ValidationFailure;
import com.maslonka.reservation.errorutils.validation.model.ValidationMode;
import com.maslonka.reservation.errorutils.validation.model.ValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

/**
 * Mutable internal state for a validation run.
 */
public final class ValidationChain {

    private final ValidationMode mode;
    private final List<ValidationFailure> failures;

    private ValidationChain(ValidationMode mode, List<ValidationFailure> failures) {
        this.mode = mode;
        this.failures = failures;
    }

    /**
     * Starts a new validation chain in collect-all mode.
     *
     * @return fresh validation chain
     */
    public static ValidationChain start() {
        return new ValidationChain(ValidationMode.COLLECT_ALL, new ArrayList<>());
    }

    /**
     * Returns a chain view configured for fail-fast execution.
     *
     * @return chain in fail-fast mode
     */
    public ValidationChain failFast() {
        return new ValidationChain(ValidationMode.FAIL_FAST, failures);
    }

    /**
     * Returns a chain view configured for collect-all execution.
     *
     * @return chain in collect-all mode
     */
    public ValidationChain collectAll() {
        return new ValidationChain(ValidationMode.COLLECT_ALL, failures);
    }

    /**
     * Records the supplied failure when the validation result is invalid.
     *
     * @param valid   {@code true} when the check passed
     * @param failure failure description to record when the check fails
     * @return the current chain
     */
    public ValidationChain check(boolean valid, ValidationFailure failure) {
        Objects.requireNonNull(failure, "failure");
        if (shouldSkipEvaluation() || valid) {
            return this;
        }

        failures.add(failure);
        return this;
    }

    /**
     * Evaluates the predicate and records the supplied failure when it returns {@code false}.
     *
     * @param predicate lazily evaluated validation predicate
     * @param failure   failure description to record when the predicate fails
     * @return the current chain
     */
    public ValidationChain check(BooleanSupplier predicate, ValidationFailure failure) {
        Objects.requireNonNull(predicate, "predicate");
        return check(predicate.getAsBoolean(), failure);
    }

    /**
     * Alias for {@link #check(boolean, ValidationFailure)} intended for required invariants.
     *
     * @param condition required condition
     * @param failure failure description to record when the condition fails
     * @return the current chain
     */
    public ValidationChain require(boolean condition, ValidationFailure failure) {
        return check(condition, failure);
    }

    /**
     * Alias for {@link #check(BooleanSupplier, ValidationFailure)} intended for required invariants.
     *
     * @param predicate lazily evaluated required condition
     * @param failure failure description to record when the condition fails
     * @return the current chain
     */
    public ValidationChain require(BooleanSupplier predicate, ValidationFailure failure) {
        return check(predicate, failure);
    }

    /**
     * Builds an immutable snapshot of the current validation state.
     *
     * @return validation result snapshot
     */
    public ValidationResult toResult() {
        List<ValidationFailure> snapshot = List.copyOf(failures);
        List<FieldViolation> violations = snapshot.stream().map(ValidationFailure::toFieldViolation).toList();
        return new ValidationResult(mode, snapshot, violations);
    }

    /**
     * Returns field-level violations derived from the current failures.
     *
     * @return immutable list of field violations
     */
    public List<FieldViolation> toViolations() {
        return toResult().violations();
    }

    /**
     * Throws the default validation exception when any failure was collected.
     */
    public void throwIfInvalid() {
        throwIfInvalid(this::defaultException);
    }

    /**
     * Throws a custom exception when any failure was collected.
     *
     * @param exceptionFactory factory used to convert a failed result into an exception
     */
    public void throwIfInvalid(Function<ValidationResult, ? extends RuntimeException> exceptionFactory) {
        Objects.requireNonNull(exceptionFactory, "exceptionFactory");
        ValidationResult result = toResult();
        if (result.isValid()) {
            return;
        }

        throw exceptionFactory.apply(result);
    }

    private RuntimeException defaultException(ValidationResult result) {
        ValidationFailure firstFailure = result.firstFailure();
        if (firstFailure != null && firstFailure.exceptionFactory() != null) {
            return firstFailure.exceptionFactory().apply(firstFailure);
        }

        return new BusinessException(Objects.requireNonNull(firstFailure, "firstFailure").errorCode(),
                                     firstFailure.message(),
                                     null,
                                     result.metadata(),
                                     result.violations());
    }

    private boolean shouldSkipEvaluation() {
        return mode == ValidationMode.FAIL_FAST && !failures.isEmpty();
    }
}
