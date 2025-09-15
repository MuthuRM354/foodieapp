import api from './api';

const BASE_URL = 'http://localhost:8083/api/v1';

class OrderService {
  async createOrder(orderData) {
    try {
      const response = await api.post(`${BASE_URL}/orders`, orderData);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to create order');
    }
  }

  async getOrderById(orderId) {
    try {
      const response = await api.get(`${BASE_URL}/orders/${orderId}`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch order');
    }
  }

  async getMyOrders(limit) {
    try {
      const url = limit ? `${BASE_URL}/orders/my?limit=${limit}` : `${BASE_URL}/orders/my`;
      const response = await api.get(url);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch orders');
    }
  }

  async getOwnerOrders(limit) {
    try {
      const url = limit ? `${BASE_URL}/orders/restaurant?limit=${limit}` : `${BASE_URL}/orders/restaurant`;
      const response = await api.get(url);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch restaurant orders');
    }
  }

  async getUserOrders(userId) {
    try {
      const response = await api.get(`${BASE_URL}/orders/user/${userId}`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch user orders');
    }
  }

  async getRestaurantOrders(restaurantId) {
    try {
      const response = await api.get(`${BASE_URL}/orders/restaurant/${restaurantId}`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch restaurant orders');
    }
  }

  async updateOrderStatus(orderId, status, notes = '') {
    try {
      const response = await api.put(`${BASE_URL}/orders/${orderId}/status`, {
        status,
        notes
      });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to update order status');
    }
  }

  async getAllOrders(page = 0, size = 20) {
    try {
      const response = await api.get(`${BASE_URL}/orders?page=${page}&size=${size}`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch orders');
    }
  }

  // Cart methods
  async addToCart(item) {
    try {
      const response = await api.post(`${BASE_URL}/cart`, item);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to add item to cart');
    }
  }

  async getCart() {
    try {
      const response = await api.get(`${BASE_URL}/cart`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch cart');
    }
  }

  async updateCartItem(itemId, quantity) {
    try {
      const response = await api.put(`${BASE_URL}/cart/${itemId}`, { quantity });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to update cart item');
    }
  }

  async removeFromCart(itemId) {
    try {
      const response = await api.delete(`${BASE_URL}/cart/${itemId}`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to remove item from cart');
    }
  }

  async clearCart() {
    try {
      const response = await api.delete(`${BASE_URL}/cart`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to clear cart');
    }
  }
}

export default new OrderService();
