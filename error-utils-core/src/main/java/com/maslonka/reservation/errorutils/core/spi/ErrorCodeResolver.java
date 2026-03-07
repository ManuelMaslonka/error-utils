package com.maslonka.reservation.errorutils.core.spi;

import com.maslonka.reservation.errorutils.core.api.ErrorCode;

/**
 * Resolves a domain {@link ErrorCode} from a throwable.
 */
public interface ErrorCodeResolver {

    /**
     * Resolves an error code for the provided throwable.
     *
     * @param throwable source throwable
     * @return resolved error code
     */
    ErrorCode resolve(Throwable throwable);
}
