import React, { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import LoadingStates from '../common/LoadingStates';
import { useToast } from '../../hooks/useToast';

const Orders = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('active');
  const { showToast } = useToast();

  useEffect(() => {
    fetchOrders();
  }, [activeTab]);

  const fetchOrders = async () => {
    setLoading(true);
    try {
      // API call to fetch orders based on activeTab
      setLoading(false);
    } catch (error) {
      showToast('Failed to fetch orders', 'error');
      setLoading(false);
    }
  };

  const renderOrderCard = (order) => (
    <div key={order.id} className="order-card">
      <div className="order-header">
        <div className="restaurant-info">
          <img src={order.restaurantImage} alt={order.restaurantName} />
          <div>
            <h3>{order.restaurantName}</h3>
            <p className="order-id">Order #{order.id}</p>
          </div>
        </div>
        <div className="order-status">
          <span className={`status-badge ${order.status.toLowerCase()}`}>
            {order.status}
          </span>
        </div>
      </div>

      <div className="order-items">
        {order.items.map((item, index) => (
          <div key={index} className="order-item">
            <span className="item-quantity">{item.quantity}x</span>
            <span className="item-name">{item.name}</span>
            <span className="item-price">${item.price}</span>
          </div>
        ))}
      </div>

      <div className="order-footer">
        <div className="order-meta">
          <p className="order-date">
            <i className="far fa-calendar"></i>
            {new Date(order.date).toLocaleDateString()}
          </p>
          <p className="order-total">
            <i className="fas fa-receipt"></i>
            Total: ${order.total}
          </p>
        </div>
        <div className="order-actions">
          <button className="track-btn">
            <i className="fas fa-map-marker-alt"></i>
            Track Order
          </button>
          <button className="reorder-btn">
            <i className="fas fa-redo"></i>
            Reorder
          </button>
        </div>
      </div>
    </div>
  );

  return (
    <div className="orders-page">
      <div className="orders-header">
        <h2>My Orders</h2>
        <div className="order-tabs">
          <button
            className={`tab ${activeTab === 'active' ? 'active' : ''}`}
            onClick={() => setActiveTab('active')}
          >
            Active Orders
          </button>
          <button
            className={`tab ${activeTab === 'past' ? 'active' : ''}`}
            onClick={() => setActiveTab('past')}
          >
            Past Orders
          </button>
        </div>
      </div>

      {loading ? (
        <LoadingStates />
      ) : (
        <div className="orders-list">
          {orders.length > 0 ? (
            orders.map(order => renderOrderCard(order))
          ) : (
            <div className="no-orders">
              <i className="fas fa-shopping-bag"></i>
              <h3>No orders found</h3>
              <p>Looks like you haven't placed any orders yet</p>
              <button className="browse-btn">Browse Restaurants</button>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default Orders;
