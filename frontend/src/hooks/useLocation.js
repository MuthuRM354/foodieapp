import { useState, useCallback } from 'react';

export const useToast = () => {
  const [toast, setToast] = useState({ visible: false, message: '', type: '' });

  const showToast = useCallback((message, type = 'info') => {
    setToast({ visible: true, message, type });
    setTimeout(() => {
      setToast({ visible: false, message: '', type: '' });
    }, 3000);
  }, []);

  const hideToast = useCallback(() => {
    setToast({ visible: false, message: '', type: '' });
  }, []);

  return {
    toast,
    showToast,
    hideToast,
  };
};
