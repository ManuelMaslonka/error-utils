package com.maslonka.reservation.errorutils.spring.boot.autoconfigure;

import com.maslonka.reservation.errorutils.spring.web.ErrorUtilsProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(ErrorUtilsProperties.class)
@Import(ErrorUtilsBeanRegistrar.class)
public class ErrorUtilsAutoConfiguration {
}
