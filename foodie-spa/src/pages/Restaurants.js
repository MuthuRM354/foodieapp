import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { FiFilter, FiMapPin, FiClock, FiStar } from 'react-icons/fi';
import restaurantService from '../services/restaurantService';
import LoadingSpinner from '../components/LoadingSpinner';
import toast from 'react-hot-toast';

const Restaurants = () => {
  const [restaurants, setRestaurants] = useState([]);
  const [loading, setLoading] = useState(true);
  const [cuisines, setCuisines] = useState([]);
  const [filters, setFilters] = useState({
    cuisine: '',
    priceRange: '',
    rating: '',
    deliveryTime: '',
  });
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  useEffect(() => {
    loadData();
  }, []);

  useEffect(() => {
    const searchQuery = searchParams.get('search');
    const cuisineFilter = searchParams.get('cuisine');
    if (searchQuery || cuisineFilter) {
      setFilters(prev => ({
        ...prev,
        cuisine: cuisineFilter || '',
      }));
      searchRestaurants(searchQuery, cuisineFilter);
    }
  }, [searchParams]);

  const loadData = async () => {
    try {
      const [restaurantsData, cuisinesData] = await Promise.all([
        restaurantService.getAllRestaurants(),
        restaurantService.getAllCuisines(),
      ]);
      setRestaurants(restaurantsData);
      setCuisines(cuisinesData);
    } catch (error) {
      console.error('Error loading data:', error);
      toast.error('Failed to load restaurants');
    } finally {
      setLoading(false);
    }
  };

  const searchRestaurants = async (query, cuisine) => {
    setLoading(true);
    try {
      let results;
      if (query) {
        results = await restaurantService.searchRestaurants(query);
      } else if (cuisine) {
        results = await restaurantService.getRestaurantsByCuisine(cuisine);
      } else {
        results = await restaurantService.getAllRestaurants();
      }
      setRestaurants(results);
    } catch (error) {
      console.error('Error searching restaurants:', error);
      toast.error('Failed to search restaurants');
    } finally {
      setLoading(false);
    }
  };

  const handleFilterChange = (filterType, value) => {
    const newFilters = { ...filters, [filterType]: value };
    setFilters(newFilters);
    applyFilters(newFilters);
  };

  const applyFilters = async (currentFilters) => {
    setLoading(true);
    try {
      let results = await restaurantService.getAllRestaurants();
      
      // Apply filters
      if (currentFilters.cuisine) {
        results = results.filter(restaurant => 
          restaurant.cuisineTypes?.includes(currentFilters.cuisine)
        );
      }
      
      if (currentFilters.rating) {
        const minRating = parseFloat(currentFilters.rating);
        results = results.filter(restaurant => 
          (restaurant.averageRating || 0) >= minRating
        );
      }
      
      if (currentFilters.deliveryTime) {
        const maxTime = parseInt(currentFilters.deliveryTime);
        results = results.filter(restaurant => 
          (restaurant.estimatedDeliveryTime || 60) <= maxTime
        );
      }
      
      setRestaurants(results);
    } catch (error) {
      console.error('Error applying filters:', error);
      toast.error('Failed to filter restaurants');
    } finally {
      setLoading(false);
    }
  };

  const clearFilters = () => {
    setFilters({
      cuisine: '',
      priceRange: '',
      rating: '',
      deliveryTime: '',
    });
    loadData();
  };

  if (loading) {
    return <LoadingSpinner />;
  }

  return (
    <div className="container" style={{ padding: '2rem 20px' }}>
      {/* Header */}
      <div style={{ marginBottom: '2rem' }}>
        <h1>Restaurants Near You</h1>
        <p style={{ color: '#666', marginTop: '0.5rem' }}>
          {restaurants.length} restaurants available
        </p>
      </div>

      {/* Filters */}
      <div className="card" style={{ marginBottom: '2rem', padding: '1.5rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '1rem' }}>
          <FiFilter />
          <h3 style={{ margin: 0 }}>Filters</h3>
          <button 
            onClick={clearFilters}
            style={{ 
              marginLeft: 'auto', 
              background: 'none', 
              border: 'none', 
              color: '#667eea', 
              cursor: 'pointer',
              textDecoration: 'underline'
            }}
          >
            Clear All
          </button>
        </div>
        
        <div style={{ 
          display: 'grid', 
          gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', 
          gap: '1rem' 
        }}>
          <div>
            <label className="form-label">Cuisine</label>
            <select
              value={filters.cuisine}
              onChange={(e) => handleFilterChange('cuisine', e.target.value)}
              className="form-input"
            >
              <option value="">All Cuisines</option>
              {cuisines.map(cuisine => (
                <option key={cuisine.id} value={cuisine.name}>
                  {cuisine.name}
                </option>
              ))}
            </select>
          </div>
          
          <div>
            <label className="form-label">Rating</label>
            <select
              value={filters.rating}
              onChange={(e) => handleFilterChange('rating', e.target.value)}
              className="form-input"
            >
              <option value="">Any Rating</option>
              <option value="4.5">4.5+ Stars</option>
              <option value="4.0">4.0+ Stars</option>
              <option value="3.5">3.5+ Stars</option>
              <option value="3.0">3.0+ Stars</option>
            </select>
          </div>
          
          <div>
            <label className="form-label">Delivery Time</label>
            <select
              value={filters.deliveryTime}
              onChange={(e) => handleFilterChange('deliveryTime', e.target.value)}
              className="form-input"
            >
              <option value="">Any Time</option>
              <option value="30">Under 30 min</option>
              <option value="45">Under 45 min</option>
              <option value="60">Under 1 hour</option>
            </select>
          </div>
        </div>
      </div>

      {/* Restaurant Grid */}
      {restaurants.length === 0 ? (
        <div className="text-center" style={{ padding: '3rem', color: '#666' }}>
          <h3>No restaurants found</h3>
          <p>Try adjusting your filters or search criteria</p>
        </div>
      ) : (
        <div style={{ 
          display: 'grid', 
          gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))', 
          gap: '1.5rem' 
        }}>
          {restaurants.map(restaurant => (
            <RestaurantCard key={restaurant.id} restaurant={restaurant} />
          ))}
        </div>
      )}
    </div>
  );
};

const RestaurantCard = ({ restaurant }) => {
  const navigate = useNavigate();
  
  const handleClick = () => {
    navigate(`/restaurant/${restaurant.id}`);
  };

  return (
    <div 
      className="card hover-scale" 
      style={{ cursor: 'pointer', overflow: 'hidden' }}
      onClick={handleClick}
    >
      <div style={{ 
        height: '200px', 
        backgroundImage: `url(${restaurant.imageUrl || '/api/placeholder/320/200'})`,
        backgroundSize: 'cover',
        backgroundPosition: 'center',
        position: 'relative'
      }}>
        {restaurant.isOpen === false && (
          <div style={{
            position: 'absolute',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            backgroundColor: 'rgba(0,0,0,0.7)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: 'white',
            fontSize: '1.1rem',
            fontWeight: 'bold'
          }}>
            Currently Closed
          </div>
        )}
        <div style={{
          position: 'absolute',
          top: '10px',
          right: '10px',
          backgroundColor: 'white',
          borderRadius: '20px',
          padding: '5px 10px',
          display: 'flex',
          alignItems: 'center',
          gap: '5px',
          fontSize: '0.9rem',
          fontWeight: '500'
        }}>
          <FiStar color="#fbbf24" />
          {restaurant.averageRating?.toFixed(1) || 'New'}
        </div>
      </div>
      
      <div style={{ padding: '1.5rem' }}>
        <h3 style={{ margin: '0 0 0.5rem 0', fontSize: '1.3rem' }}>
          {restaurant.name}
        </h3>
        
        <div style={{ 
          display: 'flex', 
          flexWrap: 'wrap', 
          gap: '0.5rem', 
          marginBottom: '1rem' 
        }}>
          {restaurant.cuisineTypes?.slice(0, 3).map(cuisine => (
            <span 
              key={cuisine}
              style={{
                backgroundColor: '#f3f4f6',
                color: '#374151',
                padding: '0.25rem 0.5rem',
                borderRadius: '12px',
                fontSize: '0.8rem'
              }}
            >
              {cuisine}
            </span>
          ))}
        </div>
        
        <div style={{ 
          display: 'flex', 
          justifyContent: 'space-between', 
          alignItems: 'center',
          color: '#666',
          fontSize: '0.9rem'
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <FiClock />
            {restaurant.estimatedDeliveryTime || 30}-{(restaurant.estimatedDeliveryTime || 30) + 15} min
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <FiMapPin />
            {restaurant.deliveryFee ? `$${restaurant.deliveryFee}` : 'Free'} delivery
          </div>
        </div>
        
        {restaurant.description && (
          <p style={{ 
            color: '#666', 
            fontSize: '0.9rem', 
            marginTop: '1rem',
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            display: '-webkit-box',
            WebkitLineClamp: 2,
            WebkitBoxOrient: 'vertical'
          }}>
            {restaurant.description}
          </p>
        )}
      </div>
    </div>
  );
};

export default Restaurants;
