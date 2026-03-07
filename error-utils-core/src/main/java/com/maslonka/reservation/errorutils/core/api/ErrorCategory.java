package com.maslonka.reservation.errorutils.core.api;

/**
 * High-level classification of application errors.
 */
public enum ErrorCategory {
    /**
     * Validation failure caused by invalid input or invalid state.
     */
    VALIDATION,
    /**
     * Expected business rule violation.
     */
    BUSINESS,
    /** Policy or rule-engine rejection. */
    POLICY,
    /** Authentication or authorization failure. */
    SECURITY,
    /** Unexpected internal or infrastructure error. */
    TECHNICAL
}
