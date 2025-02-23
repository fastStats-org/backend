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
public class SQLController {
    private static final String COUNT_PROJECTS = statement("sql/query/count_projects.sql");
    private static final String CREATE_PROJECT = statement("sql/create_project.sql");
    private static final String DELETE_PROJECT = statement("sql/delete_project.sql");
    private static final String GET_LAYOUT = statement("sql/query/get_layout.sql");
    private static final String GET_PROJECT = statement("sql/query/get_project.sql");
    private static final String GET_PROJECTS = statement("sql/query/get_projects.sql");
    private static final String SLUG_USED = statement("sql/query/slug_used.sql");

    private final Connection connection;

    public SQLController() {
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + new File("data", "saves.db"));
            createLayoutsTable();
            createMetricsTable();
            createProjectsTable();
            createServersTable();
            createIndex();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect or setup database", e);
        }
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

    public @Nullable Project createProject(String name, String owner, boolean isPrivate) throws SQLException {
        var slug = generateUniqueSlug(name);
        var id = executeUpdate(CREATE_PROJECT, owner, name, slug, isPrivate);
        return new Project(name, owner, slug, id, isPrivate, null, null, null, null);
    }

    public @Nullable Project getProject(String slug, @Nullable String owner) throws SQLException {
        var project = executeQuery(GET_PROJECT, this::getProject, slug, owner);
        return project != null ? project.withLayout(getLayout(project.id())) : null;
    }

    public @Nullable Layout getLayout(int projectId) throws SQLException {
        return executeQuery(GET_LAYOUT, this::getLayout, projectId);
    }

    public boolean deleteProject(int projectId, @Nullable String ownerId) throws SQLException {
        return executeUpdate(DELETE_PROJECT, projectId, ownerId) > 0;
    }

    public List<Project> getProjects(int offset, int limit, @Nullable String ownerId, @Nullable Boolean publicOnly) throws SQLException {
        var projects = executeQuery(GET_PROJECTS, this::getProjects, ownerId, publicOnly, limit, offset);
        return projects != null ? projects : List.of();
    }

    private List<Project> getProjects(ResultSet resultSet) throws SQLException {
        Project project;
        var projects = new ArrayList<Project>();
        while ((project = getProject(resultSet)) != null) projects.add(project);
        return projects;
    }

    private String generateUniqueSlug(String name) throws SQLException {
        var base = name.replaceAll("([A-Z])", "-$1").replaceAll("[^a-zA-Z0-9]", "-")
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

    private @Nullable Project getProject(ResultSet resultSet) throws SQLException {
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

    private @Nullable Layout getLayout(ResultSet resultSet) throws SQLException {
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
            var result = preparedStatement.executeUpdate();
            try (var generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) return generatedKeys.getInt(1);
            }
            return result;
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
