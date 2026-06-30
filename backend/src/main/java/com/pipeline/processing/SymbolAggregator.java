package com.pipeline.processing;

import com.pipeline.model.MetricsSnapshot;
import com.pipeline.model.TradeEvent;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class SymbolAggregator {

    private final ConcurrentHashMap<String, SymbolStats> symbolStats = new ConcurrentHashMap<>();

    public void recordTrade(TradeEvent event) {
        symbolStats.computeIfAbsent(event.getSymbol(), k -> new SymbolStats())
            .update(event.getPrice(), event.getQuantity(), event.getSide());
    }

    public List<MetricsSnapshot.SymbolVolume> getTopSymbols(int limit) {
        return symbolStats.entrySet().stream()
            .map(e -> MetricsSnapshot.SymbolVolume.builder()
                .symbol(e.getKey())
                .totalVolume(e.getValue().totalVolume)
                .vwap(e.getValue().getVwap())
                .tradeCount(e.getValue().tradeCount.get())
                .build())
            .sorted(Comparator.comparingDouble(MetricsSnapshot.SymbolVolume::getTotalVolume).reversed())
            .limit(limit)
            .toList();
    }

    public int getActiveSymbolCount() {
        return symbolStats.size();
    }

    private static class SymbolStats {
        final AtomicLong tradeCount = new AtomicLong(0);
        // Use double accumulators for thread safety (approximate but fast)
        volatile double totalVolume = 0;
        volatile double priceVolumeSum = 0; // for VWAP: sum(price * qty)
        volatile double volumeSum = 0; // for VWAP: sum(qty)
        final AtomicLong buyCount = new AtomicLong(0);
        final AtomicLong sellCount = new AtomicLong(0);

        synchronized void update(double price, double quantity, String side) {
            tradeCount.incrementAndGet();
            totalVolume += quantity;
            priceVolumeSum += price * quantity;
            volumeSum += quantity;
            if ("BUY".equals(side)) {
                buyCount.incrementAndGet();
            } else {
                sellCount.incrementAndGet();
            }
        }

        double getVwap() {
            if (volumeSum == 0) return 0;
            return Math.round(priceVolumeSum / volumeSum * 100.0) / 100.0;
        }
    }
}