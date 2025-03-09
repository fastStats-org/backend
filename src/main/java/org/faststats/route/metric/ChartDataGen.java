package org.faststats.route.metric;

import org.faststats.model.chart.Chart;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class ChartDataGen {
    public static Set<Chart> generateRandomChartData(int chartCount, String chartKeyPrefix) {
        Set<Chart> charts = new HashSet<>();
        long currentTime = System.currentTimeMillis();

        for (int i = 0; i < chartCount; i++) {
            String key = chartKeyPrefix + "_" + i;
            int value = ThreadLocalRandom.current().nextInt(10, 1000);
            long randomTimeOffset = ThreadLocalRandom.current().nextLong(0, TimeUnit.DAYS.toMillis(30));
            long timestamp = currentTime - randomTimeOffset;

            charts.add(new Chart(key, value, timestamp));
        }

        return charts;
    }

    public static Set<Chart> generateTimeSeriesData(int days, String chartKey) {
        Set<Chart> charts = new HashSet<>();
        long currentTime = System.currentTimeMillis();

        for (int i = 0; i < days; i++) {
            int value = ThreadLocalRandom.current().nextInt(10, 1000);
            long timestamp = currentTime - TimeUnit.DAYS.toMillis(days - i);
            charts.add(new Chart(chartKey, value, timestamp));
        }

        return charts;
    }

    public static Set<Chart> generateMultiSeriesData(int seriesCount, int daysPerSeries) {
        Set<Chart> charts = new HashSet<>();

        for (int series = 0; series < seriesCount; series++) {
            String seriesKey = "series_" + series;
            charts.addAll(generateTimeSeriesData(daysPerSeries, seriesKey));
        }

        return charts;
    }
}
