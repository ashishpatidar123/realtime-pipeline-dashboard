package com.pipeline.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TradeEvent {

    private String tradeId;
    private String symbol;
    private double price;
    private double quantity;
    private String side; // "buy" or "sell"
    private Instant timestamp;
    private String venue;
    private String traderDesk;
    private String assetClass; // equity, fx, crypto
    private long generatedAt; // Epoch milliseconds when the event was generated
}