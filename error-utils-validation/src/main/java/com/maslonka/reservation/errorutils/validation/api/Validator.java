package com.maslonka.reservation.errorutils.validation.api;

import com.maslonka.reservation.errorutils.validation.pipeline.ObjectValidator;
import com.maslonka.reservation.errorutils.validation.pipeline.ValidationChain;

public final class Validator {

    private Validator() {
    }

    public static <T> ObjectValidator<T> forObject(T target) {
        return new ObjectValidator<>(target, ValidationChain.start());
    }
}
