CREATE TABLE trades (
  id BIGSERIAL PRIMARY KEY,
  trade_id VARCHAR(50) UNIQUE NOT NULL,
  symbol VARCHAR(20) NOT NULL,
  side VARCHAR(4) NOT NULL,
  quantity DOUBLE PRECISION NOT NULL,
  price DOUBLE PRECISION NOT NULL,
  venue VARCHAR(20),
  asset_class VARCHAR(20),
  trader_desk VARCHAR(30),
  event_timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
  ingested_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_trades_symbol ON trades(symbol);
CREATE INDEX idx_trades_event_ts ON trades(event_timestamp);

CREATE TABLE metric_snapshots (
  id BIGSERIAL PRIMARY KEY,
  timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
  throughput DOUBLE PRECISION,
  latency_p50_ms DOUBLE PRECISION,
  latency_p95_ms DOUBLE PRECISION,
  latency_p99_ms DOUBLE PRECISION,
  error_rate DOUBLE PRECISION,
  active_symbols INTEGER,
  consumer_lag BIGINT DEFAULT 0
);

CREATE INDEX idx_metrics_ts ON metric_snapshots(timestamp);