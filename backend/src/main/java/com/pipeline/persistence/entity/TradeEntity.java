package com.pipeline.persistence.entity;


import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Entity
@Table(name = "trades")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trade_id", nullable = false, unique = true, length = 50)
    private String tradeId;

    @Column(nullable = false, length = 20)
    private String symbol;

    @Column(nullable = false, length = 4)
    private String side;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private int quantity;

    @Column(length = 20)
    private String venue;

    @Column(name = "asset_class", length = 20)
    private String assetClass;

    @Column(name = "trader_desk", length = 20)
    private String traderDesk;

    @Column(name = "event_timestamp", nullable = false)
    private String eventTimestamp;

    @Column(name = "ingested_at", nullable= false)
    private String ingestedAt;
    
}