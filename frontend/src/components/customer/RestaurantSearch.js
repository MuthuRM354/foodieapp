import React, { useState, useEffect } from 'react';
import LoadingStates from '../common/LoadingStates';
import { useLocation } from '../../hooks/useLocation';
import { useToast } from '../../hooks/useToast';
import zomatoApi from '../../services/zomatoApi';

const RestaurantSearch = () => {
  const [searchQuery, setSearchQuery] = useState('');
  const [filters, setFilters] = useState({
    cuisine: [],
    priceRange: '',
    rating: '',
    sortBy: 'rating',
    deliveryTime: ''
  });
  const [restaurants, setRestaurants] = useState([]);
  const [loading, setLoading] = useState(false);
  const { location } = useLocation();
  const { showToast } = useToast();

  const handleSearch = async () => {
    setLoading(true);
    try {
      const params = {
        query: searchQuery,
        ...filters,
        latitude: location?.coords?.latitude,
        longitude: location?.coords?.longitude
      };
      const results = await zomatoApi.searchRestaurants(params);
      setRestaurants(results);
    } catch (error) {
      showToast('Restaurant search failed', 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (location) {
      handleSearch();
    }
  }, [filters.sortBy, location]);

  return (
    <div className="restaurant-search">
      <div className="search-header">
        <div className="search-input-wrapper">
          <i className="fas fa-search"></i>
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Search restaurants..."
            className="search-input"
            onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
          />
        </div>

        <div className="filters">
          <select
            value={filters.priceRange}
            onChange={(e) => setFilters({...filters, priceRange: e.target.value})}
            className="filter-select"
          >
            <option value="">Price Range</option>
            <option value="low">$</option>
            <option value="medium">$$</option>
            <option value="high">$$$</option>
          </select>

          <select
            value={filters.rating}
            onChange={(e) => setFilters({...filters, rating: e.target.value})}
            className="filter-select"
          >
            <option value="">Rating</option>
            <option value="4">4+ Stars</option>
            <option value="3">3+ Stars</option>
            <option value="2">2+ Stars</option>
          </select>

          <select
            value={filters.deliveryTime}
            onChange={(e) => setFilters({...filters, deliveryTime: e.target.value})}
            className="filter-select"
          >
            <option value="">Delivery Time</option>
            <option value="30">Under 30 mins</option>
            <option value="45">Under 45 mins</option>
            <option value="60">Under 1 hour</option>
          </select>

          <select
            value={filters.sortBy}
            onChange={(e) => setFilters({...filters, sortBy: e.target.value})}
            className="filter-select"
          >
            <option value="rating">Sort by Rating</option>
            <option value="price">Sort by Price</option>
            <option value="distance">Sort by Distance</option>
            <option value="deliveryTime">Sort by Delivery Time</option>
          </select>
        </div>
      </div>

      {loading ? (
        <LoadingStates />
      ) : (
        <div className="restaurants-grid">
          {restaurants.map(restaurant => (
            <div key={restaurant.id} className="restaurant-card">
              <div className="restaurant-image">
                <img src={restaurant.imageUrl} alt={restaurant.name} loading="lazy" />
                <button className="favorite-btn">
                  <i className="far fa-heart"></i>
                </button>
              </div>
              <div className="restaurant-info">
                <h3>{restaurant.name}</h3>
                <div className="rating">
                  <span className="stars">{'⭐'.repeat(restaurant.rating)}</span>
                  <span className="review-count">({restaurant.reviewCount})</span>
                </div>
                <p className="cuisine">{restaurant.cuisineType.join(', ')}</p>
                <div className="meta-info">
                  <span><i className="fas fa-clock"></i> {restaurant.deliveryTime} mins</span>
                  <span><i className="fas fa-dollar-sign"></i> {restaurant.priceRange}</span>
                  <span><i className="fas fa-map-marker-alt"></i> {restaurant.distance} km</span>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default RestaurantSearch;
