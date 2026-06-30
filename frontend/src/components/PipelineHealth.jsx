import { Server, Database, Wifi, Clock } from 'lucide-react';

function StatusItem({ icon: Icon, name, status, message }) {
  const statusClass = status === 'UP' ? 'status-up' : status === 'DOWN' ? 'status-down' : 'status-degraded';
  const textClass = status === 'UP' ? 'text-emerald-400' : status === 'DOWN' ? 'text-red-400' : 'text-yellow-400';

  return (
    <div className="flex items-center justify-between py-2 border-b border-dark-border/50 last:border-0">
      <div className="flex items-center gap-2">
        <Icon className="w-4 h-4 text-dark-muted" />
        <span className="text-sm">{name}</span>
      </div>
      <div className="flex items-center gap-2">
        <span className={`status-dot ${statusClass}`} />
        <span className={`text-xs ${textClass}`}>{message || status}</span>
      </div>
    </div>
  );
}

export default function PipelineHealthPanel({ health }) {
  if (!health) {
    return (
      <div className="card">
        <h3 className="text-sm font-semibold text-dark-muted uppercase tracking-wider mb-4">
          Pipeline Health
        </h3>
        <div className="text-center text-dark-muted py-4">Waiting...</div>
      </div>
    );
  }

  const uptimeFormatted = health.uptimeSeconds
    ? `${Math.floor(health.uptimeSeconds / 3600)}h ${Math.floor((health.uptimeSeconds % 3600) / 60)}m`
    : '-';

  return (
    <div className="card">
      <h3 className="text-sm font-semibold text-dark-muted uppercase tracking-wider mb-4">
        Pipeline Health
      </h3>
      <div className="space-y-0">
        {health.kafka && (
          <StatusItem icon={Server} name="Kafka" status={health.kafka.status} message={health.kafka.message} />
        )}
        {health.database && (
          <StatusItem icon={Database} name="PostgreSQL" status={health.database.status} message={health.database.message} />
        )}
        {health.websocket && (
          <StatusItem icon={Wifi} name="WebSocket" status={health.websocket.status} message={health.websocket.message} />
        )}
        <div className="flex items-center justify-between py-2">
          <div className="flex items-center gap-2">
            <Clock className="w-4 h-4 text-dark-muted" />
            <span className="text-sm">Uptime</span>
          </div>
          <span className="text-xs text-dark-muted">{uptimeFormatted}</span>
        </div>
      </div>
    </div>
  );
}