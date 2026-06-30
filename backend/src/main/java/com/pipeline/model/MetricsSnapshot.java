package com.pipeline.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class MetricsSnapshot {
    
    private Instant timestamp;
    private double throughputPerSecond;
    private double latencyP50Ms;
    private double latencyP95Ms;
    private double latencyP99Ms;

    private double errorRate;
    private long totalEventsProcessed;
    private long totalErrors;
    private int activeSymbols;
    private long consumerLag;

    private List<SymbolVolume> topSymbols;
    private Map<String, Long> tradesBySide;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SymbolVolume {
        private String symbol;
        private double totalVolume;
        private double vwap;
        private long tradeCount;
    }
}
