package ch.puzzle.eft.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.Architectures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.testcontainers.shaded.org.apache.commons.lang3.StringUtils;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

class ExamFeedbackToolArchitectureTest {

    private static final String ROOT_PACKAGE_NAME = "ch.puzzle.eft";

    @ParameterizedTest
    @ValueSource(strings = {"controller", "service", "converter", "mapper", "repository", "dto", "exception"})
    void classesInRightPackages(String passedName) {
        JavaClasses importedClasses = new ClassFileImporter()
                .importPackages(ROOT_PACKAGE_NAME);

        ArchRule rule = classes()
                .that()
                .haveSimpleNameEndingWith(StringUtils
                        .capitalize(passedName))
                .and()
                .areTopLevelClasses()
                .should()
                .resideInAPackage(ROOT_PACKAGE_NAME + "." + passedName + "..")
                .allowEmptyShould(true);

        rule
                .check(importedClasses);
    }

    @Test
    void serviceLayerCheck() {
        JavaClasses importedClasses = getMainSourceClasses();
        Architectures.LayeredArchitecture layeredArchitecture = layeredArchitecture()
                .consideringAllDependencies()
                .layer("Controller")
                .definedBy("..controller..")
                .layer("BusinessService")
                .definedBy("..service..")

                .whereLayer("Controller")
                .mayNotBeAccessedByAnyLayer()
                .whereLayer("BusinessService")
                .mayOnlyBeAccessedByLayers("Controller", "BusinessService");

        layeredArchitecture
                .check(importedClasses);
    }

    private static JavaClasses getMainSourceClasses() {
        return new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .importPackages(ROOT_PACKAGE_NAME);
    }
}
