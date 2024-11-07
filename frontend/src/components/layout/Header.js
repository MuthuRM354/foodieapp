import React from 'react';
import { Link } from 'react-router-dom';
import { useSelector } from 'react-redux';

const Header = () => {
  const user = useSelector(state => state.auth.user);

  return (
    <header className="main-header">
      <div className="header-container">
        <Link to="/" className="logo">
          <img src="/assets/images/logo.png" alt="FoodieApp" />
        </Link>
        <nav className="main-nav">
          <Link to="/restaurants">Explore</Link>
          {user ? (
            <>
              <Link to="/orders">Orders</Link>
              <Link to="/profile">Profile</Link>
            </>
          ) : (
            <>
              <Link to="/login">Login</Link>
              <Link to="/register">Register</Link>
            </>
          )}
        </nav>
      </div>
    </header>
  );
};

export default Header;