package com.maslonka.reservation.errorutils.spring.web.trace;

public record TraceContext(String correlationId, String traceId) {}
