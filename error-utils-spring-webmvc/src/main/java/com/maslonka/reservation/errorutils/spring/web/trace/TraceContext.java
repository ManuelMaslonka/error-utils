package com.maslonka.reservation.errorutils.spring.web.trace;

import org.jspecify.annotations.Nullable;

/**
 * Holds the resolved identifiers that can be attached to an API error response.
 *
 * @param correlationId correlation identifier associated with the request, or {@code null} when unresolved
 * @param traceId       trace identifier associated with the request, or {@code null} when unresolved
 */
public record TraceContext(@Nullable String correlationId, @Nullable String traceId) {}
