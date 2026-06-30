package com.pipeline.kafka;

import com.pipeline.model.TradeEvent;
import com.pipeline.processing.AggregationEngine;
import com.pipeline.persistence.PersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TradeConsumer {

    private static final Logger log = LoggerFactory.getLogger(TradeConsumer.class);

    private final AggregationEngine aggregationEngine;
    private final PersistenceService persistenceService;

    public TradeConsumer(AggregationEngine aggregationEngine,
                         PersistenceService persistenceService) {
        this.aggregationEngine = aggregationEngine;
        this.persistenceService = persistenceService;
    }

    @KafkaListener(
            topics = KafkaConfig.TRADES_TOPIC,
            groupId = "pipeline-dashboard",
            concurrency = "3"
    )
    public void consume(TradeEvent event) {
        long ingestedAt = System.currentTimeMillis();

        // Validate
        if (!isValid(event)) {
            aggregationEngine.recordError();
            log.warn("Invalid trade event: tradeId={}, symbol={}, price={}",
                    event.getTradeId(), event.getSymbol(), event.getPrice());
            return;
        }

        // Calculate end-to-end latency
        long latencyMs = ingestedAt - event.getGeneratedAt();

        // Feed into aggregation engine
        aggregationEngine.process(event, latencyMs);

        // Async persistence (buffered)
        persistenceService.bufferTrade(event, ingestedAt);
    }

    private boolean isValid(TradeEvent event) {
        return event.getSymbol() != null
            && !event.getSymbol().isBlank()
            && event.getPrice() > 0
            && event.getQuantity() > 0;
    }
}