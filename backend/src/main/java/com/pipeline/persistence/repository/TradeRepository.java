package com.pipeline.persistence.repository;
import com.pipeline.persistence.entity.TradeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<TradeEntity, Long> {

    List<TradeEntity> findTop50ByOrderByEventTimestampDesc();
    List<TradeEntity> findBySymbolOrderByEventTimestampDesc(String symbol);
    void deleteByEventTimestampBefore(Instant cutoff);

}
