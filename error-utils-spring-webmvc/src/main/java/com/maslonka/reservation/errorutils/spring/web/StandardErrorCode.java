package com.maslonka.reservation.errorutils.spring.web;

import com.maslonka.reservation.errorutils.core.api.ErrorCategory;
import com.maslonka.reservation.errorutils.core.api.ErrorCode;

/**
 * Built-in {@link ErrorCode} values used by the HTTP adapters for common framework failures.
 *
 * <p>These codes cover failures raised by the adapter layer itself, such as malformed requests,
 * authentication/authorization failures, and generic technical fallbacks. Domain services can
 * define their own {@link ErrorCode} implementations and use them alongside these defaults.</p>
 *
 * @see com.maslonka.reservation.errorutils.spring.web.advice.GlobalApiExceptionHandler
 * @see com.maslonka.reservation.errorutils.spring.web.security.GlobalSecurityExceptionHandler
 */
public enum StandardErrorCode implements ErrorCode {
    /**
     * Validation failed for request-bound data.
     */
    REQUEST_VALIDATION_FAILED("REQUEST_VALIDATION_FAILED", "error.request.validationFailed", 400, ErrorCategory.VALIDATION),
    /**
     * Request body could not be parsed.
     */
    MALFORMED_REQUEST_BODY("MALFORMED_REQUEST_BODY", "error.request.malformedBody", 400, ErrorCategory.VALIDATION),
    /** Authentication is required or has failed. */
    UNAUTHORIZED("UNAUTHORIZED", "error.security.unauthorized", 401, ErrorCategory.SECURITY),
    /** Authenticated caller is not allowed to access the resource. */
    ACCESS_DENIED("ACCESS_DENIED", "error.security.accessDenied", 403, ErrorCategory.SECURITY),
    /** Requested resource does not exist. */
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", "error.business.notFound", 404, ErrorCategory.BUSINESS),
    /** Requested operation would conflict with the current resource state. */
    RESOURCE_CONFLICT("RESOURCE_CONFLICT", "error.business.conflict", 409, ErrorCategory.BUSINESS),
    /** Request violates a policy or rule outside basic validation. */
    POLICY_VIOLATION("POLICY_VIOLATION", "error.policy.violation", 422, ErrorCategory.POLICY),
    /** Unexpected technical failure. */
    TECHNICAL_ERROR("TECHNICAL_ERROR", "error.technical.generic", 500, ErrorCategory.TECHNICAL);

    private final String code;
    private final String messageKey;
    private final int httpStatus;
    private final ErrorCategory category;

    StandardErrorCode(String code, String messageKey, int httpStatus, ErrorCategory category) {
        this.code = code;
        this.messageKey = messageKey;
        this.httpStatus = httpStatus;
        this.category = category;
    }

    /**
     * Returns the stable machine-readable error code.
     *
     * @return error code identifier
     */
    @Override
    public String code() {
        return code;
    }

    /**
     * Returns the translation key associated with the error.
     *
     * @return message key for localization or lookup
     */
    @Override
    public String messageKey() {
        return messageKey;
    }

    /**
     * Returns the HTTP status to be rendered for this error.
     *
     * @return HTTP status code
     */
    @Override
    public int httpStatus() {
        return httpStatus;
    }

    /**
     * Returns the high-level category of the error.
     *
     * @return error category
     */
    @Override
    public ErrorCategory category() {
        return category;
    }
}
