package com.maslonka.reservation.errorutils.spring.web.trace;

import com.maslonka.reservation.errorutils.spring.web.ErrorUtilsProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DefaultTraceContextResolverTest {

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    @Test
    @DisplayName("Should prefer request attributes when headers and MDC also contain trace identifiers")
    void shouldPreferRequestAttributesWhenHeadersAndMdcAlsoContainTraceIdentifiers() {
        DefaultTraceContextResolver resolver = new DefaultTraceContextResolver(new ErrorUtilsProperties());
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("correlationId", "attr-corr");
        request.setAttribute("traceId", "attr-trace");
        request.addHeader("X-Correlation-Id", "header-corr");
        request.addHeader("X-Trace-Id", "header-trace");
        MDC.put("correlationId", "mdc-corr");
        MDC.put("traceId", "mdc-trace");

        TraceContext traceContext = resolver.resolve(request);

        assertEquals("attr-corr", traceContext.correlationId());
        assertEquals("attr-trace", traceContext.traceId());
    }

    @Test
    @DisplayName("Should fall back to headers when request attribute values are blank")
    void shouldFallBackToHeadersWhenRequestAttributeValuesAreBlank() {
        DefaultTraceContextResolver resolver = new DefaultTraceContextResolver(new ErrorUtilsProperties());
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("correlationId", "  ");
        request.setAttribute("traceId", "");
        request.addHeader("X-Correlation-Id", "header-corr");
        request.addHeader("X-Trace-Id", "header-trace");

        TraceContext traceContext = resolver.resolve(request);

        assertEquals("header-corr", traceContext.correlationId());
        assertEquals("header-trace", traceContext.traceId());
    }

    @Test
    @DisplayName("Should return null identifiers when request is null and no request source is available")
    void shouldReturnNullIdentifiersWhenRequestIsNullAndNoRequestSourceIsAvailable() {
        DefaultTraceContextResolver resolver = new DefaultTraceContextResolver(new ErrorUtilsProperties());

        TraceContext traceContext = resolver.resolve(null);

        assertNull(traceContext.correlationId());
        assertNull(traceContext.traceId());
    }

    @Test
    @DisplayName("Should use custom lookup keys when ErrorUtilsProperties overrides default names")
    void shouldUseCustomLookupKeysWhenErrorUtilsPropertiesOverridesDefaultNames() {
        ErrorUtilsProperties properties = new ErrorUtilsProperties();
        properties.setCorrelationIdRequestAttribute("req-corr");
        properties.setTraceIdRequestAttribute("req-trace");
        properties.setCorrelationIdHeader("X-Req-Corr");
        properties.setTraceIdHeader("X-Req-Trace");

        DefaultTraceContextResolver resolver = new DefaultTraceContextResolver(properties);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("req-corr", "custom-corr");
        request.addHeader("X-Req-Trace", "custom-trace");

        TraceContext traceContext = resolver.resolve(request);

        assertEquals("custom-corr", traceContext.correlationId());
        assertEquals("custom-trace", traceContext.traceId());
    }
}
