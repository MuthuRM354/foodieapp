import React, { useState, useEffect } from 'react';
import LoadingStates from '../common/LoadingStates';

const RestaurantSearch = () => {
  const [searchQuery, setSearchQuery] = useState('');
  const [filters, setFilters] = useState({
    cuisine: [],
    priceRange: '',
    rating: '',
    sortBy: 'rating'
  });
  const [restaurants, setRestaurants] = useState([]);
  const [loading, setLoading] = useState(false);

  const handleSearch = async () => {
    setLoading(true);
    try {
      // API call to search restaurants
      const results = await searchRestaurants(searchQuery, filters);
      setRestaurants(results);
    } catch (error) {
      console.error('Restaurant search failed:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="restaurant-search">
      <div className="search-header">
        <input
          type="text"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          placeholder="Search restaurants..."
          className="search-input"
        />

        <div className="filters">
          <select
            value={filters.priceRange}
            onChange={(e) => setFilters({...filters, priceRange: e.target.value})}
          >
            <option value="">Price Range</option>
            <option value="low">$</option>
            <option value="medium">$$</option>
            <option value="high">$$$</option>
          </select>

          <select
            value={filters.rating}
            onChange={(e) => setFilters({...filters, rating: e.target.value})}
          >
            <option value="">Rating</option>
            <option value="4">4+ Stars</option>
            <option value="3">3+ Stars</option>
            <option value="2">2+ Stars</option>
          </select>

          <select
            value={filters.sortBy}
            onChange={(e) => setFilters({...filters, sortBy: e.target.value})}
          >
            <option value="rating">Sort by Rating</option>
            <option value="price">Sort by Price</option>
            <option value="distance">Sort by Distance</option>
          </select>
        </div>

        <button onClick={handleSearch} className="search-button">
          Search
        </button>
      </div>

      {loading ? (
        <LoadingStates />
      ) : (
        <div className="restaurants-grid">
          {restaurants.map(restaurant => (
            <div key={restaurant.id} className="restaurant-card">
              <img src={restaurant.imageUrl} alt={restaurant.name} />
              <div className="restaurant-info">
                <h3>{restaurant.name}</h3>
                <div className="rating">
                  <span className="stars">{'⭐'.repeat(restaurant.rating)}</span>
                  <span>({restaurant.reviewCount} reviews)</span>
                </div>
                <p>{restaurant.cuisineType}</p>
                <p>{restaurant.priceRange}</p>
                <p>{restaurant.deliveryTime} mins</p>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default RestaurantSearch;
