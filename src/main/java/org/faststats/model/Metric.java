package org.faststats.model;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import org.faststats.model.chart.Chart;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public record Metric(
        int projectId,
        UUID consumerId,
        @Nullable String osName,
        @Nullable String osArch,
        @Nullable String osVersion,
        @Nullable Locale locale,
        @Nullable Integer processors,
        Map<String, Chart> charts
) {

    public static Metric fromJson(JsonObject metric) {
        Preconditions.checkState(metric.has("projectId"), "Project ID is required");
        Preconditions.checkState(metric.has("consumerId"), "Consumer ID is required");
        var projectId = metric.get("projectId").getAsInt();
        var consumerId = UUID.fromString(metric.get("consumerId").getAsString());
        var osName = metric.has("osName") ? metric.get("osName").getAsString() : null;
        var osArch = metric.has("osArch") ? metric.get("osArch").getAsString() : null;
        var osVersion = metric.has("osVersion") ? metric.get("osVersion").getAsString() : null;
        var locale = metric.has("locale") ? Locale.forLanguageTag(metric.get("locale").getAsString()) : null;
        var processors = metric.has("processors") ? metric.get("processors").getAsInt() : null;
        var charts = new HashMap<String, Chart>();
        // todo: add charts
        // var chartTypes = FastStats.DATABASE_M.getChartTypes(projectId);
        // metric.getAsJsonObject("charts").entrySet().forEach(entry -> {
        //     var chartType = chartTypes.get(entry.getKey());
        //     if (chartType == null) return;
        //     var chart = Chart.fromJson(chartType, entry.getValue());
        //     charts.put(entry.getKey(), chart);
        // });
        return new Metric(projectId, consumerId, osName, osArch, osVersion, locale, processors, charts);
    }
}
