import api from './api';
import mockAuthService from './mockAuthService';

const BASE_URL = 'http://localhost:8081/user-service/api/v1';

class AuthService {
  async login(email, password) {
    try {
      const response = await api.post(`${BASE_URL}/auth/login`, {
        email,
        password,
      });
      
      if (response.data.success) {
        const { accessToken, user } = response.data.data;
        localStorage.setItem('authToken', accessToken);
        localStorage.setItem('user', JSON.stringify(user));
        return response.data;
      }
      throw new Error(response.data.message || 'Login failed');
    } catch (error) {
      // Fallback to mock authentication for testing
      console.log('API login failed, using mock authentication');
      return await mockAuthService.mockLogin(email, password);
    }
  }

  async register(userData) {
    try {
      const response = await api.post(`${BASE_URL}/registration/customer`, userData);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Registration failed');
    }
  }

  async registerRestaurantOwner(ownerData) {
    try {
      const response = await api.post(`${BASE_URL}/registration/restaurant-owner`, ownerData);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Restaurant owner registration failed');
    }
  }

  async logout() {
    try {
      await api.post(`${BASE_URL}/auth/logout`);
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      // Use mock logout to ensure proper cleanup
      mockAuthService.mockLogout();
      localStorage.removeItem('authToken');
      localStorage.removeItem('user');
    }
  }

  async validateToken() {
    try {
      const response = await api.get(`${BASE_URL}/auth/validate-token`);
      return response.data;
    } catch (error) {
      // For mock authentication, always validate as true if user exists
      const user = this.getCurrentUser();
      if (user) {
        return { success: true, user };
      }
      localStorage.removeItem('authToken');
      localStorage.removeItem('user');
      throw error;
    }
  }

  async forgotPassword(email) {
    try {
      const response = await api.post(`${BASE_URL}/password/forgot`, { email });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Password reset request failed');
    }
  }

  getCurrentUser() {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  }

  isAuthenticated() {
    return !!localStorage.getItem('authToken');
  }

  getToken() {
    return localStorage.getItem('authToken');
  }
}

export default new AuthService();
