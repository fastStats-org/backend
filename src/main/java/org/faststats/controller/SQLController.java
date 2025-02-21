package org.faststats.controller;

import org.jspecify.annotations.Nullable;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLController {
    private final Connection connection;

    public SQLController() throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + new File("data", "saves.db"));
        createProjectsTable();
        createMetricsTable();
        createServersTable();
        createLayoutTable();
    }

    private void createServersTable() throws SQLException {
        executeUpdate("""
                CREATE TABLE IF NOT EXIST servers (
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  uuid TEXT NOT NULL UNIQUE,
                )""");
    }

    private void createMetricsTable() throws SQLException {
        executeUpdate("""
                CREATE TABLE IF NOT EXIST metrics (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                type TEXT NOT NULL,
                )
                """);
    }

    private void createProjectsTable() throws SQLException {
        executeUpdate("""
                CREATE TABLE IF NOT EXIST projects (
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  owner TEXT NOT NULL,
                  name TEXT NOT NULL,
                  slug TEXT NOT NULL UNIQUE,
                  private BOOLEAN NOT NULL DEFAULT 0,
                  FOREIGN KEY (project_id) REFERENCES layout(id) ON DELETE CASCADE,
                  UNIQUE (owner, name)
                )""");
    }

    private void createLayoutTable() throws SQLException {
        executeUpdate("""
                CREATE TABLE IF NOT EXIST layout (
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  
                )""");
    }

    @SuppressWarnings("SqlSourceToSinkFlow")
    protected <T> @Nullable T executeQuery(String query, ThrowingFunction<ResultSet, T> mapper, @Nullable Object... parameters) throws SQLException {
        try (var preparedStatement = connection.prepareStatement(query)) {
            for (var i = 0; i < parameters.length; i++)
                preparedStatement.setObject(i + 1, parameters[i]);
            try (var resultSet = preparedStatement.executeQuery()) {
                return ThrowingFunction.unchecked(mapper).apply(resultSet);
            }
        }
    }

    @SuppressWarnings("SqlSourceToSinkFlow")
    protected int executeUpdate(String query, @Nullable Object... parameters) throws SQLException {
        try (var preparedStatement = connection.prepareStatement(query)) {
            for (var i = 0; i < parameters.length; i++)
                preparedStatement.setObject(i + 1, parameters[i]);
            return preparedStatement.executeUpdate();
        }
    }

    @FunctionalInterface
    protected interface ThrowingFunction<T, R> {
        @Nullable
        R apply(T t) throws SQLException;

        static <T, R> ThrowingFunction<T, R> unchecked(ThrowingFunction<T, R> f) {
            return f;
        }
    }
}
