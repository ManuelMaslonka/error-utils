/**
 * Default metadata sanitization implementation.
 *
 * <p>The starter registers {@code NoopErrorMetadataSanitizer} as the default bean unless a
 * custom {@link com.maslonka.reservation.errorutils.core.spi.ErrorMetadataSanitizer} is provided
 * by the application.</p>
 */
@NullMarked
package com.maslonka.reservation.errorutils.spring.web.metadata;

import org.jspecify.annotations.NullMarked;
