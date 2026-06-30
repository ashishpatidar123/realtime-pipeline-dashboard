package com.pipeline.processing;

import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class ErrorRateMonitor {

    private final AtomicLong totalEvents = new AtomicLong(0);
    private final AtomicLong totalErrors = new AtomicLong(0);

    public void recordEvent() {
        totalEvents.incrementAndGet();
    }

    public void recordError() {
        totalErrors.incrementAndGet();
        totalEvents.incrementAndGet();
    }

    public double getErrorRate() {
        long events = totalEvents.get();
        if (events == 0) return 0.0;
        return (double) totalErrors.get() / events;
    }

    public long getTotalErrors() {
        return totalErrors.get();
    }

    public long getTotalEvents() {
        return totalEvents.get();
    }
}