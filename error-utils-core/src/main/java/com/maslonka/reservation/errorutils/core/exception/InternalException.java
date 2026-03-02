package com.maslonka.reservation.errorutils.core.exception;

import com.maslonka.reservation.errorutils.core.api.ErrorCode;

public class InternalException extends RuntimeException {

    private final ErrorCode errorCode;

    public InternalException(ErrorCode errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public InternalException(ErrorCode errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }

    public ErrorCode errorCode() {
        return errorCode;
    }
}
