import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer } from 'recharts';

export default function TopSymbols({ symbols }) {
  if (!symbols || symbols.length === 0) {
    return (
      <div className="card">
        <h3 className="text-sm font-semibold text-dark-muted uppercase tracking-wider mb-4">
          Top Symbols by Volume
        </h3>
        <div className="text-center text-dark-muted py-8">Waiting for data...</div>
      </div>
    );
  }

  const data = symbols.slice(0, 10).map(s => ({
    symbol: s.symbol,
    volume: Math.round(s.totalVolume),
    vwap: s.vwap,
  }));

  return (
    <div className="card">
      <h3 className="text-sm font-semibold text-dark-muted uppercase tracking-wider mb-4">
        Top Symbols by Volume
      </h3>
      <div className="h-64">
        <ResponsiveContainer width="100%" height="100%">
          <BarChart data={data} layout="vertical" margin={{ left: 10 }}>
            <XAxis type="number" stroke="#64748b" tick={{ fontSize: 11 }} />
            <YAxis
              type="category"
              dataKey="symbol"
              stroke="#64748b"
              tick={{ fontSize: 11 }}
              width={70}
            />
            <Tooltip
              contentStyle={{
                backgroundColor: '#1e293b',
                border: '1px solid #334155',
                borderRadius: '8px',
                fontSize: 12,
              }}
              formatter={(value) => [value.toLocaleString(), 'Volume']}
            />
          </BarChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}