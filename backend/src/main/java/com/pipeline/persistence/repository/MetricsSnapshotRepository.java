package com.pipeline.persistence.repository;

import com.pipeline.persistence.entity.MetricsSnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface MetricsSnapshotRepository extends JpaRepository<MetricsSnapshotEntity, Long> {
    List<MetricsSnapshotEntity> findByTimestampAfterOrderByTimestampAsc(Instant after);
}
