package com.maslonka.reservation.errorutils.core.exception;

import com.maslonka.reservation.errorutils.core.api.ErrorCode;
import com.maslonka.reservation.errorutils.core.api.FieldViolation;

import java.util.List;
import java.util.Map;

public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Map<String, Object> metadata;
    private final List<FieldViolation> violations;

    public BusinessException(ErrorCode errorCode) {
        this(errorCode, null, null, Map.of(), List.of());
    }

    public BusinessException(ErrorCode errorCode, String detail) {
        this(errorCode, detail, null, Map.of(), List.of());
    }

    public BusinessException(ErrorCode errorCode, String detail, Throwable cause) {
        this(errorCode, detail, cause, Map.of(), List.of());
    }

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

    public ErrorCode errorCode() {
        return errorCode;
    }

    public Map<String, Object> metadata() {
        return metadata;
    }

    public List<FieldViolation> violations() {
        return violations;
    }
}
