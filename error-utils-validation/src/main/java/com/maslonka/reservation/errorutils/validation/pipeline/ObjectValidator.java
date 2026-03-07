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

public final class ObjectValidator<T> {

    private final T target;
    private final ValidationChain chain;
    private final ValidationCollector collector;

    public ObjectValidator(T target, ValidationChain chain) {
        this.target = target;
        this.chain = chain;
        this.collector = new ValidationCollector(chain);
    }

    public ObjectValidator<T> failFast() {
        return new ObjectValidator<>(target, chain.failFast());
    }

    public ObjectValidator<T> collectAll() {
        return new ObjectValidator<>(target, chain.collectAll());
    }

    public ObjectValidator<T> step(ValidationStep<? super T> step) {
        Objects.requireNonNull(step, "step");
        step.validate(target, collector);
        return this;
    }

    public ObjectValidator<T> step(Consumer<? super ValidationCollector> step) {
        Objects.requireNonNull(step, "step");
        step.accept(collector);
        return this;
    }

    public ObjectValidator<T> check(boolean valid, ValidationFailure failure) {
        collector.check(valid, failure);
        return this;
    }

    public ObjectValidator<T> check(java.util.function.BooleanSupplier predicate, ValidationFailure failure) {
        collector.check(predicate, failure);
        return this;
    }

    public ObjectValidator<T> require(boolean condition, ValidationFailure failure) {
        collector.require(condition, failure);
        return this;
    }

    public ValidationResult toResult() {
        return collector.toResult();
    }

    public List<FieldViolation> toViolations() {
        return collector.toViolations();
    }

    public void throwIfInvalid() {
        collector.throwIfInvalid();
    }

    public void throwIfInvalid(Function<ValidationResult, ? extends RuntimeException> exceptionFactory) {
        collector.throwIfInvalid(exceptionFactory);
    }
}
