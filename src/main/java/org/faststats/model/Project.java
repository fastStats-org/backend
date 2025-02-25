package org.faststats.model;

import com.google.gson.JsonObject;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public record Project(
        String name,
        String ownerId,
        String slug,
        int id,
        boolean isPrivate,
        @Nullable Layout layout,
        @Nullable String icon,
        @Nullable String previewChart,
        @Nullable String projectUrl
) {
    public JsonObject toJson() {
        var object = new JsonObject();
        object.addProperty("id", id);
        object.addProperty("name", name);
        object.addProperty("ownerId", ownerId);
        object.addProperty("private", isPrivate);
        object.addProperty("slug", slug);
        if (layout != null) object.add("layout", layout.toJson());
        if (icon != null) object.addProperty("icon", icon);
        if (previewChart != null) object.addProperty("previewChart", previewChart);
        if (projectUrl != null) object.addProperty("url", projectUrl);
        return object;
    }

    public Project withLayout(@Nullable Layout layout) {
        return new Project(name, ownerId, slug, id, isPrivate, layout, icon, previewChart, projectUrl);
    }

    public static boolean isValidSlug(String slug) {
        return slug.matches("^(?=.{3,32}$)[a-z0-9]+(-[a-z0-9]+)*$");
    }
}
