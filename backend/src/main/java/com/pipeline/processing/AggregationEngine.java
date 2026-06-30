package com.pipeline.processing;

import com.pipeline.model.MetricsSnapshot;
import com.pipeline.model.TradeEvent;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class AggregationEngine {

    private final ThroughputCalculator throughputCalculator;
    private final LatencyTracker latencyTracker;
    private final ErrorRateMonitor errorRateMonitor;
    private final SymbolAggregator symbolAggregator;

    private final Deque<TradeEvent> recentTrades = new ConcurrentLinkedDeque<>();
    private static final int MAX_RECENT_TRADES = 50;

    private final AtomicLong totalProcessed = new AtomicLong(0);

    public AggregationEngine(ThroughputCalculator throughputCalculator,
                             LatencyTracker latencyTracker,
                             ErrorRateMonitor errorRateMonitor,
                             SymbolAggregator symbolAggregator) {
        this.throughputCalculator = throughputCalculator;
        this.latencyTracker = latencyTracker;
        this.errorRateMonitor = errorRateMonitor;
        this.symbolAggregator = symbolAggregator;
    }

    public void process(TradeEvent event, long latencyMs) {
        totalProcessed.incrementAndGet();
        throughputCalculator.recordEvent();
        latencyTracker.recordLatency(latencyMs);
        errorRateMonitor.recordEvent();
        symbolAggregator.recordTrade(event);

        // Maintain bounded recent trades deque
        recentTrades.addFirst(event);
        while (recentTrades.size() > MAX_RECENT_TRADES) {
            recentTrades.removeLast();
        }
    }

    public void recordError() {
        errorRateMonitor.recordError();
    }

    public MetricsSnapshot snapshot() {
        return MetricsSnapshot.builder()
            .timestamp(Instant.now())
            .throughputPerSecond(throughputCalculator.getThroughputPerSecond())
            .latencyP50Ms(latencyTracker.getP50())
            .latencyP95Ms(latencyTracker.getP95())
            .latencyP99Ms(latencyTracker.getP99())
            .errorRate(errorRateMonitor.getErrorRate())
            .totalEventsProcessed(totalProcessed.get())
            .totalErrors(errorRateMonitor.getTotalErrors())
            .activeSymbols(symbolAggregator.getActiveSymbolCount())
            .topSymbols(symbolAggregator.getTopSymbols(10))
            .tradesBySide(Map.of(
                "BUY", 0L, // simplified - could add side tracking
                "SELL", 0L
            ))
            .build();
    }

    public List<TradeEvent> getRecentTrades(int limit) {
        return recentTrades.stream().limit(limit).toList();
    }
}