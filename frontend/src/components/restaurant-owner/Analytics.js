import React, { useState, useEffect } from 'react';
import LoadingStates from '../common/LoadingStates';
import { Line, Bar, Pie } from 'react-chartjs-2';

const Analytics = () => {
  const [timeFrame, setTimeFrame] = useState('week'); // week, month, year
  const [analytics, setAnalytics] = useState({
    revenue: [],
    orders: [],
    topItems: [],
    customerStats: {}
  });
  const [loading, setLoading] = useState(true);

  return (
    <div className="restaurant-analytics">
      <h2>Restaurant Analytics</h2>

      <div className="time-filter">
        <button
          className={timeFrame === 'week' ? 'active' : ''}
          onClick={() => setTimeFrame('week')}
        >
          Weekly
        </button>
        <button
          className={timeFrame === 'month' ? 'active' : ''}
          onClick={() => setTimeFrame('month')}
        >
          Monthly
        </button>
        <button
          className={timeFrame === 'year' ? 'active' : ''}
          onClick={() => setTimeFrame('year')}
        >
          Yearly
        </button>
      </div>

      {loading ? (
        <LoadingStates />
      ) : (
        <div className="analytics-dashboard">
          <div className="chart-container">
            <h3>Revenue Overview</h3>
            <Line
              data={analytics.revenue}
              options={{
                responsive: true,
                maintainAspectRatio: false
              }}
            />
          </div>

          <div className="chart-container">
            <h3>Order Trends</h3>
            <Bar
              data={analytics.orders}
              options={{
                responsive: true,
                maintainAspectRatio: false
              }}
            />
          </div>

          <div className="chart-container">
            <h3>Top Selling Items</h3>
            <Pie
              data={analytics.topItems}
              options={{
                responsive: true,
                maintainAspectRatio: false
              }}
            />
          </div>

          <div className="stats-grid">
            <div className="stat-card">
              <h4>Total Orders</h4>
              <p>{analytics.customerStats.totalOrders}</p>
            </div>
            <div className="stat-card">
              <h4>Average Order Value</h4>
              <p>${analytics.customerStats.avgOrderValue}</p>
            </div>
            <div className="stat-card">
              <h4>Customer Satisfaction</h4>
              <p>{analytics.customerStats.satisfaction}%</p>
            </div>
            <div className="stat-card">
              <h4>Repeat Customers</h4>
              <p>{analytics.customerStats.repeatCustomers}%</p>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Analytics;
