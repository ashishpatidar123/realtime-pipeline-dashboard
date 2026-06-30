package com.pipeline.processing;

import org.HdrHistogram.Histogram;
import org.springframework.stereotype.Component;

@Component
public class LatencyTracker {

    // Records latencies from 1 microsecond to 10 seconds with 3 significant digits
    private volatile Histogram histogram = new Histogram(10_000_000L, 3);

    public void recordLatency(long latencyMs) {
        if (latencyMs >= 0 && latencyMs < 10_000_000L) {
            histogram.recordValue(latencyMs);
        }
    }

    public double getP50() {
        return histogram.getValueAtPercentile(50.0);
    }

    public double getP95() {
        return histogram.getValueAtPercentile(95.0);
    }

    public double getP99() {
        return histogram.getValueAtPercentile(99.0);
    }

    public double getMean() {
        return histogram.getMean();
    }

    public long getTotalCount() {
        return histogram.getTotalCount();
    }

    public void reset() {
        histogram = new Histogram(10_000_000L, 3);
    }
}