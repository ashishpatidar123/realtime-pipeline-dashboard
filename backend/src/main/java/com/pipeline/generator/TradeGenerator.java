package com.pipeline.generator;

import com.pipeline.kafka.TradeProducer;
import com.pipeline.model.TradeEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class TradeGenerator {
    private static final Logger log = LoggerFactory.getLogger(TradeGenerator.class);

    private final TradeProducer tradeProducer;
    private final SymbolRegistry symbolRegistry;
    private final PriceSimulator priceSimulator;

    @Value("${pipeline.generator.enabled:true}")
    private boolean enabled;

    @Value("${pipeline.generator.rate-per-second:1000}")
    private int ratePerSecond;

    @Value("${pipeline.generator.error-injection-rate:0.001}")
    private double errorInjectionRate;

    @Value("${pipeline.generator.burst-interval-seconds:60}")
    private int burstIntervalSeconds;

    @Value("${pipeline.generator.burst-multiplier:5}")
    private int burstMultiplier;

    private final AtomicLong tradeCounter = new AtomicLong(0);
    private final AtomicLong burstCounter = new AtomicLong(0);
    private ScheduledExecutorService scheduler;

    public TradeGenerator(TradeProducer tradeProducer,
                          SymbolRegistry symbolRegistry,
                          PriceSimulator priceSimulator) {
        this.tradeProducer = tradeProducer;
        this.symbolRegistry = symbolRegistry;
        this.priceSimulator = priceSimulator;
    }

    @PostConstruct
    public void start() {
        if (!enabled) {
            log.info("Trade generator is disabled");
            return;
        }

        scheduler = Executors.newScheduledThreadPool(2);

        // Main generation loop: fires every 1ms, generates (ratePerSecond / 1000) events per tick
        int eventsPerTick = Math.max(1, ratePerSecond / 1000);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                int currentRate = eventsPerTick;
                // Burst mode check
                if (burstCounter.get() > 0) {
                    currentRate = eventsPerTick * burstMultiplier;
                    burstCounter.decrementAndGet();
                }
                for (int i = 0; i < currentRate; i++) {
                    TradeEvent event = generateTrade();
                    tradeProducer.send(event);
                }
            } catch (Exception e) {
                log.error("Error generating trades", e);
            }
        }, 1000, 1, TimeUnit.MILLISECONDS);

        // Burst trigger: every N seconds, enable burst mode for 5 seconds
        scheduler.scheduleAtFixedRate(() -> {
            log.info("Burst mode activated for 5 seconds");
            burstCounter.set(5000); // 5000 ticks = 5 seconds
        }, burstIntervalSeconds, burstIntervalSeconds, TimeUnit.SECONDS);

        log.info("Trade generator started: {} events/sec, burst every {}s",
            ratePerSecond, burstIntervalSeconds);
    }

    @PreDestroy
    public void stop() {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }

    private TradeEvent generateTrade() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        SymbolRegistry.SymbolInfo symbolInfo = symbolRegistry.getRandomSymbol();
        long count = tradeCounter.incrementAndGet();

        // Error injection: 0.1% chance of malformed event
        if (random.nextDouble() < errorInjectionRate) {
            return TradeEvent.builder()
                .tradeId("TRD-ERR-" + count)
                .symbol(null) // intentionally null
                .side("BUY")
                .quantity(-1) // intentionally invalid
                .price(-1) // intentionally invalid
                .venue(symbolInfo.venue())
                .traderDesk(symbolInfo.traderDesk())
                .assetClass(symbolInfo.assetClass())
                .timestamp(Instant.now())
                .generatedAt(System.currentTimeMillis())
                .build();
        }

        double price = priceSimulator.getNextPrice(symbolInfo.symbol(), symbolInfo.basePrice());
        double quantity = generateQuantity(symbolInfo.assetClass(), random);
        String side = random.nextBoolean() ? "BUY" : "SELL";

        return TradeEvent.builder()
            .tradeId(String.format("TRD-%d-%06d", System.currentTimeMillis() % 100000, count))
            .symbol(symbolInfo.symbol())
            .side(side)
            .quantity(quantity)
            .price(price)
            .venue(symbolInfo.venue())
            .traderDesk(symbolInfo.traderDesk())
            .assetClass(symbolInfo.assetClass())
            .timestamp(Instant.now())
            .generatedAt(System.currentTimeMillis())
            .build();
    }

    private double generateQuantity(String assetClass, ThreadLocalRandom random) {
        return switch (assetClass) {
            case "EQUITY" -> random.nextInt(10, 5000); // shares
            case "FX" -> random.nextInt(10000, 1000000); // units (lot sizes)
            case "CRYPTO" -> Math.round(random.nextDouble(0.01, 10.0) * 100.0) / 100.0;
            case "COMMODITY" -> random.nextInt(1, 100); // contracts
            default -> random.nextInt(1, 1000);
        };
    }
}