import React, { useState, useEffect } from 'react';
import ImageUpload from '../common/ImageUpload';
import LoadingStates from '../common/LoadingStates';
import { useToast } from '../../hooks/useToast';

const RestaurantProfile = () => {
  const [profile, setProfile] = useState({
    name: '',
    description: '',
    cuisine: '',
    address: '',
    phone: '',
    email: '',
    openingHours: {},
    deliveryRadius: 0,
    minimumOrder: 0,
    image: '',
    banner: ''
  });

  const [loading, setLoading] = useState(true);
  const { showToast } = useToast();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      // API call to update restaurant profile
      showToast('Profile updated successfully', 'success');
    } catch (error) {
      showToast('Failed to update profile', 'error');
    }
  };

  return (
    <div className="restaurant-profile">
      <h2>Restaurant Profile</h2>

      {loading ? (
        <LoadingStates />
      ) : (
        <form onSubmit={handleSubmit} className="profile-form">
          <div className="image-section">
            <div className="profile-image">
              <h3>Profile Image</h3>
              <ImageUpload
                currentImage={profile.image}
                onImageUpload={(url) => setProfile({...profile, image: url})}
              />
            </div>
            <div className="banner-image">
              <h3>Banner Image</h3>
              <ImageUpload
                currentImage={profile.banner}
                onImageUpload={(url) => setProfile({...profile, banner: url})}
              />
            </div>
          </div>

          <div className="form-section">
            <div className="form-group">
              <label>Restaurant Name</label>
              <input
                type="text"
                value={profile.name}
                onChange={(e) => setProfile({...profile, name: e.target.value})}
              />
            </div>

            <div className="form-group">
              <label>Description</label>
              <textarea
                value={profile.description}
                onChange={(e) => setProfile({...profile, description: e.target.value})}
              />
            </div>

            <div className="form-group">
              <label>Cuisine Type</label>
              <input
                type="text"
                value={profile.cuisine}
                onChange={(e) => setProfile({...profile, cuisine: e.target.value})}
              />
            </div>

            <div className="form-group">
              <label>Opening Hours</label>
              {/* Opening hours component */}
            </div>

            <div className="form-group">
              <label>Delivery Settings</label>
              <input
                type="number"
                placeholder="Delivery Radius (km)"
                value={profile.deliveryRadius}
                onChange={(e) => setProfile({...profile, deliveryRadius: e.target.value})}
              />
              <input
                type="number"
                placeholder="Minimum Order ($)"
                value={profile.minimumOrder}
                onChange={(e) => setProfile({...profile, minimumOrder: e.target.value})}
              />
            </div>
          </div>

          <button type="submit" className="save-button">
            Save Changes
          </button>
        </form>
      )}
    </div>
  );
};

export default RestaurantProfile;