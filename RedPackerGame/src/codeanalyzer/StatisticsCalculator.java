package codeanalyzer;

import java.util.Collections;
import java.util.List;

public class StatisticsCalculator {
    public static AnalysisResult calculateStatistics(List<Integer> data) {
        if (data == null || data.isEmpty()) {
            return new AnalysisResult(0, 0, 0, 0);
        }

        Collections.sort(data);

        int min = data.get(0);
        int max = data.get(data.size() - 1);
        double mean = calculateMean(data);
        double median = calculateMedian(data);

        return new AnalysisResult(min, max, mean, median);
    }

    private static double calculateMean(List<Integer> data) {
        int sum = 0;
        for (int value : data) {
            sum += value;
        }
        return (double) sum / data.size();
    }

    private static double calculateMedian(List<Integer> data) {
        int size = data.size();
        if (size % 2 == 0) {
            return (data.get(size / 2 - 1) + data.get(size / 2)) / 2.0;
        } else {
            return data.get(size / 2);
        }
    }
}