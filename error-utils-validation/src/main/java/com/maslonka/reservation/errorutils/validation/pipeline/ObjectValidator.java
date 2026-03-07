package com.maslonka.reservation.errorutils.validation.pipeline;

import com.maslonka.reservation.errorutils.core.api.FieldViolation;
import com.maslonka.reservation.errorutils.validation.api.ValidationCollector;
import com.maslonka.reservation.errorutils.validation.api.ValidationStep;
import com.maslonka.reservation.errorutils.validation.model.ValidationFailure;
import com.maslonka.reservation.errorutils.validation.model.ValidationResult;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Fluent validator bound to a concrete target object.
 *
 * <p>This class is the main consumer-facing API of the validation module. It combines the target
 * being validated with a mutable {@link ValidationChain} and an imperative
 * {@link ValidationCollector}. Reusable typed rules are typically modeled as {@link ValidationStep},
 * while ad-hoc checks are usually expressed through {@link #step(Consumer)} or
 * {@link #check(boolean, ValidationFailure)}.</p>
 *
 * <p>Example:</p>
 *
 * <pre>{@code
 * Validator.forObject(command)
 *     .failFast()
 *     .step(createCustomerValidator)
 *     .step((collector) -> collector.require(
 *         tenantEnabled,
 *         ValidationFailure.of(CustomerErrorCode.TENANT_DISABLED, "Tenant is disabled")
 *     ))
 *     .throwIfInvalid();
 * }</pre>
 *
 * @param <T> validated target type
 * @see ValidationStep
 * @see ValidationCollector
 * @see ValidationChain
 */
public final class ObjectValidator<T> {

    private final T target;
    private final ValidationChain chain;
    private final ValidationCollector collector;

    /**
     * Creates a validator for the given target and chain.
     *
     * @param target validated object
     * @param chain  mutable validation state
     */
    public ObjectValidator(T target, ValidationChain chain) {
        this.target = target;
        this.chain = chain;
        this.collector = new ValidationCollector(chain);
    }

    /**
     * Returns a validator view that stops recording after the first failure.
     *
     * @return validator configured for fail-fast execution
     */
    public ObjectValidator<T> failFast() {
        return new ObjectValidator<>(target, chain.failFast());
    }

    /**
     * Returns a validator view that records all failures.
     *
     * @return validator configured for collect-all execution
     */
    public ObjectValidator<T> collectAll() {
        return new ObjectValidator<>(target, chain.collectAll());
    }

    /**
     * Executes a reusable validation step against the current target.
     *
     * @param step validation step to invoke
     * @return the current validator for fluent usage
     */
    public ObjectValidator<T> step(ValidationStep<? super T> step) {
        Objects.requireNonNull(step, "step");
        step.validate(target, collector);
        return this;
    }

    /**
     * Executes inline validation logic using the current collector.
     *
     * @param step consumer operating on the collector
     * @return the current validator for fluent usage
     */
    public ObjectValidator<T> step(Consumer<? super ValidationCollector> step) {
        Objects.requireNonNull(step, "step");
        step.accept(collector);
        return this;
    }

    /**
     * Records the supplied failure when the validation result is invalid.
     *
     * @param valid   {@code true} when the check passed
     * @param failure failure description to record when the check fails
     * @return the current validator for fluent usage
     */
    public ObjectValidator<T> check(boolean valid, ValidationFailure failure) {
        collector.check(valid, failure);
        return this;
    }

    /**
     * Evaluates the predicate and records the supplied failure when it returns {@code false}.
     *
     * @param predicate lazily evaluated validation predicate
     * @param failure   failure description to record when the predicate fails
     * @return the current validator for fluent usage
     */
    public ObjectValidator<T> check(java.util.function.BooleanSupplier predicate, ValidationFailure failure) {
        collector.check(predicate, failure);
        return this;
    }

    /**
     * Alias for {@link #check(boolean, ValidationFailure)} intended for required invariants.
     *
     * @param condition required condition
     * @param failure   failure description to record when the condition fails
     * @return the current validator for fluent usage
     */
    public ObjectValidator<T> require(boolean condition, ValidationFailure failure) {
        collector.require(condition, failure);
        return this;
    }

    /**
     * Builds an immutable validation result.
     *
     * @return validation result snapshot
     */
    public ValidationResult toResult() {
        return collector.toResult();
    }

    /**
     * Returns field-level violations derived from the current failures.
     *
     * @return immutable list of field violations
     */
    public List<FieldViolation> toViolations() {
        return collector.toViolations();
    }

    /**
     * Throws the default validation exception when any failure was collected.
     */
    public void throwIfInvalid() {
        collector.throwIfInvalid();
    }

    /**
     * Throws a custom exception when any failure was collected.
     *
     * @param exceptionFactory factory used to convert a failed result into an exception
     */
    public void throwIfInvalid(Function<ValidationResult, ? extends RuntimeException> exceptionFactory) {
        collector.throwIfInvalid(exceptionFactory);
    }
}
