export default function TradeTable({ trades }) {
  if (!trades || trades.length === 0) {
    return (
      <div className="card">
        <h3 className="text-sm font-semibold text-dark-muted uppercase tracking-wider mb-4">
          Live Trade Feed
        </h3>
        <div className="text-center text-dark-muted py-8">Waiting for trades...</div>
      </div>
    );
  }

  return (
    <div className="card">
      <h3 className="text-sm font-semibold text-dark-muted uppercase tracking-wider mb-4">
        Live Trade Feed
      </h3>
      <div className="overflow-x-auto">
        <table className="w-full text-sm">
          <thead>
            <tr className="text-dark-muted text-xs uppercase border-b border-dark-border">
              <th className="text-left py-2 px-2">Symbol</th>
              <th className="text-left py-2 px-2">Side</th>
              <th className="text-right py-2 px-2">Qty</th>
              <th className="text-right py-2 px-2">Price</th>
              <th className="text-left py-2 px-2">Venue</th>
              <th className="text-left py-2 px-2">Time</th>
            </tr>
          </thead>
          <tbody>
            {trades.map((trade, i) => (
              <tr
                key={trade.tradeId || i}
                className="border-b border-dark-border/50 hover:bg-dark-border/20 transition-colors"
              >
                <td className="py-1.5 px-2 font-mono font-semibold text-blue-400">
                  {trade.symbol}
                </td>
                <td className={`py-1.5 px-2 font-semibold ${trade.side === 'BUY' ? 'text-emerald-400' : 'text-red-400'}`}>
                  {trade.side}
                </td>
                <td className="py-1.5 px-2 text-right tabular-nums">
                  {Number(trade.quantity).toLocaleString()}
                </td>
                <td className="py-1.5 px-2 text-right tabular-nums font-mono">
                  {Number(trade.price).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                </td>
                <td className="py-1.5 px-2 text-dark-muted">{trade.venue}</td>
                <td className="py-1.5 px-2 text-dark-muted font-mono text-xs">
                  {trade.timestamp ? new Date(trade.timestamp).toLocaleTimeString() : '-'}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}