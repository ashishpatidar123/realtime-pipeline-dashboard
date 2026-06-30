import Header from './components/Header';
import MetricsBar from './components/MetricsBar';
import ThroughputChart from './components/ThroughputChart';
import LatencyChart from './components/LatencyChart';
import TradeTable from './components/TradeTable';
import TopSymbols from './components/TopSymbols';
import PipelineHealthPanel from './components/PipelineHealth';
import useWebSocket from './hooks/useWebSocket';
import useMetricsHistory from './hooks/useMetrics';

export default function App() {
  const { connected, metrics, trades, health } = useWebSocket();
  const history = useMetricsHistory(metrics);

  return (
    <div className="min-h-screen bg-dark-bg">
      <Header connected={connected} />

      <main className="p-6 space-y-6 max-w-screen-2xl mx-auto">
        {/* Top metrics row */}
        <MetricsBar metrics={metrics} />

        {/* Charts row */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <ThroughputChart history={history} />
          <LatencyChart history={history} />
        </div>

        {/* Bottom row: trade table + sidebar */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="lg:col-span-2">
            <TradeTable trades={trades} />
          </div>
          <div className="space-y-6">
            <TopSymbols symbols={metrics?.topSymbols} />
            <PipelineHealthPanel health={health} />
          </div>
        </div>
      </main>

      <footer className="text-center text-dark-muted text-xs py-4 border-t border-dark-border">
        Real-Time Pipeline Dashboard &middot; Spring Boot + Kafka + React
        {metrics && (
          <span> &middot; {Math.round(metrics.totalEventsProcessed).toLocaleString()} events processed</span>
        )}
      </footer>
    </div>
  );
}