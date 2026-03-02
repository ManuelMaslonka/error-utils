package com.maslonka.reservation.errorutils.core.spi;

import java.util.Map;

public interface ErrorMetadataSanitizer {

    Map<String, Object> sanitize(Map<String, Object> metadata);
}
