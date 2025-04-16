package org.faststats.route.project;

import org.faststats.BaseTest;
import org.faststats.model.Project;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.sql.SQLException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
public class CreateProjectTest extends BaseTest {
    @ParameterizedTest
    @MethodSource("projectArguments")
    public void testCreateProject(String name, String ownerId, boolean isPrivate) throws SQLException {
        var project = database.createProject(name, ownerId, isPrivate);

        assertEquals(name, project.name(), "project name doesn't match");
        assertEquals(ownerId, project.ownerId(), "project owner doesn't match");
        assertEquals(isPrivate, project.isPrivate(), "project visibility doesn't match");
        assertTrue(Project.isValidSlug(project.slug()), "invalid slug");
        assertNull(project.layout(), "layout should be null");
        assertNull(project.icon(), "icon should be null");
        assertNull(project.previewChart(), "preview chart should be null");
        assertNull(project.projectUrl(), "project url should be null");
    }

    public static Stream<Arguments> projectArguments() {
        return Stream.of(
                Arguments.of("test?)(/(", "test", true),
                Arguments.of("dev-project", "dev", true),
                Arguments.of("demo-project", "owner", false),
                Arguments.of("my Project", "user1", true),
                Arguments.of("Project@123", "user2", false),
                Arguments.of("analytics-2025", "analytics_team", true)
        );
    }
}
