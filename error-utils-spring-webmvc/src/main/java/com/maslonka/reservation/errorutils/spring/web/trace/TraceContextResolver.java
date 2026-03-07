package com.maslonka.reservation.errorutils.spring.web.trace;

import jakarta.servlet.http.HttpServletRequest;

public interface TraceContextResolver {

    TraceContext resolve(HttpServletRequest request);
}
