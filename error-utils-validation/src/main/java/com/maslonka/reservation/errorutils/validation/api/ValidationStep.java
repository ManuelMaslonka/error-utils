package com.maslonka.reservation.errorutils.validation.api;

@FunctionalInterface
public interface ValidationStep<T> {

    void validate(T target, ValidationCollector collector);
}
