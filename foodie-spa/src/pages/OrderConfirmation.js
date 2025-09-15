import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate, Link } from 'react-router-dom';
import { FiCheckCircle, FiClock, FiMapPin, FiPhone } from 'react-icons/fi';
import orderService from '../services/orderService';
import LoadingSpinner from '../components/LoadingSpinner';
import toast from 'react-hot-toast';

const OrderConfirmation = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  
  const orderId = location.state?.orderId;

  useEffect(() => {
    if (!orderId) {
      navigate('/');
      return;
    }
    
    loadOrderDetails();
  }, [orderId]);

  const loadOrderDetails = async () => {
    try {
      const orderData = await orderService.getOrderById(orderId);
      setOrder(orderData);
    } catch (error) {
      console.error('Error loading order:', error);
      toast.error('Failed to load order details');
      navigate('/orders');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <LoadingSpinner />;
  }

  if (!order) {
    return (
      <div className="container text-center" style={{ padding: '3rem 20px' }}>
        <h2>Order not found</h2>
        <Link to="/orders" className="btn btn-primary" style={{ marginTop: '1rem' }}>
          View All Orders
        </Link>
      </div>
    );
  }

  return (
    <div className="container" style={{ padding: '2rem 20px', maxWidth: '800px', margin: '0 auto' }}>
      {/* Success Header */}
      <div className="text-center" style={{ marginBottom: '3rem' }}>
        <div className="fade-in">
          <FiCheckCircle 
            size={80} 
            color="#10b981" 
            style={{ marginBottom: '1rem' }}
          />
          <h1 style={{ color: '#10b981', marginBottom: '0.5rem' }}>
            Order Confirmed!
          </h1>
          <p style={{ fontSize: '1.1rem', color: '#666' }}>
            Thank you for your order. We're preparing your delicious meal!
          </p>
        </div>
      </div>

      {/* Order Details */}
      <div className="card fade-in" style={{ marginBottom: '2rem' }}>
        <div style={{ 
          padding: '1rem 1.5rem',
          borderBottom: '1px solid #e5e7eb',
          backgroundColor: '#f9fafb'
        }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <h3 style={{ margin: 0 }}>Order #{order.id}</h3>
            <span style={{
              backgroundColor: getStatusColor(order.status),
              color: 'white',
              padding: '0.25rem 0.75rem',
              borderRadius: '20px',
              fontSize: '0.9rem',
              textTransform: 'capitalize'
            }}>
              {order.status}
            </span>
          </div>
          <p style={{ margin: '0.5rem 0 0 0', color: '#666' }}>
            Placed on {new Date(order.createdAt).toLocaleString()}
          </p>
        </div>
        
        <div style={{ padding: '1.5rem' }}>
          {/* Restaurant Info */}
          <div style={{ marginBottom: '2rem' }}>
            <h4 style={{ marginBottom: '0.5rem' }}>From {order.restaurant?.name}</h4>
            <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', color: '#666' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <FiClock size={16} />
                <span>Estimated delivery: {order.estimatedDeliveryTime || 30}-{(order.estimatedDeliveryTime || 30) + 15} min</span>
              </div>
              {order.restaurant?.phoneNumber && (
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                  <FiPhone size={16} />
                  <span>{order.restaurant.phoneNumber}</span>
                </div>
              )}
            </div>
          </div>

          {/* Order Items */}
          <div style={{ marginBottom: '2rem' }}>
            <h4 style={{ marginBottom: '1rem' }}>Order Items</h4>
            {order.items?.map((item, index) => (
              <div 
                key={index}
                style={{ 
                  display: 'flex', 
                  justifyContent: 'space-between',
                  alignItems: 'center',
                  padding: '0.75rem 0',
                  borderBottom: index < order.items.length - 1 ? '1px solid #f3f4f6' : 'none'
                }}
              >
                <div>
                  <div style={{ fontWeight: '500' }}>{item.name || item.menuItem?.name}</div>
                  <div style={{ fontSize: '0.9rem', color: '#666' }}>
                    ${item.price?.toFixed(2)} × {item.quantity}
                  </div>
                </div>
                <div style={{ fontWeight: '500' }}>
                  ${((item.price || 0) * (item.quantity || 1)).toFixed(2)}
                </div>
              </div>
            ))}
          </div>

          {/* Delivery Address */}
          <div style={{ marginBottom: '2rem' }}>
            <h4 style={{ marginBottom: '0.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <FiMapPin />
              Delivery Address
            </h4>
            <div style={{ color: '#666', lineHeight: '1.5' }}>
              {order.deliveryAddress && (
                <>
                  <div>{order.deliveryAddress.street}</div>
                  <div>
                    {order.deliveryAddress.city}, {order.deliveryAddress.state} {order.deliveryAddress.zipCode}
                  </div>
                  {order.deliveryAddress.instructions && (
                    <div style={{ marginTop: '0.5rem', fontStyle: 'italic' }}>
                      Instructions: {order.deliveryAddress.instructions}
                    </div>
                  )}
                </>
              )}
            </div>
          </div>

          {/* Order Total */}
          <div style={{ 
            backgroundColor: '#f9fafb',
            padding: '1rem',
            borderRadius: '8px'
          }}>
            <div style={{ 
              display: 'flex', 
              justifyContent: 'space-between',
              fontSize: '1.1rem',
              fontWeight: 'bold'
            }}>
              <span>Total Paid</span>
              <span>${order.totalAmount?.toFixed(2)}</span>
            </div>
          </div>
        </div>
      </div>

      {/* Action Buttons */}
      <div style={{ 
        display: 'flex', 
        gap: '1rem', 
        justifyContent: 'center',
        flexWrap: 'wrap'
      }}>
        <Link to="/orders" className="btn btn-primary">
          View All Orders
        </Link>
        <Link to="/restaurants" className="btn btn-secondary">
          Order Again
        </Link>
      </div>

      {/* Order Tracking Info */}
      <div className="card" style={{ marginTop: '2rem', padding: '1.5rem', backgroundColor: '#f0f9ff' }}>
        <h4 style={{ marginBottom: '1rem', color: '#0284c7' }}>What's Next?</h4>
        <div style={{ color: '#0369a1', lineHeight: '1.6' }}>
          <p style={{ margin: '0 0 0.5rem 0' }}>
            🍳 Your order is being prepared by the restaurant
          </p>
          <p style={{ margin: '0 0 0.5rem 0' }}>
            🚗 A delivery driver will be assigned shortly
          </p>
          <p style={{ margin: '0 0 0.5rem 0' }}>
            📱 You'll receive updates via notifications
          </p>
          <p style={{ margin: 0 }}>
            🍽️ Enjoy your meal when it arrives!
          </p>
        </div>
      </div>
    </div>
  );
};

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

export default OrderConfirmation;
