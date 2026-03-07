/**
 * Exception hierarchy for controlled domain and technical failures.
 *
 * <p>Types in this package are meant to be thrown by application code and later translated by the
 * web adapter into a standardized error response. The normal mapping path is:</p>
 *
 * <pre>{@code
 * ErrorCode -> BusinessException/InternalException -> web adapter -> ApiError
 * }</pre>
 *
 * <p>Use {@link com.maslonka.reservation.errorutils.core.exception.BusinessException} and its
 * specializations for expected failures, and
 * {@link com.maslonka.reservation.errorutils.core.exception.InternalException} for controlled
 * internal errors.</p>
 */
package com.maslonka.reservation.errorutils.core.exception;
