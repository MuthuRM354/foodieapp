import {
  FETCH_RESTAURANTS,
  SET_RESTAURANT,
  UPDATE_RESTAURANT,
  SET_LOADING,
  SET_ERROR
} from '../types';

const initialState = {
  restaurants: [],
  currentRestaurant: null,
  loading: false,
  error: null
};

const restaurantReducer = (state = initialState, action) => {
  switch (action.type) {
    case FETCH_RESTAURANTS:
      return {
        ...state,
        restaurants: action.payload,
        loading: false
      };

    case SET_RESTAURANT:
      return {
        ...state,
        currentRestaurant: action.payload,
        loading: false
      };

    case UPDATE_RESTAURANT:
      return {
        ...state,
        restaurants: state.restaurants.map(restaurant =>
          restaurant.id === action.payload.id ? action.payload : restaurant
        ),
        currentRestaurant: state.currentRestaurant?.id === action.payload.id
          ? action.payload
          : state.currentRestaurant
      };

    case SET_LOADING:
      return {
        ...state,
        loading: true
      };

    case SET_ERROR:
      return {
        ...state,
        error: action.payload,
        loading: false
      };

    default:
      return state;
  }
};

export default restaurantReducer;
