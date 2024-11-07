import React from 'react';
import { Link } from 'react-router-dom';

const Footer = () => {
  const currentYear = new Date().getFullYear();

  return (
    <footer className="footer">
      <div className="footer-container">
        <div className="footer-section brand">
          <h3>FoodieApp</h3>
          <p>Discover great food near you</p>
          <div className="social-links">
            <a href="https://facebook.com/foodieapp" target="_blank" rel="noopener noreferrer">
              <i className="fab fa-facebook"></i>
            </a>
            <a href="https://twitter.com/foodieapp" target="_blank" rel="noopener noreferrer">
              <i className="fab fa-twitter"></i>
            </a>
            <a href="https://instagram.com/foodieapp" target="_blank" rel="noopener noreferrer">
              <i className="fab fa-instagram"></i>
            </a>
            <a href="https://linkedin.com/company/foodieapp" target="_blank" rel="noopener noreferrer">
              <i className="fab fa-linkedin"></i>
            </a>
          </div>
          <div className="app-downloads">
            <a href="#" className="app-store-btn">
              <i className="fab fa-apple"></i> App Store
            </a>
            <a href="#" className="play-store-btn">
              <i className="fab fa-google-play"></i> Play Store
            </a>
          </div>
        </div>

        <div className="footer-section">
          <h4>Quick Links</h4>
          <ul>
            <li><Link to="/about">About Us</Link></li>
            <li><Link to="/contact">Contact</Link></li>
            <li><Link to="/careers">Careers</Link></li>
            <li><Link to="/blog">Blog</Link></li>
            <li><Link to="/press">Press</Link></li>
          </ul>
        </div>

        <div className="footer-section">
          <h4>For Restaurants</h4>
          <ul>
            <li><Link to="/partner">Partner with us</Link></li>
            <li><Link to="/restaurant/app">Restaurant App</Link></li>
            <li><Link to="/restaurant/dashboard">Restaurant Dashboard</Link></li>
            <li><Link to="/restaurant/marketing">Marketing Tools</Link></li>
            <li><Link to="/restaurant/support">Restaurant Support</Link></li>
          </ul>
        </div>

        <div className="footer-section">
          <h4>Legal</h4>
          <ul>
            <li><Link to="/terms">Terms & Conditions</Link></li>
            <li><Link to="/privacy">Privacy Policy</Link></li>
            <li><Link to="/refund">Refund Policy</Link></li>
            <li><Link to="/cookies">Cookie Policy</Link></li>
            <li><Link to="/security">Security</Link></li>
          </ul>
        </div>

        <div className="footer-section">
          <h4>Support</h4>
          <ul>
            <li><Link to="/help">Help Center</Link></li>
            <li><Link to="/faq">FAQ</Link></li>
            <li><a href="mailto:support@foodieapp.com">Email Support</a></li>
            <li><a href="tel:+1234567890">Call Support</a></li>
            <li><Link to="/feedback">Send Feedback</Link></li>
          </ul>
        </div>
      </div>

      <div className="footer-bottom">
        <p>&copy; {currentYear} FoodieApp. All rights reserved.</p>
        <div className="footer-bottom-links">
          <Link to="/sitemap">Sitemap</Link>
          <Link to="/accessibility">Accessibility</Link>
          <Link to="/responsible-disclosure">Responsible Disclosure</Link>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
