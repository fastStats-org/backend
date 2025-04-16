package org.faststats.route.projects;

import org.faststats.BaseTest;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
public class ListProjectsTest extends BaseTest {
    private final String testUser = "testUser";

    @Test
    public void testListPublicProjects() throws SQLException {
        var projects = database.getProjects(0, 100, null, true);
        assertFalse(projects.isEmpty(), "projects should not be empty");
        projects.forEach(project -> assertFalse(project.isPrivate(), "project should be public"));
    }

    @Test
    public void testListPrivateProjects() throws SQLException {
        var projects = database.getProjects(0, 100, null, false);
        assertFalse(projects.isEmpty(), "projects should not be empty");
        projects.forEach(project -> assertTrue(project.isPrivate(), "project should be private"));
    }

    @Test
    public void testListPublicProjectsFromOwner() throws SQLException {
        var projects = database.getProjects(0, 100, testUser, true);
        assertFalse(projects.isEmpty(), "projects should not be empty");
        projects.forEach(project -> {
            assertFalse(project.isPrivate(), "project should be public");
            assertEquals(testUser, project.ownerId(), "project owner should match");
        });
    }

    @Test
    public void testListPrivateProjectsFromOwner() throws SQLException {
        var projects = database.getProjects(0, 100, testUser, false);
        assertFalse(projects.isEmpty(), "projects should not be empty");
        projects.forEach(project -> {
            assertTrue(project.isPrivate(), "project should be private");
            assertEquals(testUser, project.ownerId(), "project owner should match");
        });
    }

    @Test
    public void testListAllProjectsFromOwner() throws SQLException {
        var projects = database.getProjects(0, 100, testUser, null);
        projects.forEach(project -> assertEquals(testUser, project.ownerId(), "project owner should match"));
    }

    @BeforeEach
    public void createSampleProjects() throws SQLException {
        database.createProject("Public Project 1", testUser, false);
        database.createProject("Public Project 2", testUser, false);
        database.createProject("Public Project 3", "anotherUser", false);

        database.createProject("Private Project 1", testUser, true);
        database.createProject("Private Project 2", testUser, true);
        database.createProject("Private Project 3", "anotherUser", true);
    }
}