import axiosInstance from './api/axiosConfig';

const zomatoApi = {
  searchLocation: async (query) => {
    const response = await axiosInstance.get(`/locations/search?q=${query}`);
    return response.data;
  },

  searchRestaurants: async (params) => {
    const response = await axiosInstance.get('/restaurants/search', { params });
    return response.data;
  },

  getRestaurantDetails: async (restaurantId) => {
    const response = await axiosInstance.get(`/restaurants/${restaurantId}`);
    return response.data;
  },

  getRestaurantReviews: async (restaurantId, params) => {
    const response = await axiosInstance.get(`/restaurants/${restaurantId}/reviews`, {
      params
    });
    return response.data;
  },

  getCuisines: async (cityId) => {
    const response = await axiosInstance.get(`/cuisines/${cityId}`);
    return response.data;
  },

  getCategories: async () => {
    const response = await axiosInstance.get('/categories');
    return response.data;
  },

  submitRestaurantRating: async (restaurantId, ratingData) => {
    const response = await axiosInstance.post(
      `/restaurants/${restaurantId}/ratings`,
      ratingData
    );
    return response.data;
  },

  getNearbyRestaurants: async (latitude, longitude, radius = 5000) => {
    const response = await axiosInstance.get('/restaurants/nearby', {
      params: { latitude, longitude, radius }
    });
    return response.data;
  },

  getPopularRestaurants: async (cityId) => {
    const response = await axiosInstance.get(`/restaurants/popular/${cityId}`);
    return response.data;
  }
};

export default zomatoApi;
