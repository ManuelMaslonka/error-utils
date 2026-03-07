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

public final class ValidationChain {

    private final ValidationMode mode;
    private final List<ValidationFailure> failures;

    private ValidationChain(ValidationMode mode, List<ValidationFailure> failures) {
        this.mode = mode;
        this.failures = failures;
    }

    public static ValidationChain start() {
        return new ValidationChain(ValidationMode.COLLECT_ALL, new ArrayList<>());
    }

    public ValidationChain failFast() {
        return new ValidationChain(ValidationMode.FAIL_FAST, failures);
    }

    public ValidationChain collectAll() {
        return new ValidationChain(ValidationMode.COLLECT_ALL, failures);
    }

    public ValidationChain check(boolean valid, ValidationFailure failure) {
        Objects.requireNonNull(failure, "failure");
        if (shouldSkipEvaluation() || valid) {
            return this;
        }

        failures.add(failure);
        return this;
    }

    public ValidationChain check(BooleanSupplier predicate, ValidationFailure failure) {
        Objects.requireNonNull(predicate, "predicate");
        return check(predicate.getAsBoolean(), failure);
    }

    public ValidationChain require(boolean condition, ValidationFailure failure) {
        return check(condition, failure);
    }

    public ValidationChain require(BooleanSupplier predicate, ValidationFailure failure) {
        return check(predicate, failure);
    }

    public ValidationResult toResult() {
        List<ValidationFailure> snapshot = List.copyOf(failures);
        List<FieldViolation> violations = snapshot.stream().map(ValidationFailure::toFieldViolation).toList();
        return new ValidationResult(mode, snapshot, violations);
    }

    public List<FieldViolation> toViolations() {
        return toResult().violations();
    }

    public void throwIfInvalid() {
        throwIfInvalid(this::defaultException);
    }

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
