package com.pipeline.persistence.entity;


import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;


@Entity
@Table(name = "metrics_snapshots")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricsSnapshotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Instant timestamp;

    private double throughput;

    @Column(name = "latency_p50_ms")
    private double latencyP50Ms;

    @Column(name = "latency_p95_ms")
    private double latencyP95Ms;

    @Column(name = "latency_p99_ms")
    private double latencyP99Ms;

    @Column(name = "error_rate")
    private double errorRate;

    @Column(name = "active_symbols")
    private int activeSymbols;
    
    @Column(name = "consumer_lag")
    private long consumerLag;

}