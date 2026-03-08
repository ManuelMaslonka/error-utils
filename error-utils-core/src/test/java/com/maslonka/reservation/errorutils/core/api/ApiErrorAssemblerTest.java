package com.maslonka.reservation.errorutils.core.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApiErrorAssemblerTest {

    private final ApiErrorAssembler assembler = new ApiErrorAssembler();

    @Test
    @DisplayName("Should assemble public ApiError fields when normalized input is provided")
    void shouldAssembleApiErrorWhenNormalizedInputIsProvided() {
        Instant timestamp = Instant.parse("2026-03-06T10:15:30Z");
        ApiErrorInput input = new ApiErrorInput(timestamp,
                                                TestErrorCode.CUSTOMER_NOT_FOUND,
                                                "Customer 42 not found",
                                                "/api/customers/42",
                                                "corr-1",
                                                "trace-1",
                                                List.of(new FieldViolation("customerId", 42, "must exist", "NotFound")),
                                                Map.of("service", "customer-service"));

        ApiError apiError = assembler.assemble(input);

        assertEquals(timestamp, apiError.timestamp());
        assertEquals(404, apiError.status());
        assertEquals("NOT_FOUND", apiError.error());
        assertEquals("CUSTOMER_NOT_FOUND", apiError.code());
        assertEquals("Customer 42 not found", apiError.message());
        assertEquals("/api/customers/42", apiError.path());
        assertEquals("corr-1", apiError.correlationId());
        assertEquals("trace-1", apiError.traceId());
        assertEquals(1, apiError.violations().size());
        assertEquals("customer-service", apiError.metadata().get("service"));
    }

    private enum TestErrorCode implements ErrorCode {
        CUSTOMER_NOT_FOUND;

        @Override
        public String code() {
            return name();
        }

        @Override
        public String messageKey() {
            return "error.customer.notFound";
        }

        @Override
        public int httpStatus() {
            return 404;
        }

        @Override
        public ErrorCategory category() {
            return ErrorCategory.BUSINESS;
        }
    }
}
