# error-utils

Reusable internal platform library that provides a consistent API error contract for Spring Boot microservices.

## Modules

- `error-utils-core`: Spring-free error abstractions, models, and exception hierarchy
- `error-utils-spring-webmvc`: Spring MVC exception mapping and security JSON handlers
- `error-utils-spring-boot-starter`: auto-configuration for zero-boilerplate adoption
- `error-utils-bom`: version alignment for all modules

## BOM usage

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
      <groupId>com.maslonka.reservation</groupId>
      <artifactId>error-utils-bom</artifactId>
      <version>${error.utils.version}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>

<dependencies>
  <dependency>
    <groupId>com.maslonka.reservation</groupId>
    <artifactId>error-utils-spring-boot-starter</artifactId>
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

## Properties

```yaml
error-utils:
  include-exception-message: false
  internal-error-message: Internal server error
  correlation-id-mdc-key: correlationId
  trace-id-mdc-key: traceId
  correlation-id-request-attribute: correlationId
  trace-id-request-attribute: traceId
  correlation-id-header: X-Correlation-Id
  trace-id-header: X-Trace-Id
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
