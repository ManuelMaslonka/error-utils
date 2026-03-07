package com.maslonka.reservation.errorutils.spring.web.trace;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Resolves tracing identifiers that may be included in an API error response.
 */
public interface TraceContextResolver {

    /**
     * Resolves correlation and trace identifiers for the supplied request.
     *
     * @param request current HTTP request, may be {@code null}
     * @return resolved trace context
     */
    TraceContext resolve(HttpServletRequest request);
}
