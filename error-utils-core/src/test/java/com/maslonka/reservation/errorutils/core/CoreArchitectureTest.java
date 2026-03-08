package com.maslonka.reservation.errorutils.core;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

class CoreArchitectureTest {

    private static final String ROOT_PACKAGE = "com.maslonka.reservation.errorutils.core";

    @Test
    @DisplayName("Should remain framework free when core module classes are analyzed")
    void shouldRemainFrameworkFreeWhenCoreModuleClassesAreAnalyzed() {
        noClasses().should()
                .dependOnClassesThat()
                .resideInAnyPackage("org.springframework..", "jakarta.servlet..", "jakarta.validation..", "org.springdoc..")
                .allowEmptyShould(true)
                .check(importedClasses());
    }

    @Test
    @DisplayName("Should avoid package cycles when core module classes are analyzed")
    void shouldAvoidPackageCyclesWhenCoreModuleClassesAreAnalyzed() {
        slices().matching(ROOT_PACKAGE + ".(*)..").should().beFreeOfCycles().check(importedClasses());
    }

    private JavaClasses importedClasses() {
        return new ClassFileImporter().withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS).importPackages(ROOT_PACKAGE);
    }
}
