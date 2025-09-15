import React from 'react';
import { useNavigate } from 'react-router-dom';
import { FiShoppingCart, FiMinus, FiPlus, FiTrash2 } from 'react-icons/fi';
import { useCart } from '../contexts/CartContext';
import { useAuth } from '../contexts/AuthContext';

const Cart = () => {
  const { 
    cartItems, 
    updateQuantity, 
    removeFromCart, 
    clearCart, 
    getCartTotal, 
    getCartCount 
  } = useCart();
  const { user } = useAuth();
  const navigate = useNavigate();

  const handleQuantityChange = (itemId, newQuantity) => {
    if (newQuantity < 1) {
      removeFromCart(itemId);
    } else {
      updateQuantity(itemId, newQuantity);
    }
  };

  const handleCheckout = () => {
    if (!user) {
      navigate('/login', { state: { from: { pathname: '/checkout' } } });
      return;
    }
    navigate('/checkout');
  };

  if (cartItems.length === 0) {
    return (
      <div className="container" style={{ padding: '3rem 20px', textAlign: 'center' }}>
        <div className="card" style={{ padding: '3rem', maxWidth: '500px', margin: '0 auto' }}>
          <FiShoppingCart size={64} style={{ color: '#ccc', margin: '0 auto 1rem' }} />
          <h2>Your cart is empty</h2>
          <p style={{ color: '#666', marginBottom: '2rem' }}>
            Add some delicious items from our restaurants to get started!
          </p>
          <button 
            onClick={() => navigate('/restaurants')}
            className="btn btn-primary"
          >
            Browse Restaurants
          </button>
        </div>
      </div>
    );
  }

  // Group items by restaurant
  const itemsByRestaurant = cartItems.reduce((acc, item) => {
    const restaurantId = item.restaurantId;
    if (!acc[restaurantId]) {
      acc[restaurantId] = {
        restaurantName: item.restaurantName,
        items: []
      };
    }
    acc[restaurantId].items.push(item);
    return acc;
  }, {});

  const subtotal = getCartTotal();
  const deliveryFee = 2.99;
  const tax = subtotal * 0.08; // 8% tax
  const total = subtotal + deliveryFee + tax;

  return (
    <div className="container" style={{ padding: '2rem 20px' }}>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 400px', gap: '2rem' }}>
        {/* Cart Items */}
        <div>
          <div style={{ 
            display: 'flex', 
            justifyContent: 'space-between', 
            alignItems: 'center',
            marginBottom: '1.5rem'
          }}>
            <h1>Your Cart ({getCartCount()} items)</h1>
            <button
              onClick={clearCart}
              style={{
                background: 'none',
                border: 'none',
                color: '#ef4444',
                cursor: 'pointer',
                textDecoration: 'underline',
                fontSize: '0.9rem'
              }}
            >
              Clear Cart
            </button>
          </div>

          {Object.entries(itemsByRestaurant).map(([restaurantId, restaurant]) => (
            <div key={restaurantId} className="card" style={{ marginBottom: '1.5rem' }}>
              <div style={{ 
                padding: '1rem 1.5rem',
                borderBottom: '1px solid #e5e7eb',
                backgroundColor: '#f9fafb'
              }}>
                <h3 style={{ margin: 0 }}>{restaurant.restaurantName}</h3>
              </div>
              
              <div style={{ padding: '1.5rem' }}>
                {restaurant.items.map(item => (
                  <CartItem
                    key={item.id}
                    item={item}
                    onQuantityChange={handleQuantityChange}
                    onRemove={() => removeFromCart(item.id)}
                  />
                ))}
              </div>
            </div>
          ))}
        </div>

        {/* Order Summary */}
        <div>
          <div className="card" style={{ padding: '1.5rem', position: 'sticky', top: '2rem' }}>
            <h3 style={{ marginBottom: '1.5rem' }}>Order Summary</h3>
            
            <div style={{ marginBottom: '1rem' }}>
              <div style={{ 
                display: 'flex', 
                justifyContent: 'space-between',
                marginBottom: '0.5rem'
              }}>
                <span>Subtotal</span>
                <span>${subtotal.toFixed(2)}</span>
              </div>
              
              <div style={{ 
                display: 'flex', 
                justifyContent: 'space-between',
                marginBottom: '0.5rem'
              }}>
                <span>Delivery Fee</span>
                <span>${deliveryFee.toFixed(2)}</span>
              </div>
              
              <div style={{ 
                display: 'flex', 
                justifyContent: 'space-between',
                marginBottom: '0.5rem'
              }}>
                <span>Tax</span>
                <span>${tax.toFixed(2)}</span>
              </div>
              
              <hr style={{ margin: '1rem 0' }} />
              
              <div style={{ 
                display: 'flex', 
                justifyContent: 'space-between',
                fontSize: '1.2rem',
                fontWeight: 'bold'
              }}>
                <span>Total</span>
                <span>${total.toFixed(2)}</span>
              </div>
            </div>
            
            <button
              onClick={handleCheckout}
              className="btn btn-primary"
              style={{ width: '100%', marginTop: '1rem' }}
            >
              Proceed to Checkout
            </button>
            
            <div style={{ 
              textAlign: 'center', 
              marginTop: '1rem',
              fontSize: '0.9rem',
              color: '#666'
            }}>
              Estimated delivery: 30-45 minutes
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

const CartItem = ({ item, onQuantityChange, onRemove }) => {
  return (
    <div style={{ 
      display: 'grid', 
      gridTemplateColumns: 'auto 1fr auto auto',
      gap: '1rem',
      alignItems: 'center',
      padding: '1rem 0',
      borderBottom: '1px solid #f3f4f6'
    }}>
      {/* Item Image */}
      <div style={{
        width: '60px',
        height: '60px',
        borderRadius: '8px',
        backgroundImage: `url(${item.image || '/api/placeholder/60/60'})`,
        backgroundSize: 'cover',
        backgroundPosition: 'center',
        backgroundColor: '#f3f4f6'
      }} />
      
      {/* Item Details */}
      <div>
        <h4 style={{ margin: '0 0 0.25rem 0' }}>{item.name}</h4>
        <p style={{ 
          color: '#667eea', 
          fontWeight: '500',
          margin: 0,
          fontSize: '0.9rem'
        }}>
          ${item.price.toFixed(2)} each
        </p>
      </div>
      
      {/* Quantity Controls */}
      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
        <button
          onClick={() => onQuantityChange(item.id, item.quantity - 1)}
          style={{
            backgroundColor: '#f3f4f6',
            border: 'none',
            borderRadius: '50%',
            width: '28px',
            height: '28px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            cursor: 'pointer'
          }}
        >
          <FiMinus size={14} />
        </button>
        
        <span style={{ 
          minWidth: '24px', 
          textAlign: 'center',
          fontWeight: '500'
        }}>
          {item.quantity}
        </span>
        
        <button
          onClick={() => onQuantityChange(item.id, item.quantity + 1)}
          style={{
            backgroundColor: '#f3f4f6',
            border: 'none',
            borderRadius: '50%',
            width: '28px',
            height: '28px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            cursor: 'pointer'
          }}
        >
          <FiPlus size={14} />
        </button>
      </div>
      
      {/* Remove Button */}
      <button
        onClick={onRemove}
        style={{
          backgroundColor: 'transparent',
          border: 'none',
          color: '#ef4444',
          cursor: 'pointer',
          padding: '0.5rem'
        }}
        title="Remove item"
      >
        <FiTrash2 size={18} />
      </button>
    </div>
  );
};

export default Cart;
