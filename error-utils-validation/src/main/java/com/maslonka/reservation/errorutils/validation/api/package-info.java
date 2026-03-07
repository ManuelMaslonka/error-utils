/**
 * Consumer-facing entry points for the fluent validation pipeline.
 *
 * <p>This package is typically used directly by application services. A common path is:</p>
 *
 * <pre>{@code
 * Validator.forObject(command)
 *     .collectAll()
 *     .step(reusableValidationStep)
 *     .throwIfInvalid();
 * }</pre>
 *
 * <p>The actual execution state lives in the {@code pipeline} package, while result objects live in
 * the {@code model} package.</p>
 */
package com.maslonka.reservation.errorutils.validation.api;
