import React, { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import { useSelector } from 'react-redux';
import Header from './Header';
import Footer from './Footer';
import Navigation from './Navigation';
import ErrorBoundary from '../common/ErrorBoundary';
import Toast from '../common/Toast';
import { useToast } from '../../hooks/useToast';

const Layout = ({ children }) => {
  const location = useLocation();
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const user = useSelector(state => state.auth.user);
  const { showToast } = useToast();

  useEffect(() => {
    const handleResize = () => {
      if (window.innerWidth > 768) {
        setIsSidebarOpen(true);
      } else {
        setIsSidebarOpen(false);
      }
    };

    window.addEventListener('resize', handleResize);
    handleResize();

    return () => window.removeEventListener('resize', handleResize);
  }, []);

  const toggleSidebar = () => {
    setIsSidebarOpen(!isSidebarOpen);
  };

  const handleNavigation = () => {
    if (window.innerWidth <= 768) {
      setIsSidebarOpen(false);
    }
  };

  return (
    <div className={`app-layout ${isSidebarOpen ? 'sidebar-open' : ''}`}>
      <ErrorBoundary>
        <Header
          onMenuClick={toggleSidebar}
          user={user}
        />
        <div className="main-container">
          <aside className={`sidebar ${isSidebarOpen ? 'open' : ''}`}>
            <Navigation onNavigate={handleNavigation} />
          </aside>
          <main className="main-content">
            <div className="content-wrapper">
              <ErrorBoundary>
                {children}
              </ErrorBoundary>
            </div>
          </main>
        </div>
        <Footer />
        <Toast />
      </ErrorBoundary>
    </div>
  );
};

export default Layout;
