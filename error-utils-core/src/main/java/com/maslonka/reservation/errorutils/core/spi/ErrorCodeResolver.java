package com.maslonka.reservation.errorutils.core.spi;

import com.maslonka.reservation.errorutils.core.api.ErrorCode;

public interface ErrorCodeResolver {

    ErrorCode resolve(Throwable throwable);
}
