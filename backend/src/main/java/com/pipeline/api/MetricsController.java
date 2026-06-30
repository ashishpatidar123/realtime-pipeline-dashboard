package com.pipeline.api;

import com.pipeline.model.MetricsSnapshot;
import com.pipeline.persistence.entity.MetricsSnapshotEntity;
import com.pipeline.persistence.repository.MetricsSnapshotRepository;
import com.pipeline.processing.AggregationEngine;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/api/metrics")
@CrossOrigin(origins = "*")
public class MetricsController {

    private final AggregationEngine aggregationEngine;
    private final MetricsSnapshotRepository metricsSnapshotRepository;

    public MetricsController(AggregationEngine aggregationEngine,
                             MetricsSnapshotRepository metricsSnapshotRepository) {
        this.aggregationEngine = aggregationEngine;
        this.metricsSnapshotRepository = metricsSnapshotRepository;
    }

    @GetMapping("/current")
    public MetricsSnapshot getCurrentMetrics() {
        return aggregationEngine.snapshot();
    }

    @GetMapping("/history")
    public List<MetricsSnapshotEntity> getHistory(
            @RequestParam(defaultValue = "1") int hours) {
        Instant since = Instant.now().minus(hours, ChronoUnit.HOURS);
        return metricsSnapshotRepository.findByTimestampAfterOrderByTimestampAsc(since);
    }
}
