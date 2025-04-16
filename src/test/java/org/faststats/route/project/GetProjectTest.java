package org.faststats.route.project;

import org.faststats.BaseTest;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@NullMarked
public class GetProjectTest extends BaseTest {
    @Test
    public void testGetProject() throws SQLException {
        var project = database.createProject("backend", "FastStats", true);
        var get = database.getProject(project.slug(), project.ownerId());
        assertEquals(project, get, "projects should match");
    }

    @Test
    public void testGetProjectWithoutOwner() throws SQLException {
        var project = database.createProject("backend", "FastStats", true);
        var get = database.getProject(project.slug(), null);
        assertEquals(project, get, "projects should match");
    }

    @Test
    public void testGetPrivateProjectInvalidOwner() throws SQLException {
        var project = database.createProject("backend", "FastStats", true);
        var get = database.getProject(project.slug(), "WrongOwner");
        assertNull(get, "project should not be found");
    }

    @Test
    public void testGetPublicProjectInvalidOwner() throws SQLException {
        var project = database.createProject("backend", "FastStats", false);
        var get = database.getProject(project.slug(), "WrongOwner");
        assertNotNull(get, "project should be found");
        assertEquals(project, get, "projects should match");
    }
}
