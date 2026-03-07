package com.maslonka.reservation.errorutils.spring.web;

import com.maslonka.reservation.errorutils.core.api.ApiError;
import com.maslonka.reservation.errorutils.core.api.ApiErrorAssembler;
import com.maslonka.reservation.errorutils.core.api.ErrorCategory;
import com.maslonka.reservation.errorutils.core.api.ErrorCode;
import com.maslonka.reservation.errorutils.core.exception.BusinessException;
import com.maslonka.reservation.errorutils.core.exception.InternalException;
import com.maslonka.reservation.errorutils.core.spi.ErrorMetadataSanitizer;
import com.maslonka.reservation.errorutils.spring.web.advice.GlobalApiExceptionHandler;
import com.maslonka.reservation.errorutils.spring.web.trace.DefaultTraceContextResolver;
import com.maslonka.reservation.errorutils.spring.web.trace.TraceContextResolver;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.validation.metadata.ConstraintDescriptor;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class WebApiErrorFactoryTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2026-03-06T10:15:30Z"), ZoneOffset.UTC);

    @Test
    void businessExceptionUsesCoreAssemblerAndCustomizers() {
        ApiErrorFactory factory = createFactory(true, true, true);
        MockHttpServletRequest request = request("/api/customers/42");

        BusinessException exception =
                new BusinessException(TestErrorCode.CUSTOMER_NOT_FOUND, "Customer 42 not found", null, Map.of("secret", "raw"), List.of());

        ApiError apiError = factory.fromBusinessException(exception, request);

        assertEquals(404, apiError.status());
        assertEquals("NOT_FOUND", apiError.error());
        assertEquals("Customer 42 not found", apiError.message());
        assertEquals("/api/customers/42", apiError.path());
        assertEquals("corr-1", apiError.correlationId());
        assertEquals("trace-1", apiError.traceId());
        assertEquals("masked", apiError.metadata().get("secret"));
        assertEquals("GET", apiError.metadata().get("method"));
    }

    @Test
    void correlationAndTraceIdsAreHiddenByDefault() {
        ApiErrorFactory factory = createFactory(true, false, false);
        MockHttpServletRequest request = request("/api/customers/42");

        BusinessException exception =
                new BusinessException(TestErrorCode.CUSTOMER_NOT_FOUND, "Customer 42 not found", null, Map.of("secret", "raw"), List.of());

        ApiError apiError = factory.fromBusinessException(exception, request);

        assertEquals(null, apiError.correlationId());
        assertEquals(null, apiError.traceId());
    }

    @Test
    void internalAndUnhandledUseConfiguredSafeMessage() {
        ApiErrorFactory factory = createFactory(false, false, false);
        MockHttpServletRequest request = request("/api/internal");

        ApiError internal = factory.fromInternalException(new InternalException(TestErrorCode.TECHNICAL_ERROR), request);
        ApiError unhandled = factory.fromUnhandled(new RuntimeException("boom"), request);

        assertEquals("Internal server error", internal.message());
        assertEquals("Internal server error", unhandled.message());
        assertEquals(500, unhandled.status());
    }

    @Test
    void globalHandlerMapsMethodArgumentValidationErrors() throws Exception {
        GlobalApiExceptionHandler handler = new GlobalApiExceptionHandler(createFactory(true, false, false));
        MockHttpServletRequest request = request("/api/customers");

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new SamplePayload(), "samplePayload");
        bindingResult.addError(new FieldError("samplePayload", "name", "", false, new String[]{"NotBlank"}, null, "must not be blank"));

        Method method = SampleController.class.getDeclaredMethod("create", SamplePayload.class);
        MethodParameter parameter = new MethodParameter(method, 0);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(parameter, bindingResult);

        ApiError body = handler.handleMethodArgumentNotValid(exception, request).getBody();

        assertNotNull(body);
        assertEquals(400, body.status());
        assertEquals("REQUEST_VALIDATION_FAILED", body.code());
        assertEquals(1, body.violations().size());
        assertEquals("name", body.violations().getFirst().field());
    }

    @Test
    void globalHandlerMapsConstraintViolations() {
        GlobalApiExceptionHandler handler = new GlobalApiExceptionHandler(createFactory(true, false, false));
        MockHttpServletRequest request = request("/api/customers");

        ConstraintViolationException exception = new ConstraintViolationException(Set.of(new StubConstraintViolation("customerId",
                                                                                                                     42,
                                                                                                                     "must exist",
                                                                                                                     SampleConstraintHolder.constraintAnnotation())));

        ApiError body = handler.handleConstraintViolation(exception, request).getBody();

        assertNotNull(body);
        assertEquals(400, body.status());
        assertEquals("REQUEST_VALIDATION_FAILED", body.code());
        assertEquals(1, body.violations().size());
        assertEquals("customerId", body.violations().getFirst().field());
        assertEquals("Deprecated", body.violations().getFirst().code());
    }

    private ApiErrorFactory createFactory(boolean includeExceptionMessage, boolean includeCorrelationId, boolean includeTraceId) {
        ErrorUtilsProperties properties = new ErrorUtilsProperties();
        properties.setIncludeExceptionMessage(includeExceptionMessage);
        properties.setIncludeCorrelationId(includeCorrelationId);
        properties.setIncludeTraceId(includeTraceId);
        TraceContextResolver traceContextResolver = new DefaultTraceContextResolver(properties);
        ErrorMetadataSanitizer sanitizer = metadata -> metadata == null ?
                                                       Map.of() :
                                                       Map.of("secret", "masked");

        return new ApiErrorFactory(FIXED_CLOCK,
                                   new ApiErrorAssembler(),
                                   traceContextResolver,
                                   sanitizer,
                                   properties,
                                   List.of((apiError, context) -> new ApiError(apiError.timestamp(),
                                                                               apiError.status(),
                                                                               apiError.error(),
                                                                               apiError.code(),
                                                                               apiError.message(),
                                                                               apiError.path(),
                                                                               apiError.correlationId(),
                                                                               apiError.traceId(),
                                                                               apiError.violations(),
                                                                               Map.of("secret",
                                                                                      apiError.metadata().getOrDefault("secret", "missing"),
                                                                                      "method",
                                                                                      context.request().getMethod()))));
    }

    private MockHttpServletRequest request(String path) {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", path);
        request.addHeader("X-Correlation-Id", "corr-1");
        request.addHeader("X-Trace-Id", "trace-1");
        return request;
    }

    private enum TestErrorCode implements ErrorCode {
        CUSTOMER_NOT_FOUND(404, ErrorCategory.BUSINESS), TECHNICAL_ERROR(500, ErrorCategory.TECHNICAL);

        private final int status;
        private final ErrorCategory category;

        TestErrorCode(int status, ErrorCategory category) {
            this.status = status;
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
            return status;
        }

        @Override
        public ErrorCategory category() {
            return category;
        }
    }

    private static final class SampleController {
        @SuppressWarnings("unused")
        void create(SamplePayload payload) {
        }
    }

    private static final class SamplePayload {
        @SuppressWarnings("unused")
        private String name;
    }

    private static final class StubConstraintViolation implements ConstraintViolation<Object> {

        private final String path;
        private final Object invalidValue;
        private final String message;
        private final Annotation annotation;

        private StubConstraintViolation(String path, Object invalidValue, String message, Annotation annotation) {
            this.path = path;
            this.invalidValue = invalidValue;
            this.message = message;
            this.annotation = annotation;
        }

        @Override
        public String getMessage() {
            return message;
        }

        @Override
        public String getMessageTemplate() {
            return message;
        }

        @Override
        public Object getRootBean() {
            return null;
        }

        @Override
        public Class<Object> getRootBeanClass() {
            return Object.class;
        }

        @Override
        public Object getLeafBean() {
            return null;
        }

        @Override
        public Object[] getExecutableParameters() {
            return new Object[0];
        }

        @Override
        public Object getExecutableReturnValue() {
            return null;
        }

        @Override
        public Path getPropertyPath() {
            return new Path() {
                @Override
                public java.util.Iterator<Node> iterator() {
                    return List.<Path.Node>of().iterator();
                }

                @Override
                public String toString() {
                    return path;
                }
            };
        }

        @Override
        public Object getInvalidValue() {
            return invalidValue;
        }

        @Override
        public ConstraintDescriptor<?> getConstraintDescriptor() {
            return new ConstraintDescriptor<>() {
                @Override
                public Annotation getAnnotation() {
                    return annotation;
                }

                @Override
                public String getMessageTemplate() {
                    return message;
                }

                @Override
                public Set<Class<?>> getGroups() {
                    return Set.of();
                }

                @Override
                public Set<Class<? extends jakarta.validation.Payload>> getPayload() {
                    return Set.of();
                }

                @Override
                public jakarta.validation.ConstraintTarget getValidationAppliesTo() {
                    return null;
                }

                @Override
                public List<Class<? extends jakarta.validation.ConstraintValidator<Annotation, ?>>> getConstraintValidatorClasses() {
                    return List.of();
                }

                @Override
                public Map<String, Object> getAttributes() {
                    return Map.of();
                }

                @Override
                public Set<ConstraintDescriptor<?>> getComposingConstraints() {
                    return Set.of();
                }

                @Override
                public boolean isReportAsSingleViolation() {
                    return false;
                }

                @Override
                public jakarta.validation.metadata.ValidateUnwrappedValue getValueUnwrapping() {
                    return jakarta.validation.metadata.ValidateUnwrappedValue.DEFAULT;
                }

                @Override
                public <U> U unwrap(Class<U> type) {
                    throw new UnsupportedOperationException();
                }
            };
        }

        @Override
        public <U> U unwrap(Class<U> type) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String toString() {
            return path;
        }
    }

    private static final class SampleConstraintHolder {
        @Deprecated
        private String value;

        private static Annotation constraintAnnotation() {
            try {
                return SampleConstraintHolder.class.getDeclaredField("value").getDeclaredAnnotation(Deprecated.class);
            } catch (NoSuchFieldException ex) {
                throw new IllegalStateException(ex);
            }
        }
    }
}
