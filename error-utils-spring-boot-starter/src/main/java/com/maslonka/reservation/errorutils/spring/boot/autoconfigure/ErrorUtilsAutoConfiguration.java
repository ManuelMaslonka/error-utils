package com.maslonka.reservation.errorutils.spring.boot.autoconfigure;

import com.maslonka.reservation.errorutils.spring.web.ErrorUtilsProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

/**
 * Auto-configuration entry point for the servlet-based error-utils integration.
 *
 * <p>The configuration enables {@link ErrorUtilsProperties} binding and imports the registrar that
 * contributes the default beans used by the library.</p>
 *
 * <p>This is the main runtime integration point when consumers add the
 * {@code error-utils-spring-boot-starter} dependency. The actual bean graph is created by
 * {@link ErrorUtilsBeanRegistrar} and includes types from:
 *
 * <ul>
 *     <li>{@code error-utils-core} for payload assembly</li>
 *     <li>{@code error-utils-spring-webmvc} for MVC advice, tracing, and security integration</li>
 *     <li>{@code error-utils-openapi} when springdoc is available</li>
 * </ul>
 *
 * <p>See also {@link ErrorUtilsOpenApiCustomizer} and
 * {@link com.maslonka.reservation.errorutils.spring.web.ErrorUtilsProperties}.</p>
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(ErrorUtilsProperties.class)
@Import(ErrorUtilsBeanRegistrar.class)
public class ErrorUtilsAutoConfiguration {}
