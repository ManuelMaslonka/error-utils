/**
 * Payload assembly components for HTTP error responses.
 *
 * <p>The main entry point is
 * {@link com.maslonka.reservation.errorutils.spring.web.factory.WebApiErrorFactory}, which combines
 * core error contracts, tracing information, metadata sanitization, and customizers into the final
 * {@link com.maslonka.reservation.errorutils.core.api.ApiError}.</p>
 */
package com.maslonka.reservation.errorutils.spring.web.factory;
