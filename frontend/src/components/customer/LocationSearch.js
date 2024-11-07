import React, { useState } from 'react';
import zomatoApi from '../../services/zomatoApi';

const LocationSearch = () => {
  const [searchQuery, setSearchQuery] = useState('');
  const [locations, setLocations] = useState([]);

  const handleSearch = async () => {
    const results = await zomatoApi.searchLocation(searchQuery);
    setLocations(results);
  };

  return (
    <div className="location-search">
      <input
        type="text"
        value={searchQuery}
        onChange={(e) => setSearchQuery(e.target.value)}
        placeholder="Search for a location"
      />
      <button onClick={handleSearch}>Search</button>

      <div className="location-results">
        {locations.map(location => (
          <div key={location.id} className="location-item">
            {location.name}
          </div>
        ))}
      </div>
    </div>
  );
};

export default LocationSearch;