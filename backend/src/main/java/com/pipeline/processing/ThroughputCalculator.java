package com.pipeline.processing;

import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class ThroughputCalculator {

    private final AtomicLong eventCount = new AtomicLong(0);
    private final AtomicLong windowStartMs = new AtomicLong(System.currentTimeMillis());
    private final AtomicReference<Double> lastThroughput = new AtomicReference<>(0.0);

    public void recordEvent() {
        eventCount.incrementAndGet();
    }

    public double getThroughputPerSecond() {
        long now = System.currentTimeMillis();
        long start = windowStartMs.get();
        long count = eventCount.get();
        long elapsed = now - start;

        if (elapsed <= 0) return lastThroughput.get();
        
        double throughput = (count * 1000.0) / elapsed;
        lastThroughput.set(throughput);
        return throughput;
    }

    public void resetWindow() {
        eventCount.set(0);
        windowStartMs.set(System.currentTimeMillis());
    }

    public long getTotalCount() {
        return eventCount.get();
    }
}