package org.faststats.model;

import com.google.gson.JsonObject;
import org.bson.Document;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public record ProjectSettings(
        @Nullable Boolean isPrivate,
        @Nullable Layout layout,
        @Nullable String icon,
        @Nullable String previewChart,
        @Nullable String projectUrl,
        @Nullable String slug
) {
    public boolean isEmpty() {
        return isPrivate == null && previewChart == null
               && layout == null && projectUrl == null
               && icon == null && slug == null;
    }

    public Document toDocument() {
        var document = new Document();
        if (isPrivate != null) document.put("private", isPrivate);
        if (layout != null) document.put("layout", layout.toDocument());
        if (icon != null) document.put("icon", icon);
        if (previewChart != null) document.put("preview_chart", previewChart);
        if (projectUrl != null) document.put("project_url", projectUrl);
        if (slug != null) document.put("slug", slug);
        return document;
    }

    public boolean isValid() {
        return previewChart == null || layout == null || layout.charts().containsKey(previewChart);
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
