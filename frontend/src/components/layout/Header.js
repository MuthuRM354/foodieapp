import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import { useAuth } from '../../hooks/useAuth';

const Header = () => {
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const user = useSelector(state => state.auth.user);
  const { logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <header className="main-header">
      <div className="header-container">
        <Link to="/" className="logo">
          <img src="/assets/images/logo.png" alt="FoodieApp" />
        </Link>

        <nav className="main-nav">
          {user ? (
            <>
              <Link to="/restaurants" className="nav-link">
                <i className="fas fa-utensils"></i>
                <span>Explore</span>
              </Link>
              <Link to="/orders" className="nav-link">
                <i className="fas fa-shopping-bag"></i>
                <span>Orders</span>
              </Link>
              <div className="user-menu">
                <button
                  className="profile-button"
                  onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                >
                  <img
                    src={user.avatar || '/assets/images/default-avatar.png'}
                    alt={user.name}
                    className="avatar"
                  />
                  <span>{user.name}</span>
                  <i className="fas fa-chevron-down"></i>
                </button>
                {isDropdownOpen && (
                  <div className="dropdown-menu">
                    <Link to="/profile" className="dropdown-item">
                      <i className="fas fa-user"></i> Profile
                    </Link>
                    <Link to="/settings" className="dropdown-item">
                      <i className="fas fa-cog"></i> Settings
                    </Link>
                    <button onClick={handleLogout} className="dropdown-item">
                      <i className="fas fa-sign-out-alt"></i> Logout
                    </button>
                  </div>
                )}
              </div>
            </>
          ) : (
            <>
              <Link to="/login" className="auth-button login">Login</Link>
              <Link to="/register" className="auth-button register">Register</Link>
            </>
          )}
        </nav>
      </div>
    </header>
  );
};

export default Header;
