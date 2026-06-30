export default function MetricCard({ title, value, unit, icon: Icon, color = 'blue' }) {
  const colorMap = {
    blue: 'text-blue-400 bg-blue-400/10',
    green: 'text-emerald-400 bg-emerald-400/10',
    yellow: 'text-yellow-400 bg-yellow-400/10',
    red: 'text-red-400 bg-red-400/10',
    purple: 'text-purple-400 bg-purple-400/10',
  };

  return (
    <div className="card flex items-center gap-4">
      <div className={`p-3 rounded-lg ${colorMap[color]}`}>
        <Icon className="w-6 h-6" />
      </div>
      <div>
        <p className="text-dark-muted text-xs uppercase tracking-wider">{title}</p>
        <p className="text-2xl font-bold tabular-nums">
          {value}
          {unit && <span className="text-sm text-dark-muted ml-1">{unit}</span>}
        </p>
      </div>
    </div>
  );
}