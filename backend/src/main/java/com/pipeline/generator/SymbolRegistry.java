package com.pipeline.generator;

import lombok.Getter;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class SymbolRegistry {

    @Getter
    private final List<SymbolInfo> symbols;

    // Zipf-like weights: top symbols get more volume
    private final double[] cumulativeWeights;

    public SymbolRegistry() {
        this.symbols = List.of(
            new SymbolInfo("AAPL", 185.0, "EQUITY", "NYSE", "EQ-DESK-1"),
            new SymbolInfo("GOOGL", 174.0, "EQUITY", "NASDAQ", "EQ-DESK-1"),
            new SymbolInfo("TSLA", 245.0, "EQUITY", "NASDAQ", "EQ-DESK-2"),
            new SymbolInfo("MSFT", 420.0, "EQUITY", "NASDAQ", "EQ-DESK-1"),
            new SymbolInfo("AMZN", 185.0, "EQUITY", "NASDAQ", "EQ-DESK-2"),
            new SymbolInfo("META", 500.0, "EQUITY", "NASDAQ", "EQ-DESK-2"),
            new SymbolInfo("NVDA", 130.0, "EQUITY", "NASDAQ", "EQ-DESK-3"),
            new SymbolInfo("JPM", 195.0, "EQUITY", "NYSE", "EQ-DESK-3"),
            new SymbolInfo("V", 280.0, "EQUITY", "NYSE", "EQ-DESK-3"),
            new SymbolInfo("JNJ", 155.0, "EQUITY", "NYSE", "EQ-DESK-4"),
            new SymbolInfo("BTC/USD", 67000.0, "CRYPTO", "COINBASE", "CRYPTO-DESK"),
            new SymbolInfo("ETH/USD", 3500.0, "CRYPTO", "COINBASE", "CRYPTO-DESK"),
            new SymbolInfo("SOL/USD", 145.0, "CRYPTO", "COINBASE", "CRYPTO-DESK"),
            new SymbolInfo("EUR/USD", 1.0842, "FX", "EBS", "FX-DESK"),
            new SymbolInfo("GBP/USD", 1.2710, "FX", "EBS", "FX-DESK"),
            new SymbolInfo("USD/JPY", 155.50, "FX", "EBS", "FX-DESK"),
            new SymbolInfo("AUD/USD", 0.6620, "FX", "REUTERS", "FX-DESK"),
            new SymbolInfo("USD/CAD", 1.3640, "FX", "REUTERS", "FX-DESK"),
            new SymbolInfo("XAU/USD", 2350.0, "COMMODITY", "CME", "CMDTY-DESK"),
            new SymbolInfo("CL=F", 78.50, "COMMODITY", "CME", "CMDTY-DESK")
        );

        // Zipf distribution weights
        double[] weights = new double[symbols.size()];
        double totalWeight = 0;
        for (int i = 0; i < symbols.size(); i++) {
            weights[i] = 1.0 / (i + 1); // Zipf: 1, 1/2, 1/3, ...
            totalWeight += weights[i];
        }

        this.cumulativeWeights = new double[symbols.size()];
        double cumulative = 0;
        for (int i = 0; i < symbols.size(); i++) {
            cumulative += weights[i] / totalWeight;
            cumulativeWeights[i] = cumulative;
        }
    }

    public SymbolInfo getRandomSymbol() {
        double random = ThreadLocalRandom.current().nextDouble();
        for (int i = 0; i < cumulativeWeights.length; i++) {
            if (random <= cumulativeWeights[i]) {
                return symbols.get(i);
            }
        }
        return symbols.get(symbols.size() - 1);
    }

    public record SymbolInfo(
        String symbol,
        double basePrice,
        String assetClass,
        String venue,
        String traderDesk
    ) {}
}