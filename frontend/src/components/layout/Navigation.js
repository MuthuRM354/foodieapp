import React from 'react';
import { Link } from 'react-router-dom';
import { useSelector } from 'react-redux';

const Navigation = () => {
  const userRole = useSelector(state => state.auth.user?.role);

  const renderNavLinks = () => {
    switch(userRole) {
      case 'ADMIN':
        return (
          <nav className="admin-nav">
            <Link to="/admin/dashboard">Dashboard</Link>
            <Link to="/admin/users">User Management</Link>
            <Link to="/admin/restaurants">Restaurant Approvals</Link>
            <Link to="/admin/analytics">System Analytics</Link>
          </nav>
        );

      case 'RESTAURANT_OWNER':
        return (
          <nav className="restaurant-nav">
            <Link to="/restaurant/menu">Menu Management</Link>
            <Link to="/restaurant/orders">Orders</Link>
            <Link to="/restaurant/profile">Restaurant Profile</Link>
            <Link to="/restaurant/analytics">Analytics</Link>
          </nav>
        );

      default:
        return (
          <nav className="customer-nav">
            <Link to="/search">Search</Link>
            <Link to="/cuisines">Cuisines</Link>
            <Link to="/orders">My Orders</Link>
            <Link to="/favorites">Favorites</Link>
          </nav>
        );
    }
  };

  return (
    <div className="main-navigation">
      {renderNavLinks()}
    </div>
  );
};

export default Navigation;
