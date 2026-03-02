package com.maslonka.reservation.errorutils.spring.web;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "error-utils")
public class ErrorUtilsProperties {

    private boolean includeExceptionMessage = false;
    private String internalErrorMessage = "Internal server error";
    private String correlationIdMdcKey = "correlationId";
    private String traceIdMdcKey = "traceId";
    private String correlationIdRequestAttribute = "correlationId";
    private String traceIdRequestAttribute = "traceId";
    private String correlationIdHeader = "X-Correlation-Id";
    private String traceIdHeader = "X-Trace-Id";

    public boolean isIncludeExceptionMessage() {
        return includeExceptionMessage;
    }

    public void setIncludeExceptionMessage(boolean includeExceptionMessage) {
        this.includeExceptionMessage = includeExceptionMessage;
    }

    public String getInternalErrorMessage() {
        return internalErrorMessage;
    }

    public void setInternalErrorMessage(String internalErrorMessage) {
        this.internalErrorMessage = internalErrorMessage;
    }

    public String getCorrelationIdMdcKey() {
        return correlationIdMdcKey;
    }

    public void setCorrelationIdMdcKey(String correlationIdMdcKey) {
        this.correlationIdMdcKey = correlationIdMdcKey;
    }

    public String getTraceIdMdcKey() {
        return traceIdMdcKey;
    }

    public void setTraceIdMdcKey(String traceIdMdcKey) {
        this.traceIdMdcKey = traceIdMdcKey;
    }

    public String getCorrelationIdRequestAttribute() {
        return correlationIdRequestAttribute;
    }

    public void setCorrelationIdRequestAttribute(String correlationIdRequestAttribute) {
        this.correlationIdRequestAttribute = correlationIdRequestAttribute;
    }

    public String getTraceIdRequestAttribute() {
        return traceIdRequestAttribute;
    }

    public void setTraceIdRequestAttribute(String traceIdRequestAttribute) {
        this.traceIdRequestAttribute = traceIdRequestAttribute;
    }

    public String getCorrelationIdHeader() {
        return correlationIdHeader;
    }

    public void setCorrelationIdHeader(String correlationIdHeader) {
        this.correlationIdHeader = correlationIdHeader;
    }

    public String getTraceIdHeader() {
        return traceIdHeader;
    }

    public void setTraceIdHeader(String traceIdHeader) {
        this.traceIdHeader = traceIdHeader;
    }
}
