package org.faststats;

import org.faststats.controller.DatabaseController;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

import java.io.File;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
public class BaseTest {
    private static final File databaseFile = new File("test.db");
    protected DatabaseController database = new DatabaseController(databaseFile);

    @AfterAll
    @BeforeAll
    public static void cleanup() {
        deleteDatabase();
    }

    @AfterEach
    public void cleanupDatabase() throws SQLException {
        database.close();
        deleteDatabase();
        database = new DatabaseController(databaseFile);
    }

    private static void deleteDatabase() {
        assertTrue(!databaseFile.exists() | databaseFile.delete(), "Failed to delete database file");
    }
}