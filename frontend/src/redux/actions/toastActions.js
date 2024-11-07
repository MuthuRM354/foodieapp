export const SHOW_TOAST = 'SHOW_TOAST';
export const HIDE_TOAST = 'HIDE_TOAST';

export const showToast = (message, type = 'info') => ({
  type: SHOW_TOAST,
  payload: { message, type }
});

export const hideToast = () => ({
  type: HIDE_TOAST
});

// Alias for removeToast
export const removeToast = hideToast;