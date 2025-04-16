package org.faststats.controller;

import com.google.gson.JsonParser;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NullMarked
class SQLController implements AutoCloseable {
    protected static final String COUNT_PROJECTS = statement("sql/query/count_projects.sql");
    protected static final String CREATE_CHART = statement("sql/update/create_chart.sql");
    protected static final String CREATE_PROJECT = statement("sql/update/create_project.sql");
    protected static final String DELETE_PROJECT = statement("sql/update/delete_project.sql");
    protected static final String DELETE_CHART = statement("sql/update/delete_chart.sql");
    protected static final String GET_LAYOUT = statement("sql/query/get_layout.sql");
    protected static final String GET_PROJECT = statement("sql/query/get_project.sql");
    protected static final String GET_PROJECTS = statement("sql/query/get_projects.sql");
    protected static final String GET_SERVER_ID = statement("sql/query/get_server_id.sql");
    protected static final String INSERT_METRIC = statement("sql/update/insert_metric.sql");
    protected static final String SET_CHART_DIMENSIONS = statement("sql/update/set_chart_dimensions.sql");
    protected static final String SET_CHART_ICON = statement("sql/update/set_chart_icon.sql");
    protected static final String SET_CHART_ID = statement("sql/update/set_chart_id.sql");
    protected static final String SET_CHART_NAME = statement("sql/update/set_chart_name.sql");
    protected static final String SET_CHART_POSITION = statement("sql/update/set_chart_position.sql");
    protected static final String SET_CHART_TYPE = statement("sql/update/set_chart_type.sql");
    protected static final String SET_PROJECT_ICON = statement("sql/update/set_project_icon.sql");
    protected static final String SET_PROJECT_NAME = statement("sql/update/set_project_name.sql");
    protected static final String SET_PROJECT_PREVIEW_CHART = statement("sql/update/set_project_preview_chart.sql");
    protected static final String SET_PROJECT_SLUG = statement("sql/update/set_project_slug.sql");
    protected static final String SET_PROJECT_URL = statement("sql/update/set_project_url.sql");
    protected static final String SET_PROJECT_VISIBILITY = statement("sql/update/set_project_visibility.sql");
    protected static final String SLUG_USED = statement("sql/query/slug_used.sql");

    protected final Connection connection;

    protected SQLController(File file) {
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + file);
            executeUpdate(statement("sql/table/layouts.sql"));
            executeUpdate(statement("sql/table/metrics.sql"));
            executeUpdate(statement("sql/table/projects.sql"));
            executeUpdate(statement("sql/table/servers.sql"));
            executeUpdate(statement("sql/index/layouts.sql"));
            executeUpdate(statement("sql/index/metrics.sql"));
            executeUpdate(statement("sql/index/projects.sql"));
            executeUpdate(statement("sql/pragma/foreign_keys.sql"));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect or setup database", e);
        }
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }

    protected List<Project> readProjects(ResultSet resultSet) throws SQLException {
        var projects = new ArrayList<Project>();
        while (resultSet.next()) {
            var project = readProject(resultSet);
            if (project.previewChart() == null) projects.add(project);
            else projects.add(project.withLayout(new Layout(Set.of(readLayoutOption(resultSet)))));
        }
        return projects;
    }

    protected Project readProject(ResultSet resultSet) throws SQLException {
        var id = resultSet.getInt("project_id");
        var owner = resultSet.getString("project_owner");
        var name = resultSet.getString("project_name");
        var slug = resultSet.getString("project_slug");
        var isPrivate = resultSet.getBoolean("project_private");
        var icon = resultSet.getString("project_icon");
        var previewChart = resultSet.getString("project_preview_chart");
        var projectUrl = resultSet.getString("project_url");
        return new Project(name, owner, slug, id, isPrivate, null, icon, previewChart, projectUrl);
    }

    protected Layout.Chart readLayoutOption(ResultSet resultSet) throws SQLException {
        var chart = resultSet.getString("layout_chart");
        var name = resultSet.getString("layout_name");
        var type = resultSet.getString("layout_type");
        var staticInfo = resultSet.getBoolean("layout_static_info");
        var position = resultSet.getInt("layout_position");
        var icon = resultSet.getString("layout_icon");
        var width = resultSet.getInt("layout_width");
        var height = resultSet.getInt("layout_height");
        var sources = Layout.Source.fromJson(JsonParser.parseString(resultSet.getString("layout_sources")).getAsJsonArray());
        var extras = JsonParser.parseString(resultSet.getString("layout_extras")).getAsJsonObject();
        var dimensions = new Layout.Dimensions(width, height);
        return new Layout.Chart(chart, name, type, staticInfo, dimensions, position, icon, sources, extras);
    }

    protected @Nullable Layout readLayout(ResultSet resultSet) throws SQLException {
        var charts = new HashSet<Layout.Chart>();
        while (resultSet.next()) charts.add(readLayoutOption(resultSet));
        return charts.isEmpty() ? null : new Layout(charts);
    }

    @SuppressWarnings("SqlSourceToSinkFlow")
    protected <T> @Nullable T executeQuery(String sql, ThrowingFunction<ResultSet, T> mapper, @Nullable Object... parameters) throws SQLException {
        try (var preparedStatement = connection.prepareStatement(sql)) {
            for (var i = 0; i < parameters.length; i++)
                preparedStatement.setObject(i + 1, parameters[i]);
            try (var resultSet = preparedStatement.executeQuery()) {
                return ThrowingFunction.unchecked(mapper).apply(resultSet);
            }
        }
    }

    @SuppressWarnings("SqlSourceToSinkFlow")
    protected int executeUpdate(String sql, @Nullable Object... parameters) throws SQLException {
        try (var statement = connection.prepareStatement(sql)) {
            for (var i = 0; i < parameters.length; i++) statement.setObject(i + 1, parameters[i]);
            return statement.executeUpdate();
        }
    }

    @SuppressWarnings("SqlSourceToSinkFlow")
    protected int executeUpdateGetKey(String sql, @Nullable Object... parameters) throws SQLException {
        try (var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (var i = 0; i < parameters.length; i++) statement.setObject(i + 1, parameters[i]);
            if (statement.executeUpdate() == 0) throw new SQLException("No rows affected");
            try (var generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) return generatedKeys.getInt(1);
            }
            throw new SQLException("Statement returns no generated keys");
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
