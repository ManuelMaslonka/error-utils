package com.maslonka.reservation.errorutils.core.exception;

import com.maslonka.reservation.errorutils.core.api.ErrorCode;
import com.maslonka.reservation.errorutils.core.api.FieldViolation;

import java.util.List;
import java.util.Map;

/**
 * Base runtime exception for expected business failures.
 *
 * <p>Use this exception for domain-level failures that should be returned to the caller as a
 * controlled business response instead of an unhandled technical error. The embedded
 * {@link ErrorCode} drives the HTTP status, symbolic code, and category in the final
 * {@link com.maslonka.reservation.errorutils.core.api.ApiError}.</p>
 *
 * <p>Typical related types:</p>
 *
 * <ul>
 *     <li>{@link NotFoundException} for missing resources</li>
 *     <li>{@link ConflictException} for state conflicts</li>
 *     <li>{@link PolicyViolationException} for policy-driven rejections</li>
 *     <li>{@code ValidationFailure} from the validation module when validation failures are
 *     converted into a business exception</li>
 * </ul>
 *
 * <p>Example:</p>
 *
 * <pre>{@code
 * throw new BusinessException(
 *     CustomerErrorCode.CUSTOMER_EMAIL_CONFLICT,
 *     "Customer with email " + email + " already exists",
 *     null,
 *     Map.of("email", email),
 *     List.of(new FieldViolation("email", email, "Already used", "Unique"))
 * );
 * }</pre>
 *
 * @see NotFoundException
 * @see ConflictException
 * @see PolicyViolationException
 */
public class BusinessException extends RuntimeException {

    /**
     * Domain error code driving response semantics.
     */
    private final ErrorCode errorCode;
    /**
     * Additional structured metadata attached to the failure.
     */
    private final Map<String, Object> metadata;
    /** Field-level validation details attached to the failure. */
    private final List<FieldViolation> violations;

    /**
     * Creates a business exception with the given error code.
     *
     * @param errorCode domain error code
     */
    public BusinessException(ErrorCode errorCode) {
        this(errorCode, null, null, Map.of(), List.of());
    }

    /**
     * Creates a business exception with the given error code and detail message.
     *
     * @param errorCode domain error code
     * @param detail detail message
     */
    public BusinessException(ErrorCode errorCode, String detail) {
        this(errorCode, detail, null, Map.of(), List.of());
    }

    /**
     * Creates a business exception with the given error code, detail message, and cause.
     *
     * @param errorCode domain error code
     * @param detail detail message
     * @param cause root cause
     */
    public BusinessException(ErrorCode errorCode, String detail, Throwable cause) {
        this(errorCode, detail, cause, Map.of(), List.of());
    }

    /**
     * Creates a business exception with optional metadata and field violations.
     *
     * @param errorCode domain error code
     * @param detail detail message
     * @param cause root cause
     * @param metadata additional structured metadata
     * @param violations field-level validation details
     */
    public BusinessException(
        ErrorCode errorCode,
        String detail,
        Throwable cause,
        Map<String, Object> metadata,
        List<FieldViolation> violations
    ) {
        super(detail, cause);
        this.errorCode = errorCode;
        this.metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
        this.violations = violations == null ? List.of() : List.copyOf(violations);
    }

    /**
     * Returns the domain error code attached to this exception.
     *
     * @return error code
     */
    public ErrorCode errorCode() {
        return errorCode;
    }

    /**
     * Returns immutable structured metadata attached to the exception.
     *
     * @return metadata map
     */
    public Map<String, Object> metadata() {
        return metadata;
    }

    /**
     * Returns immutable field-level validation details attached to the exception.
     *
     * @return field violations
     */
    public List<FieldViolation> violations() {
        return violations;
    }
}
