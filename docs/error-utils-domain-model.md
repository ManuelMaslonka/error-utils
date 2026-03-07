# Error Utils Domain Model

This document describes the main domain objects exposed by `error-utils`, grouped by module. It focuses on intent,
lifecycle, and extension points.

## `error-utils-core`

### `ErrorCode`

Package: `com.maslonka.reservation.errorutils.core.api`

Main contract for every domain error code.

- `code()` returns the stable machine-readable code sent to clients.
- `messageKey()` returns the translation or catalog key used by the application.
- `httpStatus()` defines the HTTP status used in the serialized response.
- `category()` classifies the failure as `VALIDATION`, `BUSINESS`, `POLICY`, `SECURITY`, or `TECHNICAL`.

All domain-specific application errors should enter the library through this interface.

### `ErrorCategory`

Package: `com.maslonka.reservation.errorutils.core.api`

High-level taxonomy for failures.

- `VALIDATION`: request or state validation problem
- `BUSINESS`: domain rule violation
- `POLICY`: policy/rule rejection
- `SECURITY`: authentication or authorization failure
- `TECHNICAL`: unexpected internal or infrastructure failure

### `ApiError`

Package: `com.maslonka.reservation.errorutils.core.api`

Immutable HTTP error payload returned to clients.

Fields:

- `timestamp`: response creation time
- `status`: numeric HTTP status
- `error`: textual HTTP status name
- `code`: machine-readable domain error code
- `message`: detail message
- `path`: request URI
- `correlationId`: optional correlation id
- `traceId`: optional trace id
- `violations`: field-level validation details
- `metadata`: extra structured data

Null values are omitted from JSON.

### `ApiErrorInput`

Package: `com.maslonka.reservation.errorutils.core.api`

Internal normalized input used by `ApiErrorAssembler` to create `ApiError`.

### `ApiErrorAssembler`

Package: `com.maslonka.reservation.errorutils.core.api`

Pure assembler that turns `ApiErrorInput` into `ApiError`. It keeps payload creation independent from Spring.

### `FieldViolation`

Package: `com.maslonka.reservation.errorutils.core.api`

Field-level validation detail.

- `field`: logical field path
- `rejectedValue`: offending value
- `message`: validation message
- `code`: validation rule code

### `BusinessException`

Package: `com.maslonka.reservation.errorutils.core.exception`

Base runtime exception for expected domain failures.

Carries:

- `ErrorCode`
- optional detail message
- optional metadata
- optional `FieldViolation` list

### `NotFoundException`

Package: `com.maslonka.reservation.errorutils.core.exception`

Specialized `BusinessException` for not-found scenarios.

### `ConflictException`

Package: `com.maslonka.reservation.errorutils.core.exception`

Specialized `BusinessException` for conflict scenarios.

### `PolicyViolationException`

Package: `com.maslonka.reservation.errorutils.core.exception`

Specialized `BusinessException` for policy-driven rejections.

### `InternalException`

Package: `com.maslonka.reservation.errorutils.core.exception`

Runtime exception for technical/internal failures that should still carry a controlled `ErrorCode`.

### `ClockProvider`

Package: `com.maslonka.reservation.errorutils.core.spi`

SPI for time abstraction. The default starter currently registers a plain `Clock` bean and uses that directly.

### `ErrorCodeResolver`

Package: `com.maslonka.reservation.errorutils.core.spi`

SPI seam for resolving an `ErrorCode` from a generic `Throwable`. Useful for custom adapters.

### `ErrorMetadataSanitizer`

Package: `com.maslonka.reservation.errorutils.core.spi`

SPI executed right before metadata is serialized. Use it to strip secrets or normalize values.

## `error-utils-validation`

### `Validator`

Package: `com.maslonka.reservation.errorutils.validation.api`

Entry point to the fluent validation API. `Validator.forObject(target)` starts in `COLLECT_ALL` mode by default.

### `ObjectValidator<T>`

Package: `com.maslonka.reservation.errorutils.validation.pipeline`

Fluent facade for validating a target object.

Main operations:

- `failFast()`
- `collectAll()`
- `step(ValidationStep)`
- `step(Consumer<ValidationCollector>)`
- `check(...)`
- `require(...)`
- `toResult()`
- `toViolations()`
- `throwIfInvalid()`

### `ValidationStep<T>`

Package: `com.maslonka.reservation.errorutils.validation.api`

Typed reusable validation contract with access to the validated target and `ValidationCollector`.

### `ValidationCollector`

Package: `com.maslonka.reservation.errorutils.validation.api`

Imperative facade over the current validation chain. Useful for inline checks and validator helpers.

### `ValidationChain`

Package: `com.maslonka.reservation.errorutils.validation.pipeline`

Internal state holder that:

- stores current `ValidationMode`
- collects `ValidationFailure`
- enforces `FAIL_FAST` short-circuiting
- builds `ValidationResult`
- creates the default thrown exception

Default `throwIfInvalid()` behavior:

- use `firstFailure.exceptionFactory()` when present
- otherwise create `BusinessException`
- attach merged metadata and all field violations

### `ValidationFailure`

Package: `com.maslonka.reservation.errorutils.validation.model`

Immutable description of one validation failure.

Fields:

- `errorCode`
- `message`
- `field`
- `rejectedValue`
- `violationCode`
- `metadata`
- `exceptionFactory`

Builder-style methods return new immutable instances.

### `ValidationResult`

Package: `com.maslonka.reservation.errorutils.validation.model`

Immutable validation outcome containing:

- `mode`
- `failures`
- `violations`

Helper methods:

- `isValid()`
- `metadata()`
- `firstFailure()`

### `ValidationMode`

Package: `com.maslonka.reservation.errorutils.validation.model`

Execution strategy:

- `FAIL_FAST`
- `COLLECT_ALL`

## `error-utils-spring-webmvc`

### `ErrorUtilsProperties`

Package: `com.maslonka.reservation.errorutils.spring.web`

Spring Boot properties under prefix `error-utils`. They control message visibility and identifier lookup keys.

### `ApiErrorFactory`

Package: `com.maslonka.reservation.errorutils.spring.web`

Public Spring bean used to create `ApiError`. It extends `WebApiErrorFactory` and is the main bean to override.

### `WebApiErrorFactory`

Package: `com.maslonka.reservation.errorutils.spring.web.factory`

Central HTTP payload assembly component. It:

- reads time from `Clock`
- resolves identifiers through `TraceContextResolver`
- sanitizes metadata through `ErrorMetadataSanitizer`
- builds payload through `ApiErrorAssembler`
- applies all `ErrorResponseCustomizer` beans
- hides technical exception details unless explicitly enabled

### `ErrorResponseCustomizer`

Package: `com.maslonka.reservation.errorutils.spring.web`

Functional post-processor for `ApiError`.

- input: current `ApiError`
- input: `ErrorResponseContext`
- output: replacement `ApiError` or `null`

### `ErrorResponseContext`

Package: `com.maslonka.reservation.errorutils.spring.web.advice`

Context passed into `ErrorResponseCustomizer`.

Fields:

- `throwable`
- `request`
- `violations`
- `rejectedValue`

### `GlobalApiExceptionHandler`

Package: `com.maslonka.reservation.errorutils.spring.web.advice`

Default `@RestControllerAdvice` for framework and application exceptions.

Mappings:

- `MethodArgumentNotValidException` -> `400 REQUEST_VALIDATION_FAILED`
- `ConstraintViolationException` -> `400 REQUEST_VALIDATION_FAILED`
- `HttpMessageNotReadableException` -> `400 MALFORMED_REQUEST_BODY`
- `BusinessException` -> status from embedded `ErrorCode`
- `InternalException` -> status from embedded `ErrorCode`
- fallback `Throwable` -> `500 TECHNICAL_ERROR`

### `StandardErrorCode`

Package: `com.maslonka.reservation.errorutils.spring.web`

Built-in adapter error codes:

- `REQUEST_VALIDATION_FAILED`
- `MALFORMED_REQUEST_BODY`
- `UNAUTHORIZED`
- `ACCESS_DENIED`
- `RESOURCE_NOT_FOUND`
- `RESOURCE_CONFLICT`
- `POLICY_VIOLATION`
- `TECHNICAL_ERROR`

### `TraceContext`

Package: `com.maslonka.reservation.errorutils.spring.web.trace`

Immutable holder for resolved `correlationId` and `traceId`.

### `TraceContextResolver`

Package: `com.maslonka.reservation.errorutils.spring.web.trace`

SPI for resolving `TraceContext` from the current request.

### `DefaultTraceContextResolver`

Package: `com.maslonka.reservation.errorutils.spring.web.trace`

Default resolver with lookup order:

1. request attribute
2. request header
3. MDC

### `NoopErrorMetadataSanitizer`

Package: `com.maslonka.reservation.errorutils.spring.web.metadata`

Default `ErrorMetadataSanitizer`. It returns an immutable copy and removes nothing.

### `GlobalSecurityExceptionHandler`

Package: `com.maslonka.reservation.errorutils.spring.web.security`

Security-specific `@RestControllerAdvice`.

Mappings:

- `AuthenticationException` -> `401 UNAUTHORIZED`
- `AccessDeniedException` -> `403 ACCESS_DENIED`

### `JsonAuthenticationEntryPoint`

Package: `com.maslonka.reservation.errorutils.spring.web.security`

Security adapter that serializes JSON `ApiError` for unauthenticated requests.

### `JsonAccessDeniedHandler`

Package: `com.maslonka.reservation.errorutils.spring.web.security`

Security adapter that serializes JSON `ApiError` for access denied responses.

## `error-utils-spring-boot-starter`

### `ErrorUtilsAutoConfiguration`

Package: `com.maslonka.reservation.errorutils.spring.boot.autoconfigure`

Servlet-only auto-configuration entry point. It enables `ErrorUtilsProperties` and imports default bean registration.

### `ErrorUtilsBeanRegistrar`

Package: `com.maslonka.reservation.errorutils.spring.boot.autoconfigure`

Programmatic registrar responsible for conditional default bean creation. Defaults are registered only if no bean of the
same type already exists.

### `ErrorUtilsOpenApiCustomizer`

Package: `com.maslonka.reservation.errorutils.spring.boot.autoconfigure`

OpenAPI integration bean that contributes reusable error components when `springdoc-openapi` is present.
