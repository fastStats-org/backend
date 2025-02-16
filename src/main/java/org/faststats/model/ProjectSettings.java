package org.faststats.model;

import com.google.gson.JsonObject;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public record ProjectSettings(
        @Nullable Boolean isPrivate,
        @Nullable Layout layout,
        @Nullable String previewChart,
        @Nullable String projectUrl
) {
    public boolean isEmpty() {
        return isPrivate == null && previewChart == null && layout == null && projectUrl == null;
    }

    public boolean isValid() {
        return previewChart == null || layout == null || layout.charts().containsKey(previewChart);
    }

    public static @Nullable ProjectSettings fromJson(JsonObject settings) {
        try {
            var isPrivate = settings.has("private") ? settings.get("private").getAsBoolean() : null;
            var layout = settings.has("layout") ? Layout.fromJson(settings.getAsJsonObject("layout")) : null;
            var previewChart = settings.has("preview_chart") ? settings.get("preview_chart").getAsString() : null;
            var projectUrl = settings.has("project_url") ? settings.get("project_url").getAsString() : null;
            return new ProjectSettings(isPrivate, layout, previewChart, projectUrl);
        } catch (Exception e) {
            return null;
        }
    }
}
