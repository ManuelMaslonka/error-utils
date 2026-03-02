package com.maslonka.reservation.errorutils.core.exception;

import com.maslonka.reservation.errorutils.core.api.ErrorCode;

public class NotFoundException extends BusinessException {

    public NotFoundException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }
}
