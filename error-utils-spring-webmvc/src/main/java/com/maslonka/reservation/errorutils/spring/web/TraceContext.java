package com.maslonka.reservation.errorutils.spring.web;

public record TraceContext(String correlationId, String traceId) {
}
