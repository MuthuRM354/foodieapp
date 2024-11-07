import React, { useState } from 'react';
import { useDispatch } from 'react-redux';
import { useNavigate, useLocation } from 'react-router-dom';
import { login } from '../../redux/actions/authActions';
import { useToast } from '../../hooks/useToast';
import LoadingStates from '../common/LoadingStates';

const Login = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const location = useLocation();
  const { showToast } = useToast();
  const [loading, setLoading] = useState(false);
  const [credentials, setCredentials] = useState({
    email: '',
    password: ''
  });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      await dispatch(login(credentials));
      const redirectPath = location.state?.from?.pathname || '/';
      showToast('Login successful!', 'success');
      navigate(redirectPath);
    } catch (error) {
      showToast(error.message || 'Login failed', 'error');
    } finally {
      setLoading(false);
    }
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
              placeholder="Enter your email"
            />
          </div>

          <div className="form-group">
            <label>Password</label>
            <div className="password-input">
              <input
                type="password"
                value={credentials.password}
                onChange={(e) => setCredentials({...credentials, password: e.target.value})}
                required
                placeholder="Enter your password"
              />
            </div>
          </div>

          <button type="submit" disabled={loading} className="submit-btn">
            {loading ? <LoadingStates type="spinner" /> : 'Sign In'}
          </button>
        </form>

        <div className="auth-links">
          <a href="/forgot-password" className="forgot-password">
            Forgot Password?
          </a>
          <a href="/register" className="create-account">
            Create an Account
          </a>
        </div>

        <div className="social-auth">
          <button className="google-auth">
            <i className="fab fa-google"></i> Continue with Google
          </button>
          <button className="facebook-auth">
            <i className="fab fa-facebook"></i> Continue with Facebook
          </button>
        </div>
      </div>
    </div>
  );
};

export default Login;
