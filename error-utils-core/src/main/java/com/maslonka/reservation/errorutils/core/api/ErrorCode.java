package com.maslonka.reservation.errorutils.core.api;

/**
 * Contract describing a domain error that can be serialized into an API response.
 *
 * <p>This is the central domain abstraction of the library. Applications usually implement it as
 * an enum and then use the enum values with
 * {@link com.maslonka.reservation.errorutils.core.exception.BusinessException} or
 * {@link com.maslonka.reservation.errorutils.core.exception.InternalException}. The web layer later
 * consumes the contract through {@link ApiErrorInput} and {@link ApiErrorAssembler} to create the
 * final {@link ApiError} payload.</p>
 *
 * <p>Typical usage:</p>
 *
 * <pre>{@code
 * enum CustomerErrorCode implements ErrorCode {
 *     CUSTOMER_NOT_FOUND;
 *
 *     public String code() { return name(); }
 *     public String messageKey() { return "error.customer.notFound"; }
 *     public int httpStatus() { return 404; }
 *     public ErrorCategory category() { return ErrorCategory.BUSINESS; }
 * }
 * }</pre>
 *
 * @see ErrorCategory
 * @see ApiError
 * @see com.maslonka.reservation.errorutils.core.exception.BusinessException
 */
public interface ErrorCode {

    /**
     * Returns the stable machine-readable error code.
     *
     * @return application error code
     */
    String code();

    /**
     * Returns the message catalog or translation key associated with the error.
     *
     * @return message key
     */
    String messageKey();

    /**
     * Returns the HTTP status that should be used when serializing the error.
     *
     * @return HTTP status code
     */
    int httpStatus();

    /**
     * Returns the high-level error classification.
     *
     * @return error category
     */
    ErrorCategory category();
}
