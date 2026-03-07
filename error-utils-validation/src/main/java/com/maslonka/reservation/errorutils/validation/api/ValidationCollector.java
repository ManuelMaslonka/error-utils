package com.maslonka.reservation.errorutils.validation.api;

import com.maslonka.reservation.errorutils.core.api.FieldViolation;
import com.maslonka.reservation.errorutils.validation.model.ValidationFailure;
import com.maslonka.reservation.errorutils.validation.model.ValidationResult;
import com.maslonka.reservation.errorutils.validation.pipeline.ValidationChain;

import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

public final class ValidationCollector {

    private final ValidationChain chain;

    public ValidationCollector(ValidationChain chain) {
        this.chain = chain;
    }

    public ValidationCollector check(boolean valid, ValidationFailure failure) {
        chain.check(valid, failure);
        return this;
    }

    public ValidationCollector check(BooleanSupplier predicate, ValidationFailure failure) {
        Objects.requireNonNull(predicate, "predicate");
        chain.check(predicate, failure);
        return this;
    }

    public ValidationCollector require(boolean condition, ValidationFailure failure) {
        chain.require(condition, failure);
        return this;
    }

    public ValidationCollector require(BooleanSupplier predicate, ValidationFailure failure) {
        Objects.requireNonNull(predicate, "predicate");
        chain.require(predicate, failure);
        return this;
    }

    public ValidationResult toResult() {
        return chain.toResult();
    }

    public List<FieldViolation> toViolations() {
        return chain.toViolations();
    }

    public void throwIfInvalid() {
        chain.throwIfInvalid();
    }

    public void throwIfInvalid(Function<ValidationResult, ? extends RuntimeException> exceptionFactory) {
        chain.throwIfInvalid(exceptionFactory);
    }
}
