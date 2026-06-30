package com.pipeline.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PipelineHealth {
    
    private Instant timestamp;
    private ComponentStatus kafka;
    private ComponentStatus database;
    private ComponentStatus websocket;
    

 
    private long uptimeSeconds;
    private int connectedClients;
    private long consumerLag;

    public enum Status {
        UP, DOWN, DEGRADED
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComponentStatus {
        private Status status;
        private String message;
        private String name;
    }
}