package org.faststats.model;

import com.google.gson.JsonObject;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Deprecated(forRemoval = true)
public record ProjectSettings(
        @Nullable Boolean isPrivate,
        @Nullable Layout layout,
        @Nullable String icon,
        @Nullable String previewChart,
        @Nullable String url,
        @Nullable String slug
) {
    public boolean isEmpty() {
        return isPrivate == null && previewChart == null
               && layout == null && url == null
               && icon == null && slug == null;
    }

    public boolean isValid() {
        return (previewChart == null || layout == null || layout.charts().containsKey(previewChart))
               && (slug == null || Project.isValidSlug(slug));
    }

    public static @Nullable ProjectSettings fromJson(JsonObject settings) {
        try {
            var icon = settings.has("icon") ? settings.get("icon").getAsString() : null;
            var isPrivate = settings.has("private") ? settings.get("private").getAsBoolean() : null;
            var layout = settings.has("layout") ? Layout.fromJson(settings.getAsJsonObject("layout")) : null;
            var previewChart = settings.has("preview_chart") ? settings.get("preview_chart").getAsString() : null;
            var projectUrl = settings.has("project_url") ? settings.get("project_url").getAsString() : null;
            var slug = settings.has("slug") ? settings.get("slug").getAsString() : null;
            return new ProjectSettings(isPrivate, layout, icon, previewChart, projectUrl, slug);
        } catch (Exception e) {
            return null;
        }
    }
}
