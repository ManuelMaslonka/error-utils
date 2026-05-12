package com.maslonka.reservation.errorutils.spring.web.trace;

import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.Nullable;

/**
 * Resolves tracing identifiers that may be included in an API error response.
 */
public interface TraceContextResolver {

    /**
     * Resolves correlation and trace identifiers for the supplied request.
     *
     * @param request current HTTP request, or {@code null} when not available
     * @return resolved trace context, never {@code null}
     */
    TraceContext resolve(@Nullable HttpServletRequest request);
}
