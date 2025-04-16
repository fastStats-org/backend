package org.faststats.route.project.settings;

import org.faststats.BaseTest;
import org.faststats.model.Project;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.sql.SQLException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
public class CheckSlugTest extends BaseTest {
    @Test
    public void testSlug() throws SQLException {
        var name = "Test Project";

        var slug = database.generateUniqueSlug(name);
        assertFalse(database.isSlugUsed(slug), "slug should not be used");

        var project = database.createProject(name, "owner", false);
        assertTrue(database.isSlugUsed(project.slug()), "slug should be used");

        var newSlug = "test";
        assertTrue(database.setProjectSlug(project.id(), newSlug, project.ownerId()), "project slug should be updated");
        assertFalse(database.isSlugUsed(project.slug()), "slug should not be used");
        assertTrue(database.isSlugUsed(newSlug), "slug should be used");
    }

    @ParameterizedTest
    @MethodSource("invalidSlugs")
    public void testInvalidSlug(String value) {
        assertFalse(Project.isValidSlug(value));
    }

    @ParameterizedTest
    @MethodSource("validSlugs")
    public void testValidSlug(String value) {
        assertTrue(Project.isValidSlug(value));
    }

    private static Stream<Arguments> invalidSlugs() {
        return Stream.of(
                Arguments.argumentSet("too short", "ab"),
                Arguments.argumentSet("too long", "a".repeat(33)),
                Arguments.argumentSet("spaces", "invalid slug"),
                Arguments.argumentSet("uppercase", "UPPERCASE"),
                Arguments.argumentSet("special characters", "special$chars"),
                Arguments.argumentSet("starts with dash", "-starts-with-dash"),
                Arguments.argumentSet("ends with dash", "ends-with-dash-"),
                Arguments.argumentSet("double dash", "double--dash")
        );
    }

    private static Stream<Arguments> validSlugs() {
        return Stream.of(
                Arguments.of("test"),
                Arguments.of("abc"),
                Arguments.of("a".repeat(32)),
                Arguments.of("project-1234567890"),
                Arguments.of("test-project-1"),
                Arguments.of("test-project-1-copy"),
                Arguments.of("slug-with-multiple-dashes")
        );
    }
}
