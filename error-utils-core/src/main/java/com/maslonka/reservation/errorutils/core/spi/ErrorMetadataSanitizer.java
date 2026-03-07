package com.maslonka.reservation.errorutils.core.spi;

import java.util.Map;

/**
 * Sanitizes error metadata before it is exposed to clients.
 */
public interface ErrorMetadataSanitizer {

    /**
     * Returns sanitized metadata suitable for serialization.
     *
     * @param metadata raw metadata
     * @return sanitized metadata
     */
    Map<String, Object> sanitize(Map<String, Object> metadata);
}
