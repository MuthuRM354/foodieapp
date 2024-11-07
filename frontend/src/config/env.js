const config = {
  API_BASE_URL: process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080',
  MONGODB_URI: process.env.MONGODB_URI || 'mongodb://localhost:27017/foodieapp'
};

export default config;
