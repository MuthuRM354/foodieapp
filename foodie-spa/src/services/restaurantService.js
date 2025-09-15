import api from './api';

const BASE_URL = 'http://localhost:8082/api/v1';

class RestaurantService {
  async getAllRestaurants() {
    try {
      const response = await api.get(`${BASE_URL}/restaurants`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch restaurants');
    }
  }

  async getPopularRestaurants(limit = 6) {
    try {
      const response = await api.get(`${BASE_URL}/restaurants?popular=true&limit=${limit}`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch popular restaurants');
    }
  }

  async getOwnerRestaurants() {
    try {
      const response = await api.get(`${BASE_URL}/owner/restaurants`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch owner restaurants');
    }
  }

  async getRestaurantById(id) {
    try {
      const response = await api.get(`${BASE_URL}/restaurants/${id}`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch restaurant');
    }
  }

  async searchRestaurants(query) {
    try {
      const response = await api.get(`${BASE_URL}/restaurants/search?query=${encodeURIComponent(query)}`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Search failed');
    }
  }

  async getRestaurantMenu(restaurantId) {
    try {
      const response = await api.get(`${BASE_URL}/restaurants/${restaurantId}/menu`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch menu');
    }
  }

  async getCuisines() {
    try {
      const response = await api.get(`${BASE_URL}/cuisines`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch cuisines');
    }
  }

  async getPopularCuisines() {
    try {
      const response = await api.get(`${BASE_URL}/cuisines/popular`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch popular cuisines');
    }
  }

  async createRestaurant(restaurantData) {
    try {
      const response = await api.post(`${BASE_URL}/restaurants`, restaurantData);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to create restaurant');
    }
  }

  async updateRestaurant(id, restaurantData) {
    try {
      const response = await api.put(`${BASE_URL}/restaurants/${id}`, restaurantData);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to update restaurant');
    }
  }

  async getOwnerRestaurants() {
    try {
      const response = await api.get(`${BASE_URL}/restaurants/owner`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch owner restaurants');
    }
  }

  async addMenuItem(restaurantId, menuItem) {
    try {
      const response = await api.post(`${BASE_URL}/restaurants/${restaurantId}/menu`, menuItem);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to add menu item');
    }
  }

  async updateMenuItem(restaurantId, itemId, menuItem) {
    try {
      const response = await api.put(`${BASE_URL}/restaurants/${restaurantId}/menu/${itemId}`, menuItem);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to update menu item');
    }
  }

  async deleteMenuItem(restaurantId, itemId) {
    try {
      const response = await api.delete(`${BASE_URL}/restaurants/${restaurantId}/menu/${itemId}`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to delete menu item');
    }
  }
}

export default new RestaurantService();
