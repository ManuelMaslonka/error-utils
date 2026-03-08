package com.maslonka.reservation.errorutils.spring.web;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties controlling how API error responses are rendered.
 *
 * <p>These properties influence several parts of the web integration:</p>
 *
 * <ul>
 *     <li>{@link com.maslonka.reservation.errorutils.spring.web.factory.WebApiErrorFactory}
 *     uses message visibility flags</li>
 *     <li>{@link com.maslonka.reservation.errorutils.spring.web.trace.DefaultTraceContextResolver}
 *     uses the attribute/header/MDC lookup keys</li>
 *     <li>generated {@link com.maslonka.reservation.errorutils.core.api.ApiError} payloads expose
 *     correlation and trace identifiers only when enabled here</li>
 * </ul>
 *
 * <p>Example:</p>
 *
 * <pre>{@code
 * error-utils:
 *   include-exception-message: false
 *   include-correlation-id: true
 *   correlation-id-header: X-Request-Id
 * }</pre>
 */
@ConfigurationProperties(prefix = "error-utils")
public class ErrorUtilsProperties {

    private boolean includeExceptionMessage = false;
    private boolean includeCorrelationId = false;
    private boolean includeTraceId = false;
    private String internalErrorMessage = "Internal server error";
    private String correlationIdMdcKey = "correlationId";
    private String traceIdMdcKey = "traceId";
    private String correlationIdRequestAttribute = "correlationId";
    private String traceIdRequestAttribute = "traceId";
    private String correlationIdHeader = "X-Correlation-Id";
    private String traceIdHeader = "X-Trace-Id";

    /**
     * Indicates whether raw exception messages may be exposed for technical failures.
     *
     * @return {@code true} when technical exception messages should be included
     */
    public boolean isIncludeExceptionMessage() {
        return includeExceptionMessage;
    }

    /**
     * Sets whether raw exception messages may be exposed for technical failures.
     *
     * @param includeExceptionMessage {@code true} to expose technical exception messages
     */
    public void setIncludeExceptionMessage(boolean includeExceptionMessage) {
        this.includeExceptionMessage = includeExceptionMessage;
    }

    /**
     * Indicates whether the resolved correlation id should be included in the payload.
     *
     * @return {@code true} when the correlation id should be serialized
     */
    public boolean isIncludeCorrelationId() {
        return includeCorrelationId;
    }

    /**
     * Sets whether the resolved correlation id should be included in the payload.
     *
     * @param includeCorrelationId {@code true} to serialize the correlation id
     */
    public void setIncludeCorrelationId(boolean includeCorrelationId) {
        this.includeCorrelationId = includeCorrelationId;
    }

    /**
     * Indicates whether the resolved trace id should be included in the payload.
     *
     * @return {@code true} when the trace id should be serialized
     */
    public boolean isIncludeTraceId() {
        return includeTraceId;
    }

    /**
     * Sets whether the resolved trace id should be included in the payload.
     *
     * @param includeTraceId {@code true} to serialize the trace id
     */
    public void setIncludeTraceId(boolean includeTraceId) {
        this.includeTraceId = includeTraceId;
    }

    /**
     * Returns the fallback message used for technical failures.
     *
     * @return internal error message shown when exception details are hidden
     */
    public String getInternalErrorMessage() {
        return internalErrorMessage;
    }

    /**
     * Sets the fallback message used for technical failures.
     *
     * @param internalErrorMessage internal error message shown to clients
     */
    public void setInternalErrorMessage(String internalErrorMessage) {
        this.internalErrorMessage = internalErrorMessage;
    }

    /**
     * Returns the MDC key used to resolve the correlation id.
     *
     * @return correlation id MDC key
     */
    public String getCorrelationIdMdcKey() {
        return correlationIdMdcKey;
    }

    /**
     * Sets the MDC key used to resolve the correlation id.
     *
     * @param correlationIdMdcKey correlation id MDC key
     */
    public void setCorrelationIdMdcKey(String correlationIdMdcKey) {
        this.correlationIdMdcKey = correlationIdMdcKey;
    }

    /**
     * Returns the MDC key used to resolve the trace id.
     *
     * @return trace id MDC key
     */
    public String getTraceIdMdcKey() {
        return traceIdMdcKey;
    }

    /**
     * Sets the MDC key used to resolve the trace id.
     *
     * @param traceIdMdcKey trace id MDC key
     */
    public void setTraceIdMdcKey(String traceIdMdcKey) {
        this.traceIdMdcKey = traceIdMdcKey;
    }

    /**
     * Returns the request attribute name used to resolve the correlation id.
     *
     * @return correlation id request attribute name
     */
    public String getCorrelationIdRequestAttribute() {
        return correlationIdRequestAttribute;
    }

    /**
     * Sets the request attribute name used to resolve the correlation id.
     *
     * @param correlationIdRequestAttribute correlation id request attribute name
     */
    public void setCorrelationIdRequestAttribute(String correlationIdRequestAttribute) {
        this.correlationIdRequestAttribute = correlationIdRequestAttribute;
    }

    /**
     * Returns the request attribute name used to resolve the trace id.
     *
     * @return trace id request attribute name
     */
    public String getTraceIdRequestAttribute() {
        return traceIdRequestAttribute;
    }

    /**
     * Sets the request attribute name used to resolve the trace id.
     *
     * @param traceIdRequestAttribute trace id request attribute name
     */
    public void setTraceIdRequestAttribute(String traceIdRequestAttribute) {
        this.traceIdRequestAttribute = traceIdRequestAttribute;
    }

    /**
     * Returns the header name used to resolve the correlation id.
     *
     * @return correlation id header name
     */
    public String getCorrelationIdHeader() {
        return correlationIdHeader;
    }

    /**
     * Sets the header name used to resolve the correlation id.
     *
     * @param correlationIdHeader correlation id header name
     */
    public void setCorrelationIdHeader(String correlationIdHeader) {
        this.correlationIdHeader = correlationIdHeader;
    }

    /**
     * Returns the header name used to resolve the trace id.
     *
     * @return trace id header name
     */
    public String getTraceIdHeader() {
        return traceIdHeader;
    }

    /**
     * Sets the header name used to resolve the trace id.
     *
     * @param traceIdHeader trace id header name
     */
    public void setTraceIdHeader(String traceIdHeader) {
        this.traceIdHeader = traceIdHeader;
    }
}
