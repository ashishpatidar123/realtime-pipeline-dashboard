package com.pipeline.persistence;

import com.pipeline.persistence.entity.TradeEntity;
import com.pipeline.persistence.repository.TradeRepository;
import com.pipeline.model.MetricsSnapshot;
import com.pipeline.model.TradeEvent;
import com.pipeline.persistence.entity.MetricsSnapshotEntity;

import com.pipeline.persistence.repository.MetricsSnapshotRepository;

import com.pipeline.processing.aggregation.AggregationEngine;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;




@Service
public class PersistenceService {
    
    private static final Logger logger = LoggerFactory.getLogger(PersistenceService.class);

    private final TradeRepository tradeRepository;
    private final MetricsSnapshotRepository metricsSnapshotRepository;
    private final AggregationEngine aggregationEngine;

    private final Queue<TradeEntity> tradeBuffer = new ConcurrentLinkedQueue<>();
    private static final int MAX_BUFFER_SIZE = 500;

    @Value("${pipeline.persistence.trade-retention-days:7}")
    private int tradeRetentionDays;

    public PersistenceService(TradeRepository tradeRepository, MetricsSnapshotRepository metricsSnapshotRepository, AggregationEngine aggregationEngine) {
        this.tradeRepository = tradeRepository;
        this.metricsSnapshotRepository = metricsSnapshotRepository;
        this.aggregationEngine = aggregationEngine;
    }

    public void bufferTrade(TradeEvent event, long ingestedAt) {
        if (tradeBuffer.size() >= MAX_BUFFER_SIZE) {
            logger.warn("Trade buffer is full. Dropping trade: {}", trade);
            return;
        }

        TradeEntity tradeEntity = TradeEntity.builder()
                .tradeId(event.getTradeId())
                .symbol(event.getSymbol())
                .side(event.getSide())
                .price(event.getPrice())
                .quantity(event.getQuantity())
                .venue(event.getVenue())
                .assetClass(event.getAssetClass())
                .traderDesk(event.getTraderDesk())
                .eventTimestamp(event.getEventTimestamp().toString())
                .ingestedAt(Instant.ofEpochMilli(ingestedAt).toString())
                .build();

        tradeBuffer.offer(tradeEntity);
    }
    @Scheduled(fixedRateString = "${pipeline.persistence.flush-interval-ms:5}000")
    @Transactional
    public void flushBatch(){

        List<TradeEntity> batch = new ArrayList<>();

        TradeEntity entity;

        while ((entity = tradeBuffer.poll()) != null && batch.size() < MAX_BUFFER_SIZE) {
            batch.add(entity);
        }
        if (!batch.isEmpty()) {
            try{
                tradeRepository.saveAll(batch);
                logger.info("Flushed {} trades to the database.", batch.size());
            } catch (Exception e) {
                logger.error("Error while flushing trades to the database: {}", batch.size(), e);
            }
        }

        try{
            MetricsSnapshot snapshot = aggregationEngine.snapshot();
            MetricsSnapshotEntity metricEntity = MetricsSnapshotEntity.builder()
                    .timestamp(snapshot.getTimestamp())
                    .throughput(snapshot.getThroughput())
                    .latencyP50Ms(snapshot.getLatencyP50Ms())
                    .latencyP95Ms(snapshot.getLatencyP95Ms())
                    .latencyP99Ms(snapshot.getLatencyP99Ms())
                    .errorRate(snapshot.getErrorRate())
                    .activeSymbols(snapshot.getActiveSymbols())
                    .build();

            metricsSnapshotRepository.save(metricEntity);
            logger.info("Saved metrics snapshot at {}", snapshot.getTimestamp());
        } catch (Exception e) {
            logger.error("Error while saving metrics snapshot", e);
        }
    }

    @Transactional
    @Scheduled(cron = "0 0 2 * * *") // Default: every day at midnight
    public void cleanupOldTrades() {
        
        Instant cutoff = Instant.now().minus(tradeRetentionDays, ChronoUnit.DAYS);
        tradeRepository.deleteByEventTimestampBefore(cutoff);
        logger.info("Deleted trades older than {}", cutoff);
    }
}

