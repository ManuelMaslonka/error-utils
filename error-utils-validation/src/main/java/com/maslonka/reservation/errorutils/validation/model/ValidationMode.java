package com.maslonka.reservation.errorutils.validation.model;

/**
 * Strategy controlling whether validation stops on the first failure or collects all failures.
 */
public enum ValidationMode {
    /**
     * Stops evaluation after the first recorded failure.
     */
    FAIL_FAST,
    /**
     * Continues evaluation and collects every encountered failure.
     */
    COLLECT_ALL
}
