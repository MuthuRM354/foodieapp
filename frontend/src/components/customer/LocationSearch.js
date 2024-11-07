import React, { useState, useEffect } from 'react';
import { useLocation } from '../../hooks/useLocation';
import { useToast } from '../../hooks/useToast';
import locationService from '../../services/locationService';

const LocationSearch = () => {
  const [searchQuery, setSearchQuery] = useState('');
  const [suggestions, setSuggestions] = useState([]);
  const [recentLocations, setRecentLocations] = useState([]);
  const { getCurrentLocation } = useLocation();
  const { showToast } = useToast();

  useEffect(() => {
    const savedLocations = localStorage.getItem('recentLocations');
    if (savedLocations) {
      setRecentLocations(JSON.parse(savedLocations));
    }
  }, []);

  const handleSearch = async (query) => {
    setSearchQuery(query);
    if (query.length > 2) {
      try {
        const results = await locationService.searchLocations(query);
        setSuggestions(results);
      } catch (error) {
        showToast('Failed to fetch locations', 'error');
      }
    } else {
      setSuggestions([]);
    }
  };

  const handleLocationSelect = (location) => {
    setSearchQuery(location.address);
    setSuggestions([]);

    // Update recent locations
    const updated = [
      location,
      ...recentLocations.filter(loc => loc.id !== location.id).slice(0, 4)
    ];
    setRecentLocations(updated);
    localStorage.setItem('recentLocations', JSON.stringify(updated));
  };

  const handleCurrentLocation = async () => {
    try {
      const position = await getCurrentLocation();
      const address = await locationService.getLocationDetails(
        position.coords.latitude,
        position.coords.longitude
      );
      handleLocationSelect({
        id: 'current',
        address: address,
        coordinates: position.coords
      });
    } catch (error) {
      showToast('Unable to get current location', 'error');
    }
  };

  return (
    <div className="location-search">
      <div className="search-container">
        <i className="fas fa-search search-icon"></i>
        <input
          type="text"
          value={searchQuery}
          onChange={(e) => handleSearch(e.target.value)}
          placeholder="Enter your delivery location"
          className="location-input"
        />
        <button
          onClick={handleCurrentLocation}
          className="current-location-btn"
        >
          <i className="fas fa-location-arrow"></i>
          Use Current Location
        </button>
      </div>

      {suggestions.length > 0 && (
        <div className="suggestions-dropdown">
          {suggestions.map((suggestion, index) => (
            <div
              key={index}
              className="suggestion-item"
              onClick={() => handleLocationSelect(suggestion)}
            >
              <i className="fas fa-map-marker-alt"></i>
              <div className="suggestion-details">
                <p className="address">{suggestion.address}</p>
                <p className="area">{suggestion.area}</p>
              </div>
            </div>
          ))}
        </div>
      )}

      {recentLocations.length > 0 && !suggestions.length && (
        <div className="recent-locations">
          <h4>Recent Locations</h4>
          {recentLocations.map((location, index) => (
            <div
              key={index}
              className="recent-location-item"
              onClick={() => handleLocationSelect(location)}
            >
              <i className="fas fa-history"></i>
              <div className="location-details">
                <p className="address">{location.address}</p>
                {location.area && <p className="area">{location.area}</p>}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default LocationSearch;
