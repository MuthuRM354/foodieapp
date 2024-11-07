const initialState = {
  loading: false,
  toast: {
    show: false,
    message: '',
    type: ''
  }
};

export default function uiReducer(state = initialState, action) {
  switch (action.type) {
    // Add your UI reducer cases here
    default:
      return state;
  }
}
