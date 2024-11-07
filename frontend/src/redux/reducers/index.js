import { combineReducers } from 'redux';
import authReducer from './authReducer';
import restaurantReducer from './restaurantReducer';
import uiReducer from './uiReducer';

export default combineReducers({
  auth: authReducer,
  restaurants: restaurantReducer,
  ui: uiReducer,
});
