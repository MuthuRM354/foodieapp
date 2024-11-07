import {
  LOGIN_SUCCESS,
  LOGIN_FAIL,
  LOGOUT,
  SET_USER,
  UPDATE_USER,
} from '../types';

const initialState = {
  token: localStorage.getItem('token'),
  isAuthenticated: null,
  user: null,
  error: null,
};

export default function authReducer(state = initialState, action) {
  switch (action.type) {
    case LOGIN_SUCCESS:
      return {
        ...state,
        token: action.payload.token,
        isAuthenticated: true,
        error: null,
      };
    case LOGIN_FAIL:
      return {
        ...state,
        token: null,
        isAuthenticated: false,
        error: action.payload,
      };
    case LOGOUT:
      return {
        ...state,
        token: null,
        isAuthenticated: false,
        user: null,
      };
    case SET_USER:
      return {
        ...state,
        user: action.payload,
        isAuthenticated: true,
      };
    case UPDATE_USER:
      return {
        ...state,
        user: { ...state.user, ...action.payload },
      };
    default:
      return state;
  }
}
