import { useSelector, useDispatch } from 'react-redux';
import { useCallback } from 'react';
import { login, logout, getCurrentUser } from '../redux/actions/authActions';

export const useAuth = () => {
  const dispatch = useDispatch();
  const { user, isAuthenticated, loading } = useSelector((state) => state.auth);

  const handleLogin = useCallback(
    async (credentials) => {
      return dispatch(login(credentials));
    },
    [dispatch]
  );

  const handleLogout = useCallback(() => {
    dispatch(logout());
  }, [dispatch]);

  const fetchCurrentUser = useCallback(() => {
    return dispatch(getCurrentUser());
  }, [dispatch]);

  return {
    user,
    isAuthenticated,
    loading,
    login: handleLogin,
    logout: handleLogout,
    getCurrentUser: fetchCurrentUser,
  };
};
