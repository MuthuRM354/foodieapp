import React from 'react';

const LoadingStates = ({ type = 'default' }) => {
  const renderLoadingState = () => {
    switch (type) {
      case 'skeleton':
        return (
          <div className="skeleton-loading">
            <div className="skeleton-item"></div>
            <div className="skeleton-item"></div>
            <div className="skeleton-item"></div>
          </div>
        );

      case 'spinner':
        return (
          <div className="spinner-loading">
            <div className="spinner"></div>
          </div>
        );

      case 'pulse':
        return (
          <div className="pulse-loading">
            <div className="pulse"></div>
          </div>
        );

      default:
        return (
          <div className="default-loading">
            <div className="loading-dots">
              <span></span>
              <span></span>
              <span></span>
            </div>
            <p>Loading...</p>
          </div>
        );
    }
  };

  return (
    <div className="loading-states">
      {renderLoadingState()}
    </div>
  );
};

export default LoadingStates;
