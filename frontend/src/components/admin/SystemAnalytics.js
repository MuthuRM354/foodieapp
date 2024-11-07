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

const SystemAnalytics = () => {
  const [analytics, setAnalytics] = useState({
    userGrowth: [],
    orderStats: [],
    revenueData: [],
    restaurantPerformance: []
  });
  const [timeRange, setTimeRange] = useState('week');
  const [loading, setLoading] = useState(true);
  const [selectedMetrics, setSelectedMetrics] = useState(['users', 'orders', 'revenue']);

  const handleMetricToggle = (metric) => {
    setSelectedMetrics(prev =>
      prev.includes(metric)
        ? prev.filter(m => m !== metric)
        : [...prev, metric]
    );
  };

  return (
    <div className="system-analytics">
      <div className="analytics-header">
        <h2>System Analytics</h2>
        <div className="controls">
          <select
            value={timeRange}
            onChange={(e) => setTimeRange(e.target.value)}
            className="time-range-select"
          >
            <option value="day">Last 24 Hours</option>
            <option value="week">Last Week</option>
            <option value="month">Last Month</option>
            <option value="quarter">Last Quarter</option>
            <option value="year">Last Year</option>
          </select>
          <div className="metric-toggles">
            {['users', 'orders', 'revenue', 'restaurants'].map(metric => (
              <button
                key={metric}
                className={`metric-toggle ${selectedMetrics.includes(metric) ? 'active' : ''}`}
                onClick={() => handleMetricToggle(metric)}
              >
                {metric.charAt(0).toUpperCase() + metric.slice(1)}
              </button>
            ))}
          </div>
        </div>
      </div>

      {loading ? (
        <LoadingStates type="skeleton" />
      ) : (
        <div className="analytics-grid">
          <div className="chart-container">
            <h3>User Growth Trends</h3>
            <ResponsiveContainer width="100%" height={300}>
              <LineChart data={analytics.userGrowth}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="date" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Line type="monotone" dataKey="newUsers" stroke="#8884d8" name="New Users" />
                <Line type="monotone" dataKey="activeUsers" stroke="#82ca9d" name="Active Users" />
              </LineChart>
            </ResponsiveContainer>
          </div>

          <div className="chart-container">
            <h3>Order Statistics</h3>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={analytics.orderStats}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="date" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Bar dataKey="completed" stackId="a" fill="#82ca9d" name="Completed" />
                <Bar dataKey="pending" stackId="a" fill="#ffc658" name="Pending" />
                <Bar dataKey="cancelled" stackId="a" fill="#ff8042" name="Cancelled" />
              </BarChart>
            </ResponsiveContainer>
          </div>

          <div className="chart-container">
            <h3>Revenue Distribution</h3>
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie
                  data={analytics.revenueData}
                  dataKey="value"
                  nameKey="category"
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
            <h3>Restaurant Performance</h3>
            <ResponsiveContainer width="100%" height={300}>
              <AreaChart data={analytics.restaurantPerformance}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="date" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Area type="monotone" dataKey="orders" stackId="1" stroke="#8884d8" fill="#8884d8" />
                <Area type="monotone" dataKey="revenue" stackId="2" stroke="#82ca9d" fill="#82ca9d" />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>
      )}
    </div>
  );
};

export default SystemAnalytics;
