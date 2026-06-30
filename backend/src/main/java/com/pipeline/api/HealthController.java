package com.pipeline.api;

import com.pipeline.model.PipelineHealth;
import com.pipeline.processing.AggregationEngine;
import com.pipeline.websocket.WebSocketEventListener;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/health")
@CrossOrigin(origins = "*")
public class HealthController {

    private final WebSocketEventListener eventListener;
    private final long startTime = System.currentTimeMillis();

    public HealthController(WebSocketEventListener eventListener) {
        this.eventListener = eventListener;
    }

    @GetMapping
    public PipelineHealth getHealth() {
        return PipelineHealth.builder()
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
    }
}