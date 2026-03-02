package com.maslonka.reservation.errorutils.spring.web;

import com.maslonka.reservation.errorutils.core.spi.ErrorMetadataSanitizer;

import java.util.Map;

public class NoopErrorMetadataSanitizer implements ErrorMetadataSanitizer {

    @Override
    public Map<String, Object> sanitize(Map<String, Object> metadata) {
        return metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
