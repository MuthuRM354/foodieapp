import React, { useState, useEffect } from 'react';
import { useLocation } from '../../hooks/useLocation';
import { useToast } from '../../hooks/useToast';
import LocationSearch from './LocationSearch';
import RestaurantSearch from './RestaurantSearch';
import CuisineExplorer from './CuisineExplorer';
import LoadingStates from '../common/LoadingStates';

const Search = () => {
  const [searchResults, setSearchResults] = useState({
    restaurants: [],
    cuisines: [],
    dishes: []
  });
  const [activeTab, setActiveTab] = useState('restaurants');
  const [loading, setLoading] = useState(false);
  const { location } = useLocation();
  const { showToast } = useToast();

  const handleSearch = async (query) => {
    if (!query.trim()) return;

    setLoading(true);
    try {
      // API calls to fetch search results
      setLoading(false);
    } catch (error) {
      showToast('Search failed', 'error');
      setLoading(false);
    }
  };

  return (
    <div className="search-page">
      <div className="search-header">
        <h2>Find Your Favorite Food</h2>
        <LocationSearch />
      </div>

      <div className="search-content">
        <div className="search-tabs">
          <button
            className={`tab ${activeTab === 'restaurants' ? 'active' : ''}`}
            onClick={() => setActiveTab('restaurants')}
          >
            <i className="fas fa-utensils"></i> Restaurants
          </button>
          <button
            className={`tab ${activeTab === 'cuisines' ? 'active' : ''}`}
            onClick={() => setActiveTab('cuisines')}
          >
            <i className="fas fa-globe"></i> Cuisines
          </button>
          <button
            className={`tab ${activeTab === 'dishes' ? 'active' : ''}`}
            onClick={() => setActiveTab('dishes')}
          >
            <i className="fas fa-hamburger"></i> Dishes
          </button>
        </div>

        {activeTab === 'restaurants' && <RestaurantSearch />}
        {activeTab === 'cuisines' && <CuisineExplorer />}
        {activeTab === 'dishes' && (
          <div className="dishes-search">
            {/* Dishes search implementation */}
          </div>
        )}
      </div>
    </div>
  );
};

export default Search;
