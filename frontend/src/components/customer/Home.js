import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import LocationSearch from './LocationSearch';
import CuisineExplorer from './CuisineExplorer';
import { useLocation } from '../../hooks/useLocation';
import LoadingStates from '../common/LoadingStates';

const Home = () => {
  const [topRestaurants, setTopRestaurants] = useState([]);
  const [trending, setTrending] = useState([]);
  const [loading, setLoading] = useState(true);
  const { location } = useLocation();

  useEffect(() => {
    if (location) {
      fetchHomeData();
    }
  }, [location]);

  const fetchHomeData = async () => {
    setLoading(true);
    try {
      // Fetch data from APIs
      setLoading(false);
    } catch (error) {
      setLoading(false);
    }
  };

  return (
    <div className="home-page">
      <div className="hero-section">
        <h1>Discover the best food & drinks</h1>
        <LocationSearch />
      </div>

      <div className="content-section">
        {loading ? (
          <LoadingStates type="skeleton" />
        ) : (
          <>
            <section className="quick-links">
              <div className="link-card">
                <i className="fas fa-utensils"></i>
                <h3>Order Food</h3>
                <Link to="/restaurants">Explore Restaurants</Link>
              </div>
              <div className="link-card">
                <i className="fas fa-concierge-bell"></i>
                <h3>Table Booking</h3>
                <Link to="/reservations">Book Now</Link>
              </div>
              <div className="link-card">
                <i className="fas fa-glass-cheers"></i>
                <h3>Nightlife</h3>
                <Link to="/nightlife">Explore</Link>
              </div>
            </section>

            <section className="trending-section">
              <h2>Trending Now</h2>
              <div className="trending-grid">
                {trending.map(item => (
                  <div key={item.id} className="trending-card">
                    <img src={item.image} alt={item.name} loading="lazy" />
                    <div className="trending-info">
                      <h3>{item.name}</h3>
                      <p>{item.description}</p>
                    </div>
                  </div>
                ))}
              </div>
            </section>

            <section className="cuisine-section">
              <h2>Popular Cuisines</h2>
              <CuisineExplorer />
            </section>

            <section className="top-restaurants">
              <h2>Top Rated Restaurants</h2>
              <div className="restaurant-grid">
                {topRestaurants.map(restaurant => (
                  <div key={restaurant.id} className="restaurant-card">
                    <img src={restaurant.image} alt={restaurant.name} loading="lazy" />
                    <div className="restaurant-info">
                      <h3>{restaurant.name}</h3>
                      <div className="rating">⭐ {restaurant.rating}</div>
                      <p>{restaurant.cuisine}</p>
                      <p>{restaurant.location}</p>
                    </div>
                  </div>
                ))}
              </div>
            </section>
          </>
        )}
      </div>
    </div>
  );
};

export default Home;
