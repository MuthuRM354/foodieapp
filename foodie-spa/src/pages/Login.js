import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { mockLogin, mockTestUsers } from '../services/mockAuthService';
import toast from 'react-hot-toast';

const Login = () => {
  const [formData, setFormData] = useState({
    email: '',
    password: '',
  });
  const [loading, setLoading] = useState(false);
  
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  
  const from = location.state?.from?.pathname || '/';

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      await login(formData.email, formData.password);
      toast.success('Login successful!');
      navigate(from, { replace: true });
    } catch (error) {
      toast.error(error.message || 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  const handleTestLogin = async (testUser) => {
    setLoading(true);
    try {
      await mockLogin(testUser.email, testUser.password);
      // Update auth context with mock user
      await login(testUser.email, testUser.password);
      toast.success(`Logged in as ${testUser.role.replace('ROLE_', '').toLowerCase()}`);
      navigate(from, { replace: true });
    } catch (error) {
      toast.error('Test login failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container" style={{ maxWidth: '400px', margin: '3rem auto', padding: '0 20px' }}>
      <div className="card fade-in">
        <div className="card-header text-center">
          <h2>Welcome Back</h2>
          <p style={{ color: '#666', marginTop: '0.5rem' }}>Sign in to your account</p>
        </div>
        
        <div className="card-body">
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label className="form-label">Email</label>
              <input
                type="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                className="form-input"
                required
                placeholder="Enter your email"
              />
            </div>

            <div className="form-group">
              <label className="form-label">Password</label>
              <input
                type="password"
                name="password"
                value={formData.password}
                onChange={handleChange}
                className="form-input"
                required
                placeholder="Enter your password"
              />
            </div>

            <button
              type="submit"
              className="btn btn-primary"
              disabled={loading}
              style={{ width: '100%', marginBottom: '1rem' }}
            >
              {loading ? 'Signing in...' : 'Sign In'}
            </button>
          </form>
          
          {/* Test User Buttons for Development */}
          <div style={{ marginTop: '1rem', padding: '1rem', backgroundColor: '#f8f9fa', borderRadius: '8px', border: '1px solid #e9ecef' }}>
            <h5 style={{ margin: '0 0 1rem 0', color: '#495057', fontSize: '14px', fontWeight: 'bold' }}>
              🧪 Test Users (Development Only)
            </h5>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
              <button
                type="button"
                onClick={() => handleTestLogin(mockTestUsers.customer)}
                className="btn"
                style={{ 
                  padding: '8px 12px', 
                  fontSize: '12px', 
                  backgroundColor: '#28a745', 
                  color: 'white', 
                  border: 'none', 
                  borderRadius: '4px' 
                }}
              >
                Login as Customer (john.doe@example.com)
              </button>
              <button
                type="button"
                onClick={() => handleTestLogin(mockTestUsers.restaurantOwner)}
                className="btn"
                style={{ 
                  padding: '8px 12px', 
                  fontSize: '12px', 
                  backgroundColor: '#fd7e14', 
                  color: 'white', 
                  border: 'none', 
                  borderRadius: '4px' 
                }}
              >
                Login as Restaurant Owner (mario@pizzapalace.com)
              </button>
              <button
                type="button"
                onClick={() => handleTestLogin(mockTestUsers.admin)}
                className="btn"
                style={{ 
                  padding: '8px 12px', 
                  fontSize: '12px', 
                  backgroundColor: '#dc3545', 
                  color: 'white', 
                  border: 'none', 
                  borderRadius: '4px' 
                }}
              >
                Login as Admin (admin@foodie.com)
              </button>
            </div>
          </div>
          
          <div style={{ textAlign: 'center', marginTop: '1rem' }}>
            <Link to="/forgot-password" style={{ color: '#667eea', textDecoration: 'none' }}>
              Forgot your password?
            </Link>
          </div>
        </div>
        
        <div className="card-footer text-center">
          <p>
            Don't have an account?{' '}
            <Link to="/register" style={{ color: '#667eea', textDecoration: 'none', fontWeight: '500' }}>
              Sign up
            </Link>
          </p>
          <p style={{ marginTop: '1rem' }}>
            Restaurant owner?{' '}
            <Link to="/register-restaurant" style={{ color: '#667eea', textDecoration: 'none', fontWeight: '500' }}>
              Register your restaurant
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Login;
