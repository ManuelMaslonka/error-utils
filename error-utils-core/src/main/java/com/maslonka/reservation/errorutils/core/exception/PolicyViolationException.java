package com.maslonka.reservation.errorutils.core.exception;

import com.maslonka.reservation.errorutils.core.api.ErrorCode;

public class PolicyViolationException extends BusinessException {

    public PolicyViolationException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }
}
