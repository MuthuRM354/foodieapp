import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { removeToast } from '../../redux/actions/toastActions';

const Toast = () => {
  const toasts = useSelector(state => state.toast.toasts);
  const dispatch = useDispatch();

  return (
    <div className="toast-container">
      {toasts.map(toast => (
        <div key={toast.id} className={`toast toast-${toast.type}`}>
          <p>{toast.message}</p>
          <button onClick={() => dispatch(removeToast(toast.id))}>×</button>
        </div>
      ))}
    </div>
  );
};

export default Toast;
