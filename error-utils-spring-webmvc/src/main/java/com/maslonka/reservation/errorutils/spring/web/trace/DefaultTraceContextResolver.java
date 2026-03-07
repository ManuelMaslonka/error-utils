package com.maslonka.reservation.errorutils.spring.web.trace;

import com.maslonka.reservation.errorutils.spring.web.ErrorUtilsProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;

/**
 * Default {@link TraceContextResolver} that looks up identifiers from request attributes, headers,
 * and finally MDC.
 */
public class DefaultTraceContextResolver implements TraceContextResolver {

    private final ErrorUtilsProperties properties;

    /**
     * Creates a new resolver using the configured lookup keys.
     *
     * @param properties runtime properties controlling attribute, header, and MDC names
     */
    public DefaultTraceContextResolver(ErrorUtilsProperties properties) {
        this.properties = properties;
    }

    /**
     * Resolves correlation and trace identifiers for the current request.
     *
     * @param request current HTTP request, may be {@code null}
     * @return resolved trace context
     */
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
