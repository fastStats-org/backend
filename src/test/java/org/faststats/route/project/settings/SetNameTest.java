package org.faststats.route.project.settings;

import org.faststats.BaseTest;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
public class SetNameTest extends BaseTest {
    private final String originalName = "original name";
    private final String newName = "updated name";
    private final String ownerId = "owner";

    @Test
    public void testSetNameWithOwnerId() throws SQLException {
        var project = database.createProject(originalName, ownerId, true);
        assertTrue(database.setProjectName(project.id(), newName, ownerId));
        var updated = database.getProject(project.slug(), ownerId);
        assertNotNull(updated, "project should not be null");
        assertEquals(newName, updated.name(), "project name doesn't match");
        assertNotEquals(project, updated, "projects should not match");
    }

    @Test
    public void testSetNameWithoutOwnerId() throws SQLException {
        var project = database.createProject(originalName, ownerId, true);
        assertTrue(database.setProjectName(project.id(), newName, null));
        var updated = database.getProject(project.slug(), ownerId);
        assertNotNull(updated, "project should not be null");
        assertEquals(newName, updated.name(), "project name doesn't match");
        assertNotEquals(project, updated, "projects should not match");
    }
}
