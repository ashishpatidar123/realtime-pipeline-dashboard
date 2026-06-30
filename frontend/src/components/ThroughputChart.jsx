import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

export default function ThroughputChart({ history }) {
  return (
    <div className="card">
      <h3 className="text-sm font-semibold text-dark-muted uppercase tracking-wider mb-4">
        Throughput (events/sec)
      </h3>
      <div className="h-64">
        {history.length === 0 ? (
          <div className="flex items-center justify-center h-full text-dark-muted">
            Waiting for data...
          </div>
        ) : (
          <ResponsiveContainer width="100%" height="100%">
            <LineChart data={history}>
              <CartesianGrid strokeDasharray="3 3" stroke="#334155" />
              <XAxis
                dataKey="time"
                stroke="#64748b"
                tick={{ fontSize: 11 }}
                interval="preserveStartEnd"
              />
              <YAxis stroke="#64748b" tick={{ fontSize: 11 }} />
              <Tooltip 
                contentStyle={{
                  backgroundColor: '#1e293b',
                  border: '1px solid #334155',
                    borderRadius: '8px',
                    fontSize: 12,
                }}
              />
              <Line
                type="monotone"
                dataKey="throughput"
                stroke="#3b82f6"
                strokeWidth={2}
                dot={false}
                activeDot={{ r: 6 }}
              />
            </LineChart>
          </ResponsiveContainer>
        )}
      </div>
    </div>
  );
}