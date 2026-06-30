package com.pipeline.generator;

import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class PriceSimulator {

    private final ConcurrentHashMap<String, Double> currentPrices = new ConcurrentHashMap<>();

    // Geometric Brownian Motion: random walk with ±0.5% max step
    private static final double MAX_PRICE_CHANGE_PCT = 0.005;

    public double getNextPrice(String symbol, double basePrice) {
        double current = currentPrices.computeIfAbsent(symbol, k -> basePrice);
        double change = (ThreadLocalRandom.current().nextDouble() - 0.5) * 2 * MAX_PRICE_CHANGE_PCT;
        double newPrice = current * (1.0 + change);

        // Clamp to prevent unrealistic drift (±20% from base)
        newPrice = Math.max(basePrice * 0.8, Math.min(basePrice * 1.2, newPrice));
        newPrice = Math.round(newPrice * 100.0) / 100.0; // 2 decimal places

        currentPrices.put(symbol, newPrice);
        return newPrice;
    }

    public double getCurrentPrice(String symbol) {
        return currentPrices.getOrDefault(symbol, 0.0);
    }
}