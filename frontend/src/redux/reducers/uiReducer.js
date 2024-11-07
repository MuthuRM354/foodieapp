import { SET_LOADING, SET_ERROR, CLEAR_ERROR, SHOW_TOAST, HIDE_TOAST } from '../types';

const initialState = {
  loading: false,
  toast: {
    show: false,
    message: '',
    type: ''
  },
  error: null
};

export default function uiReducer(state = initialState, action) {
  switch (action.type) {
    case SET_LOADING:
      return {
        ...state,
        loading: action.payload
      };

    case SET_ERROR:
      return {
        ...state,
        error: action.payload,
        toast: {
          show: true,
          message: action.payload,
          type: 'error'
        }
      };

    case CLEAR_ERROR:
      return {
        ...state,
        error: null
      };

    case SHOW_TOAST:
      return {
        ...state,
        toast: {
          show: true,
          message: action.payload.message,
          type: action.payload.type
        }
      };

    case HIDE_TOAST:
      return {
        ...state,
        toast: {
          show: false,
          message: '',
          type: ''
        }
      };

    default:
      return state;
  }
}
