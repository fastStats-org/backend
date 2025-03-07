package org.faststats.controller;

import com.google.common.base.Preconditions;
import org.faststats.model.Layout;
import org.faststats.model.Project;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@NullMarked
public class DatabaseController extends SQLController {
    public Project createProject(String name, String owner, boolean isPrivate) throws SQLException {
        var slug = generateUniqueSlug(name);
        var id = executeUpdateGetKey(CREATE_PROJECT, owner, name, slug, isPrivate);
        return new Project(name, owner, slug, id, isPrivate, null, null, null, null);
    }

    public boolean setProjectName(int projectId, String name, @Nullable String ownerId) throws SQLException {
        return executeUpdate(SET_PROJECT_NAME, name, projectId, ownerId) > 0;
    }

    public boolean setProjectSlug(int projectId, String slug, @Nullable String ownerId) throws SQLException {
        if (!Project.isValidSlug(slug)) throw new IllegalArgumentException("Invalid slug: " + slug);
        return executeUpdate(SET_PROJECT_SLUG, slug, projectId, ownerId) > 0;
    }

    public boolean setProjectIcon(int projectId, @Nullable String icon, @Nullable String ownerId) throws SQLException {
        return executeUpdate(SET_PROJECT_ICON, icon, projectId, ownerId) > 0;
    }

    public boolean createChart(int projectId, String chart, Layout.Options options, @Nullable String ownerId) throws SQLException {
        return executeUpdate(CREATE_CHART, projectId, chart, options.name(), options.type(),
                options.color(), options.dimensions().width(), options.dimensions().height(),
                options.index(), options.icon(), ownerId, projectId) > 0;
    }

    public boolean setChartIcon(int projectId, String chart, @Nullable String icon, @Nullable String ownerId) throws SQLException {
        return executeUpdate(SET_CHART_ICON, icon, chart, projectId, ownerId) > 0;
    }

    public boolean setChartName(int projectId, String chart, String name, @Nullable String ownerId) throws SQLException {
        return executeUpdate(SET_CHART_NAME, name, chart, projectId, ownerId) > 0;
    }

    public boolean setChartDimensions(int projectId, String chart, Layout.Dimensions dimensions, @Nullable String ownerId) throws SQLException {
        return executeUpdate(SET_CHART_DIMENSIONS, dimensions.width(), dimensions.height(), chart, projectId, ownerId) > 0;
    }

    public boolean setChartId(int projectId, String chart, String id, @Nullable String ownerId) throws SQLException {
        Preconditions.checkArgument(Project.isValidChartId(id), "Invalid chart id: %s", id);
        return executeUpdate(SET_CHART_ID, id, chart, projectId, ownerId) > 0;
    }

    public boolean setChartIndex(int projectId, String chart, int index, @Nullable String ownerId) throws SQLException {
        return executeUpdate(SET_CHART_INDEX, index, chart, projectId, ownerId) > 0;
    }

    public boolean setChartType(int projectId, String chart, String type, @Nullable String ownerId) throws SQLException {
        return executeUpdate(SET_CHART_TYPE, type, chart, projectId, ownerId) > 0;
    }

    public boolean setChartColor(int projectId, String chart, String color, @Nullable String ownerId) throws SQLException {
        return executeUpdate(SET_CHART_COLOR, color, chart, projectId, ownerId) > 0;
    }

    public boolean setProjectPreviewChart(int projectId, @Nullable String chart, @Nullable String ownerId) throws SQLException {
        return executeUpdate(SET_PROJECT_PREVIEW_CHART, chart, projectId, ownerId) > 0;
    }

    public boolean setProjectUrl(int projectId, @Nullable String url, @Nullable String ownerId) throws SQLException {
        return executeUpdate(SET_PROJECT_URL, url, projectId, ownerId) > 0;
    }

    public boolean setProjectVisibility(int projectId, boolean isPrivate, @Nullable String ownerId) throws SQLException {
        return executeUpdate(SET_PROJECT_VISIBILITY, isPrivate, projectId, ownerId) > 0;
    }

    public @Nullable Project getProject(String slug, @Nullable String owner) throws SQLException {
        var project = executeQuery(GET_PROJECT, this::readProject, slug, owner);
        return project != null ? project.withLayout(getLayout(project.id())) : null;
    }

    public @Nullable Layout getLayout(int projectId) throws SQLException {
        return executeQuery(GET_LAYOUT, this::readLayout, projectId);
    }

    public boolean deleteProject(int projectId, @Nullable String ownerId) throws SQLException {
        return executeUpdate(DELETE_PROJECT, projectId, ownerId) > 0;
    }

    public int getServerId(UUID uuid) throws SQLException {
        return Objects.requireNonNull(executeQuery(GET_SERVER_ID, result -> {
            if (result.next()) return result.getInt(1);
            throw new SQLException("Expected one row, but got none");
        }, uuid));
    }

    public List<Project> getProjects(int offset, int limit, @Nullable String ownerId, @Nullable Boolean publicOnly) throws SQLException {
        var projects = executeQuery(GET_PROJECTS, this::readProjects, ownerId, publicOnly, limit, offset);
        return projects != null ? projects : List.of();
    }

    public boolean isSlugUsed(String slug) throws SQLException {
        return Boolean.TRUE.equals(executeQuery(SLUG_USED, resultSet ->
                resultSet.next() && resultSet.getBoolean(1), slug));
    }

    public String generateUniqueSlug(String name) throws SQLException {
        var base = name.replaceAll("([A-Z])", "-$1").replaceAll("[^a-zA-Z0-9]", "-")
                .replaceAll("-+", "-").replaceAll("^-|-$", "").toLowerCase();
        var unique = base;
        var counter = 1;
        while (isSlugUsed(unique)) unique = base + "-" + counter++;
        return unique;
    }

    public long countProjects(@Nullable String ownerId) throws SQLException {
        var projects = executeQuery(COUNT_PROJECTS, resultSet -> resultSet.next() ? resultSet.getLong(1) : 0, ownerId);
        return projects != null ? projects : 0;
    }
}
