package com.maslonka.reservation.errorutils.spring.web;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

class SpringWebMvcArchitectureTest {

    private static final String ROOT_PACKAGE = "com.maslonka.reservation.errorutils.spring.web";

    @Test
    @DisplayName("Should not depend on validation module when spring webmvc classes are analyzed")
    void shouldNotDependOnValidationModuleWhenSpringWebMvcClassesAreAnalyzed() {
        noClasses().should()
                .dependOnClassesThat()
                .resideInAnyPackage("com.maslonka.reservation.errorutils.validation..")
                .allowEmptyShould(true)
                .check(importedClasses());
    }

    @Test
    @DisplayName("Should not depend on starter package when spring webmvc classes are analyzed")
    void shouldNotDependOnStarterPackageWhenSpringWebMvcClassesAreAnalyzed() {
        noClasses().should()
                .dependOnClassesThat()
                .resideInAnyPackage("com.maslonka.reservation.errorutils.spring.boot.autoconfigure..")
                .allowEmptyShould(true)
                .check(importedClasses());
    }

    @Test
    @DisplayName("Should avoid package cycles when spring webmvc classes are analyzed")
    void shouldAvoidPackageCyclesWhenSpringWebMvcClassesAreAnalyzed() {
        slices().matching(ROOT_PACKAGE + ".(*)..").should().beFreeOfCycles().check(importedClasses());
    }

    private JavaClasses importedClasses() {
        return new ClassFileImporter().withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS).importPackages(ROOT_PACKAGE);
    }
}
