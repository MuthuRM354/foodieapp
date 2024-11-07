import React, { useState, useEffect } from 'react';
import LoadingStates from '../common/LoadingStates';
import { useToast } from '../../hooks/useToast';
import zomatoApi from '../../services/zomatoApi';

const CuisineExplorer = () => {
  const [cuisines, setCuisines] = useState([]);
  const [selectedCuisines, setSelectedCuisines] = useState([]);
  const [loading, setLoading] = useState(true);
  const { showToast } = useToast();

  useEffect(() => {
    fetchCuisines();
  }, []);

  const fetchCuisines = async () => {
    try {
      const response = await zomatoApi.getCuisines();
      setCuisines(response.data);
      setLoading(false);
    } catch (error) {
      showToast('Failed to fetch cuisines', 'error');
      setLoading(false);
    }
  };

  const handleCuisineSelect = (cuisineId) => {
    setSelectedCuisines(prev => {
      if (prev.includes(cuisineId)) {
        return prev.filter(id => id !== cuisineId);
      }
      return [...prev, cuisineId];
    });
  };

  return (
    <div className="cuisine-explorer">
      <div className="explorer-header">
        <h2>Explore Cuisines</h2>
        <p>Discover restaurants by your favorite cuisine</p>
      </div>

      {loading ? (
        <LoadingStates type="skeleton" />
      ) : (
        <>
          <div className="cuisines-grid">
            {cuisines.map(cuisine => (
              <div
                key={cuisine.id}
                className={`cuisine-card ${selectedCuisines.includes(cuisine.id) ? 'selected' : ''}`}
                onClick={() => handleCuisineSelect(cuisine.id)}
              >
                <div className="cuisine-image">
                  <img src={cuisine.imageUrl} alt={cuisine.name} loading="lazy" />
                  {selectedCuisines.includes(cuisine.id) && (
                    <div className="selected-overlay">
                      <i className="fas fa-check"></i>
                    </div>
                  )}
                </div>
                <div className="cuisine-info">
                  <h3>{cuisine.name}</h3>
                  <p>{cuisine.description}</p>
                  <div className="cuisine-meta">
                    <span className="restaurant-count">
                      <i className="fas fa-store"></i>
                      {cuisine.restaurantCount} Restaurants
                    </span>
                    {cuisine.avgRating && (
                      <span className="avg-rating">
                        <i className="fas fa-star"></i>
                        {cuisine.avgRating}
                      </span>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>

          {selectedCuisines.length > 0 && (
            <div className="selected-cuisines">
              <h3>Selected Cuisines</h3>
              <div className="cuisine-tags">
                {selectedCuisines.map(id => {
                  const cuisine = cuisines.find(c => c.id === id);
                  return (
                    <span key={id} className="cuisine-tag">
                      {cuisine.name}
                      <button
                        onClick={(e) => {
                          e.stopPropagation();
                          handleCuisineSelect(id);
                        }}
                        className="remove-tag"
                      >
                        <i className="fas fa-times"></i>
                      </button>
                    </span>
                  );
                })}
                <button
                  className="clear-all"
                  onClick={() => setSelectedCuisines([])}
                >
                  Clear All
                </button>
              </div>
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default CuisineExplorer;
