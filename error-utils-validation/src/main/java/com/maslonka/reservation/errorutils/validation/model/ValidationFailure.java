package com.maslonka.reservation.errorutils.validation.model;

import com.maslonka.reservation.errorutils.core.api.ErrorCode;
import com.maslonka.reservation.errorutils.core.api.FieldViolation;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public final class ValidationFailure {

    private final ErrorCode errorCode;
    private final String message;
    private final String field;
    private final Object rejectedValue;
    private final String violationCode;
    private final Map<String, Object> metadata;
    private final Function<ValidationFailure, ? extends RuntimeException> exceptionFactory;

    private ValidationFailure(ErrorCode errorCode,
                              String message,
                              String field,
                              Object rejectedValue,
                              String violationCode,
                              Map<String, Object> metadata,
                              Function<ValidationFailure, ? extends RuntimeException> exceptionFactory) {
        this.errorCode = Objects.requireNonNull(errorCode, "errorCode");
        this.message = message;
        this.field = field;
        this.rejectedValue = rejectedValue;
        this.violationCode = violationCode;
        this.metadata = metadata == null ?
                        Map.of() :
                        Map.copyOf(metadata);
        this.exceptionFactory = exceptionFactory;
    }

    public static ValidationFailure of(ErrorCode errorCode, String message) {
        return new ValidationFailure(errorCode, message, null, null, null, Map.of(), null);
    }

    public ErrorCode errorCode() {
        return errorCode;
    }

    public String message() {
        return message;
    }

    public String field() {
        return field;
    }

    public Object rejectedValue() {
        return rejectedValue;
    }

    public String violationCode() {
        return violationCode;
    }

    public Map<String, Object> metadata() {
        return metadata;
    }

    public Function<ValidationFailure, ? extends RuntimeException> exceptionFactory() {
        return exceptionFactory;
    }

    public ValidationFailure field(String field) {
        return new ValidationFailure(errorCode, message, field, rejectedValue, violationCode, metadata, exceptionFactory);
    }

    public ValidationFailure rejectedValue(Object rejectedValue) {
        return new ValidationFailure(errorCode, message, field, rejectedValue, violationCode, metadata, exceptionFactory);
    }

    public ValidationFailure violationCode(String violationCode) {
        return new ValidationFailure(errorCode, message, field, rejectedValue, violationCode, metadata, exceptionFactory);
    }

    public ValidationFailure metadata(String key, Object value) {
        Map<String, Object> copy = new java.util.LinkedHashMap<>(metadata);
        copy.put(key, value);
        return new ValidationFailure(errorCode, message, field, rejectedValue, violationCode, copy, exceptionFactory);
    }

    public ValidationFailure metadata(Map<String, Object> metadata) {
        return new ValidationFailure(errorCode, message, field, rejectedValue, violationCode, metadata, exceptionFactory);
    }

    public ValidationFailure exceptionFactory(Function<ValidationFailure, ? extends RuntimeException> exceptionFactory) {
        return new ValidationFailure(errorCode, message, field, rejectedValue, violationCode, metadata, exceptionFactory);
    }

    public FieldViolation toFieldViolation() {
        return new FieldViolation(field, rejectedValue, message, violationCode);
    }
}
