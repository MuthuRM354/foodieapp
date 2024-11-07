import React, { useState } from 'react';
import { useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { register } from '../../redux/actions/authActions';
import { useToast } from '../../hooks/useToast';
import LoadingStates from '../common/LoadingStates';

const Register = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { showToast } = useToast();
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    confirmPassword: '',
    role: 'CUSTOMER'
  });

  const validateForm = () => {
    if (formData.password !== formData.confirmPassword) {
      showToast('Passwords do not match', 'error');
      return false;
    }
    if (formData.password.length < 8) {
      showToast('Password must be at least 8 characters', 'error');
      return false;
    }
    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;

    setLoading(true);
    try {
      await dispatch(register(formData));
      showToast('Registration successful!', 'success');
      navigate('/login');
    } catch (error) {
      showToast(error.message || 'Registration failed', 'error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h2>Create Account</h2>
        <p>Join FoodieApp today</p>

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Full Name</label>
            <input
              type="text"
              value={formData.name}
              onChange={(e) => setFormData({...formData, name: e.target.value})}
              required
              placeholder="Enter your full name"
            />
          </div>

          <div className="form-group">
            <label>Email Address</label>
            <input
              type="email"
              value={formData.email}
              onChange={(e) => setFormData({...formData, email: e.target.value})}
              required
              placeholder="Enter your email"
            />
          </div>

          <div className="form-group">
            <label>Password</label>
            <input
              type="password"
              value={formData.password}
              onChange={(e) => setFormData({...formData, password: e.target.value})}
              required
              placeholder="Create a password"
              minLength="8"
            />
          </div>

          <div className="form-group">
            <label>Confirm Password</label>
            <input
              type="password"
              value={formData.confirmPassword}
              onChange={(e) => setFormData({...formData, confirmPassword: e.target.value})}
              required
              placeholder="Confirm your password"
            />
          </div>

          <div className="form-group">
            <label>Register as</label>
            <select
              value={formData.role}
              onChange={(e) => setFormData({...formData, role: e.target.value})}
            >
              <option value="CUSTOMER">Customer</option>
              <option value="RESTAURANT_OWNER">Restaurant Owner</option>
            </select>
          </div>

          <button type="submit" disabled={loading} className="submit-btn">
            {loading ? <LoadingStates type="spinner" /> : 'Create Account'}
          </button>
        </form>

        <div className="auth-links">
          <p>Already have an account? <a href="/login">Sign In</a></p>
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

export default Register;
