import React, { useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { removeToast } from '../../redux/actions/toastActions';

const Toast = () => {
  const toasts = useSelector(state => state.toast.toasts);
  const dispatch = useDispatch();

  useEffect(() => {
    if (toasts.length > 0) {
      const timer = setTimeout(() => {
        dispatch(removeToast(toasts[0].id));
      }, 3000);

      return () => clearTimeout(timer);
    }
  }, [toasts, dispatch]);

  const getToastIcon = (type) => {
    switch (type) {
      case 'success':
        return <i className="fas fa-check-circle"></i>;
      case 'error':
        return <i className="fas fa-exclamation-circle"></i>;
      case 'warning':
        return <i className="fas fa-exclamation-triangle"></i>;
      default:
        return <i className="fas fa-info-circle"></i>;
    }
  };

  return (
    <div className="toast-container">
      {toasts.map(toast => (
        <div
          key={toast.id}
          className={`toast toast-${toast.type}`}
          role="alert"
        >
          <div className="toast-icon">
            {getToastIcon(toast.type)}
          </div>
          <p className="toast-message">{toast.message}</p>
          <button
            onClick={() => dispatch(removeToast(toast.id))}
            className="toast-close"
            aria-label="Close notification"
          >
            <i className="fas fa-times"></i>
          </button>
        </div>
      ))}
    </div>
  );
};

export default Toast;
