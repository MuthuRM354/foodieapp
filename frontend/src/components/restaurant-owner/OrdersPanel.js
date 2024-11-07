import React, { useState, useEffect } from 'react';
import LoadingStates from '../common/LoadingStates';
import { useToast } from '../../hooks/useToast';
import Modal from '../common/Modal';

const OrdersPanel = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('new');
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const { showToast } = useToast();

  useEffect(() => {
    fetchOrders();
  }, [filter]);

  const fetchOrders = async () => {
    setLoading(true);
    try {
      // API call to fetch orders based on filter
      setLoading(false);
    } catch (error) {
      showToast('Failed to fetch orders', 'error');
      setLoading(false);
    }
  };

  const handleStatusUpdate = async (orderId, newStatus) => {
    try {
      // API call to update order status
      showToast(`Order ${newStatus} successfully`, 'success');
      setOrders(orders.map(order =>
        order.id === orderId ? { ...order, status: newStatus } : order
      ));
    } catch (error) {
      showToast('Failed to update order status', 'error');
    }
  };

  const calculateTotalTime = (order) => {
    const preparationTimes = order.items.map(item => parseInt(item.preparationTime));
    return Math.max(...preparationTimes);
  };

  const renderOrderCard = (order) => (
    <div key={order.id} className={`order-card ${order.status}`}>
      <div className="order-header">
        <div className="order-info">
          <h3>Order #{order.id}</h3>
          <span className={`status-badge ${order.status}`}>
            {order.status}
          </span>
        </div>
        <div className="time-info">
          <span>{new Date(order.createdAt).toLocaleTimeString()}</span>
          <span>Est. Time: {calculateTotalTime(order)} mins</span>
        </div>
      </div>

      <div className="order-items">
        {order.items.map(item => (
          <div key={item.id} className="order-item">
            <span className="quantity">{item.quantity}x</span>
            <span className="name">{item.name}</span>
            <span className="notes">{item.specialInstructions}</span>
            <span className="price">${(item.price * item.quantity).toFixed(2)}</span>
          </div>
        ))}
      </div>

      <div className="order-footer">
        <div className="customer-info">
          <h4>Customer Details</h4>
          <p><i className="fas fa-user"></i> {order.customer.name}</p>
          <p><i className="fas fa-phone"></i> {order.customer.phone}</p>
          <p><i className="fas fa-map-marker-alt"></i> {order.deliveryAddress}</p>
        </div>
        <div className="order-actions">
          {order.status === 'new' && (
            <>
              <button
                className="accept-btn"
                onClick={() => handleStatusUpdate(order.id, 'preparing')}
              >
                <i className="fas fa-check"></i> Accept
              </button>
              <button
                className="reject-btn"
                onClick={() => handleStatusUpdate(order.id, 'rejected')}
              >
                <i className="fas fa-times"></i> Reject
              </button>
            </>
          )}
          {order.status === 'preparing' && (
            <button
              className="ready-btn"
              onClick={() => handleStatusUpdate(order.id, 'ready')}
            >
              <i className="fas fa-utensils"></i> Mark Ready
            </button>
          )}
          <button
            className="details-btn"
            onClick={() => {
              setSelectedOrder(order);
              setIsModalOpen(true);
            }}
          >
            <i className="fas fa-info-circle"></i> View Details
          </button>
        </div>
      </div>
    </div>
  );

  return (
    <div className="orders-panel">
      <div className="panel-header">
        <h2>Orders Management</h2>
        <div className="order-filters">
          {['new', 'preparing', 'ready', 'completed'].map(status => (
            <button
              key={status}
              className={`filter-btn ${filter === status ? 'active' : ''}`}
              onClick={() => setFilter(status)}
            >
              {status.charAt(0).toUpperCase() + status.slice(1)}
              {status === 'new' && orders.filter(o => o.status === 'new').length > 0 && (
                <span className="order-count">
                  {orders.filter(o => o.status === 'new').length}
                </span>
              )}
            </button>
          ))}
        </div>
      </div>

      {loading ? (
        <LoadingStates />
      ) : (
        <div className="orders-grid">
          {orders
            .filter(order => filter === 'all' || order.status === filter)
            .map(order => renderOrderCard(order))}
        </div>
      )}

      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title="Order Details"
      >
        {selectedOrder && (
          <div className="order-details">
            {/* Modal content implementation */}
          </div>
        )}
      </Modal>
    </div>
  );
};

export default OrdersPanel;
