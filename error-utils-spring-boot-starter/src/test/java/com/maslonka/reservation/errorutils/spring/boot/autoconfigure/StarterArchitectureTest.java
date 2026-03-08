package com.maslonka.reservation.errorutils.spring.boot.autoconfigure;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class StarterArchitectureTest {

    private static final String ROOT_PACKAGE = "com.maslonka.reservation.errorutils.spring.boot.autoconfigure";

    @Test
    @DisplayName("Should avoid direct validation dependency when starter classes are analyzed")
    void shouldAvoidDirectValidationDependencyWhenStarterClassesAreAnalyzed() {
        noClasses().should()
                .dependOnClassesThat()
                .resideInAnyPackage("com.maslonka.reservation.errorutils.validation..")
                .allowEmptyShould(true)
                .check(importedClasses());
    }

    @Test
    @DisplayName("Should avoid direct servlet and controller dependencies when starter classes are analyzed")
    void shouldAvoidDirectServletAndControllerDependenciesWhenStarterClassesAreAnalyzed() {
        noClasses().should()
                .dependOnClassesThat()
                .resideInAnyPackage("jakarta.servlet..", "org.springframework.web.bind.annotation..")
                .allowEmptyShould(true)
                .check(importedClasses());
    }

    @Test
    @DisplayName("Should isolate openapi dependencies when starter classes are analyzed")
    void shouldIsolateOpenApiDependenciesWhenStarterClassesAreAnalyzed() {
        noClasses().that()
                .haveSimpleNameNotContaining("OpenApiCustomizer")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage("org.springdoc..", "io.swagger.v3..")
                .allowEmptyShould(true)
                .check(importedClasses());
    }

    private JavaClasses importedClasses() {
        return new ClassFileImporter().withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS).importPackages(ROOT_PACKAGE);
    }
}
