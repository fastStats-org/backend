package org.faststats.controller;

import org.faststats.model.Project;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

@NullMarked
public class SQLController {
    private static final String COUNT_PROJECTS = statement("sql/query/count_projects.sql");
    private static final String CREATE_PROJECT = statement("sql/create_project.sql");
    private static final String SLUG_USED = statement("sql/query/slug_used.sql");

    private final Connection connection;

    public SQLController() {
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + new File("data", "saves.db"));
            createLayoutOptionsTable();
            createLayoutsTable();
            createMetricsTable();
            createProjectsTable();
            createServersTable();
            createIndex();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect or setup database", e);
        }
    }

    private void createLayoutOptionsTable() throws SQLException {
        executeUpdate(statement("sql/table/layout_options.sql"));
    }

    private void createLayoutsTable() throws SQLException {
        executeUpdate(statement("sql/table/layouts.sql"));
    }

    private void createMetricsTable() throws SQLException {
        executeUpdate(statement("sql/table/metrics.sql"));
    }

    private void createProjectsTable() throws SQLException {
        executeUpdate(statement("sql/table/projects.sql"));
    }

    private void createServersTable() throws SQLException {
        executeUpdate(statement("sql/table/servers.sql"));
    }

    private void createIndex() throws SQLException {
        executeUpdate(statement("sql/index/metrics.sql"));
    }

    public @Nullable Project createProject(String name, String ownerId, boolean isPrivate) throws SQLException {
        var slug = generateUniqueSlug(name);
        var id = executeUpdate(CREATE_PROJECT, ownerId, name, slug, isPrivate);
        return new Project(name, ownerId, slug, id, isPrivate, null, null, null, null);
    }

    private String generateUniqueSlug(String name) throws SQLException {
        var base = name.replaceAll("([A-Z0-9])", "-$1").replaceAll("[^a-zA-Z0-9-]", "-")
                .replaceAll("-+", "-").replaceAll("^-|-$", "").toLowerCase();
        var unique = base;
        var counter = 1;
        while (isSlugUsed(unique)) unique = base + "-" + counter++;
        return unique;
    }

    public boolean isSlugUsed(String slug) throws SQLException {
        return Boolean.TRUE.equals(executeQuery(SLUG_USED, resultSet ->
                resultSet.next() && resultSet.getBoolean(1), slug));
    }

    public long countProjects(@Nullable String ownerId) throws SQLException {
        var projects = executeQuery(COUNT_PROJECTS, resultSet -> resultSet.next() ? resultSet.getLong(1) : 0, ownerId);
        return projects != null ? projects : 0;
    }

    private static String statement(String file) {
        try (var resource = SQLController.class.getClassLoader().getResourceAsStream(file)) {
            if (resource == null) throw new FileNotFoundException("Resource not found: " + file);
            try (var reader = new BufferedReader(new InputStreamReader(resource, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
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
        try (var preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
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
