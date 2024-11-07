import React, { useState } from 'react';
import { useDispatch } from 'react-redux';
import { login } from '../../redux/actions/authActions';

const Login = () => {
  const dispatch = useDispatch();
  const [credentials, setCredentials] = useState({ email: '', password: '' });

  const handleSubmit = async (e) => {
    e.preventDefault();
    await dispatch(login(credentials));
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h2>Welcome Back</h2>
        <p>Sign in to continue to FoodieApp</p>

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Email Address</label>
            <input
              type="email"
              value={credentials.email}
              onChange={(e) => setCredentials({...credentials, email: e.target.value})}
              required
            />
          </div>

          <div className="form-group">
            <label>Password</label>
            <input
              type="password"
              value={credentials.password}
              onChange={(e) => setCredentials({...credentials, password: e.target.value})}
              required
            />
          </div>

          <button type="submit">Sign In</button>
        </form>

        <div className="auth-links">
          <a href="/forgot-password">Forgot Password?</a>
          <a href="/register">Create an Account</a>
        </div>

        <div className="social-auth">
          <button className="google-auth">
            Continue with Google
          </button>
          <button className="facebook-auth">
            Continue with Facebook
          </button>
        </div>
      </div>
    </div>
  );
};

export default Login;