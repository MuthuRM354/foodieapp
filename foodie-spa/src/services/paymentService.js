import api from './api';

const BASE_URL = 'http://localhost:8085/payment-service/api';

class PaymentService {
  async createPayment(paymentData) {
    try {
      const response = await api.post(`${BASE_URL}/payments`, paymentData);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to create payment');
    }
  }

  async getPaymentById(paymentId) {
    try {
      const response = await api.get(`${BASE_URL}/payments/${paymentId}`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch payment');
    }
  }

  async getPaymentsByOrder(orderId) {
    try {
      const response = await api.get(`${BASE_URL}/payments/order/${orderId}`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch payments for order');
    }
  }

  async updatePaymentStatus(paymentId, status) {
    try {
      const response = await api.put(`${BASE_URL}/payments/${paymentId}/status`, status, {
        headers: {
          'Content-Type': 'text/plain'
        }
      });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to update payment status');
    }
  }

  // Mock payment methods for demo
  async processPayment(paymentData) {
    try {
      // Simulate payment processing
      await new Promise(resolve => setTimeout(resolve, 2000));
      
      const isSuccess = Math.random() > 0.1; // 90% success rate for demo
      
      if (isSuccess) {
        return {
          success: true,
          transactionId: `TXN_${Date.now()}`,
          amount: paymentData.amount,
          status: 'SUCCESS'
        };
      } else {
        throw new Error('Payment failed');
      }
    } catch (error) {
      throw new Error('Payment processing failed');
    }
  }

  getPaymentMethods() {
    return [
      { id: 'card', name: 'Credit/Debit Card', icon: '💳' },
      { id: 'upi', name: 'UPI', icon: '📱' },
      { id: 'netbanking', name: 'Net Banking', icon: '🏦' },
      { id: 'wallet', name: 'Digital Wallet', icon: '👛' },
      { id: 'cod', name: 'Cash on Delivery', icon: '💵' }
    ];
  }
}

export default new PaymentService();
