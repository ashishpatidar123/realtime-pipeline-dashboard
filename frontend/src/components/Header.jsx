import { Activity } from 'lucide-react';

export default function Header({ connected }) {
  return (
    <header className="flex items-center justify-between px-6 py-4 border-b border-dark-border">
      <div className="flex items-center gap-3">
        <Activity className="w-7 h-7 text-blue-400" />
        <h1 className="text-xl font-bold tracking-tight">Real-Time Pipeline Dashboard</h1>
      </div>
      <div className="flex items-center gap-2 text-sm">
        <span className={`status-dot ${connected ? 'status-up' : 'status-down'}`} />
        <span className={connected ? 'text-emerald-400' : 'text-red-400'}>
          {connected ? 'Connected' : 'Reconnecting...'}
        </span>
      </div>
    </header>
  );
}