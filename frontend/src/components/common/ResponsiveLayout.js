import React, { useState, useEffect } from 'react';
import { useLocation, Link } from 'react-router-dom';
import { useSelector } from 'react-redux';

const ResponsiveLayout = ({ children }) => {
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const location = useLocation();
  const user = useSelector(state => state.auth.user);

  useEffect(() => {
    setIsMobileMenuOpen(false);
  }, [location]);

  useEffect(() => {
    const handleResize = () => {
      if (window.innerWidth > 768) {
        setIsMobileMenuOpen(false);
      }
    };

    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  return (
    <div className="responsive-layout">
      <div className="layout-container">
        <aside className={`sidebar ${isMobileMenuOpen ? 'open' : ''}`}>
          <nav className="sidebar-nav">
            <ul>
              <li>
                <Link to="/dashboard">
                  <i className="fas fa-home"></i>
                  <span>Dashboard</span>
                </Link>
              </li>
              <li>
                <Link to="/restaurants">
                  <i className="fas fa-utensils"></i>
                  <span>Restaurants</span>
                </Link>
              </li>
              <li>
                <Link to="/orders">
                  <i className="fas fa-shopping-bag"></i>
                  <span>Orders</span>
                </Link>
              </li>
              <li>
                <Link to="/favorites">
                  <i className="fas fa-heart"></i>
                  <span>Favorites</span>
                </Link>
              </li>
              <li>
                <Link to="/profile">
                  <i className="fas fa-user"></i>
                  <span>Profile</span>
                </Link>
              </li>
            </ul>
          </nav>
        </aside>

        <main className="main-content">
          <header className="mobile-header">
            <button
              className={`menu-toggle ${isMobileMenuOpen ? 'active' : ''}`}
              onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
              aria-label="Toggle menu"
            >
              <span className="menu-icon"></span>
            </button>
            <h1>FoodieApp</h1>
            {user && (
              <div className="user-avatar">
                <img src={user.avatar || '/assets/images/default-avatar.png'} alt={user.name} />
              </div>
            )}
          </header>

          <div className="content-wrapper">
            {children}
          </div>
        </main>
      </div>

      <nav className="mobile-nav">
        <Link to="/dashboard" className="nav-item">
          <i className="fas fa-home"></i>
          <span>Dashboard</span>
        </Link>
        <Link to="/restaurants" className="nav-item">
          <i className="fas fa-utensils"></i>
          <span>Restaurants</span>
        </Link>
        <Link to="/orders" className="nav-item">
          <i className="fas fa-shopping-bag"></i>
          <span>Orders</span>
        </Link>
        <Link to="/profile" className="nav-item">
          <i className="fas fa-user"></i>
          <span>Profile</span>
        </Link>
      </nav>
    </div>
  );
};

export default ResponsiveLayout;
