import React, { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import LocationSearch from './LocationSearch';
import CuisineExplorer from './CuisineExplorer';
import RestaurantSearch from './RestaurantSearch';
import LoadingStates from '../common/LoadingStates';
import { useToast } from '../../hooks/useToast';

const CustomerDashboard = () => {
  const user = useSelector(state => state.auth.user);
  const [recentOrders, setRecentOrders] = useState([]);
  const [favoriteRestaurants, setFavoriteRestaurants] = useState([]);
  const [recommendations, setRecommendations] = useState([]);
  const [loading, setLoading] = useState(true);
  const { showToast } = useToast();

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    setLoading(true);
    try {
      // Fetch dashboard data from APIs
      setLoading(false);
    } catch (error) {
      showToast('Failed to load dashboard data', 'error');
      setLoading(false);
    }
  };

  return (
    <div className="customer-dashboard">
      <div className="dashboard-header">
        <h1>Welcome back, {user?.name}!</h1>
        <LocationSearch />
      </div>

      <div className="dashboard-content">
        <section className="recent-orders">
          <div className="section-header">
            <h2>Recent Orders</h2>
            <a href="/orders" className="view-all">View All</a>
          </div>
          {loading ? (
            <LoadingStates />
          ) : (
            <div className="orders-grid">
              {recentOrders.map(order => (
                <div key={order.id} className="order-card">
                  <img src={order.restaurantImage} alt={order.restaurantName} loading="lazy" />
                  <div className="order-details">
                    <h3>{order.restaurantName}</h3>
                    <p className="order-meta">
                      <span>{order.items.length} items</span>
                      <span>•</span>
                      <span>${order.total}</span>
                    </p>
                    <p className="order-date">{new Date(order.date).toLocaleDateString()}</p>
                    <button className="reorder-btn">
                      <i className="fas fa-redo"></i> Reorder
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </section>

        <section className="favorites">
          <div className="section-header">
            <h2>Your Favorites</h2>
            <a href="/favorites" className="view-all">View All</a>
          </div>
          <div className="favorites-grid">
            {favoriteRestaurants.map(restaurant => (
              <div key={restaurant.id} className="favorite-card">
                <div className="restaurant-image">
                  <img src={restaurant.imageUrl} alt={restaurant.name} loading="lazy" />
                  <button className="favorite-btn active">
                    <i className="fas fa-heart"></i>
                  </button>
                </div>
                <div className="restaurant-info">
                  <h3>{restaurant.name}</h3>
                  <p className="cuisine">{restaurant.cuisine}</p>
                  <div className="restaurant-meta">
                    <span className="rating">
                      <i className="fas fa-star"></i> {restaurant.rating}
                    </span>
                    <span className="delivery-time">
                      <i className="fas fa-clock"></i> {restaurant.deliveryTime} mins
                    </span>
                  </div>
                  <button className="order-now-btn">Order Now</button>
                </div>
              </div>
            ))}
          </div>
        </section>

        <section className="recommendations">
          <div className="section-header">
            <h2>Recommended for You</h2>
            <button className="refresh-btn">
              <i className="fas fa-sync-alt"></i> Refresh
            </button>
          </div>
          <div className="recommendations-grid">
            {recommendations.map(restaurant => (
              <div key={restaurant.id} className="recommendation-card">
                <img src={restaurant.imageUrl} alt={restaurant.name} loading="lazy" />
                <div className="recommendation-info">
                  <h3>{restaurant.name}</h3>
                  <p className="cuisine">{restaurant.cuisine}</p>
                  <div className="rating-info">
                    <span className="rating">
                      <i className="fas fa-star"></i> {restaurant.rating}
                    </span>
                    <span className="delivery-time">
                      {restaurant.deliveryTime} mins
                    </span>
                  </div>
                  <p className="recommendation-reason">
                    {restaurant.recommendationReason}
                  </p>
                </div>
              </div>
            ))}
          </div>
        </section>
      </div>
    </div>
  );
};

export default CustomerDashboard;
