package com.maslonka.reservation.errorutils.spring.boot.autoconfigure;

import com.maslonka.reservation.errorutils.core.api.ApiErrorAssembler;
import com.maslonka.reservation.errorutils.core.spi.ErrorMetadataSanitizer;
import com.maslonka.reservation.errorutils.spring.web.ApiErrorFactory;
import com.maslonka.reservation.errorutils.spring.web.advice.GlobalApiExceptionHandler;
import com.maslonka.reservation.errorutils.spring.web.metadata.NoopErrorMetadataSanitizer;
import com.maslonka.reservation.errorutils.spring.web.security.GlobalSecurityExceptionHandler;
import com.maslonka.reservation.errorutils.spring.web.security.JsonAccessDeniedHandler;
import com.maslonka.reservation.errorutils.spring.web.security.JsonAuthenticationEntryPoint;
import com.maslonka.reservation.errorutils.spring.web.trace.DefaultTraceContextResolver;
import com.maslonka.reservation.errorutils.spring.web.trace.TraceContextResolver;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

import java.time.Clock;

public class ErrorUtilsBeanRegistrar implements ImportBeanDefinitionRegistrar {

    private static final String AUTH_ENTRY_POINT = "org.springframework.security.web.AuthenticationEntryPoint";
    private static final String ACCESS_DENIED_HANDLER = "org.springframework.security.web.access.AccessDeniedHandler";
    private static final String AUTHENTICATION_EXCEPTION = "org.springframework.security.core.AuthenticationException";
    private static final String ACCESS_DENIED_EXCEPTION = "org.springframework.security.access.AccessDeniedException";
    private static final String OPENAPI_CUSTOMIZER = "org.springdoc.core.customizers.OpenApiCustomizer";
    private static final String OPENAPI_MODEL = "io.swagger.v3.oas.models.OpenAPI";

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        registerClock(registry);
        registerIfMissing(registry, "apiErrorAssembler", ApiErrorAssembler.class, bean(ApiErrorAssembler.class));
        registerIfMissing(registry, "errorMetadataSanitizer", ErrorMetadataSanitizer.class, bean(NoopErrorMetadataSanitizer.class));
        registerIfMissing(registry, "traceContextResolver", TraceContextResolver.class, bean(DefaultTraceContextResolver.class));
        registerIfMissing(registry, "apiErrorFactory", ApiErrorFactory.class, bean(ApiErrorFactory.class));
        registerIfMissing(registry, "globalApiExceptionHandler", GlobalApiExceptionHandler.class, bean(GlobalApiExceptionHandler.class));

        if (isSecurityCorePresent()) {
            registerIfMissing(registry, "globalSecurityExceptionHandler", GlobalSecurityExceptionHandler.class, bean(GlobalSecurityExceptionHandler.class));
        }

        if (isSecurityWebPresent()) {
            registerIfMissing(registry, "errorUtilsAuthenticationEntryPoint", JsonAuthenticationEntryPoint.class, bean(JsonAuthenticationEntryPoint.class));
            registerIfMissing(registry, "errorUtilsAccessDeniedHandler", JsonAccessDeniedHandler.class, bean(JsonAccessDeniedHandler.class));
        }

        if (isOpenApiPresent()) {
            registerIfMissing(registry, "errorUtilsOpenApiCustomizer", ErrorUtilsOpenApiCustomizer.class, bean(ErrorUtilsOpenApiCustomizer.class));
        }
    }

    private void registerClock(BeanDefinitionRegistry registry) {
        if (!registry.containsBeanDefinition("errorUtilsClock")) {
            AbstractBeanDefinition def = BeanDefinitionBuilder.genericBeanDefinition(Clock.class).getBeanDefinition();
            def.setInstanceSupplier(Clock::systemUTC);
            registry.registerBeanDefinition("errorUtilsClock", def);
        }
    }

    private void registerIfMissing(BeanDefinitionRegistry registry, String beanName, Class<?> beanType, AbstractBeanDefinition definition) {
        if (registry.containsBeanDefinition(beanName)) {
            return;
        }

        if (registry instanceof ListableBeanFactory listableBeanFactory) {
            String[] existing =
                    org.springframework.beans.factory.BeanFactoryUtils.beanNamesForTypeIncludingAncestors(listableBeanFactory, beanType, true, false);
            if (existing.length > 0) {
                return;
            }
        }

        registry.registerBeanDefinition(beanName, definition);
    }

    private AbstractBeanDefinition bean(Class<?> type) {
        AbstractBeanDefinition definition = BeanDefinitionBuilder.genericBeanDefinition(type).getBeanDefinition();
        definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);
        return definition;
    }

    private boolean isSecurityCorePresent() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        return ClassUtils.isPresent(AUTHENTICATION_EXCEPTION, loader) && ClassUtils.isPresent(ACCESS_DENIED_EXCEPTION, loader);
    }

    private boolean isSecurityWebPresent() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        return ClassUtils.isPresent(AUTH_ENTRY_POINT, loader) && ClassUtils.isPresent(ACCESS_DENIED_HANDLER, loader);
    }

    private boolean isOpenApiPresent() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        return ClassUtils.isPresent(OPENAPI_CUSTOMIZER, loader) && ClassUtils.isPresent(OPENAPI_MODEL, loader);
    }
}
