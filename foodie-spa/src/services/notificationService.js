import api from './api';

const BASE_URL = 'http://localhost:8084/notification-service/api';

class NotificationService {
  async sendEmail(emailData) {
    try {
      const response = await api.post(`${BASE_URL}/notifications/email`, emailData);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to send email');
    }
  }

  async sendSms(smsData) {
    try {
      const response = await api.post(`${BASE_URL}/notifications/sms`, smsData);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to send SMS');
    }
  }

  async getNotificationLogs(page = 0, size = 20) {
    try {
      const response = await api.get(`${BASE_URL}/notifications/logs?page=${page}&size=${size}`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch notification logs');
    }
  }

  // Helper methods for common notifications
  async sendOrderConfirmation(orderData) {
    const emailData = {
      to: orderData.customerEmail,
      subject: `Order Confirmed - #${orderData.orderId}`,
      template: 'order-confirmation',
      data: orderData
    };
    return this.sendEmail(emailData);
  }

  async sendOrderStatusUpdate(orderId, status, customerEmail) {
    const emailData = {
      to: customerEmail,
      subject: `Order Update - ${status}`,
      template: 'order-status',
      data: { orderId, status }
    };
    return this.sendEmail(emailData);
  }

  async sendPaymentConfirmation(paymentData) {
    const emailData = {
      to: paymentData.customerEmail,
      subject: `Payment Received - ₹${paymentData.amount}`,
      template: 'payment-confirmation',
      data: paymentData
    };
    return this.sendEmail(emailData);
  }
}

export default new NotificationService();
