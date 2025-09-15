import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import toast from 'react-hot-toast';

const RegisterRestaurant = () => {
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    confirmPassword: '',
    phoneNumber: '',
    verificationMethod: 'email',
  });
  const [loading, setLoading] = useState(false);
  
  const { registerRestaurantOwner } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (formData.password !== formData.confirmPassword) {
      toast.error('Passwords do not match');
      return;
    }

    if (formData.password.length < 6) {
      toast.error('Password must be at least 6 characters long');
      return;
    }

    setLoading(true);

    try {
      const ownerData = {
        firstName: formData.firstName,
        lastName: formData.lastName,
        email: formData.email,
        password: formData.password,
        phoneNumber: formData.phoneNumber,
        verificationMethod: formData.verificationMethod,
      };
      
      await registerRestaurantOwner(ownerData);
      toast.success('Restaurant owner registration successful! Please verify your account and wait for admin approval.');
      navigate('/login');
    } catch (error) {
      toast.error(error.message || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container" style={{ maxWidth: '600px', margin: '3rem auto', padding: '0 20px' }}>
      <div className="card fade-in">
        <div className="card-header text-center">
          <h2>Register Your Restaurant</h2>
          <p style={{ color: '#666', marginTop: '0.5rem' }}>
            Join Foodie as a restaurant partner and grow your business
          </p>
        </div>
        
        <div className="card-body">
          <form onSubmit={handleSubmit}>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
              <div className="form-group">
                <label className="form-label">First Name</label>
                <input
                  type="text"
                  name="firstName"
                  value={formData.firstName}
                  onChange={handleChange}
                  className="form-input"
                  required
                  placeholder="John"
                />
              </div>

              <div className="form-group">
                <label className="form-label">Last Name</label>
                <input
                  type="text"
                  name="lastName"
                  value={formData.lastName}
                  onChange={handleChange}
                  className="form-input"
                  required
                  placeholder="Doe"
                />
              </div>
            </div>

            <div className="form-group">
              <label className="form-label">Email</label>
              <input
                type="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                className="form-input"
                required
                placeholder="john.doe@restaurant.com"
              />
            </div>

            <div className="form-group">
              <label className="form-label">Phone Number</label>
              <input
                type="tel"
                name="phoneNumber"
                value={formData.phoneNumber}
                onChange={handleChange}
                className="form-input"
                required
                placeholder="(555) 123-4567"
              />
            </div>

            <div className="form-group">
              <label className="form-label">Verification Method</label>
              <select
                name="verificationMethod"
                value={formData.verificationMethod}
                onChange={handleChange}
                className="form-input"
                required
              >
                <option value="email">Email Verification</option>
                <option value="phone">Phone Verification (SMS)</option>
              </select>
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
                placeholder="Enter a strong password"
                minLength="6"
              />
            </div>

            <div className="form-group">
              <label className="form-label">Confirm Password</label>
              <input
                type="password"
                name="confirmPassword"
                value={formData.confirmPassword}
                onChange={handleChange}
                className="form-input"
                required
                placeholder="Confirm your password"
                minLength="6"
              />
            </div>

            <div style={{ 
              backgroundColor: '#f0f4ff', 
              border: '1px solid #c7d2fe', 
              borderRadius: '8px', 
              padding: '1rem', 
              marginBottom: '1.5rem' 
            }}>
              <h4 style={{ color: '#4338ca', margin: '0 0 0.5rem 0', fontSize: '1rem' }}>
                What happens next?
              </h4>
              <ul style={{ color: '#666', fontSize: '0.9rem', margin: 0, paddingLeft: '1.2rem' }}>
                <li>Account verification via your chosen method</li>
                <li>Admin review and approval (1-2 business days)</li>
                <li>Restaurant profile setup assistance</li>
                <li>Menu upload and configuration help</li>
                <li>Go live on Foodie platform!</li>
              </ul>
            </div>

            <button
              type="submit"
              className="btn btn-primary"
              disabled={loading}
              style={{ width: '100%', marginBottom: '1rem' }}
            >
              {loading ? 'Submitting Application...' : 'Apply to Join Foodie'}
            </button>
          </form>
          
          <div style={{ textAlign: 'center', color: '#666', fontSize: '0.9rem', marginTop: '1rem' }}>
            By applying, you agree to our{' '}
            <Link to="/terms" style={{ color: '#667eea', textDecoration: 'none' }}>
              Restaurant Partner Terms
            </Link>{' '}
            and{' '}
            <Link to="/privacy" style={{ color: '#667eea', textDecoration: 'none' }}>
              Privacy Policy
            </Link>
          </div>
        </div>
        
        <div className="card-footer text-center">
          <p>
            Already have a restaurant account?{' '}
            <Link to="/login" style={{ color: '#667eea', textDecoration: 'none', fontWeight: '500' }}>
              Sign in
            </Link>
          </p>
          <p style={{ marginTop: '0.5rem' }}>
            Looking for a personal account?{' '}
            <Link to="/register" style={{ color: '#667eea', textDecoration: 'none', fontWeight: '500' }}>
              Customer Registration
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default RegisterRestaurant;
