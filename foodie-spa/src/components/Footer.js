import React from 'react';

const Footer = () => {
  return (
    <footer style={{ backgroundColor: '#333', color: 'white', padding: '2rem 0', marginTop: '4rem' }}>
      <div className="container">
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '2rem' }}>
          <div>
            <h3 style={{ marginBottom: '1rem' }}>🍽️ Foodie</h3>
            <p>Your favorite food delivery service. Fresh food, fast delivery, great taste!</p>
          </div>
          
          <div>
            <h4 style={{ marginBottom: '1rem' }}>Quick Links</h4>
            <ul style={{ listStyle: 'none' }}>
              <li style={{ marginBottom: '0.5rem' }}>
                <a href="/" style={{ color: 'white', textDecoration: 'none' }}>Home</a>
              </li>
              <li style={{ marginBottom: '0.5rem' }}>
                <a href="/restaurants" style={{ color: 'white', textDecoration: 'none' }}>Restaurants</a>
              </li>
              <li style={{ marginBottom: '0.5rem' }}>
                <a href="/about" style={{ color: 'white', textDecoration: 'none' }}>About Us</a>
              </li>
              <li style={{ marginBottom: '0.5rem' }}>
                <a href="/contact" style={{ color: 'white', textDecoration: 'none' }}>Contact</a>
              </li>
            </ul>
          </div>
          
          <div>
            <h4 style={{ marginBottom: '1rem' }}>For Restaurants</h4>
            <ul style={{ listStyle: 'none' }}>
              <li style={{ marginBottom: '0.5rem' }}>
                <a href="/register-restaurant" style={{ color: 'white', textDecoration: 'none' }}>Partner with us</a>
              </li>
              <li style={{ marginBottom: '0.5rem' }}>
                <a href="/restaurant-login" style={{ color: 'white', textDecoration: 'none' }}>Restaurant Login</a>
              </li>
            </ul>
          </div>
          
          <div>
            <h4 style={{ marginBottom: '1rem' }}>Contact Info</h4>
            <p>📧 support@foodie.com</p>
            <p>📞 +91 12345 67890</p>
            <p>📍 Bangalore, India</p>
          </div>
        </div>
        
        <div style={{ textAlign: 'center', marginTop: '2rem', paddingTop: '2rem', borderTop: '1px solid #555' }}>
          <p>&copy; 2025 Foodie. All rights reserved.</p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
