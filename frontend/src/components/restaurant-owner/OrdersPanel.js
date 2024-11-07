import React, { useState, useEffect } from 'react';
import LoadingStates from '../common/LoadingStates';
import { useToast } from '../../hooks/useToast';

const OrdersPanel = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('new');
  const { showToast } = useToast();

  const handleStatusUpdate = async (orderId, status) => {
    try {
      // API call to update order status
      showToast(`Order ${status} successfully`, 'success');
    } catch (error) {
      showToast('Failed to update order status', 'error');
    }
  };

  return (
    <div className="orders-panel">
      <div className="header">
        <h2>Orders Management</h2>
        <div className="filters">
          <button
            className={filter === 'new' ? 'active' : ''}
            onClick={() => setFilter('new')}
          >
            New Orders
          </button>
          <button
            className={filter === 'preparing' ? 'active' : ''}
            onClick={() => setFilter('preparing')}
          >
            Preparing
          </button>
          <button
            className={filter === 'ready' ? 'active' : ''}
            onClick={() => setFilter('ready')}
          >
            Ready
          </button>
          <button
            className={filter === 'completed' ? 'active' : ''}
            onClick={() => setFilter('completed')}
          >
            Completed
          </button>
        </div>
      </div>

      {loading ? (
        <LoadingStates />
      ) : (
        <div className="orders-grid">
          {orders.map(order => (
            <div key={order.id} className="order-card">
              <div className="order-header">
                <h3>Order #{order.id}</h3>
                <span className={`status ${order.status}`}>
                  {order.status}
                </span>
              </div>

              <div className="order-items">
                {order.items.map(item => (
                  <div key={item.id} className="order-item">
                    <span>{item.quantity}x</span>
                    <span>{item.name}</span>
                    <span>${item.price}</span>
                  </div>
                ))}
              </div>

              <div className="order-footer">
                <div className="total">
                  <span>Total:</span>
                  <span>${order.total}</span>
                </div>
                <div className="actions">
                  {order.status === 'new' && (
                    <button onClick={() => handleStatusUpdate(order.id, 'accept')}>
                      Accept
                    </button>
                  )}
                  {order.status === 'preparing' && (
                    <button onClick={() => handleStatusUpdate(order.id, 'ready')}>
                      Mark Ready
                    </button>
                  )}
                  <button onClick={() => handleStatusUpdate(order.id, 'cancel')}>
                    Cancel
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default OrdersPanel;