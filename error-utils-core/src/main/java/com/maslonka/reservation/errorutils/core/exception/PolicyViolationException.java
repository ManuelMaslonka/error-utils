package com.maslonka.reservation.errorutils.core.exception;

import com.maslonka.reservation.errorutils.core.api.ErrorCode;

/**
 * Business exception representing a policy-driven rejection.
 */
public class PolicyViolationException extends BusinessException {

    /**
     * Creates a policy violation exception with the provided error code and detail message.
     *
     * @param errorCode domain error code
     * @param detail    detail message
     */
    public PolicyViolationException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }
}
