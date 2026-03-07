package com.maslonka.reservation.errorutils.spring.web.metadata;

import com.maslonka.reservation.errorutils.core.spi.ErrorMetadataSanitizer;

import java.util.Map;

/**
 * Default metadata sanitizer that simply returns an immutable copy of the supplied metadata.
 */
public class NoopErrorMetadataSanitizer implements ErrorMetadataSanitizer {

    /**
     * Creates the default no-op sanitizer.
     */
    public NoopErrorMetadataSanitizer() {
    }

    /**
     * Returns an immutable copy of the supplied metadata.
     *
     * @param metadata metadata to sanitize
     * @return immutable metadata map, or an empty map when metadata is {@code null}
     */
    @Override
    public Map<String, Object> sanitize(Map<String, Object> metadata) {
        return metadata == null ?
               Map.of() :
               Map.copyOf(metadata);
    }
}
