package org.faststats.controller;

import org.faststats.model.Layout;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@NullMarked
class SQLController {
    protected static final String COUNT_PROJECTS = statement("sql/query/count_projects.sql");
    protected static final String CREATE_PROJECT = statement("sql/create_project.sql");
    protected static final String DELETE_PROJECT = statement("sql/delete_project.sql");
    protected static final String GET_LAYOUT = statement("sql/query/get_layout.sql");
    protected static final String GET_PROJECT = statement("sql/query/get_project.sql");
    protected static final String GET_PROJECTS = statement("sql/query/get_projects.sql");
    protected static final String RENAME_PROJECT = statement("sql/rename_project.sql");
    protected static final String SLUG_USED = statement("sql/query/slug_used.sql");
    @Deprecated(forRemoval = true)
    protected static final String UPDATE_PROJECT = statement("sql/update_project.sql");
    protected static final String UPDATE_SLUG = statement("sql/update_slug.sql");

    protected final Connection connection;

    protected SQLController() {
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + new File("data", "saves.db"));
            executeUpdate(statement("sql/table/layouts.sql"));
            executeUpdate(statement("sql/table/metrics.sql"));
            executeUpdate(statement("sql/table/projects.sql"));
            executeUpdate(statement("sql/table/servers.sql"));
            executeUpdate(statement("sql/index/metrics.sql"));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect or setup database", e);
        }
    }

    protected List<Project> readProjects(ResultSet resultSet) throws SQLException {
        Project project;
        var projects = new ArrayList<Project>();
        while ((project = readProject(resultSet)) != null) projects.add(project);
        return projects;
    }

    protected @Nullable Project readProject(ResultSet resultSet) throws SQLException {
        if (!resultSet.next()) return null;
        var id = resultSet.getInt("id");
        var owner = resultSet.getString("owner");
        var name = resultSet.getString("name");
        var slug = resultSet.getString("slug");
        var isPrivate = resultSet.getBoolean("private");
        var icon = resultSet.getString("icon");
        var previewChart = resultSet.getString("preview_chart");
        var projectUrl = resultSet.getString("url");
        return new Project(name, owner, slug, id, isPrivate, null, icon, previewChart, projectUrl);
    }

    protected @Nullable Layout readLayout(ResultSet resultSet) throws SQLException {
        if (!resultSet.next()) return null;
        var charts = new HashMap<String, Layout.Options>();
        do {
            var name = resultSet.getString("name");
            var type = resultSet.getString("type");
            var color = resultSet.getString("color");
            var icon = resultSet.getString("icon");
            var size = resultSet.getInt("size");
            charts.put(name, new Layout.Options(name, type, color, icon, size));
        } while (resultSet.next());
        return new Layout(charts);
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
            var result = preparedStatement.executeUpdate();
            try (var generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) return generatedKeys.getInt(1);
            }
            return result;
        }
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

    @FunctionalInterface
    interface ThrowingFunction<T, R> {
        @Nullable
        R apply(T t) throws SQLException;

        static <T, R> ThrowingFunction<T, R> unchecked(ThrowingFunction<T, R> f) {
            return f;
        }
    }
}
