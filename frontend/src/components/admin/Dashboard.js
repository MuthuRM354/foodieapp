import React, { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import LoadingStates from '../common/LoadingStates';
import {
  LineChart,
  BarChart,
  Line,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend
} from 'recharts';

const Dashboard = () => {
  const [stats, setStats] = useState({
    totalUsers: 0,
    totalRestaurants: 0,
    activeOrders: 0,
    revenue: 0,
    pendingApprovals: 0
  });

  const [recentActivity, setRecentActivity] = useState([]);
  const [loading, setLoading] = useState(true);
  const [timeRange, setTimeRange] = useState('week');

  const handleTimeRangeChange = (range) => {
    setTimeRange(range);
    // Fetch new data based on time range
  };

  return (
    <div className="admin-dashboard">
      <div className="dashboard-header">
        <h1>Admin Dashboard</h1>
        <div className="time-range-selector">
          <select value={timeRange} onChange={(e) => handleTimeRangeChange(e.target.value)}>
            <option value="day">Today</option>
            <option value="week">This Week</option>
            <option value="month">This Month</option>
            <option value="year">This Year</option>
          </select>
        </div>
      </div>

      {loading ? (
        <LoadingStates type="skeleton" />
      ) : (
        <>
          <div className="stats-grid">
            <div className="stat-card">
              <h3>Total Users</h3>
              <p>{stats.totalUsers}</p>
              <span className="trend-indicator">↑ 12%</span>
            </div>
            <div className="stat-card">
              <h3>Total Restaurants</h3>
              <p>{stats.totalRestaurants}</p>
              <span className="trend-indicator">↑ 8%</span>
            </div>
            <div className="stat-card">
              <h3>Active Orders</h3>
              <p>{stats.activeOrders}</p>
              <span className="trend-indicator">↑ 15%</span>
            </div>
            <div className="stat-card">
              <h3>Revenue</h3>
              <p>${stats.revenue}</p>
              <span className="trend-indicator">↑ 20%</span>
            </div>
            <div className="stat-card">
              <h3>Pending Approvals</h3>
              <p>{stats.pendingApprovals}</p>
            </div>
          </div>

          <div className="charts-grid">
            <div className="chart-container">
              <h3>User Growth</h3>
              <LineChart width={600} height={300} data={recentActivity}>
                <XAxis dataKey="date" />
                <YAxis />
                <CartesianGrid strokeDasharray="3 3" />
                <Tooltip />
                <Legend />
                <Line type="monotone" dataKey="users" stroke="#8884d8" />
              </LineChart>
            </div>

            <div className="chart-container">
              <h3>Revenue Trends</h3>
              <BarChart width={600} height={300} data={recentActivity}>
                <XAxis dataKey="date" />
                <YAxis />
                <CartesianGrid strokeDasharray="3 3" />
                <Tooltip />
                <Legend />
                <Bar dataKey="revenue" fill="#82ca9d" />
              </BarChart>
            </div>
          </div>

          <div className="recent-activity">
            <h3>Recent Activity</h3>
            <div className="activity-list">
              {recentActivity.map((activity, index) => (
                <div key={index} className="activity-item">
                  <span className="activity-type">{activity.type}</span>
                  <span className="activity-description">{activity.description}</span>
                  <span className="activity-time">{activity.time}</span>
                </div>
              ))}
            </div>
          </div>
        </>
      )}
    </div>
  );
};

export default Dashboard;
