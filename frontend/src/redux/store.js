import { configureStore } from '@reduxjs/toolkit';

const initialState = {
  auth: {
    user: null
  }
};

const rootReducer = (state = initialState, action) => {
  switch (action.type) {
    default:
      return state;
  }
};

const store = configureStore({
  reducer: rootReducer
});

export default store;
