import React, { useState, useEffect } from 'react';
import LocationSearch from './LocationSearch';
import CuisineExplorer from './CuisineExplorer';
import RestaurantSearch from './RestaurantSearch';
import LoadingStates from '../common/LoadingStates';

const CustomerDashboard = () => {
  const [recentOrders, setRecentOrders] = useState([]);
  const [favoriteRestaurants, setFavoriteRestaurants] = useState([]);
  const [recommendations, setRecommendations] = useState([]);
  const [loading, setLoading] = useState(true);

  return (
    <div className="customer-dashboard">
      <div className="dashboard-header">
        <h1>Welcome back, {user.name}!</h1>
        <LocationSearch />
      </div>

      <div className="dashboard-content">
        <section className="recent-orders">
          <h2>Recent Orders</h2>
          {loading ? (
            <LoadingStates />
          ) : (
            <div className="orders-grid">
              {recentOrders.map(order => (
                <div key={order.id} className="order-card">
                  <img src={order.restaurantImage} alt={order.restaurantName} />
                  <div className="order-details">
                    <h3>{order.restaurantName}</h3>
                    <p>{order.items.length} items • ${order.total}</p>
                    <p>{order.date}</p>
                    <button>Reorder</button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </section>

        <section className="favorites">
          <h2>Your Favorites</h2>
          <div className="favorites-grid">
            {favoriteRestaurants.map(restaurant => (
              <div key={restaurant.id} className="favorite-card">
                <img src={restaurant.imageUrl} alt={restaurant.name} />
                <h3>{restaurant.name}</h3>
                <p>{restaurant.cuisine}</p>
                <button>Order Now</button>
              </div>
            ))}
          </div>
        </section>

        <section className="recommendations">
          <h2>Recommended for You</h2>
          <div className="recommendations-grid">
            {recommendations.map(restaurant => (
              <div key={restaurant.id} className="recommendation-card">
                <img src={restaurant.imageUrl} alt={restaurant.name} />
                <div className="recommendation-info">
                  <h3>{restaurant.name}</h3>
                  <p>{restaurant.cuisine}</p>
                  <div className="rating">
                    <span>⭐ {restaurant.rating}</span>
                    <span>{restaurant.deliveryTime} mins</span>
                  </div>
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
