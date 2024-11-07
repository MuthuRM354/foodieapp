import { LOGIN_SUCCESS, LOGIN_FAIL, LOGOUT, SET_USER, REGISTER_SUCCESS, REGISTER_FAIL } from '../types';
import authService from '../../services/authService';

export const login = (credentials) => async (dispatch) => {
  try {
    const data = await authService.login(credentials);
    dispatch({
      type: LOGIN_SUCCESS,
      payload: data,
    });
    return data;
  } catch (error) {
    dispatch({
      type: LOGIN_FAIL,
      payload: error.response?.data?.message || 'Login failed',
    });
    throw error;
  }
};

export const registerUser = (userData) => async (dispatch) => {
  try {
    const data = await authService.register(userData);
    dispatch({
      type: REGISTER_SUCCESS,
      payload: data,
    });
    return data;
  } catch (error) {
    dispatch({
      type: REGISTER_FAIL,
      payload: error.response?.data?.message || 'Registration failed',
    });
    throw error;
  }
};

export const logout = () => (dispatch) => {
  authService.logout();
  dispatch({ type: LOGOUT });
};

export const getCurrentUser = () => async (dispatch) => {
  try {
    const user = await authService.getCurrentUser();
    dispatch({
      type: SET_USER,
      payload: user,
    });
    return user;
  } catch (error) {
    console.error('Error fetching current user:', error);
    throw error;
  }
};
