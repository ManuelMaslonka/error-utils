package com.maslonka.reservation.errorutils.spring.boot.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maslonka.reservation.errorutils.core.api.ApiErrorAssembler;
import com.maslonka.reservation.errorutils.spring.web.ApiErrorFactory;
import com.maslonka.reservation.errorutils.spring.web.advice.GlobalApiExceptionHandler;
import com.maslonka.reservation.errorutils.spring.web.security.GlobalSecurityExceptionHandler;
import com.maslonka.reservation.errorutils.spring.web.security.JsonAccessDeniedHandler;
import com.maslonka.reservation.errorutils.spring.web.security.JsonAuthenticationEntryPoint;
import com.maslonka.reservation.errorutils.spring.web.trace.TraceContextResolver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorUtilsAutoConfigurationTest {

    private final ApplicationContextRunner nonWebContextRunner =
            new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(ErrorUtilsAutoConfiguration.class));

    private final WebApplicationContextRunner webContextRunner =
            new WebApplicationContextRunner().withConfiguration(AutoConfigurations.of(ErrorUtilsAutoConfiguration.class))
                    .withBean(ObjectMapper.class, ObjectMapper::new);

    @Test
    @DisplayName("Should register core and web beans when starter runs in a servlet application")
    void shouldRegisterCoreAndWebBeansWhenStarterRunsInAServletApplication() {
        webContextRunner.run(context -> {
            assertThat(context).hasSingleBean(ApiErrorAssembler.class);
            assertThat(context).hasSingleBean(ApiErrorFactory.class);
            assertThat(context).hasSingleBean(TraceContextResolver.class);
            assertThat(context).hasSingleBean(GlobalApiExceptionHandler.class);
        });
    }

    @Test
    @DisplayName("Should register security beans when Spring Security is present on the classpath")
    void shouldRegisterSecurityBeansWhenSpringSecurityIsPresentOnTheClasspath() {
        webContextRunner.run(context -> {
            assertThat(context).hasSingleBean(GlobalSecurityExceptionHandler.class);
            assertThat(context).hasSingleBean(JsonAuthenticationEntryPoint.class);
            assertThat(context).hasSingleBean(JsonAccessDeniedHandler.class);
        });
    }

    @Test
    @DisplayName("Should register OpenAPI customizer when springdoc is present on the classpath")
    void shouldRegisterOpenApiCustomizerWhenSpringdocIsPresentOnTheClasspath() {
        webContextRunner.run(context -> assertThat(context).hasSingleBean(ErrorUtilsOpenApiCustomizer.class));
    }

    @Test
    @DisplayName("Should back off when the application is not a servlet web application")
    void shouldBackOffWhenTheApplicationIsNotAServletWebApplication() {
        nonWebContextRunner.run(context -> {
            assertThat(context).doesNotHaveBean(ApiErrorAssembler.class);
            assertThat(context).doesNotHaveBean(ApiErrorFactory.class);
            assertThat(context).doesNotHaveBean(GlobalApiExceptionHandler.class);
        });
    }

    @Test
    @DisplayName("Should skip optional security and OpenAPI beans when the corresponding dependencies are missing")
    void shouldSkipOptionalSecurityAndOpenApiBeansWhenTheCorrespondingDependenciesAreMissing() {
        webContextRunner.withClassLoader(new FilteredClassLoader("org.springframework.security", "org.springdoc.core.customizers", "io.swagger.v3.oas.models"))
                .run(context -> {
                    assertThat(context).hasSingleBean(ApiErrorAssembler.class);
                    assertThat(context).hasSingleBean(ApiErrorFactory.class);
                    assertThat(context).doesNotHaveBean(GlobalSecurityExceptionHandler.class);
                    assertThat(context).doesNotHaveBean(JsonAuthenticationEntryPoint.class);
                    assertThat(context).doesNotHaveBean(JsonAccessDeniedHandler.class);
                    assertThat(context).doesNotHaveBean(ErrorUtilsOpenApiCustomizer.class);
                });
    }

    @Test
    @DisplayName("Should back off from default assembler registration when the application already provides one")
    void shouldBackOffFromDefaultAssemblerRegistrationWhenTheApplicationAlreadyProvidesOne() {
        webContextRunner.withBean("customApiErrorAssembler", ApiErrorAssembler.class, ApiErrorAssembler::new).run(context -> {
            assertThat(context).hasSingleBean(ApiErrorAssembler.class);
            assertThat(context).hasBean("customApiErrorAssembler");
            assertThat(context).doesNotHaveBean("apiErrorAssembler");
            assertThat(context).hasSingleBean(ApiErrorFactory.class);
        });
    }
}
