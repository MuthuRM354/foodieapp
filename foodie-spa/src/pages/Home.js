import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import restaurantService from '../services/restaurantService';
import LoadingSpinner from '../components/LoadingSpinner';
import CustomerDashboard from './dashboards/CustomerDashboard';
import RestaurantOwnerDashboard from './dashboards/RestaurantOwnerDashboard';
import AdminDashboard from './dashboards/AdminDashboard';
import { FiSearch, FiStar, FiClock, FiMapPin } from 'react-icons/fi';

const Home = () => {
  const { user, isAuthenticated } = useAuth();
  const [restaurants, setRestaurants] = useState([]);
  const [cuisines, setCuisines] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');

  useEffect(() => {
    if (!isAuthenticated) {
      loadData();
    } else {
      setLoading(false);
    }
  }, [isAuthenticated]);

  // Helper function to check user roles
  const hasRole = (role) => {
    if (!user) return false;
    
    // Handle both single role (mock) and roles array (backend) formats
    if (user.role) {
      return user.role === role;
    }
    
    if (user.roles) {
      return user.roles.includes(role) || user.roles.some(r => r.name === role);
    }
    
    return false;
  };

  // If user is authenticated, show role-based dashboard
  if (isAuthenticated && user) {
    if (hasRole('ROLE_ADMIN')) {
      return <AdminDashboard />;
    } else if (hasRole('ROLE_RESTAURANT_OWNER')) {
      return <RestaurantOwnerDashboard />;
    } else {
      return <CustomerDashboard />;
    }
  }

  const loadData = async () => {
    try {
      const [restaurantsData, cuisinesData] = await Promise.all([
        restaurantService.getAllRestaurants(),
        restaurantService.getPopularCuisines()
      ]);
      
      setRestaurants(restaurantsData.data || []);
      setCuisines(cuisinesData.data || []);
    } catch (error) {
      console.error('Failed to load data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!searchQuery.trim()) return;

    setLoading(true);
    try {
      const response = await restaurantService.searchRestaurants(searchQuery);
      setRestaurants(response.data || []);
    } catch (error) {
      console.error('Search failed:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <LoadingSpinner message="Loading restaurants..." />;
  }

  // Public landing page for non-authenticated users
  return (
    <div className="fade-in">
      {/* Hero Section */}
      <section style={{
        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
        color: 'white',
        padding: '4rem 0',
        textAlign: 'center'
      }}>
        <div className="container">
          <h1 style={{ fontSize: '3rem', marginBottom: '1rem', fontWeight: 'bold' }}>
            Delicious Food, Delivered Fast
          </h1>
          <p style={{ fontSize: '1.2rem', marginBottom: '2rem', opacity: 0.9 }}>
            Discover amazing restaurants and get your favorite food delivered to your doorstep
          </p>
          
          {/* Search Bar */}
          <form onSubmit={handleSearch} style={{ 
            maxWidth: '600px', 
            margin: '0 auto',
            display: 'flex',
            gap: '1rem',
            background: 'white',
            padding: '0.5rem',
            borderRadius: '50px',
            boxShadow: '0 4px 20px rgba(0,0,0,0.1)'
          }}>
            <input
              type="text"
              placeholder="Search for restaurants, cuisines, or dishes..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              style={{
                flex: 1,
                border: 'none',
                padding: '1rem 1.5rem',
                fontSize: '1rem',
                borderRadius: '50px',
                outline: 'none',
                color: '#333'
              }}
            />
            <button type="submit" className="btn btn-primary" style={{ borderRadius: '50px' }}>
              <FiSearch size={20} />
            </button>
          </form>
          
          {/* Call to Action */}
          <div style={{ marginTop: '2rem' }}>
            <Link to="/register" className="btn btn-light" style={{ margin: '0 0.5rem' }}>
              Join as Customer
            </Link>
            <Link to="/register-restaurant" className="btn btn-outline" style={{ margin: '0 0.5rem', borderColor: 'white', color: 'white' }}>
              Partner with Us
            </Link>
          </div>
        </div>
      </section>

      <div className="container" style={{ padding: '3rem 0' }}>
        {/* Popular Cuisines */}
        {cuisines.length > 0 && (
          <section style={{ marginBottom: '3rem' }}>
            <h2 style={{ textAlign: 'center', marginBottom: '2rem', color: '#333' }}>
              Popular Cuisines
            </h2>
            <div className="grid grid-4">
              {cuisines.map((cuisine) => (
                <Link
                  key={cuisine.id}
                  to={`/restaurants?cuisine=${cuisine.name}`}
                  className="card"
                  style={{ textDecoration: 'none', color: 'inherit', textAlign: 'center' }}
                >
                  <div className="card-body">
                    <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>
                      {cuisine.icon || '🍽️'}
                    </div>
                    <h3 style={{ marginBottom: '0.5rem' }}>{cuisine.name}</h3>
                    <p style={{ color: '#666', fontSize: '0.9rem' }}>
                      {cuisine.restaurantCount || 0} restaurants
                    </p>
                  </div>
                </Link>
              ))}
            </div>
          </section>
        )}

        {/* Featured Restaurants */}
        <section>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
            <h2 style={{ color: '#333' }}>Featured Restaurants</h2>
            <Link to="/restaurants" className="btn btn-outline">
              View All
            </Link>
          </div>
          
          {restaurants.length > 0 ? (
            <div className="grid grid-3">
              {restaurants.slice(0, 6).map((restaurant) => (
                <Link
                  key={restaurant.id}
                  to={`/restaurant/${restaurant.id}`}
                  className="card food-card"
                  style={{ textDecoration: 'none', color: 'inherit' }}
                >
                  <div style={{ position: 'relative' }}>
                    <img
                      src={restaurant.imageUrl || '/api/placeholder/300/200'}
                      alt={restaurant.name}
                      className="food-image"
                      style={{ width: '100%', height: '200px', objectFit: 'cover' }}
                    />
                    {restaurant.isOpen && (
                      <span className="badge badge-success" style={{
                        position: 'absolute',
                        top: '1rem',
                        left: '1rem'
                      }}>
                        Open
                      </span>
                    )}
                  </div>
                  <div className="card-body">
                    <h3 style={{ marginBottom: '0.5rem' }}>{restaurant.name}</h3>
                    <p style={{ color: '#666', marginBottom: '1rem', fontSize: '0.9rem' }}>
                      {restaurant.description}
                    </p>
                    
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                      <div className="rating">
                        <FiStar fill="currentColor" />
                        <span>{restaurant.rating || '4.0'}</span>
                      </div>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.25rem', color: '#666' }}>
                        <FiClock size={14} />
                        <span>{restaurant.deliveryTime || '30-45'} mins</span>
                      </div>
                    </div>
                    
                    <div style={{ marginTop: '1rem', display: 'flex', alignItems: 'center', gap: '0.25rem', color: '#666' }}>
                      <FiMapPin size={14} />
                      <span style={{ fontSize: '0.9rem' }}>{restaurant.address || 'Location'}</span>
                    </div>
                  </div>
                </Link>
              ))}
            </div>
          ) : (
            <div style={{ textAlign: 'center', padding: '3rem', color: '#666' }}>
              <h3>No restaurants found</h3>
              <p>Try searching with different keywords</p>
            </div>
          )}
        </section>

        {/* Features Section */}
        <section style={{ marginTop: '4rem', textAlign: 'center' }}>
          <h2 style={{ marginBottom: '3rem', color: '#333' }}>Why Choose Foodie?</h2>
          <div className="grid grid-3">
            <div>
              <div style={{ fontSize: '4rem', marginBottom: '1rem' }}>🚀</div>
              <h3 style={{ marginBottom: '1rem' }}>Fast Delivery</h3>
              <p style={{ color: '#666' }}>Get your food delivered in 30 minutes or less</p>
            </div>
            <div>
              <div style={{ fontSize: '4rem', marginBottom: '1rem' }}>🍽️</div>
              <h3 style={{ marginBottom: '1rem' }}>Quality Food</h3>
              <p style={{ color: '#666' }}>Fresh ingredients and delicious meals from top restaurants</p>
            </div>
            <div>
              <div style={{ fontSize: '4rem', marginBottom: '1rem' }}>💳</div>
              <h3 style={{ marginBottom: '1rem' }}>Easy Payment</h3>
              <p style={{ color: '#666' }}>Multiple payment options for your convenience</p>
            </div>
          </div>
        </section>

        {/* Business Features */}
        <section style={{ marginTop: '4rem', padding: '3rem', backgroundColor: '#f8fafc', borderRadius: '12px' }}>
          <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
            <h2 style={{ color: '#333', marginBottom: '1rem' }}>Grow Your Business with Foodie</h2>
            <p style={{ color: '#666', fontSize: '1.1rem' }}>
              Join thousands of restaurant partners and reach more customers
            </p>
          </div>
          
          <div className="grid grid-3">
            <div style={{ textAlign: 'center' }}>
              <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>📈</div>
              <h3 style={{ marginBottom: '1rem' }}>Increase Sales</h3>
              <p style={{ color: '#666' }}>Reach new customers and boost your revenue</p>
            </div>
            <div style={{ textAlign: 'center' }}>
              <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>📱</div>
              <h3 style={{ marginBottom: '1rem' }}>Easy Management</h3>
              <p style={{ color: '#666' }}>Manage orders and menu through our dashboard</p>
            </div>
            <div style={{ textAlign: 'center' }}>
              <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>🤝</div>
              <h3 style={{ marginBottom: '1rem' }}>Dedicated Support</h3>
              <p style={{ color: '#666' }}>Get help from our restaurant success team</p>
            </div>
          </div>
          
          <div style={{ textAlign: 'center', marginTop: '2rem' }}>
            <Link to="/register-restaurant" className="btn btn-primary">
              Become a Partner
            </Link>
          </div>
        </section>
      </div>
    </div>
  );
};

export default Home;
