package com.maslonka.reservation.errorutils.spring.web.metadata;

import com.maslonka.reservation.errorutils.core.spi.ErrorMetadataSanitizer;
import org.jspecify.annotations.Nullable;

import java.util.Map;

/**
 * Default metadata sanitizer that simply returns an immutable copy of the supplied metadata.
 */
public class NoopErrorMetadataSanitizer implements ErrorMetadataSanitizer {

    /**
     * Returns an immutable copy of the supplied metadata.
     *
     * @param metadata metadata to sanitize, or {@code null}
     * @return immutable metadata map, or an empty map when metadata is {@code null} or empty
     */
    @Override
    public Map<String, Object> sanitize(@Nullable Map<String, Object> metadata) {
        return metadata == null ?
               Map.of() :
               Map.copyOf(metadata);
    }
}
