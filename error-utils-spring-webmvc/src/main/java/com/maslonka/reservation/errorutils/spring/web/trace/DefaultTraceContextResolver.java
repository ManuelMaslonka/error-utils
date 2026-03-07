package com.maslonka.reservation.errorutils.spring.web.trace;

import com.maslonka.reservation.errorutils.spring.web.ErrorUtilsProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;

public class DefaultTraceContextResolver implements TraceContextResolver {

    private final ErrorUtilsProperties properties;

    public DefaultTraceContextResolver(ErrorUtilsProperties properties) {
        this.properties = properties;
    }

    @Override
    public TraceContext resolve(HttpServletRequest request) {
        String correlationId =
                resolveValue(request, properties.getCorrelationIdRequestAttribute(), properties.getCorrelationIdHeader(), properties.getCorrelationIdMdcKey());

        String traceId = resolveValue(request, properties.getTraceIdRequestAttribute(), properties.getTraceIdHeader(), properties.getTraceIdMdcKey());

        return new TraceContext(correlationId, traceId);
    }

    private String resolveValue(HttpServletRequest request, String attributeName, String headerName, String mdcKey) {
        if (request != null) {
            Object attribute = request.getAttribute(attributeName);
            if (attribute instanceof String value && !value.isBlank()) {
                return value;
            }

            String headerValue = request.getHeader(headerName);
            if (headerValue != null && !headerValue.isBlank()) {
                return headerValue;
            }
        }

        String mdcValue = MDC.get(mdcKey);
        if (mdcValue != null && !mdcValue.isBlank()) {
            return mdcValue;
        }

        return null;
    }
}
