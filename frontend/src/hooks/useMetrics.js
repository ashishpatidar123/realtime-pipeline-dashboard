import { useState, useEffect, useRef } from 'react';

const MAX_HISTORY = 300; // 5 minutes of 1-second data

export default function useMetricsHistory(currentMetrics) {
  const [history, setHistory] = useState([]);

  useEffect(() => {
    if (!currentMetrics) return;

    setHistory(prev => {
      const newEntry = {
        time: new Date(currentMetrics.timestamp).toLocaleTimeString(),
        throughput: Math.round(currentMetrics.throughputPerSecond),
        p50: currentMetrics.latencyP50Ms,
        p95: currentMetrics.latencyP95Ms,
        p99: currentMetrics.latencyP99Ms,
        errorRate: (currentMetrics.errorRate * 100).toFixed(3),
      };
      const updated = [...prev, newEntry];
      return updated.length > MAX_HISTORY ? updated.slice(-MAX_HISTORY) : updated;
    });
  }, [currentMetrics]);

  return history;
}