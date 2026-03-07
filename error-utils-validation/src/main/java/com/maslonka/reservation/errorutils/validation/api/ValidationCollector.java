package com.maslonka.reservation.errorutils.validation.api;

import com.maslonka.reservation.errorutils.core.api.FieldViolation;
import com.maslonka.reservation.errorutils.validation.model.ValidationFailure;
import com.maslonka.reservation.errorutils.validation.model.ValidationResult;
import com.maslonka.reservation.errorutils.validation.pipeline.ValidationChain;

import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

/**
 * Imperative facade for collecting validation failures in the active {@link ValidationChain}.
 */
public final class ValidationCollector {

    private final ValidationChain chain;

    /**
     * Creates a collector backed by the given validation chain.
     *
     * @param chain mutable validation state used to store failures
     */
    public ValidationCollector(ValidationChain chain) {
        this.chain = chain;
    }

    /**
     * Records the supplied failure when the validation result is invalid.
     *
     * @param valid   {@code true} when the check passed
     * @param failure failure description to record when the check fails
     * @return the current collector for fluent usage
     */
    public ValidationCollector check(boolean valid, ValidationFailure failure) {
        chain.check(valid, failure);
        return this;
    }

    /**
     * Evaluates the predicate and records the supplied failure when it returns {@code false}.
     *
     * @param predicate lazily evaluated validation predicate
     * @param failure   failure description to record when the predicate fails
     * @return the current collector for fluent usage
     */
    public ValidationCollector check(BooleanSupplier predicate, ValidationFailure failure) {
        Objects.requireNonNull(predicate, "predicate");
        chain.check(predicate, failure);
        return this;
    }

    /**
     * Alias for {@link #check(boolean, ValidationFailure)} intended for required invariants.
     *
     * @param condition {@code true} when the required condition is satisfied
     * @param failure failure description to record when the condition is not satisfied
     * @return the current collector for fluent usage
     */
    public ValidationCollector require(boolean condition, ValidationFailure failure) {
        chain.require(condition, failure);
        return this;
    }

    /**
     * Alias for {@link #check(BooleanSupplier, ValidationFailure)} intended for required invariants.
     *
     * @param predicate lazily evaluated required condition
     * @param failure failure description to record when the condition is not satisfied
     * @return the current collector for fluent usage
     */
    public ValidationCollector require(BooleanSupplier predicate, ValidationFailure failure) {
        Objects.requireNonNull(predicate, "predicate");
        chain.require(predicate, failure);
        return this;
    }

    /**
     * Builds an immutable snapshot of the current validation result.
     *
     * @return validation result containing collected failures and derived violations
     */
    public ValidationResult toResult() {
        return chain.toResult();
    }

    /**
     * Returns field-level violations derived from the current failures.
     *
     * @return immutable list of field violations
     */
    public List<FieldViolation> toViolations() {
        return chain.toViolations();
    }

    /**
     * Throws the default validation exception when any failure was collected.
     */
    public void throwIfInvalid() {
        chain.throwIfInvalid();
    }

    /**
     * Throws a custom exception when any failure was collected.
     *
     * @param exceptionFactory factory used to convert a failed result into an exception
     */
    public void throwIfInvalid(Function<ValidationResult, ? extends RuntimeException> exceptionFactory) {
        chain.throwIfInvalid(exceptionFactory);
    }
}
