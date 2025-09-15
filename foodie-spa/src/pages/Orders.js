import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { FiClock, FiMapPin, FiRefreshCw, FiShoppingCart } from 'react-icons/fi';
import orderService from '../services/orderService';
import { useAuth } from '../contexts/AuthContext';
import LoadingSpinner from '../components/LoadingSpinner';
import toast from 'react-hot-toast';

const Orders = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('all');
  const { user } = useAuth();

  useEffect(() => {
    if (user) {
      loadOrders();
    }
  }, [user]);

  const loadOrders = async () => {
    try {
      const ordersData = await orderService.getUserOrders();
      setOrders(ordersData.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt)));
    } catch (error) {
      console.error('Error loading orders:', error);
      toast.error('Failed to load orders');
    } finally {
      setLoading(false);
    }
  };

  const handleReorder = async (order) => {
    try {
      // This would typically add items back to cart
      toast.success('Items added to cart!');
    } catch (error) {
      console.error('Error reordering:', error);
      toast.error('Failed to reorder');
    }
  };

  const getFilteredOrders = () => {
    if (filter === 'all') return orders;
    return orders.filter(order => order.status?.toLowerCase() === filter.toLowerCase());
  };

  const getStatusOptions = () => {
    const statuses = [...new Set(orders.map(order => order.status))];
    return ['all', ...statuses];
  };

  if (loading) {
    return <LoadingSpinner />;
  }

  if (!user) {
    return (
      <div className="container text-center" style={{ padding: '3rem 20px' }}>
        <h2>Please login to view your orders</h2>
        <Link to="/login" className="btn btn-primary" style={{ marginTop: '1rem' }}>
          Login
        </Link>
      </div>
    );
  }

  return (
    <div className="container" style={{ padding: '2rem 20px' }}>
      {/* Header */}
      <div style={{ 
        display: 'flex', 
        justifyContent: 'space-between', 
        alignItems: 'center',
        marginBottom: '2rem'
      }}>
        <h1>Your Orders</h1>
        <button
          onClick={loadOrders}
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '0.5rem',
            background: 'none',
            border: '1px solid #667eea',
            color: '#667eea',
            padding: '0.5rem 1rem',
            borderRadius: '6px',
            cursor: 'pointer'
          }}
        >
          <FiRefreshCw size={16} />
          Refresh
        </button>
      </div>

      {/* Filter */}
      {orders.length > 0 && (
        <div style={{ marginBottom: '2rem' }}>
          <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
            {getStatusOptions().map(status => (
              <button
                key={status}
                onClick={() => setFilter(status)}
                style={{
                  backgroundColor: filter === status ? '#667eea' : 'white',
                  color: filter === status ? 'white' : '#333',
                  border: '2px solid #667eea',
                  padding: '0.5rem 1rem',
                  borderRadius: '25px',
                  cursor: 'pointer',
                  textTransform: 'capitalize'
                }}
              >
                {status === 'all' ? 'All Orders' : status.replace('_', ' ')}
              </button>
            ))}
          </div>
        </div>
      )}

      {/* Orders List */}
      {getFilteredOrders().length === 0 ? (
        <div className="text-center" style={{ padding: '3rem' }}>
          {orders.length === 0 ? (
            <div className="card" style={{ padding: '3rem', maxWidth: '500px', margin: '0 auto' }}>
              <FiShoppingCart size={64} style={{ color: '#ccc', margin: '0 auto 1rem' }} />
              <h3>No orders yet</h3>
              <p style={{ color: '#666', marginBottom: '2rem' }}>
                Start ordering from your favorite restaurants!
              </p>
              <Link to="/restaurants" className="btn btn-primary">
                Browse Restaurants
              </Link>
            </div>
          ) : (
            <div>
              <h3>No orders found for "{filter}"</h3>
              <p style={{ color: '#666' }}>Try a different filter</p>
            </div>
          )}
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          {getFilteredOrders().map(order => (
            <OrderCard 
              key={order.id} 
              order={order} 
              onReorder={() => handleReorder(order)}
            />
          ))}
        </div>
      )}
    </div>
  );
};

const OrderCard = ({ order, onReorder }) => {
  const getStatusColor = (status) => {
    switch (status?.toLowerCase()) {
      case 'pending':
        return '#f59e0b';
      case 'confirmed':
        return '#0284c7';
      case 'preparing':
        return '#7c3aed';
      case 'ready':
        return '#059669';
      case 'out_for_delivery':
        return '#0891b2';
      case 'delivered':
        return '#10b981';
      case 'cancelled':
        return '#ef4444';
      default:
        return '#6b7280';
    }
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString() + ' at ' + date.toLocaleTimeString([], { 
      hour: '2-digit', 
      minute: '2-digit' 
    });
  };

  return (
    <div className="card hover-scale" style={{ padding: '1.5rem' }}>
      <div style={{ 
        display: 'grid', 
        gridTemplateColumns: '1fr auto', 
        gap: '1rem',
        alignItems: 'start'
      }}>
        <div>
          {/* Order Header */}
          <div style={{ marginBottom: '1rem' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '0.5rem' }}>
              <h3 style={{ margin: 0 }}>Order #{order.id}</h3>
              <span style={{
                backgroundColor: getStatusColor(order.status),
                color: 'white',
                padding: '0.25rem 0.75rem',
                borderRadius: '20px',
                fontSize: '0.8rem',
                textTransform: 'capitalize'
              }}>
                {order.status?.replace('_', ' ')}
              </span>
            </div>
            
            <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', color: '#666', fontSize: '0.9rem' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <FiClock size={14} />
                <span>{formatDate(order.createdAt)}</span>
              </div>
              {order.restaurant && (
                <span>from {order.restaurant.name}</span>
              )}
            </div>
          </div>

          {/* Order Items */}
          <div style={{ marginBottom: '1rem' }}>
            {order.items?.slice(0, 3).map((item, index) => (
              <div key={index} style={{ fontSize: '0.9rem', color: '#666', marginBottom: '0.25rem' }}>
                {item.quantity}× {item.name || item.menuItem?.name}
              </div>
            ))}
            {order.items?.length > 3 && (
              <div style={{ fontSize: '0.9rem', color: '#666', fontStyle: 'italic' }}>
                +{order.items.length - 3} more items
              </div>
            )}
          </div>

          {/* Delivery Address */}
          {order.deliveryAddress && (
            <div style={{ display: 'flex', alignItems: 'start', gap: '0.5rem', fontSize: '0.9rem', color: '#666' }}>
              <FiMapPin size={14} style={{ marginTop: '2px' }} />
              <div>
                <div>{order.deliveryAddress.street}</div>
                <div>{order.deliveryAddress.city}, {order.deliveryAddress.state} {order.deliveryAddress.zipCode}</div>
              </div>
            </div>
          )}
        </div>

        {/* Right Side - Total and Actions */}
        <div style={{ textAlign: 'right' }}>
          <div style={{ 
            fontSize: '1.2rem', 
            fontWeight: 'bold', 
            marginBottom: '1rem',
            color: '#667eea'
          }}>
            ${order.totalAmount?.toFixed(2)}
          </div>
          
          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem', alignItems: 'flex-end' }}>
            <Link 
              to={`/order-details/${order.id}`}
              style={{
                backgroundColor: 'white',
                color: '#667eea',
                border: '1px solid #667eea',
                padding: '0.5rem 1rem',
                borderRadius: '6px',
                textDecoration: 'none',
                fontSize: '0.9rem'
              }}
            >
              View Details
            </Link>
            
            {order.status?.toLowerCase() === 'delivered' && (
              <button
                onClick={onReorder}
                style={{
                  backgroundColor: '#667eea',
                  color: 'white',
                  border: 'none',
                  padding: '0.5rem 1rem',
                  borderRadius: '6px',
                  cursor: 'pointer',
                  fontSize: '0.9rem'
                }}
              >
                Reorder
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Orders;
