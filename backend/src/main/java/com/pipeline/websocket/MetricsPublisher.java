package com.pipeline.websocket;

import com.pipeline.model.MetricsSnapshot;
import com.pipeline.model.PipelineHealth;
import com.pipeline.model.TradeEvent;
import com.pipeline.processing.AggregationEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class MetricsPublisher {

    private static final Logger log = LoggerFactory.getLogger(MetricsPublisher.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final AggregationEngine aggregationEngine;
    private final WebSocketEventListener eventListener;

    private final long startTime = System.currentTimeMillis();

    public MetricsPublisher(SimpMessagingTemplate messagingTemplate,
                            AggregationEngine aggregationEngine,
                            WebSocketEventListener eventListener) {
        this.messagingTemplate = messagingTemplate;
        this.aggregationEngine = aggregationEngine;
        this.eventListener = eventListener;
    }

    @Scheduled(fixedRateString = "${pipeline.websocket.metrics-push-interval-ms:1000}")
    public void pushMetrics() {
        if (eventListener.getConnectedClients() == 0) return;

        MetricsSnapshot snapshot = aggregationEngine.snapshot();
        messagingTemplate.convertAndSend("/topic/metrics", snapshot);
    }

    @Scheduled(fixedRateString = "${pipeline.websocket.trades-push-interval-ms:500}")
    public void pushRecentTrades() {
        if (eventListener.getConnectedClients() == 0) return;

        List<TradeEvent> recent = aggregationEngine.getRecentTrades(10);
        messagingTemplate.convertAndSend("/topic/trades", recent);
    }

    @Scheduled(fixedRateString = "${pipeline.websocket.health-push-interval-ms:5000}")
    public void pushHealth() {
        if (eventListener.getConnectedClients() == 0) return;

        PipelineHealth health = PipelineHealth.builder()
            .timestamp(Instant.now())
            .kafka(PipelineHealth.ComponentStatus.builder()
                .name("Kafka")
                .status(PipelineHealth.Status.UP)
                .message("Connected")
                .build())
            .database(PipelineHealth.ComponentStatus.builder()
                .name("PostgreSQL")
                .status(PipelineHealth.Status.UP)
                .message("Connected")
                .build())
            .websocket(PipelineHealth.ComponentStatus.builder()
                .name("WebSocket")
                .status(PipelineHealth.Status.UP)
                .message(eventListener.getConnectedClients() + " clients")
                .build())
            .connectedClients(eventListener.getConnectedClients())
            .uptimeSeconds((System.currentTimeMillis() - startTime) / 1000)
            .build();

        messagingTemplate.convertAndSend("/topic/health", health);
    }
}