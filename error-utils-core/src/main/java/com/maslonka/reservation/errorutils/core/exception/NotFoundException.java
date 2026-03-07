package com.maslonka.reservation.errorutils.core.exception;

import com.maslonka.reservation.errorutils.core.api.ErrorCode;

/**
 * Business exception representing a missing resource.
 */
public class NotFoundException extends BusinessException {

    /**
     * Creates a not-found exception with the provided error code and detail message.
     *
     * @param errorCode domain error code
     * @param detail    detail message
     */
    public NotFoundException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }
}
