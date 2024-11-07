const initialState = {
  restaurants: [],
  currentRestaurant: null,
  loading: false,
  error: null
};

const restaurantReducer = (state = initialState, action) => {
  switch (action.type) {
    // Add your cases here
    default:
      return state;
  }
};

export default restaurantReducer;
