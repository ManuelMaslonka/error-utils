package com.maslonka.reservation.errorutils.validation;

import com.maslonka.reservation.errorutils.core.api.ErrorCategory;
import com.maslonka.reservation.errorutils.core.api.ErrorCode;
import com.maslonka.reservation.errorutils.core.exception.BusinessException;
import com.maslonka.reservation.errorutils.validation.api.Validator;
import com.maslonka.reservation.errorutils.validation.model.ValidationFailure;
import com.maslonka.reservation.errorutils.validation.model.ValidationMode;
import com.maslonka.reservation.errorutils.validation.model.ValidationResult;
import com.maslonka.reservation.errorutils.validation.pipeline.ValidationChain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationChainTest {

    @Test
    @DisplayName("Should keep all failures when collect-all mode is used")
    void shouldKeepAllFailuresWhenCollectAllModeIsUsed() {
        ValidationResult result = ValidationChain.start()
                .collectAll()
                .check(false, ValidationFailure.of(TestErrorCode.NAME_REQUIRED, "Name is required").field("name").violationCode("NotBlank"))
                .check(false, ValidationFailure.of(TestErrorCode.AGE_INVALID, "Age must be positive").field("age").rejectedValue(-1).violationCode("Positive"))
                .toResult();

        assertFalse(result.isValid());
        assertEquals(ValidationMode.COLLECT_ALL, result.mode());
        assertEquals(2, result.failures().size());
        assertEquals(2, result.violations().size());
        assertEquals("name", result.violations().get(0).field());
        assertEquals("Positive", result.violations().get(1).code());
    }

    @Test
    @DisplayName("Should stop after first failure when fail-fast mode is used")
    void shouldStopAfterFirstFailureWhenFailFastModeIsUsed() {
        ValidationResult result = ValidationChain.start()
                .failFast()
                .check(false, ValidationFailure.of(TestErrorCode.NAME_REQUIRED, "Name is required").field("name"))
                .check(false, ValidationFailure.of(TestErrorCode.AGE_INVALID, "Age must be positive").field("age"))
                .toResult();

        assertFalse(result.isValid());
        assertEquals(ValidationMode.FAIL_FAST, result.mode());
        assertEquals(1, result.failures().size());
        assertEquals("name", result.firstFailure().field());
    }

    @Test
    @DisplayName("Should use failure-specific exception factory when one is attached to the first failure")
    void shouldUseFailureSpecificExceptionFactoryWhenOneIsAttachedToTheFirstFailure() {
        ValidationChain chain = ValidationChain.start()
                .check(false,
                       ValidationFailure.of(TestErrorCode.NAME_REQUIRED, "Name is required")
                               .field("name")
                               .exceptionFactory(failure -> new IllegalStateException(failure.message())));

        IllegalStateException exception = assertThrows(IllegalStateException.class, chain::throwIfInvalid);

        assertEquals("Name is required", exception.getMessage());
    }

    @Test
    @DisplayName("Should use custom terminal exception factory when caller provides one")
    void shouldUseCustomTerminalExceptionFactoryWhenCallerProvidesOne() {
        ValidationChain chain = ValidationChain.start()
                .collectAll()
                .check(false, ValidationFailure.of(TestErrorCode.NAME_REQUIRED, "Name is required").field("name"))
                .check(false, ValidationFailure.of(TestErrorCode.AGE_INVALID, "Age must be positive").field("age"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                                                          () -> chain.throwIfInvalid(result -> new IllegalArgumentException(
                                                                  "violations=" + result.violations().size())));

        assertEquals("violations=2", exception.getMessage());
    }

    @Test
    @DisplayName("Should map to BusinessException when throw-if-invalid uses the default strategy")
    void shouldMapToBusinessExceptionWhenThrowIfInvalidUsesTheDefaultStrategy() {
        ValidationChain chain = ValidationChain.start()
                .check(false, ValidationFailure.of(TestErrorCode.NAME_REQUIRED, "Name is required").field("name").metadata("source", "service"));

        BusinessException exception = assertThrows(BusinessException.class, chain::throwIfInvalid);

        assertEquals(TestErrorCode.NAME_REQUIRED, exception.errorCode());
        assertEquals(1, exception.violations().size());
        assertEquals("service", exception.metadata().get("source"));
    }

    @Test
    @DisplayName("Should produce empty result when all validations pass")
    void shouldProduceEmptyResultWhenAllValidationsPass() {
        ValidationResult result = ValidationChain.start().check(true, ValidationFailure.of(TestErrorCode.NAME_REQUIRED, "Name is required")).toResult();

        assertTrue(result.isValid());
        assertTrue(result.failures().isEmpty());
        assertTrue(result.violations().isEmpty());
    }

    @Test
    @DisplayName("Should collect failures from multiple validation steps when validator pipeline runs in collect-all mode")
    void shouldCollectFailuresFromMultipleValidationStepsWhenValidatorPipelineRunsInCollectAllMode() {
        CreateUserCommand command = new CreateUserCommand("", false, true);

        ValidationResult result = Validator.forObject(command)
                .collectAll()
                .step((target, collector) -> collector.check(!target.name().isBlank(),
                                                             ValidationFailure.of(TestErrorCode.NAME_REQUIRED, "Name is required").field("name")))
                .step((target, collector) -> collector.check(target.hasRequiredRole(),
                                                             ValidationFailure.of(TestErrorCode.ROLE_REQUIRED, "Role is required").field("roles")))
                .step((target, collector) -> collector.check(target.hasOrganizationAccess(),
                                                             ValidationFailure.of(TestErrorCode.ORGANIZATION_ACCESS_DENIED, "Organization access denied")
                                                                     .field("organizationIds")))
                .toResult();

        assertFalse(result.isValid());
        assertEquals(2, result.failures().size());
        assertEquals("name", result.failures().get(0).field());
        assertEquals("roles", result.failures().get(1).field());
    }

    @Test
    @DisplayName("Should respect fail-fast across multiple validation steps when validator pipeline runs")
    void shouldRespectFailFastAcrossMultipleValidationStepsWhenValidatorPipelineRuns() {
        CreateUserCommand command = new CreateUserCommand("", false, false);

        ValidationResult result = Validator.forObject(command)
                .failFast()
                .step((target, collector) -> collector.check(!target.name().isBlank(),
                                                             ValidationFailure.of(TestErrorCode.NAME_REQUIRED, "Name is required").field("name")))
                .step((target, collector) -> collector.check(target.hasRequiredRole(),
                                                             ValidationFailure.of(TestErrorCode.ROLE_REQUIRED, "Role is required").field("roles")))
                .toResult();

        assertFalse(result.isValid());
        assertEquals(1, result.failures().size());
        assertEquals("name", result.firstFailure().field());
    }

    @Test
    @DisplayName("Should support external arguments when validation pipeline uses inline target and collector steps")
    void shouldSupportExternalArgumentsWhenValidationPipelineUsesInlineTargetAndCollectorSteps() {
        CreateUserCommand command = new CreateUserCommand("john", true, true);
        long organizationId = 42L;
        boolean externalAccess = false;

        ValidationResult result = Validator.forObject(command)
                .collectAll()
                .step((target, collector) -> collector.check(externalAccess,
                                                             ValidationFailure.of(TestErrorCode.ORGANIZATION_ACCESS_DENIED, "Organization access denied")
                                                                     .field("organizationId")
                                                                     .rejectedValue(organizationId)))
                .toResult();

        assertFalse(result.isValid());
        assertEquals(1, result.failures().size());
        assertEquals("organizationId", result.firstFailure().field());
    }

    @Test
    @DisplayName("Should support collector-only steps when validation does not depend on the validated object")
    void shouldSupportCollectorOnlyStepsWhenValidationDoesNotDependOnTheValidatedObject() {
        ValidationResult result = Validator.forObject(new CreateUserCommand("john", true, true))
                .collectAll()
                .step(collector -> collector.check(false, ValidationFailure.of(TestErrorCode.AGE_INVALID, "Global validation failed").field("global")))
                .toResult();

        assertFalse(result.isValid());
        assertEquals("global", result.firstFailure().field());
    }

    private enum TestErrorCode implements ErrorCode {
        NAME_REQUIRED, AGE_INVALID, ROLE_REQUIRED, ORGANIZATION_ACCESS_DENIED;

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

    private record CreateUserCommand(String name, boolean hasRequiredRole, boolean hasOrganizationAccess) {}
}
