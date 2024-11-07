import React, { useState, useEffect } from 'react';
import {
  LineChart, Line,
  BarChart, Bar,
  PieChart, Pie,
  AreaChart, Area,
  XAxis, YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer
} from 'recharts';
import LoadingStates from '../common/LoadingStates';
import { useToast } from '../../hooks/useToast';

const Analytics = () => {
  const [timeRange, setTimeRange] = useState('week');
  const [metrics, setMetrics] = useState({
    sales: [],
    orders: [],
    items: [],
    customers: []
  });
  const [loading, setLoading] = useState(true);
  const { showToast } = useToast();

  useEffect(() => {
    fetchAnalyticsData();
  }, [timeRange]);

  const fetchAnalyticsData = async () => {
    setLoading(true);
    try {
      // API call to fetch analytics data based on timeRange
      setLoading(false);
    } catch (error) {
      showToast('Failed to fetch analytics data', 'error');
      setLoading(false);
    }
  };

  const renderMetricCards = () => (
    <div className="metric-cards">
      <div className="metric-card">
        <h3>Total Revenue</h3>
        <p className="amount">${metrics.totalRevenue}</p>
        <span className="trend up">↑ 15%</span>
      </div>
      <div className="metric-card">
        <h3>Total Orders</h3>
        <p className="amount">{metrics.totalOrders}</p>
        <span className="trend up">↑ 8%</span>
      </div>
      <div className="metric-card">
        <h3>Average Order Value</h3>
        <p className="amount">${metrics.avgOrderValue}</p>
        <span className="trend up">↑ 5%</span>
      </div>
      <div className="metric-card">
        <h3>New Customers</h3>
        <p className="amount">{metrics.newCustomers}</p>
        <span className="trend down">↓ 3%</span>
      </div>
    </div>
  );

  return (
    <div className="restaurant-analytics">
      <div className="analytics-header">
        <h2>Restaurant Analytics</h2>
        <select
          value={timeRange}
          onChange={(e) => setTimeRange(e.target.value)}
          className="time-range-select"
        >
          <option value="today">Today</option>
          <option value="week">This Week</option>
          <option value="month">This Month</option>
          <option value="year">This Year</option>
        </select>
      </div>

      {loading ? (
        <LoadingStates type="skeleton" />
      ) : (
        <div className="analytics-grid">
          {renderMetricCards()}

          <div className="chart-container">
            <h3>Sales Overview</h3>
            <ResponsiveContainer width="100%" height={300}>
              <AreaChart data={metrics.sales}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="date" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Area
                  type="monotone"
                  dataKey="revenue"
                  stroke="#8884d8"
                  fill="#8884d8"
                  fillOpacity={0.3}
                />
                <Area
                  type="monotone"
                  dataKey="orders"
                  stroke="#82ca9d"
                  fill="#82ca9d"
                  fillOpacity={0.3}
                />
              </AreaChart>
            </ResponsiveContainer>
          </div>

          <div className="chart-container">
            <h3>Popular Items</h3>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={metrics.items}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Bar dataKey="quantity" fill="#8884d8" />
                <Bar dataKey="revenue" fill="#82ca9d" />
              </BarChart>
            </ResponsiveContainer>
          </div>

          <div className="chart-container">
            <h3>Order Distribution</h3>
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie
                  data={metrics.orderTypes}
                  dataKey="value"
                  nameKey="name"
                  cx="50%"
                  cy="50%"
                  outerRadius={100}
                  fill="#8884d8"
                  label
                />
                <Tooltip />
                <Legend />
              </PieChart>
            </ResponsiveContainer>
          </div>

          <div className="chart-container">
            <h3>Customer Retention</h3>
            <ResponsiveContainer width="100%" height={300}>
              <LineChart data={metrics.customers}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="date" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Line
                  type="monotone"
                  dataKey="new"
                  stroke="#8884d8"
                  strokeWidth={2}
                />
                <Line
                  type="monotone"
                  dataKey="returning"
                  stroke="#82ca9d"
                  strokeWidth={2}
                />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </div>
      )}
    </div>
  );
};

export default Analytics;
