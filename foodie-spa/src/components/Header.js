import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { useCart } from '../contexts/CartContext';
import { FiShoppingCart, FiUser, FiLogOut, FiMenu } from 'react-icons/fi';

const Header = () => {
  const { user, isAuthenticated, logout } = useAuth();
  const { itemCount } = useCart();
  const navigate = useNavigate();

  const handleLogout = async () => {
    try {
      await logout();
      navigate('/login');
    } catch (error) {
      console.error('Logout failed:', error);
    }
  };

  return (
    <header className="header">
      <div className="container">
        <div className="header-content">
          <Link to="/" className="logo">
            🍽️ Foodie
          </Link>
          
          <nav>
            <ul className="nav-links">
              <li><Link to="/">Home</Link></li>
              <li><Link to="/restaurants">Restaurants</Link></li>
              {isAuthenticated && (
                <>
                  <li><Link to="/orders">My Orders</Link></li>
                  {user?.role === 'RESTAURANT_OWNER' && (
                    <li><Link to="/dashboard">Dashboard</Link></li>
                  )}
                  {user?.role === 'ADMIN' && (
                    <li><Link to="/admin">Admin</Link></li>
                  )}
                </>
              )}
            </ul>
          </nav>

          <div className="user-menu">
            {isAuthenticated ? (
              <>
                <Link to="/cart" className="cart-link">
                  <FiShoppingCart size={20} />
                  {itemCount > 0 && (
                    <span className="cart-badge">{itemCount}</span>
                  )}
                </Link>
                <div className="user-info">
                  <FiUser size={20} />
                  <span>{user?.username || user?.email}</span>
                </div>
                <button onClick={handleLogout} className="btn btn-outline">
                  <FiLogOut size={16} />
                  Logout
                </button>
              </>
            ) : (
              <div className="auth-links">
                <Link to="/login" className="btn btn-outline">Login</Link>
                <Link to="/register" className="btn btn-primary">Sign Up</Link>
              </div>
            )}
          </div>
        </div>
      </div>
      
      <style jsx>{`
        .cart-link {
          position: relative;
          color: white;
          text-decoration: none;
        }
        
        .cart-badge {
          position: absolute;
          top: -8px;
          right: -8px;
          background: #ff4757;
          color: white;
          border-radius: 50%;
          width: 20px;
          height: 20px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 0.75rem;
          font-weight: bold;
        }
        
        .user-info {
          display: flex;
          align-items: center;
          gap: 0.5rem;
          color: white;
        }
        
        .auth-links {
          display: flex;
          gap: 1rem;
        }
        
        @media (max-width: 768px) {
          .nav-links {
            display: none;
          }
          
          .user-menu {
            flex-direction: column;
            gap: 0.5rem;
          }
          
          .auth-links {
            flex-direction: column;
          }
        }
      `}</style>
    </header>
  );
};

export default Header;
