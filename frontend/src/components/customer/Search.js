import React from 'react';

const Search = () => {
  return (
    <div className="search-page">
      <h2>Search Restaurants</h2>
      <div className="search-container">
        <input type="text" placeholder="Search for restaurants or cuisines..." />
        <button>Search</button>
      </div>
    </div>
  );
};

export default Search;
