package org.faststats.route.project.settings;

import org.faststats.BaseTest;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
public class SetSlugTest extends BaseTest {
    @Test
    public void testSetSlug() throws SQLException {
        var project = database.createProject("Test Project", "test", true);

        var newSlug = "new-slug";
        assertTrue(database.setProjectSlug(project.id(), newSlug, null), "project slug should be updated");

        var updated = database.getProject(newSlug, project.ownerId());
        assertNotNull(updated, "project should not be null");
        assertEquals(newSlug, updated.slug(), "project slug should match");

        assertFalse(database.isSlugUsed(project.slug()), "original slug should not be used anymore");
        assertTrue(database.isSlugUsed(updated.slug()), "new slug should be used");
    }
}
