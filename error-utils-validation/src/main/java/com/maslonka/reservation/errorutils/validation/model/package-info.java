/**
 * Immutable read models describing validation failures and outcomes.
 *
 * <p>These types sit between the validation pipeline and the error model from {@code error-utils-core}.
 * They can be logged, inspected, merged into a business exception, or converted into API-facing
 * {@link com.maslonka.reservation.errorutils.core.api.FieldViolation} entries.</p>
 */
package com.maslonka.reservation.errorutils.validation.model;
