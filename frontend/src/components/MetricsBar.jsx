import { Gauge, Clock, AlertTriangle, Hash, Users } from 'lucide-react';
import MetricCard from './MetricCard';

export default function MetricsBar({ metrics }) {
  if (!metrics) {
    return (
      <div className="grid grid-cols-2 md:grid-cols-5 gap-4">
        {[...Array(5)].map((_, i) => (
          <div key={i} className="card animate-pulse h-20" />
        ))}
      </div>
    );
  }

  return (
    <div className="grid grid-cols-2 md:grid-cols-5 gap-4">
      <MetricCard
        title="Throughput"
        value={Math.round(metrics.throughputPerSecond).toLocaleString()}
        unit="/sec"
        icon={Gauge}
        color="blue"
      />
      <MetricCard
        title="Latency p50"
        value={metrics.latencyP50Ms?.toFixed(1) ?? '-'}
        unit="ms"
        icon={Clock}
        color="green"
      />
      <MetricCard
        title="Latency p99"
        value={metrics.latencyP99Ms?.toFixed(1) ?? '-'}
        unit="ms"
        icon={Clock}
        color="yellow"
      />
      <MetricCard
        title="Error Rate"
        value={(metrics.errorRate * 100).toFixed(3)}
        unit="%"
        icon={AlertTriangle}
        color={metrics.errorRate > 0.01 ? 'red' : 'green'}
      />
      <MetricCard
        title="Total Events"
        value={(metrics.totalEventsProcessed / 1000).toFixed(1)}
        unit="K"
        icon={Hash}
        color="purple"
      />
    </div>
  );
}