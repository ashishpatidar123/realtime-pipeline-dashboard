import axios from 'axios';

const API_BASE = '/api';

const api = axios.create({
  baseURL: API_BASE,
  timeout: 10000,
});

export const fetchCurrentMetrics = () => api.get('/metrics/current').then(r => r.data);
export const fetchMetricsHistory = (hours = 1) => api.get(`/metrics/history?hours=${hours}`).then(r => r.data);
export const fetchRecentTrades = (limit = 20) => api.get(`/trades/recent?limit=${limit}`).then(r => r.data);
export const fetchHealth = () => api.get('/health').then(r => r.data);

export default api;