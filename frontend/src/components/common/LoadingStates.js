import React from 'react';

const LoadingStates = ({ type = 'default' }) => {
  const renderLoadingState = () => {
    switch (type) {
      case 'skeleton':
        return (
          <div className="skeleton-loading" role="alert" aria-busy="true">
            <div className="skeleton-item"></div>
            <div className="skeleton-item"></div>
            <div className="skeleton-item"></div>
            <div className="skeleton-item"></div>
            <div className="skeleton-item"></div>
          </div>
        );

      case 'spinner':
        return (
          <div className="spinner-loading" role="alert" aria-busy="true">
            <div className="spinner"></div>
            <span className="sr-only">Loading...</span>
          </div>
        );

      case 'pulse':
        return (
          <div className="pulse-loading" role="alert" aria-busy="true">
            <div className="pulse"></div>
            <div className="pulse-shadow"></div>
            <span className="sr-only">Loading...</span>
          </div>
        );

      default:
        return (
          <div className="default-loading" role="alert" aria-busy="true">
            <div className="loading-dots">
              <span></span>
              <span></span>
              <span></span>
            </div>
            <p className="loading-text">Loading...</p>
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
