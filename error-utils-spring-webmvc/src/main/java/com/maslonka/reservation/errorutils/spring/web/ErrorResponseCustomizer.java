package com.maslonka.reservation.errorutils.spring.web;

import com.maslonka.reservation.errorutils.core.api.ApiError;
import com.maslonka.reservation.errorutils.spring.web.advice.ErrorResponseContext;

/**
 * Strategy interface for post-processing an {@link ApiError} before it is written to the response.
 *
 * <p>Customizers are applied by
 * {@link com.maslonka.reservation.errorutils.spring.web.factory.WebApiErrorFactory} after the base
 * payload is assembled and after metadata has been sanitized. This makes the interface suitable for
 * last-mile response shaping such as enriching metadata, redacting fields, or translating the final
 * message.</p>
 *
 * <p>Customizers are called in order and each customizer receives the output of the previous one.</p>
 *
 * @see com.maslonka.reservation.errorutils.spring.web.advice.ErrorResponseContext
 * @see com.maslonka.reservation.errorutils.spring.web.factory.WebApiErrorFactory
 */
@FunctionalInterface
public interface ErrorResponseCustomizer {

    /**
     * Returns a customized error payload for the current request context.
     *
     * @param apiError current error payload
     * @param context  request-specific context used for customization
     * @return replacement payload, or {@code null} to keep the current payload unchanged
     */
    ApiError customize(ApiError apiError, ErrorResponseContext context);
}
