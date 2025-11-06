package codeanalyzer;

public class AnalysisResult {
    private final int min;
    private final int max;
    private final double mean;
    private final double median;

    public AnalysisResult(int min, int max, double mean, double median) {
        this.min = min;
        this.max = max;
        this.mean = mean;
        this.median = median;
    }

    public int getMin() { return min; }
    public int getMax() { return max; }
    public double getMean() { return mean; }
    public double getMedian() { return median; }
}