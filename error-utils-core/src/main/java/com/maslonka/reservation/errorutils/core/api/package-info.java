/**
 * Core API contracts for the library-wide error model.
 *
 * <p>This package contains the immutable types and contracts that define what an error means in the
 * system. The usual flow starts with an application-specific
 * {@link com.maslonka.reservation.errorutils.core.api.ErrorCode}, continues through
 * {@link com.maslonka.reservation.errorutils.core.exception.BusinessException} or
 * {@link com.maslonka.reservation.errorutils.core.exception.InternalException}, and ends in a
 * serialized {@link com.maslonka.reservation.errorutils.core.api.ApiError}.</p>
 *
 * <p>Main relationships:</p>
 *
 * <ul>
 *     <li>{@link com.maslonka.reservation.errorutils.core.api.ErrorCode} defines domain semantics</li>
 *     <li>{@link com.maslonka.reservation.errorutils.core.api.ApiErrorInput} is the normalized assembly input</li>
 *     <li>{@link com.maslonka.reservation.errorutils.core.api.ApiErrorAssembler} builds the final payload</li>
 *     <li>{@link com.maslonka.reservation.errorutils.core.api.FieldViolation} is reused by both validation and web layers</li>
 * </ul>
 */
package com.maslonka.reservation.errorutils.core.api;
