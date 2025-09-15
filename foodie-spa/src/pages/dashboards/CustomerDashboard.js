import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import restaurantService from '../../services/restaurantService';
import orderService from '../../services/orderService';
import LoadingSpinner from '../../components/LoadingSpinner';
import { FiSearch, FiStar, FiClock, FiMapPin, FiShoppingBag, FiHeart, FiTrendingUp } from 'react-icons/fi';

const CustomerDashboard = () => {
  const { user } = useAuth();
  const [featuredRestaurants, setFeaturedRestaurants] = useState([]);
  const [recentOrders, setRecentOrders] = useState([]);
  const [favoriteRestaurants, setFavoriteRestaurants] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      const [restaurantsData, ordersData] = await Promise.all([
        restaurantService.getPopularRestaurants(6),
        orderService.getMyOrders(3) // Get recent 3 orders
      ]);
      
      setFeaturedRestaurants(restaurantsData.data || []);
      setRecentOrders(ordersData.data || []);
    } catch (error) {
      console.error('Failed to load dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!searchQuery.trim()) return;
    // Redirect to restaurants page with search query
    window.location.href = `/restaurants?search=${encodeURIComponent(searchQuery)}`;
  };

  if (loading) {
    return <LoadingSpinner message="Loading your dashboard..." />;
  }

  return (
    <div className="fade-in">
      {/* Welcome Section */}
      <section style={{
        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
        color: 'white',
        padding: '3rem 0',
        textAlign: 'center'
      }}>
        <div className="container">
          <h1 style={{ fontSize: '2.5rem', marginBottom: '0.5rem', fontWeight: 'bold' }}>
            Welcome back, {user?.firstName || 'Food Lover'}! 👋
          </h1>
          <p style={{ fontSize: '1.1rem', marginBottom: '2rem', opacity: 0.9 }}>
            Ready to order some delicious food?
          </p>
          
          {/* Quick Search */}
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
        </div>
      </section>

      <div className="container" style={{ padding: '3rem 0' }}>
        {/* Quick Actions */}
        <section style={{ marginBottom: '3rem' }}>
          <div className="grid grid-4">
            <Link to="/restaurants" className="card" style={{ textDecoration: 'none', color: 'inherit', textAlign: 'center', padding: '2rem' }}>
              <FiSearch size={40} style={{ color: '#667eea', marginBottom: '1rem' }} />
              <h3 style={{ marginBottom: '0.5rem' }}>Browse Restaurants</h3>
              <p style={{ color: '#666', fontSize: '0.9rem' }}>Discover new places to order from</p>
            </Link>
            
            <Link to="/orders" className="card" style={{ textDecoration: 'none', color: 'inherit', textAlign: 'center', padding: '2rem' }}>
              <FiShoppingBag size={40} style={{ color: '#10b981', marginBottom: '1rem' }} />
              <h3 style={{ marginBottom: '0.5rem' }}>My Orders</h3>
              <p style={{ color: '#666', fontSize: '0.9rem' }}>Track your order history</p>
            </Link>
            
            <Link to="/cart" className="card" style={{ textDecoration: 'none', color: 'inherit', textAlign: 'center', padding: '2rem' }}>
              <FiShoppingBag size={40} style={{ color: '#f59e0b', marginBottom: '1rem' }} />
              <h3 style={{ marginBottom: '0.5rem' }}>My Cart</h3>
              <p style={{ color: '#666', fontSize: '0.9rem' }}>Review items in your cart</p>
            </Link>
            
            <Link to="/profile" className="card" style={{ textDecoration: 'none', color: 'inherit', textAlign: 'center', padding: '2rem' }}>
              <FiHeart size={40} style={{ color: '#ef4444', marginBottom: '1rem' }} />
              <h3 style={{ marginBottom: '0.5rem' }}>Favorites</h3>
              <p style={{ color: '#666', fontSize: '0.9rem' }}>Your saved restaurants</p>
            </Link>
          </div>
        </section>

        {/* Recent Orders */}
        {recentOrders.length > 0 && (
          <section style={{ marginBottom: '3rem' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
              <h2 style={{ color: '#333' }}>Recent Orders</h2>
              <Link to="/orders" className="btn btn-outline">View All</Link>
            </div>
            
            <div className="grid grid-3">
              {recentOrders.map((order) => (
                <div key={order.id} className="card">
                  <div className="card-body">
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
                      <h3 style={{ marginBottom: 0 }}>{order.restaurantName}</h3>
                      <span className={`badge ${order.status === 'DELIVERED' ? 'badge-success' : order.status === 'CANCELLED' ? 'badge-danger' : 'badge-warning'}`}>
                        {order.status}
                      </span>
                    </div>
                    <p style={{ color: '#666', marginBottom: '0.5rem' }}>
                      Order #{order.id.slice(-6)}
                    </p>
                    <p style={{ color: '#666', marginBottom: '1rem' }}>
                      {new Date(order.createdAt).toLocaleDateString()}
                    </p>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                      <span style={{ fontWeight: 'bold', color: '#667eea' }}>₹{order.totalAmount}</span>
                      <Link 
                        to={`/restaurant/${order.restaurantId}`} 
                        className="btn btn-outline btn-sm"
                      >
                        Reorder
                      </Link>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </section>
        )}

        {/* Featured Restaurants */}
        <section>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
            <h2 style={{ color: '#333' }}>
              <FiTrendingUp style={{ marginRight: '0.5rem', color: '#667eea' }} />
              Popular Restaurants
            </h2>
            <Link to="/restaurants" className="btn btn-outline">View All</Link>
          </div>
          
          <div className="grid grid-3">
            {featuredRestaurants.map((restaurant) => (
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
                      Open Now
                    </span>
                  )}
                </div>
                <div className="card-body">
                  <h3 style={{ marginBottom: '0.5rem' }}>{restaurant.name}</h3>
                  <p style={{ color: '#666', marginBottom: '1rem', fontSize: '0.9rem' }}>
                    {restaurant.cuisine}
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
                    <span style={{ fontSize: '0.9rem' }}>{restaurant.address || 'Multiple Locations'}</span>
                  </div>
                </div>
              </Link>
            ))}
          </div>
        </section>

        {/* Quick Tips */}
        <section style={{ marginTop: '4rem', padding: '2rem', backgroundColor: '#f8fafc', borderRadius: '12px' }}>
          <h3 style={{ textAlign: 'center', marginBottom: '2rem', color: '#333' }}>💡 Pro Tips</h3>
          <div className="grid grid-3">
            <div style={{ textAlign: 'center' }}>
              <h4 style={{ color: '#667eea', marginBottom: '0.5rem' }}>Order Early</h4>
              <p style={{ color: '#666', fontSize: '0.9rem' }}>Get faster delivery during off-peak hours</p>
            </div>
            <div style={{ textAlign: 'center' }}>
              <h4 style={{ color: '#667eea', marginBottom: '0.5rem' }}>Save Favorites</h4>
              <p style={{ color: '#666', fontSize: '0.9rem' }}>Quick access to your preferred restaurants</p>
            </div>
            <div style={{ textAlign: 'center' }}>
              <h4 style={{ color: '#667eea', marginBottom: '0.5rem' }}>Group Orders</h4>
              <p style={{ color: '#666', fontSize: '0.9rem' }}>Split costs and save on delivery fees</p>
            </div>
          </div>
        </section>
      </div>
    </div>
  );
};

export default CustomerDashboard;
