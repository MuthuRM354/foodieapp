import React from 'react';
import { Link } from 'react-router-dom';

const Footer = () => {
  return (
    <footer className="main-footer">
      <div className="footer-grid">
        <div className="footer-section">
          <h3>FoodieApp</h3>
          <p>Discover great food near you</p>
          <div className="social-links">
            <Link to="https://facebook.com/foodieapp" target="_blank">
              <i className="fab fa-facebook"></i>
            </Link>
            <Link to="https://twitter.com/foodieapp" target="_blank">
              <i className="fab fa-twitter"></i>
            </Link>
            <Link to="https://instagram.com/foodieapp" target="_blank">
              <i className="fab fa-instagram"></i>
            </Link>
          </div>
        </div>

        <div className="footer-section">
          <h4>Company</h4>
          <Link to="/about">About Us</Link>
          <Link to="/careers">Careers</Link>
          <Link to="/blog">Blog</Link>
        </div>

        <div className="footer-section">
          <h4>Support</h4>
          <Link to="/help">Help Center</Link>
          <Link to="/safety">Safety</Link>
          <Link to="/terms">Terms of Service</Link>
          <Link to="/privacy">Privacy Policy</Link>
        </div>

        <div className="footer-section">
          <h4>Partner With Us</h4>
          <Link to="/restaurant/register">Add Restaurant</Link>
          <Link to="/delivery/register">Become Delivery Partner</Link>
        </div>
      </div>

      <div className="footer-bottom">
        <p>© {new Date().getFullYear()} FoodieApp. All rights reserved.</p>
      </div>
    </footer>
  );
};

export default Footer;