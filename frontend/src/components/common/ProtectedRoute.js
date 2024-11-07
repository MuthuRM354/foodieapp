import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useSelector } from 'react-redux';
import LoadingStates from './LoadingStates';

const ProtectedRoute = ({ children, roles }) => {
  const location = useLocation();
  const { user, isAuthenticated, loading } = useSelector(state => state.auth);

  if (loading) {
    return <LoadingStates />;
  }

  if (!isAuthenticated || !user) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (roles && !roles.includes(user.role)) {
    return <Navigate
      to="/"
      state={{
        error: "You don't have permission to access this page",
        from: location
      }}
      replace
    />;
  }

  return (
    <div className="protected-content">
      {children}
    </div>
  );
};

export default ProtectedRoute;
