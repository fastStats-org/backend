package org.faststats.route.project;

import org.faststats.BaseTest;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
public class DeleteProjectTest extends BaseTest {
    @Test
    public void testDeleteProjectWithOwnerId() throws SQLException {
        var project = database.createProject("Delete Me", "unknown", true);
        assertTrue(database.deleteProject(project.id(), project.ownerId()), "project should be deleted");
        assertNull(database.getProject(project.slug(), project.ownerId()), "project should not exist anymore");
    }

    @Test
    public void testDeleteProjectWithoutOwnerId() throws SQLException {
        var project = database.createProject("Delete Me", "unknown", false);
        assertTrue(database.deleteProject(project.id(), null), "project should be deleted");
        assertNull(database.getProject(project.slug(), null), "project should not exist anymore");
    }
}
