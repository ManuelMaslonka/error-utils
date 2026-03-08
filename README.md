# Error Utils

Reusable internal platform library that provides a consistent API error contract for Spring Boot microservices.


## Modules

- `error-utils-core`: Spring-free error abstractions, models, and exception hierarchy
- `error-utils-validation`: Spring-free fluent validation pipeline with `failFast` / `collectAll`
- `error-utils-spring-webmvc`: Spring MVC exception mapping and security JSON handlers
- `error-utils-openapi`: reusable OpenAPI components for standard error payloads
- `error-utils-spring-boot-starter`: auto-configuration for zero-boilerplate adoption
- `error-utils-bom`: dependency version alignment across all error-utils artifacts

## Implemented features

- Spring Boot starter with auto-configuration (`ErrorUtilsAutoConfiguration`)
- Spring MVC exception mapping with consistent JSON payloads (`GlobalApiExceptionHandler`)
- Security JSON handlers for `401` / `403`
- Spring-free validation pipeline with `failFast` and `collectAll`
- Reusable OpenAPI components for shared API contracts
- BOM module for simplified dependency management

## Default runtime behavior

After adding `error-utils-spring-boot-starter`, the library auto-registers these default beans in Servlet applications:

- `Clock` as `errorUtilsClock` using `Clock.systemUTC()`
- `ApiErrorAssembler`
- `ErrorMetadataSanitizer` as `NoopErrorMetadataSanitizer`
- `TraceContextResolver` as `DefaultTraceContextResolver`
- `ApiErrorFactory`
- `GlobalApiExceptionHandler`

Additional beans are registered only when the matching libraries are present:

- `GlobalSecurityExceptionHandler` when Spring Security core is on the classpath
- `JsonAuthenticationEntryPoint` and `JsonAccessDeniedHandler` when Spring Security web is on the classpath
- `ErrorUtilsOpenApiCustomizer` when `springdoc-openapi` is on the classpath

Default request-to-response behavior:

- `MethodArgumentNotValidException` and `ConstraintViolationException` become `400 REQUEST_VALIDATION_FAILED`
- `HttpMessageNotReadableException` becomes `400 MALFORMED_REQUEST_BODY`
- `BusinessException` uses its own domain `ErrorCode`, metadata, and violations
- `InternalException` and unknown exceptions are rendered as technical errors
- raw exception messages for technical failures are hidden by default
- `correlationId` and `traceId` are hidden by default
- trace identifiers are resolved in this order: request attribute, header, MDC

## Build

```bash
./mvnw clean verify
```

## Usage (Starter)

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-dependencies</artifactId>
      <version>${spring.boot.version}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
    <dependency>
        <groupId>io.github.manuelmaslonka</groupId>
      <artifactId>error-utils-bom</artifactId>
        <version>0.1.0</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>

<dependencies>
  <dependency>
      <groupId>io.github.manuelmaslonka</groupId>
    <artifactId>error-utils-spring-boot-starter</artifactId>
  </dependency>
  <dependency>
      <groupId>io.github.manuelmaslonka</groupId>
      <artifactId>error-utils-validation</artifactId>
  </dependency>
</dependencies>
```

## Application error codes

```java
public enum CustomerErrors implements ErrorCode {
    CUSTOMER_NOT_FOUND,
    CUSTOMER_EMAIL_CONFLICT;

    @Override public String code() { return name(); }
    @Override public String messageKey() { return "error.customer." + name().toLowerCase(); }
    @Override public int httpStatus() {
        return switch (this) {
            case CUSTOMER_NOT_FOUND -> 404;
            case CUSTOMER_EMAIL_CONFLICT -> 409;
        };
    }
    @Override public ErrorCategory category() { return ErrorCategory.BUSINESS; }
}
```

## Service usage

```java
throw new NotFoundException(CustomerErrors.CUSTOMER_NOT_FOUND, "Customer " + id + " not found");
```

## Validation usage

Validation module is Spring-free and can be used in services, domain logic, or Spring components.

Public packages:

- `com.maslonka.reservation.errorutils.validation.api`
- `com.maslonka.reservation.errorutils.validation.model`

### Fluent pipeline

```java
Validator.forObject(command)
    .

failFast()
    .

step(basicCommandValidator)
    .

step(userAccessValidationStep)
    .

step(businessRulesValidator)
    .

throwIfInvalid();
```

Supported execution modes:

- `failFast()`: stops collecting after the first validation failure
- `collectAll()`: gathers all failures and exposes them in a single result/exception

### Step contract

Object-bound validation steps can implement `ValidationStep<T>`:

```java
@Component
class BasicCommandValidator implements ValidationStep<CreateUserCommand> {

    @Override
    public void validate(CreateUserCommand command, ValidationCollector collector) {
        collector.check(
            command.username() != null && !command.username().isBlank(),
            ValidationFailure.of(UserErrorCode.USERNAME_REQUIRED, "Username is required")
                .field("username")
                .rejectedValue(command.username())
                .violationCode("NotBlank")
        );
    }
}
```

### Step with external arguments

If a validation step should not be tied to the validated object type, use an inline lambda and pass external arguments
explicitly:

```java
Validator.forObject(command)
    .

failFast()
    .

step(basicCommandValidator)
    .

step((cmd, collector) ->userAccessValidationStep.

validate(collector, organizationId, hasAccessToSomething, something))
        .

step(businessRulesValidator)
    .

throwIfInvalid();
```

Example external-context validator:

```java
@Component
class UserAccessValidationStep {

    void validate(
        ValidationCollector collector,
        Long organizationId,
        boolean hasAccessToSomething,
        String something
    ) {
        collector.check(
            hasAccessToSomething,
            ValidationFailure.of(UserErrorCode.ORGANIZATION_ACCESS_DENIED, "User has no access to organization")
                .field("organizationId")
                .rejectedValue(organizationId)
                .metadata("something", something)
        );
    }
}
```

### Collector-only step

For checks that do not depend on the validated object at all, use `step(collector -> ...)`:

```java
Validator.forObject(command)
    .

collectAll()
    .

step(collector ->collector.

check(featureFlagEnabled, ValidationFailure.of(UserErrorCode.FEATURE_DISABLED, "Feature is disabled")
            .

field("featureFlag")
    ))
            .

throwIfInvalid();
```

### Result or exception

You can either throw immediately:

```java
Validator.forObject(command)
    .

collectAll()
    .

step(basicCommandValidator)
    .

throwIfInvalid();
```

Or inspect the result first:

```java
ValidationResult result = Validator.forObject(command).collectAll().step(basicCommandValidator).toResult();

if(!result.

isValid()){
        // result.failures()
        // result.violations()
        }
```

## Error response

```json
{
  "timestamp": "2026-02-24T10:15:30Z",
  "status": 404,
  "error": "NOT_FOUND",
  "code": "CUSTOMER_NOT_FOUND",
  "message": "Customer 8fd2... not found",
  "path": "/api/customers/8fd2...",
  "correlationId": "c4f1b0d8f9d24e3f",
  "traceId": "8aa1f3c45d9a2b10",
  "violations": [],
  "metadata": {}
}
```

## Configuration defaults and options

```yaml
error-utils:
  include-exception-message: false
  include-correlation-id: false
  include-trace-id: false
  internal-error-message: Internal server error
  correlation-id-mdc-key: correlationId
  trace-id-mdc-key: traceId
  correlation-id-request-attribute: correlationId
  trace-id-request-attribute: traceId
  correlation-id-header: X-Correlation-Id
  trace-id-header: X-Trace-Id
```

`correlationId` and `traceId` are hidden by default. They are added to the response only when `include-correlation-id` /
`include-trace-id` are enabled and values are actually resolved from request attributes, headers, or MDC.

| Property                                       | Default                 | Meaning                                                      |
|------------------------------------------------|-------------------------|--------------------------------------------------------------|
| `error-utils.include-exception-message`        | `false`                 | Exposes raw exception messages for technical errors.         |
| `error-utils.include-correlation-id`           | `false`                 | Includes `correlationId` in the JSON response when resolved. |
| `error-utils.include-trace-id`                 | `false`                 | Includes `traceId` in the JSON response when resolved.       |
| `error-utils.internal-error-message`           | `Internal server error` | Fallback message used for technical failures.                |
| `error-utils.correlation-id-mdc-key`           | `correlationId`         | MDC key used as the last fallback for correlation id.        |
| `error-utils.trace-id-mdc-key`                 | `traceId`               | MDC key used as the last fallback for trace id.              |
| `error-utils.correlation-id-request-attribute` | `correlationId`         | Request attribute checked first for correlation id.          |
| `error-utils.trace-id-request-attribute`       | `traceId`               | Request attribute checked first for trace id.                |
| `error-utils.correlation-id-header`            | `X-Correlation-Id`      | Request header checked second for correlation id.            |
| `error-utils.trace-id-header`                  | `X-Trace-Id`            | Request header checked second for trace id.                  |

## Customization options

The starter registers default beans only when a bean of the same type is not already present, so you can override
behavior with your own Spring beans.

You can customize:

- `TraceContextResolver` to change where `correlationId` and `traceId` are read from
- `ErrorMetadataSanitizer` to remove secrets or normalize metadata before serialization
- `ErrorResponseCustomizer` to enrich or replace the final `ApiError`
- `ApiErrorFactory` to control payload assembly end-to-end
- `GlobalApiExceptionHandler` to change exception-to-response mapping
- Spring Security entry point / access denied handler for custom security response behavior

Example custom `TraceContextResolver`:

```java
@Bean
TraceContextResolver traceContextResolver() {
    return request -> new TraceContext(
        request.getHeader("X-Request-Id"),
        request.getHeader("traceparent")
    );
}
```

Example custom `ErrorMetadataSanitizer`:

```java
@Bean
ErrorMetadataSanitizer errorMetadataSanitizer() {
    return metadata -> {
        if (metadata == null || metadata.isEmpty()) {
            return java.util.Map.of();
        }

        java.util.Map<String, Object> sanitized = new java.util.LinkedHashMap<>(metadata);
        sanitized.remove("stackTrace");
        sanitized.remove("sql");
        return java.util.Map.copyOf(sanitized);
    };
}
```

## ErrorResponseCustomizer

You can customize generated error payloads by registering one or more Spring beans:

```java
@Bean
ErrorResponseCustomizer serviceMetadataCustomizer() {
    return (apiError, context) -> new ApiError(
        apiError.timestamp(),
        apiError.status(),
        apiError.error(),
        apiError.code(),
        apiError.message(),
        apiError.path(),
        apiError.correlationId(),
        apiError.traceId(),
        apiError.violations(),
        java.util.Map.of(
            "service", "customer-service",
            "method", context.request().getMethod(),
            "rejectedValue", context.rejectedValue()
        )
    );
}
```

Customizers are executed in order and receive the current `ApiError` plus `ErrorResponseContext` (throwable, request, violations, rejectedValue).

Common uses for `ErrorResponseCustomizer`:

- attach service or tenant metadata
- redact `rejectedValue` for sensitive fields
- translate messages using your own `messageKey` catalog
- include request method or other request-scoped values

## Domain model docs

Detailed documentation for the main domain objects is
in [docs/error-utils-domain-model.md](/Users/manuelmaslonka/Developer/PersonalDeveloper/error-utils/docs/error-utils-domain-model.md).

## Publish

```bash
./mvnw -DskipTests deploy
```

Configure credentials in `~/.m2/settings.xml` under server id `github`.

## OpenAPI integration

When `springdoc` is present in the application, starter automatically registers OpenAPI integration:

- adds `ApiError` and `FieldViolation` schemas to `components.schemas`
- adds reusable responses `Error400`, `Error401`, `Error403`, `Error404`, `Error409`, `Error422`, `Error500`
- attaches default error responses (`400/401/403/404/409/422/500`) to operations that do not define them

To enable in a service, add springdoc dependency (starter already provides integration bean):

```xml
<dependency>
  <groupId>org.springdoc</groupId>
  <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
</dependency>
```

## Shared OpenAPI Module

Shared error contract is now published in module `error-utils-openapi`.

Contract file path inside that artifact:

- `openapi/error-utils-components.yaml`

Example usage in a service OpenAPI:

```yaml
paths:
  /api/customers:
    get:
      responses:
        '200':
          description: OK
        '400':
          $ref: './openapi/error-utils-components.yaml#/components/responses/Error400'
        '401':
          $ref: './openapi/error-utils-components.yaml#/components/responses/Error401'
        '403':
          $ref: './openapi/error-utils-components.yaml#/components/responses/Error403'
        '500':
          $ref: './openapi/error-utils-components.yaml#/components/responses/Error500'
```
# Licence

Copyright (c) 2026 Manuel Mašlonka

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software...
