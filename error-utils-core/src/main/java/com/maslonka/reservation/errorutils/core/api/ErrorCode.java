package com.maslonka.reservation.errorutils.core.api;

public interface ErrorCode {

    String code();

    String messageKey();

    int httpStatus();

    ErrorCategory category();
}
