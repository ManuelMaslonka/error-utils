package com.maslonka.reservation.errorutils.core.exception;

import com.maslonka.reservation.errorutils.core.api.ErrorCode;

/**
 * Business exception representing a resource or state conflict.
 */
public class ConflictException extends BusinessException {

    /**
     * Creates a conflict exception with the provided error code and detail message.
     *
     * @param errorCode domain error code
     * @param detail    detail message
     */
    public ConflictException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }
}
