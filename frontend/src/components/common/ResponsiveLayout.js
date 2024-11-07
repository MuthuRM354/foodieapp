import React from 'react';

const ResponsiveLayout = ({ children }) => {
  return (
    <div className="responsive-layout">
      <div className="layout-container">
        {/* Sidebar for larger screens */}
        <aside className="sidebar">
          <nav className="sidebar-nav">
            <ul>
              <li><a href="/dashboard">Dashboard</a></li>
              <li><a href="/restaurants">Restaurants</a></li>
              <li><a href="/orders">Orders</a></li>
              <li><a href="/favorites">Favorites</a></li>
              <li><a href="/profile">Profile</a></li>
            </ul>
          </nav>
        </aside>

        {/* Main content area */}
        <main className="main-content">
          {/* Mobile header */}
          <header className="mobile-header">
            <button className="menu-toggle">
              <span className="menu-icon"></span>
            </button>
            <h1>FoodieApp</h1>
          </header>

          {/* Content wrapper */}
          <div className="content-wrapper">
            {children}
          </div>
        </main>
      </div>

      {/* Mobile navigation */}
      <nav className="mobile-nav">
        <a href="/dashboard" className="nav-item">
          <i className="icon-dashboard"></i>
          <span>Dashboard</span>
        </a>
        <a href="/restaurants" className="nav-item">
          <i className="icon-restaurant"></i>
          <span>Restaurants</span>
        </a>
        <a href="/orders" className="nav-item">
          <i className="icon-order"></i>
          <span>Orders</span>
        </a>
        <a href="/profile" className="nav-item">
          <i className="icon-profile"></i>
          <span>Profile</span>
        </a>
      </nav>
    </div>
  );
};

export default ResponsiveLayout;
