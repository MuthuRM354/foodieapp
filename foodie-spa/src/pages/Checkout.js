import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiCreditCard, FiUser, FiMapPin, FiClock } from 'react-icons/fi';
import { useCart } from '../contexts/CartContext';
import { useAuth } from '../contexts/AuthContext';
import orderService from '../services/orderService';
import paymentService from '../services/paymentService';
import toast from 'react-hot-toast';

const Checkout = () => {
  const { cartItems, getCartTotal, clearCart } = useCart();
  const { user } = useAuth();
  const navigate = useNavigate();
  
  const [deliveryInfo, setDeliveryInfo] = useState({
    address: '',
    city: '',
    state: '',
    zipCode: '',
    instructions: '',
  });
  
  const [paymentInfo, setPaymentInfo] = useState({
    cardNumber: '',
    expiryDate: '',
    cvv: '',
    nameOnCard: '',
  });
  
  const [loading, setLoading] = useState(false);

  const subtotal = getCartTotal();
  const deliveryFee = 2.99;
  const tax = subtotal * 0.08;
  const total = subtotal + deliveryFee + tax;

  const handleDeliveryChange = (e) => {
    setDeliveryInfo({
      ...deliveryInfo,
      [e.target.name]: e.target.value,
    });
  };

  const handlePaymentChange = (e) => {
    let value = e.target.value;
    
    // Format card number
    if (e.target.name === 'cardNumber') {
      value = value.replace(/\s/g, '').replace(/(.{4})/g, '$1 ').trim();
      if (value.length > 19) value = value.substr(0, 19);
    }
    
    // Format expiry date
    if (e.target.name === 'expiryDate') {
      value = value.replace(/\D/g, '').replace(/(\d{2})(\d)/, '$1/$2');
      if (value.length > 5) value = value.substr(0, 5);
    }
    
    // Format CVV
    if (e.target.name === 'cvv') {
      value = value.replace(/\D/g, '');
      if (value.length > 3) value = value.substr(0, 3);
    }
    
    setPaymentInfo({
      ...paymentInfo,
      [e.target.name]: value,
    });
  };

  const validateForm = () => {
    if (!deliveryInfo.address || !deliveryInfo.city || !deliveryInfo.state || !deliveryInfo.zipCode) {
      toast.error('Please fill in all delivery information');
      return false;
    }
    
    if (!paymentInfo.cardNumber || !paymentInfo.expiryDate || !paymentInfo.cvv || !paymentInfo.nameOnCard) {
      toast.error('Please fill in all payment information');
      return false;
    }
    
    return true;
  };

  const handlePlaceOrder = async () => {
    if (!validateForm()) return;
    
    setLoading(true);
    
    try {
      // Create order
      const orderData = {
        items: cartItems.map(item => ({
          menuItemId: item.id,
          quantity: item.quantity,
          price: item.price,
        })),
        restaurantId: cartItems[0]?.restaurantId,
        deliveryAddress: {
          street: deliveryInfo.address,
          city: deliveryInfo.city,
          state: deliveryInfo.state,
          zipCode: deliveryInfo.zipCode,
          instructions: deliveryInfo.instructions,
        },
        totalAmount: total,
      };
      
      const order = await orderService.createOrder(orderData);
      
      // Process payment
      const paymentData = {
        orderId: order.id,
        amount: total,
        cardNumber: paymentInfo.cardNumber.replace(/\s/g, ''),
        expiryDate: paymentInfo.expiryDate,
        cvv: paymentInfo.cvv,
        nameOnCard: paymentInfo.nameOnCard,
      };
      
      const payment = await paymentService.processPayment(paymentData);
      
      if (payment.status === 'COMPLETED') {
        clearCart();
        toast.success('Order placed successfully!');
        navigate('/order-confirmation', { state: { orderId: order.id } });
      } else {
        throw new Error('Payment failed');
      }
    } catch (error) {
      console.error('Error placing order:', error);
      toast.error(error.message || 'Failed to place order');
    } finally {
      setLoading(false);
    }
  };

  if (cartItems.length === 0) {
    navigate('/cart');
    return null;
  }

  return (
    <div className="container" style={{ padding: '2rem 20px' }}>
      <h1 style={{ marginBottom: '2rem' }}>Checkout</h1>
      
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 400px', gap: '2rem' }}>
        {/* Checkout Form */}
        <div>
          {/* Delivery Information */}
          <div className="card" style={{ marginBottom: '2rem' }}>
            <div style={{ 
              display: 'flex', 
              alignItems: 'center', 
              gap: '0.5rem',
              padding: '1rem 1.5rem',
              borderBottom: '1px solid #e5e7eb',
              backgroundColor: '#f9fafb'
            }}>
              <FiMapPin />
              <h3 style={{ margin: 0 }}>Delivery Information</h3>
            </div>
            
            <div style={{ padding: '1.5rem' }}>
              <div className="form-group">
                <label className="form-label">Street Address</label>
                <input
                  type="text"
                  name="address"
                  value={deliveryInfo.address}
                  onChange={handleDeliveryChange}
                  className="form-input"
                  placeholder="123 Main Street"
                  required
                />
              </div>
              
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 120px', gap: '1rem' }}>
                <div className="form-group">
                  <label className="form-label">City</label>
                  <input
                    type="text"
                    name="city"
                    value={deliveryInfo.city}
                    onChange={handleDeliveryChange}
                    className="form-input"
                    placeholder="New York"
                    required
                  />
                </div>
                
                <div className="form-group">
                  <label className="form-label">State</label>
                  <input
                    type="text"
                    name="state"
                    value={deliveryInfo.state}
                    onChange={handleDeliveryChange}
                    className="form-input"
                    placeholder="NY"
                    required
                  />
                </div>
                
                <div className="form-group">
                  <label className="form-label">ZIP Code</label>
                  <input
                    type="text"
                    name="zipCode"
                    value={deliveryInfo.zipCode}
                    onChange={handleDeliveryChange}
                    className="form-input"
                    placeholder="10001"
                    required
                  />
                </div>
              </div>
              
              <div className="form-group">
                <label className="form-label">Delivery Instructions (Optional)</label>
                <textarea
                  name="instructions"
                  value={deliveryInfo.instructions}
                  onChange={handleDeliveryChange}
                  className="form-input"
                  rows="3"
                  placeholder="Ring doorbell, leave at door, etc."
                />
              </div>
            </div>
          </div>

          {/* Payment Information */}
          <div className="card">
            <div style={{ 
              display: 'flex', 
              alignItems: 'center', 
              gap: '0.5rem',
              padding: '1rem 1.5rem',
              borderBottom: '1px solid #e5e7eb',
              backgroundColor: '#f9fafb'
            }}>
              <FiCreditCard />
              <h3 style={{ margin: 0 }}>Payment Information</h3>
            </div>
            
            <div style={{ padding: '1.5rem' }}>
              <div className="form-group">
                <label className="form-label">Name on Card</label>
                <input
                  type="text"
                  name="nameOnCard"
                  value={paymentInfo.nameOnCard}
                  onChange={handlePaymentChange}
                  className="form-input"
                  placeholder="John Doe"
                  required
                />
              </div>
              
              <div className="form-group">
                <label className="form-label">Card Number</label>
                <input
                  type="text"
                  name="cardNumber"
                  value={paymentInfo.cardNumber}
                  onChange={handlePaymentChange}
                  className="form-input"
                  placeholder="1234 5678 9012 3456"
                  required
                />
              </div>
              
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                <div className="form-group">
                  <label className="form-label">Expiry Date</label>
                  <input
                    type="text"
                    name="expiryDate"
                    value={paymentInfo.expiryDate}
                    onChange={handlePaymentChange}
                    className="form-input"
                    placeholder="MM/YY"
                    required
                  />
                </div>
                
                <div className="form-group">
                  <label className="form-label">CVV</label>
                  <input
                    type="text"
                    name="cvv"
                    value={paymentInfo.cvv}
                    onChange={handlePaymentChange}
                    className="form-input"
                    placeholder="123"
                    required
                  />
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Order Summary */}
        <div>
          <div className="card" style={{ padding: '1.5rem', position: 'sticky', top: '2rem' }}>
            <h3 style={{ marginBottom: '1.5rem' }}>Order Summary</h3>
            
            {/* Order Items */}
            <div style={{ marginBottom: '1.5rem' }}>
              {cartItems.map(item => (
                <div 
                  key={item.id}
                  style={{ 
                    display: 'flex', 
                    justifyContent: 'space-between',
                    alignItems: 'center',
                    padding: '0.5rem 0',
                    borderBottom: '1px solid #f3f4f6'
                  }}
                >
                  <div>
                    <div style={{ fontWeight: '500' }}>{item.name}</div>
                    <div style={{ fontSize: '0.9rem', color: '#666' }}>
                      ${item.price.toFixed(2)} × {item.quantity}
                    </div>
                  </div>
                  <div style={{ fontWeight: '500' }}>
                    ${(item.price * item.quantity).toFixed(2)}
                  </div>
                </div>
              ))}
            </div>
            
            {/* Totals */}
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
            
            {/* Delivery Time */}
            <div style={{ 
              display: 'flex', 
              alignItems: 'center', 
              gap: '0.5rem',
              backgroundColor: '#f0f9ff',
              padding: '1rem',
              borderRadius: '8px',
              marginBottom: '1.5rem',
              fontSize: '0.9rem'
            }}>
              <FiClock color="#0284c7" />
              <span>Estimated delivery: 30-45 minutes</span>
            </div>
            
            <button
              onClick={handlePlaceOrder}
              className="btn btn-primary"
              style={{ width: '100%' }}
              disabled={loading}
            >
              {loading ? 'Placing Order...' : 'Place Order'}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Checkout;
