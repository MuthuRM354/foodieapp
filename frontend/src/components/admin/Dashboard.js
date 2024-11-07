import React, { useState, useEffect } from 'react';
import LoadingStates from '../common/LoadingStates';

const Dashboard = () => {
  const [stats, setStats] = useState({
    totalUsers: 0,
    totalRestaurants: 0,
    activeOrders: 0,
    revenue: 0,
    pendingApprovals: 0
  });

  return (
    <div className="admin-dashboard">
      <h1>Admin Dashboard</h1>

      <div className="stats-grid">
        <div className="stat-card">
          <h3>Total Users</h3>
          <p>{stats.totalUsers}</p>
        </div>
        <div className="stat-card">
          <h3>Total Restaurants</h3>
          <p>{stats.totalRestaurants}</p>
        </div>
        <div className="stat-card">
          <h3>Active Orders</h3>
          <p>{stats.activeOrders}</p>
        </div>
        <div className="stat-card">
          <h3>Revenue</h3>
          <p>${stats.revenue}</p>
        </div>
        <div className="stat-card">
          <h3>Pending Approvals</h3>
          <p>{stats.pendingApprovals}</p>
        </div>
      </div>

      <div className="recent-activity">
        <h2>Recent Activity</h2>
        {/* Activity log component */}
      </div>
    </div>
  );
};

export default Dashboard;