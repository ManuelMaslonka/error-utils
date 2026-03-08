package com.maslonka.reservation.errorutils.core.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ApiErrorAssemblerBehaviorTest {

    private final ApiErrorAssembler assembler = new ApiErrorAssembler();

    @Test
    @DisplayName("Should return UNKNOWN status name when http status is not mapped")
    void shouldReturnUnknownStatusNameWhenHttpStatusIsNotMapped() {
        ApiError apiError = assembler.assemble(new ApiErrorInput(Instant.parse("2026-03-08T07:00:00Z"),
                                                                 TestErrorCode.CUSTOM_STATUS,
                                                                 "Custom failure",
                                                                 "/api/custom",
                                                                 null,
                                                                 null,
                                                                 List.of(),
                                                                 Map.of()));

        assertEquals(599, apiError.status());
        assertEquals("UNKNOWN", apiError.error());
    }

    @Test
    @DisplayName("Should make defensive copies when violations and metadata are mutable")
    void shouldMakeDefensiveCopiesWhenViolationsAndMetadataAreMutable() {
        List<FieldViolation> violations = new ArrayList<>();
        violations.add(new FieldViolation("email", "bad", "Invalid email", "Email"));
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("source", "registration");

        ApiError apiError = assembler.assemble(new ApiErrorInput(Instant.parse("2026-03-08T07:05:00Z"),
                                                                 TestErrorCode.CUSTOMER_CONFLICT,
                                                                 "Conflict",
                                                                 "/api/customers",
                                                                 "corr-1",
                                                                 "trace-1",
                                                                 violations,
                                                                 metadata));

        violations.add(new FieldViolation("name", "", "Required", "NotBlank"));
        metadata.put("source", "mutated");

        assertEquals(1, apiError.violations().size());
        assertEquals("registration", apiError.metadata().get("source"));
        List<FieldViolation> apiViolations = apiError.violations();
        Map<String, Object> apiMetadata = apiError.metadata();
        FieldViolation extraViolation = new FieldViolation("name", "", "Required", "NotBlank");

        assertThrows(UnsupportedOperationException.class, () -> apiViolations.add(extraViolation));
        assertThrows(UnsupportedOperationException.class, () -> apiMetadata.put("other", "value"));
    }

    @Test
    @DisplayName("Should normalize null violations and metadata when input collections are absent")
    void shouldNormalizeNullViolationsAndMetadataWhenInputCollectionsAreAbsent() {
        ApiError apiError = assembler.assemble(new ApiErrorInput(Instant.parse("2026-03-08T07:10:00Z"),
                                                                 TestErrorCode.CUSTOMER_NOT_FOUND,
                                                                 "Missing",
                                                                 "/api/customers/42",
                                                                 null,
                                                                 null,
                                                                 null,
                                                                 null));

        assertTrue(apiError.violations().isEmpty());
        assertTrue(apiError.metadata().isEmpty());
    }

    private enum TestErrorCode implements ErrorCode {
        CUSTOMER_NOT_FOUND(404, ErrorCategory.BUSINESS), CUSTOMER_CONFLICT(409, ErrorCategory.BUSINESS), CUSTOM_STATUS(599, ErrorCategory.TECHNICAL);

        private final int httpStatus;
        private final ErrorCategory category;

        TestErrorCode(int httpStatus, ErrorCategory category) {
            this.httpStatus = httpStatus;
            this.category = category;
        }

        @Override
        public String code() {
            return name();
        }

        @Override
        public String messageKey() {
            return "error.test." + name().toLowerCase();
        }

        @Override
        public int httpStatus() {
            return httpStatus;
        }

        @Override
        public ErrorCategory category() {
            return category;
        }
    }
}
