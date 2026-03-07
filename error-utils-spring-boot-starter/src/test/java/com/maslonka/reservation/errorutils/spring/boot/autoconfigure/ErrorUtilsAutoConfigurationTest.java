package com.maslonka.reservation.errorutils.spring.boot.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maslonka.reservation.errorutils.core.api.ApiErrorAssembler;
import com.maslonka.reservation.errorutils.spring.web.ApiErrorFactory;
import com.maslonka.reservation.errorutils.spring.web.advice.GlobalApiExceptionHandler;
import com.maslonka.reservation.errorutils.spring.web.security.GlobalSecurityExceptionHandler;
import com.maslonka.reservation.errorutils.spring.web.security.JsonAccessDeniedHandler;
import com.maslonka.reservation.errorutils.spring.web.security.JsonAuthenticationEntryPoint;
import com.maslonka.reservation.errorutils.spring.web.trace.TraceContextResolver;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorUtilsAutoConfigurationTest {

    private final WebApplicationContextRunner contextRunner =
            new WebApplicationContextRunner().withConfiguration(AutoConfigurations.of(ErrorUtilsAutoConfiguration.class))
                    .withBean(ObjectMapper.class, ObjectMapper::new);

    @Test
    void registersCoreAndWebBeans() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(ApiErrorAssembler.class);
            assertThat(context).hasSingleBean(ApiErrorFactory.class);
            assertThat(context).hasSingleBean(TraceContextResolver.class);
            assertThat(context).hasSingleBean(GlobalApiExceptionHandler.class);
        });
    }

    @Test
    void registersSecurityBeansWhenSecurityIsPresent() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(GlobalSecurityExceptionHandler.class);
            assertThat(context).hasSingleBean(JsonAuthenticationEntryPoint.class);
            assertThat(context).hasSingleBean(JsonAccessDeniedHandler.class);
        });
    }
}
