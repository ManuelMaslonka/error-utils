package com.maslonka.reservation.errorutils.validation.model;

import com.maslonka.reservation.errorutils.core.api.ErrorCode;
import com.maslonka.reservation.errorutils.core.api.FieldViolation;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Immutable description of a single validation failure.
 *
 * <p>{@code ValidationFailure} is the bridge between validation logic and the error model used by
 * the rest of the library. It contains both domain semantics ({@link ErrorCode}) and
 * response-oriented details such as field path, rejected value, and validation rule code. The
 * pipeline can later convert it into a {@link FieldViolation} or into a
 * {@link com.maslonka.reservation.errorutils.core.exception.BusinessException}.</p>
 *
 * <p>Typical construction:</p>
 *
 * <pre>{@code
 * ValidationFailure.of(UserErrorCode.USERNAME_REQUIRED, "Username is required")
 *     .field("username")
 *     .rejectedValue(command.username())
 *     .violationCode("NotBlank");
 * }</pre>
 *
 * @see ValidationResult
 * @see com.maslonka.reservation.errorutils.validation.pipeline.ValidationChain
 * @see FieldViolation
 */
public final class ValidationFailure {

    private final ErrorCode errorCode;
    private final String message;
    private final String field;
    private final Object rejectedValue;
    private final String violationCode;
    private final Map<String, Object> metadata;
    private final Function<ValidationFailure, RuntimeException> exceptionFactory;

    private ValidationFailure(ErrorCode errorCode,
                              String message,
                              String field,
                              Object rejectedValue,
                              String violationCode,
                              Map<String, Object> metadata,
                              Function<ValidationFailure, RuntimeException> exceptionFactory) {
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

    /**
     * Creates a failure with the mandatory domain error code and message.
     *
     * @param errorCode domain error code associated with the failure
     * @param message   user-facing validation message
     * @return immutable failure instance
     */
    public static ValidationFailure of(ErrorCode errorCode, String message) {
        return new ValidationFailure(errorCode, message, null, null, null, Map.of(), null);
    }

    /**
     * Returns the domain error code associated with the failure.
     *
     * @return domain error code
     */
    public ErrorCode errorCode() {
        return errorCode;
    }

    /**
     * Returns the user-facing validation message.
     *
     * @return validation message
     */
    public String message() {
        return message;
    }

    /**
     * Returns the logical field path related to the failure.
     *
     * @return field path or {@code null} when the failure is not field-specific
     */
    public String field() {
        return field;
    }

    /**
     * Returns the offending value associated with the failure.
     *
     * @return rejected value or {@code null}
     */
    public Object rejectedValue() {
        return rejectedValue;
    }

    /**
     * Returns the low-level validation rule identifier.
     *
     * @return validation rule code or {@code null}
     */
    public String violationCode() {
        return violationCode;
    }

    /**
     * Returns immutable structured metadata attached to the failure.
     *
     * @return failure metadata
     */
    public Map<String, Object> metadata() {
        return metadata;
    }

    /**
     * Returns the custom exception factory used by {@code throwIfInvalid()}, if configured.
     *
     * @return custom exception factory or {@code null}
     */
    public Function<ValidationFailure, RuntimeException> exceptionFactory() {
        return exceptionFactory;
    }

    /**
     * Returns a copy of this failure with the supplied field path.
     *
     * @param field logical field path
     * @return updated immutable failure
     */
    public ValidationFailure field(String field) {
        return new ValidationFailure(errorCode, message, field, rejectedValue, violationCode, metadata, exceptionFactory);
    }

    /**
     * Returns a copy of this failure with the supplied rejected value.
     *
     * @param rejectedValue offending value
     * @return updated immutable failure
     */
    public ValidationFailure rejectedValue(Object rejectedValue) {
        return new ValidationFailure(errorCode, message, field, rejectedValue, violationCode, metadata, exceptionFactory);
    }

    /**
     * Returns a copy of this failure with the supplied validation rule code.
     *
     * @param violationCode validation rule identifier
     * @return updated immutable failure
     */
    public ValidationFailure violationCode(String violationCode) {
        return new ValidationFailure(errorCode, message, field, rejectedValue, violationCode, metadata, exceptionFactory);
    }

    /**
     * Returns a copy of this failure with one metadata entry added or replaced.
     *
     * @param key   metadata key
     * @param value metadata value
     * @return updated immutable failure
     */
    public ValidationFailure metadata(String key, Object value) {
        Map<String, Object> copy = new java.util.LinkedHashMap<>(metadata);
        copy.put(key, value);
        return new ValidationFailure(errorCode, message, field, rejectedValue, violationCode, copy, exceptionFactory);
    }

    /**
     * Returns a copy of this failure with the supplied metadata map.
     *
     * @param metadata metadata to associate with the failure
     * @return updated immutable failure
     */
    public ValidationFailure metadata(Map<String, Object> metadata) {
        return new ValidationFailure(errorCode, message, field, rejectedValue, violationCode, metadata, exceptionFactory);
    }

    /**
     * Returns a copy of this failure with a custom exception factory.
     *
     * @param exceptionFactory factory used by validation when throwing on failure
     * @return updated immutable failure
     */
    public ValidationFailure exceptionFactory(Function<ValidationFailure, RuntimeException> exceptionFactory) {
        return new ValidationFailure(errorCode, message, field, rejectedValue, violationCode, metadata, exceptionFactory);
    }

    /**
     * Converts this failure into a field violation suitable for API responses.
     *
     * @return field violation projection
     */
    public FieldViolation toFieldViolation() {
        return new FieldViolation(field, rejectedValue, message, violationCode);
    }
}
