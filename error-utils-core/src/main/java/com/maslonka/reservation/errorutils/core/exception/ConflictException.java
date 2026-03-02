package com.maslonka.reservation.errorutils.core.exception;

import com.maslonka.reservation.errorutils.core.api.ErrorCode;

public class ConflictException extends BusinessException {

    public ConflictException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }
}
