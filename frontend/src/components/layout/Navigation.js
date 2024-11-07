import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useSelector } from 'react-redux';

const Navigation = ({ onNavigate }) => {
  const location = useLocation();
  const { user } = useSelector(state => state.auth);

  const isActive = (path) => {
    return location.pathname === path ? 'active' : '';
  };

  const renderAdminNav = () => (
    <nav className="admin-nav">
      <Link to="/admin/dashboard" className={isActive('/admin/dashboard')} onClick={onNavigate}>
        <i className="fas fa-chart-line"></i>
        <span>Dashboard</span>
      </Link>
      <Link to="/admin/users" className={isActive('/admin/users')} onClick={onNavigate}>
        <i className="fas fa-users"></i>
        <span>Users</span>
      </Link>
      <Link to="/admin/restaurants" className={isActive('/admin/restaurants')} onClick={onNavigate}>
        <i className="fas fa-utensils"></i>
        <span>Restaurants</span>
      </Link>
      <Link to="/admin/analytics" className={isActive('/admin/analytics')} onClick={onNavigate}>
        <i className="fas fa-chart-bar"></i>
        <span>Analytics</span>
      </Link>
      <Link to="/admin/settings" className={isActive('/admin/settings')} onClick={onNavigate}>
        <i className="fas fa-cog"></i>
        <span>Settings</span>
      </Link>
    </nav>
  );

  const renderRestaurantOwnerNav = () => (
    <nav className="restaurant-nav">
      <Link to="/restaurant/dashboard" className={isActive('/restaurant/dashboard')} onClick={onNavigate}>
        <i className="fas fa-home"></i>
        <span>Dashboard</span>
      </Link>
      <Link to="/restaurant/menu" className={isActive('/restaurant/menu')} onClick={onNavigate}>
        <i className="fas fa-book-open"></i>
        <span>Menu</span>
      </Link>
      <Link to="/restaurant/orders" className={isActive('/restaurant/orders')} onClick={onNavigate}>
        <i className="fas fa-shopping-bag"></i>
        <span>Orders</span>
      </Link>
      <Link to="/restaurant/profile" className={isActive('/restaurant/profile')} onClick={onNavigate}>
        <i className="fas fa-store"></i>
        <span>Profile</span>
      </Link>
      <Link to="/restaurant/analytics" className={isActive('/restaurant/analytics')} onClick={onNavigate}>
        <i className="fas fa-chart-pie"></i>
        <span>Analytics</span>
      </Link>
    </nav>
  );

  const renderCustomerNav = () => (
    <nav className="customer-nav">
      <Link to="/restaurants" className={isActive('/restaurants')} onClick={onNavigate}>
        <i className="fas fa-utensils"></i>
        <span>Restaurants</span>
      </Link>
      <Link to="/cuisines" className={isActive('/cuisines')} onClick={onNavigate}>
        <i className="fas fa-globe"></i>
        <span>Cuisines</span>
      </Link>
      <Link to="/orders" className={isActive('/orders')} onClick={onNavigate}>
        <i className="fas fa-shopping-bag"></i>
        <span>Orders</span>
      </Link>
      <Link to="/favorites" className={isActive('/favorites')} onClick={onNavigate}>
        <i className="fas fa-heart"></i>
        <span>Favorites</span>
      </Link>
      <Link to="/profile" className={isActive('/profile')} onClick={onNavigate}>
        <i className="fas fa-user"></i>
        <span>Profile</span>
      </Link>
    </nav>
  );

  const renderNavLinks = () => {
    switch(user?.role) {
      case 'ADMIN':
        return renderAdminNav();
      case 'RESTAURANT_OWNER':
        return renderRestaurantOwnerNav();
      default:
        return renderCustomerNav();
    }
  };

  return (
    <div className="main-navigation">
      {renderNavLinks()}
    </div>
  );
};

export default Navigation;
