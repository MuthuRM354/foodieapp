import { useState, useEffect } from 'react';
import locationService from '../services/locationService';

export const useLocation = () => {
  const [location, setLocation] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);

  const getCurrentLocation = () => {
    if (!navigator.geolocation) {
      setError('Geolocation is not supported by your browser');
      setLoading(false);
      return;
    }

    navigator.geolocation.getCurrentPosition(
      async (position) => {
        try {
          const { latitude, longitude } = position.coords;
          const nearbyRestaurants = await locationService.getNearbyRestaurants(
            latitude,
            longitude
          );
          setLocation({ coords: { latitude, longitude }, nearbyRestaurants });
        } catch (err) {
          setError(err.message);
        } finally {
          setLoading(false);
        }
      },
      (error) => {
        setError(error.message);
        setLoading(false);
      }
    );
  };

  useEffect(() => {
    getCurrentLocation();
  }, []);

  return { location, error, loading, refreshLocation: getCurrentLocation };
};
