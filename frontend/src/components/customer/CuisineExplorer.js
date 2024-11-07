import React, { useState, useEffect } from 'react';
import LoadingStates from '../common/LoadingStates';

const CuisineExplorer = () => {
  const [cuisines, setCuisines] = useState([]);
  const [selectedCuisines, setSelectedCuisines] = useState([]);
  const [loading, setLoading] = useState(true);

  const handleCuisineSelect = (cuisineId) => {
    if (selectedCuisines.includes(cuisineId)) {
      setSelectedCuisines(selectedCuisines.filter(id => id !== cuisineId));
    } else {
      setSelectedCuisines([...selectedCuisines, cuisineId]);
    }
  };

  return (
    <div className="cuisine-explorer">
      <h2>Explore Cuisines</h2>

      {loading ? (
        <LoadingStates />
      ) : (
        <div className="cuisines-grid">
          {cuisines.map(cuisine => (
            <div
              key={cuisine.id}
              className={`cuisine-card ${selectedCuisines.includes(cuisine.id) ? 'selected' : ''}`}
              onClick={() => handleCuisineSelect(cuisine.id)}
            >
              <div className="cuisine-image">
                <img src={cuisine.imageUrl} alt={cuisine.name} />
              </div>
              <div className="cuisine-info">
                <h3>{cuisine.name}</h3>
                <p>{cuisine.description}</p>
                <span className="restaurant-count">
                  {cuisine.restaurantCount} Restaurants
                </span>
              </div>
            </div>
          ))}
        </div>
      )}

      {selectedCuisines.length > 0 && (
        <div className="selected-cuisines">
          <h3>Selected Cuisines</h3>
          <div className="cuisine-tags">
            {selectedCuisines.map(id => {
              const cuisine = cuisines.find(c => c.id === id);
              return (
                <span key={id} className="cuisine-tag">
                  {cuisine.name}
                  <button onClick={() => handleCuisineSelect(id)}>×</button>
                </span>
              );
            })}
          </div>
        </div>
      )}
    </div>
  );
};

export default CuisineExplorer;