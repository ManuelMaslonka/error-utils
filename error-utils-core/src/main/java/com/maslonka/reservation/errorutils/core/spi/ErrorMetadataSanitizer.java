package com.maslonka.reservation.errorutils.core.spi;

import org.jspecify.annotations.Nullable;

import java.util.Map;

/**
 * Sanitizes error metadata before it is exposed to clients.
 */
public interface ErrorMetadataSanitizer {

    /**
     * Returns sanitized metadata suitable for serialization.
     *
     * @param metadata raw metadata, or {@code null}
     * @return sanitized metadata, never {@code null}
     */
    Map<String, Object> sanitize(@Nullable Map<String, Object> metadata);
}
