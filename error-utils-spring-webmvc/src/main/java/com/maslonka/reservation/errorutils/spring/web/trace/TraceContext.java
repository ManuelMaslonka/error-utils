package com.maslonka.reservation.errorutils.spring.web.trace;

/**
 * Holds the resolved identifiers that can be attached to an API error response.
 *
 * @param correlationId correlation identifier associated with the request
 * @param traceId       trace identifier associated with the request
 */
public record TraceContext(String correlationId, String traceId) {}
