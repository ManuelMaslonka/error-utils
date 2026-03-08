package com.maslonka.reservation.errorutils.validation;

import com.maslonka.reservation.errorutils.core.api.ErrorCategory;
import com.maslonka.reservation.errorutils.core.api.ErrorCode;
import com.maslonka.reservation.errorutils.core.api.FieldViolation;
import com.maslonka.reservation.errorutils.validation.api.Validator;
import com.maslonka.reservation.errorutils.validation.model.ValidationFailure;
import com.maslonka.reservation.errorutils.validation.model.ValidationMode;
import com.maslonka.reservation.errorutils.validation.model.ValidationResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ValidationModelTest {

    @Test
    @DisplayName("Should keep previous ValidationFailure instances immutable when builder-style methods are chained")
    void shouldKeepPreviousValidationFailureInstancesImmutableWhenBuilderStyleMethodsAreChained() {
        ValidationFailure base = ValidationFailure.of(TestErrorCode.NAME_REQUIRED, "Name is required");
        ValidationFailure enriched = base.field("name").rejectedValue("").violationCode("NotBlank").metadata("source", "api");

        assertNull(base.field());
        assertNull(base.rejectedValue());
        assertNull(base.violationCode());
        assertTrue(base.metadata().isEmpty());

        assertEquals("name", enriched.field());
        assertEquals("", enriched.rejectedValue());
        assertEquals("NotBlank", enriched.violationCode());
        assertEquals("api", enriched.metadata().get("source"));
        assertEquals("name", enriched.toFieldViolation().field());
    }

    @Test
    @DisplayName("Should merge metadata with last value winning when multiple failures contribute the same key")
    void shouldMergeMetadataWithLastValueWinningWhenMultipleFailuresContributeTheSameKey() {
        ValidationResult result = new ValidationResult(ValidationMode.COLLECT_ALL,
                                                       List.of(ValidationFailure.of(TestErrorCode.NAME_REQUIRED, "Name is required")
                                                                       .metadata("source", "api")
                                                                       .metadata("traceId", "trace-1"),
                                                               ValidationFailure.of(TestErrorCode.AGE_INVALID, "Age invalid").metadata("source", "service")),
                                                       List.of());

        assertEquals(Map.of("source", "service", "traceId", "trace-1"), result.metadata());
    }

    @Test
    @DisplayName("Should normalize collections and expose null first failure when ValidationResult is valid")
    void shouldNormalizeCollectionsAndExposeNullFirstFailureWhenValidationResultIsValid() {
        ValidationResult result = new ValidationResult(ValidationMode.COLLECT_ALL, null, null);

        assertTrue(result.isValid());
        assertTrue(result.failures().isEmpty());
        assertTrue(result.violations().isEmpty());
        assertTrue(result.metadata().isEmpty());
        assertNull(result.firstFailure());
    }

    @Test
    @DisplayName("Should project collected failures to field violations when validator converts to violations")
    void shouldProjectCollectedFailuresToFieldViolationsWhenValidatorConvertsToViolations() {
        List<com.maslonka.reservation.errorutils.core.api.FieldViolation> violations = Validator.forObject(new CreateUserCommand(""))
                .collectAll()
                .step((command, collector) -> collector.check(!command.name().isBlank(),
                                                              ValidationFailure.of(TestErrorCode.NAME_REQUIRED, "Name is required")
                                                                      .field("name")
                                                                      .rejectedValue(command.name())
                                                                      .violationCode("NotBlank")))
                .toViolations();

        assertEquals(1, violations.size());
        assertEquals("name", violations.getFirst().field());
        assertEquals("", violations.getFirst().rejectedValue());
        assertEquals("NotBlank", violations.getFirst().code());
    }

    @Test
    @DisplayName("Should expose immutable collections when ValidationResult is returned to callers")
    void shouldExposeImmutableCollectionsWhenValidationResultIsReturnedToCallers() {
        ValidationResult result = Validator.forObject(new CreateUserCommand(""))
                .collectAll()
                .step((command, collector) -> collector.check(!command.name().isBlank(),
                                                              ValidationFailure.of(TestErrorCode.NAME_REQUIRED, "Name is required").field("name")))
                .toResult();
        ValidationFailure failure = ValidationFailure.of(TestErrorCode.AGE_INVALID, "Age invalid");
        FieldViolation violation = result.violations().getFirst();
        List<ValidationFailure> failures = result.failures();
        List<FieldViolation> violations = result.violations();

        assertThrows(UnsupportedOperationException.class, () -> failures.add(failure));
        assertThrows(UnsupportedOperationException.class, () -> violations.add(violation));
    }

    private enum TestErrorCode implements ErrorCode {
        NAME_REQUIRED, AGE_INVALID;

        @Override
        public String code() {
            return name();
        }

        @Override
        public String messageKey() {
            return "error.validation." + name().toLowerCase();
        }

        @Override
        public int httpStatus() {
            return 422;
        }

        @Override
        public ErrorCategory category() {
            return ErrorCategory.VALIDATION;
        }
    }

    private record CreateUserCommand(String name) {}
}
