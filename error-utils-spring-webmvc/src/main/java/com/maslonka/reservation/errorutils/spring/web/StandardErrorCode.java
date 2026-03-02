package com.maslonka.reservation.errorutils.spring.web;

import com.maslonka.reservation.errorutils.core.api.ErrorCategory;
import com.maslonka.reservation.errorutils.core.api.ErrorCode;

public enum StandardErrorCode implements ErrorCode {
    REQUEST_VALIDATION_FAILED("REQUEST_VALIDATION_FAILED", "error.request.validationFailed", 400, ErrorCategory.VALIDATION),
    MALFORMED_REQUEST_BODY("MALFORMED_REQUEST_BODY", "error.request.malformedBody", 400, ErrorCategory.VALIDATION),
    UNAUTHORIZED("UNAUTHORIZED", "error.security.unauthorized", 401, ErrorCategory.SECURITY),
    ACCESS_DENIED("ACCESS_DENIED", "error.security.accessDenied", 403, ErrorCategory.SECURITY),
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", "error.business.notFound", 404, ErrorCategory.BUSINESS),
    RESOURCE_CONFLICT("RESOURCE_CONFLICT", "error.business.conflict", 409, ErrorCategory.BUSINESS),
    POLICY_VIOLATION("POLICY_VIOLATION", "error.policy.violation", 422, ErrorCategory.POLICY),
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

    @Override
    public String code() {
        return code;
    }

    @Override
    public String messageKey() {
        return messageKey;
    }

    @Override
    public int httpStatus() {
        return httpStatus;
    }

    @Override
    public ErrorCategory category() {
        return category;
    }
}
