import React, { useState, useEffect } from 'react';
import {
  LineChart,
  BarChart,
  PieChart,
  Line,
  Bar,
  Pie,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend
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

  return (
    <div className="system-analytics">
      <div className="analytics-header">
        <h2>System Analytics</h2>
        <select
          value={timeRange}
          onChange={(e) => setTimeRange(e.target.value)}
        >
          <option value="week">Last Week</option>
          <option value="month">Last Month</option>
          <option value="year">Last Year</option>
        </select>
      </div>

      {loading ? (
        <LoadingStates />
      ) : (
        <div className="analytics-grid">
          <div className="chart-container">
            <h3>User Growth</h3>
            <LineChart width={500} height={300} data={analytics.userGrowth}>
              <XAxis dataKey="date" />
              <YAxis />
              <CartesianGrid strokeDasharray="3 3" />
              <Tooltip />
              <Legend />
              <Line type="monotone" dataKey="users" stroke="#8884d8" />
            </LineChart>
          </div>

          <div className="chart-container">
            <h3>Order Statistics</h3>
            <BarChart width={500} height={300} data={analytics.orderStats}>
              <XAxis dataKey="date" />
              <YAxis />
              <CartesianGrid strokeDasharray="3 3" />
              <Tooltip />
              <Legend />
              <Bar dataKey="orders" fill="#82ca9d" />
            </BarChart>
          </div>

          <div className="chart-container">
            <h3>Revenue Distribution</h3>
            <PieChart width={400} height={300}>
              <Pie
                data={analytics.revenueData}
                dataKey="value"
                nameKey="name"
                cx="50%"
                cy="50%"
                fill="#8884d8"
                label
              />
              <Tooltip />
              <Legend />
            </PieChart>
          </div>

          <div className="chart-container">
            <h3>Restaurant Performance</h3>
            <BarChart width={500} height={300} data={analytics.restaurantPerformance}>
              <XAxis dataKey="name" />
              <YAxis />
              <CartesianGrid strokeDasharray="3 3" />
              <Tooltip />
              <Legend />
              <Bar dataKey="orders" fill="#8884d8" />
              <Bar dataKey="revenue" fill="#82ca9d" />
            </BarChart>
          </div>
        </div>
      )}
    </div>
  );
};

export default SystemAnalytics;