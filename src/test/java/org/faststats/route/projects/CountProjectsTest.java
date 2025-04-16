package org.faststats.route.projects;

import org.faststats.BaseTest;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@NullMarked
public class CountProjectsTest extends BaseTest {
    private final String ownerId = "test";

    @Test
    public void testCountProjectsFromOwner() throws SQLException {
        assertEquals(3, database.countProjects(ownerId));
    }

    @Test
    public void testCountAllProjects() throws SQLException {
        assertEquals(6, database.countProjects(null));
    }

    @BeforeEach
    public void createSampleProjects() throws SQLException {
        database.createProject("Test Project 1", ownerId, false);
        database.createProject("Test Project 2", ownerId, false);
        database.createProject("Test Project 3", ownerId, true);

        database.createProject("Other Project 1", "other", false);
        database.createProject("Other Project 2", "other", true);
        database.createProject("Another Project", "another", false);
    }
}
