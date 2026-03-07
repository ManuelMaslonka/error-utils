package com.maslonka.reservation.errorutils.validation.api;

/**
 * Contract for reusable validation logic bound to a specific target type.
 *
 * @param <T> validated target type
 */
@FunctionalInterface
public interface ValidationStep<T> {

    /**
     * Validates the supplied target and records any failures through the collector.
     *
     * @param target    validated object
     * @param collector collector used to record failures
     */
    void validate(T target, ValidationCollector collector);
}
