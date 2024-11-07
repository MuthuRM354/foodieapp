import React from 'react';
import { useLocation } from 'react-router-dom';
import Header from './Header';
import Footer from './Footer';
import Navigation from './Navigation';
import ErrorBoundary from '../common/ErrorBoundary';

const Layout = ({ children }) => {
  const location = useLocation();

  return (
    <div className="app-layout">
      <ErrorBoundary>
        <Header />
        <Navigation />
        <main className="main-content">
          <div className="content-wrapper">
            {children}
          </div>
        </main>
        <Footer />
      </ErrorBoundary>
    </div>
  );
};

export default Layout;
