package com.maslonka.reservation.errorutils.validation;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ValidationArchitectureTest {

    private static final String ROOT_PACKAGE = "com.maslonka.reservation.errorutils.validation";

    @Test
    @DisplayName("Should stay outside web frameworks when validation module classes are analyzed")
    void shouldStayOutsideWebFrameworksWhenValidationModuleClassesAreAnalyzed() {
        noClasses().should()
                .dependOnClassesThat()
                .resideInAnyPackage("org.springframework..", "jakarta.servlet..", "org.springdoc..")
                .allowEmptyShould(true)
                .check(importedClasses());
    }

    @Test
    @DisplayName("Should keep model package independent when validation module classes are analyzed")
    void shouldKeepModelPackageIndependentWhenValidationModuleClassesAreAnalyzed() {
        noClasses().that()
                .resideInAnyPackage(ROOT_PACKAGE + ".model..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(ROOT_PACKAGE + ".api..", ROOT_PACKAGE + ".pipeline..", "com.maslonka.reservation.errorutils.spring..")
                .allowEmptyShould(true)
                .check(importedClasses());
    }

    private JavaClasses importedClasses() {
        return new ClassFileImporter().withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS).importPackages(ROOT_PACKAGE);
    }
}
