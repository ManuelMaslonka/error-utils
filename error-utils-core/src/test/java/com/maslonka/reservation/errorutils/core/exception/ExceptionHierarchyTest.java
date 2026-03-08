package com.maslonka.reservation.errorutils.core.exception;

import com.maslonka.reservation.errorutils.core.api.ErrorCategory;
import com.maslonka.reservation.errorutils.core.api.ErrorCode;
import com.maslonka.reservation.errorutils.core.api.FieldViolation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionHierarchyTest {

    @Test
    @DisplayName("Should normalize optional collections when BusinessException is created with null state")
    void shouldNormalizeOptionalCollectionsWhenBusinessExceptionIsCreatedWithNullState() {
        BusinessException exception = new BusinessException(TestErrorCode.CUSTOMER_NOT_FOUND, "Missing", null, null, null);

        assertEquals(TestErrorCode.CUSTOMER_NOT_FOUND, exception.errorCode());
        assertEquals("Missing", exception.getMessage());
        assertTrue(exception.metadata().isEmpty());
        assertTrue(exception.violations().isEmpty());
    }

    @Test
    @DisplayName("Should make defensive copies when BusinessException receives mutable metadata and violations")
    void shouldMakeDefensiveCopiesWhenBusinessExceptionReceivesMutableMetadataAndViolations() {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("tenant", "sk");
        List<FieldViolation> violations = new ArrayList<>();
        violations.add(new FieldViolation("email", "invalid", "Invalid", "Email"));

        BusinessException exception = new BusinessException(TestErrorCode.CUSTOMER_CONFLICT, "Conflict", null, metadata, violations);

        metadata.put("tenant", "mutated");
        violations.add(new FieldViolation("name", "", "Required", "NotBlank"));

        assertEquals("sk", exception.metadata().get("tenant"));
        assertEquals(1, exception.violations().size());
        Map<String, Object> exceptionMetadata = exception.metadata();
        List<FieldViolation> exceptionViolations = exception.violations();
        FieldViolation extraViolation = new FieldViolation("field", "x", "msg", "code");

        assertThrows(UnsupportedOperationException.class, () -> exceptionMetadata.put("other", "value"));
        assertThrows(UnsupportedOperationException.class, () -> exceptionViolations.add(extraViolation));
    }

    @Test
    @DisplayName("Should preserve cause and default empty state when convenience BusinessException constructors are used")
    void shouldPreserveCauseAndDefaultStateWhenConvenienceBusinessExceptionConstructorsAreUsed() {
        RuntimeException cause = new RuntimeException("db down");

        BusinessException bare = new BusinessException(TestErrorCode.CUSTOMER_NOT_FOUND);
        BusinessException withDetail = new BusinessException(TestErrorCode.CUSTOMER_NOT_FOUND, "Missing");
        BusinessException withCause = new BusinessException(TestErrorCode.CUSTOMER_CONFLICT, "Conflict", cause);

        assertNull(bare.getMessage());
        assertEquals("Missing", withDetail.getMessage());
        assertSame(cause, withCause.getCause());
        assertTrue(withCause.metadata().isEmpty());
        assertTrue(withCause.violations().isEmpty());
    }

    @Test
    @DisplayName("Should preserve error code and message when specialized business exceptions are used")
    void shouldPreserveErrorCodeAndMessageWhenSpecializedBusinessExceptionsAreUsed() {
        NotFoundException notFound = new NotFoundException(TestErrorCode.CUSTOMER_NOT_FOUND, "Customer not found");
        ConflictException conflict = new ConflictException(TestErrorCode.CUSTOMER_CONFLICT, "Email already exists");
        PolicyViolationException policy = new PolicyViolationException(TestErrorCode.POLICY_VIOLATION, "Action denied");

        assertEquals(TestErrorCode.CUSTOMER_NOT_FOUND, notFound.errorCode());
        assertEquals("Customer not found", notFound.getMessage());
        assertEquals(TestErrorCode.CUSTOMER_CONFLICT, conflict.errorCode());
        assertEquals("Email already exists", conflict.getMessage());
        assertEquals(TestErrorCode.POLICY_VIOLATION, policy.errorCode());
        assertEquals("Action denied", policy.getMessage());
    }

    @Test
    @DisplayName("Should preserve error code and optional cause when InternalException is created")
    void shouldPreserveErrorCodeAndOptionalCauseWhenInternalExceptionIsCreated() {
        RuntimeException cause = new RuntimeException("boom");

        InternalException withoutCause = new InternalException(TestErrorCode.TECHNICAL_ERROR);
        InternalException withCause = new InternalException(TestErrorCode.TECHNICAL_ERROR, cause);

        assertEquals(TestErrorCode.TECHNICAL_ERROR, withoutCause.errorCode());
        assertNull(withoutCause.getCause());
        assertEquals(TestErrorCode.TECHNICAL_ERROR, withCause.errorCode());
        assertSame(cause, withCause.getCause());
    }

    private enum TestErrorCode implements ErrorCode {
        CUSTOMER_NOT_FOUND(404, ErrorCategory.BUSINESS),
        CUSTOMER_CONFLICT(409, ErrorCategory.BUSINESS),
        POLICY_VIOLATION(422, ErrorCategory.POLICY),
        TECHNICAL_ERROR(500, ErrorCategory.TECHNICAL);

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
