/**
 * Internal execution pipeline behind the fluent validation API.
 *
 * <p>This package holds the mutable state and orchestration objects used by
 * {@link com.maslonka.reservation.errorutils.validation.api.Validator}. Most consumers interact
 * with {@code ObjectValidator}, while {@code ValidationChain} manages failure collection and
 * exception creation.</p>
 */
package com.maslonka.reservation.errorutils.validation.pipeline;
