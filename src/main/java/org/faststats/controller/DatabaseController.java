package org.faststats.controller;

import org.faststats.model.Layout;
import org.faststats.model.Project;
import org.faststats.model.ProjectSettings;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.sql.SQLException;
import java.util.List;

@NullMarked
public class DatabaseController extends SQLController {

    public static void main(String[] args) throws SQLException {
        var database = new DatabaseController();
    }

    public Project createProject(String name, String owner, boolean isPrivate) throws SQLException {
        var slug = generateUniqueSlug(name);
        var id = executeUpdate(CREATE_PROJECT, owner, name, slug, isPrivate);
        return new Project(name, owner, slug, id, isPrivate, null, null, null, null);
    }

    public boolean renameProject(int projectId, String name, @Nullable String ownerId) throws SQLException {
        return executeUpdate(RENAME_PROJECT, name, projectId, ownerId) > 0;
    }

    public boolean updateSlug(int projectId, String slug, @Nullable String ownerId) throws SQLException {
        if (!Project.isValidSlug(slug)) throw new IllegalArgumentException("Invalid slug: " + slug);
        return executeUpdate(UPDATE_SLUG, slug, projectId, ownerId) > 0;
    }

    public boolean updateIcon(int projectId, @Nullable String icon, @Nullable String ownerId) throws SQLException {
        return executeUpdate(UPDATE_ICON, icon, projectId, ownerId) > 0;
    }

    public boolean updatePreviewChart(int projectId, @Nullable String chart, @Nullable String ownerId) throws SQLException {
        return executeUpdate(UPDATE_PREVIEW_CHART, chart, projectId, ownerId) > 0;
    }

    public boolean updateUrl(int projectId, @Nullable String url, @Nullable String ownerId) throws SQLException {
        return executeUpdate(UPDATE_URL, url, projectId, ownerId) > 0;
    }

    public boolean updateVisibility(int projectId, boolean isPrivate, @Nullable String ownerId) throws SQLException {
        return executeUpdate(UPDATE_VISIBILITY, isPrivate, projectId, ownerId) > 0;
    }

    @Deprecated(forRemoval = true)
    public boolean updateProject(int projectId, ProjectSettings settings, @Nullable String ownerId) throws SQLException {
        return !settings.isEmpty() && settings.isValid() && executeUpdate(UPDATE_PROJECT,
                settings.isPrivate(), settings.icon(), settings.previewChart(),
                settings.url(), settings.slug(), projectId, ownerId) > 0;
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
