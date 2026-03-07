package com.maslonka.reservation.errorutils.core.exception;

import com.maslonka.reservation.errorutils.core.api.ErrorCode;

/**
 * Runtime exception representing a controlled internal failure.
 */
public class InternalException extends RuntimeException {

    /**
     * Domain error code driving response semantics.
     */
    private final ErrorCode errorCode;

    /**
     * Creates an internal exception with the provided error code.
     *
     * @param errorCode domain error code
     */
    public InternalException(ErrorCode errorCode) {
        super();
        this.errorCode = errorCode;
    }

    /**
     * Creates an internal exception with the provided error code and cause.
     *
     * @param errorCode domain error code
     * @param cause     root cause
     */
    public InternalException(ErrorCode errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }

    /**
     * Returns the domain error code attached to this exception.
     *
     * @return error code
     */
    public ErrorCode errorCode() {
        return errorCode;
    }
}
