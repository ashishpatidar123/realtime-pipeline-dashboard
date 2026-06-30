package com.pipeline.api;

import com.pipeline.model.TradeEvent;
import com.pipeline.persistence.entity.TradeEntity;
import com.pipeline.persistence.repository.TradeRepository;
import com.pipeline.processing.AggregationEngine;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trades")
@CrossOrigin(origins = "*")
public class TradeController {

    private final AggregationEngine aggregationEngine;
    private final TradeRepository tradeRepository;

    public TradeController(AggregationEngine aggregationEngine,
                           TradeRepository tradeRepository) {
        this.aggregationEngine = aggregationEngine;
        this.tradeRepository = tradeRepository;
    }

    @GetMapping("/recent")
    public List<TradeEvent> getRecentTrades(
            @RequestParam(defaultValue = "20") int limit) {
        return aggregationEngine.getRecentTrades(Math.min(limit, 50));
    }

    @GetMapping("/symbol/{symbol}")
    public List<TradeEntity> getTradesBySymbol(@PathVariable String symbol) {
        return tradeRepository.findBySymbolOrderByEventTimestampDesc(symbol);
    }
}