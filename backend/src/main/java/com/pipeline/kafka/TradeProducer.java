package com.pipeline.kafka;

import com.pipeline.model.TradeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TradeProducer {

    private static final Logger log = LoggerFactory.getLogger(TradeProducer.class);

    private final KafkaTemplate<String, TradeEvent> kafkaTemplate;

    public TradeProducer(KafkaTemplate<String, TradeEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(TradeEvent event) {
        // Key = symbol for partition-level ordering per symbol
        String key = event.getSymbol() != null ? event.getSymbol() : "UNKNOWN";
        kafkaTemplate.send(KafkaConfig.TRADES_TOPIC, key, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send trade event: {}", event.getTradeId(), ex);
                    }
                });
    }
}