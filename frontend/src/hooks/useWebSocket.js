import { useEffect, useRef, useState, useCallback } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client/dist/sockjs';

const WS_URL = '/ws';

export default function useWebSocket() {
  const clientRef = useRef(null);
  const [connected, setConnected] = useState(false);
  const [metrics, setMetrics] = useState(null);
  const [trades, setTrades] = useState([]);
  const [health, setHealth] = useState(null);

  useEffect(() => {
    const client = new Client({
      webSocketFactory: () => new SockJS(WS_URL),
      reconnectDelay: 3000,
      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,
      onConnect: () => {
        setConnected(true);

        client.subscribe('/topic/metrics', (message) => {
          const data = JSON.parse(message.body);
          setMetrics(data);
        });

        client.subscribe('/topic/trades', (message) => {
          const data = JSON.parse(message.body);
          setTrades(data);
        });

        client.subscribe('/topic/health', (message) => {
          const data = JSON.parse(message.body);
          setHealth(data);
        });
      },
      onDisconnect: () => {
        setConnected(false);
      },
      onStompError: (frame) => {
        console.error('STOMP error:', frame.headers['message']);
        setConnected(false);
      },
    });

    client.activate();
    clientRef.current = client;

    return () => {
      if (client.active) {
        client.deactivate();
      }
    };
  }, []);

  return { connected, metrics, trades, health };
}