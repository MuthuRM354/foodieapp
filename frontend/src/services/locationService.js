import axiosInstance from './api/axiosConfig';

const locationService = {
  getCurrentLocation: () => {
    return new Promise((resolve, reject) => {
      if (!navigator.geolocation) {
        reject(new Error('Geolocation is not supported by your browser'));
      }

      navigator.geolocation.getCurrentPosition(
        (position) => {
          resolve({
            latitude: position.coords.latitude,
            longitude: position.coords.longitude
          });
        },
        (error) => {
          reject(error);
        }
      );
    });
  },

  searchLocations: async (query) => {
    const response = await axiosInstance.get(`/locations/search?q=${query}`);
    return response.data;
  },

  getNearbyRestaurants: async (latitude, longitude, radius = 5000) => {
    const response = await axiosInstance.get('/locations/nearby-restaurants', {
      params: { latitude, longitude, radius }
    });
    return response.data;
  },

  getCities: async () => {
    const response = await axiosInstance.get('/locations/cities');
    return response.data;
  },

  getLocationDetails: async (locationId) => {
    const response = await axiosInstance.get(`/locations/${locationId}`);
    return response.data;
  }
};

export default locationService;
