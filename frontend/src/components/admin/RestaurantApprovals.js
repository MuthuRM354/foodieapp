import React, { useState, useEffect } from 'react';
import Modal from '../common/Modal';
import LoadingStates from '../common/LoadingStates';
import { useToast } from '../../hooks/useToast';
import ImageUpload from '../common/ImageUpload';

const RestaurantApprovals = () => {
  const [restaurants, setRestaurants] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedRestaurant, setSelectedRestaurant] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [filters, setFilters] = useState({
    status: 'pending',
    cuisine: 'all',
    search: ''
  });
  const { showToast } = useToast();

  const handleApproval = async (restaurantId, status, reason = '') => {
    try {
      // API call to update restaurant status
      showToast(`Restaurant ${status === 'approved' ? 'approved' : 'rejected'} successfully`, 'success');
      // Update local state
      setRestaurants(restaurants.map(restaurant =>
        restaurant.id === restaurantId
          ? { ...restaurant, status }
          : restaurant
      ));
    } catch (error) {
      showToast('Failed to update status: ' + error.message, 'error');
    }
  };

  return (
    <div className="restaurant-approvals">
      <div className="approvals-header">
        <h2>Restaurant Approvals</h2>
        <div className="filters">
          <select
            value={filters.status}
            onChange={(e) => setFilters({...filters, status: e.target.value})}
          >
            <option value="pending">Pending</option>
            <option value="approved">Approved</option>
            <option value="rejected">Rejected</option>
          </select>
          <select
            value={filters.cuisine}
            onChange={(e) => setFilters({...filters, cuisine: e.target.value})}
          >
            <option value="all">All Cuisines</option>
            <option value="italian">Italian</option>
            <option value="indian">Indian</option>
            <option value="chinese">Chinese</option>
          </select>
          <input
            type="search"
            placeholder="Search restaurants..."
            value={filters.search}
            onChange={(e) => setFilters({...filters, search: e.target.value})}
          />
        </div>
      </div>

      {loading ? (
        <LoadingStates />
      ) : (
        <div className="restaurants-grid">
          {restaurants.map(restaurant => (
            <div key={restaurant.id} className="restaurant-card">
              <img src={restaurant.image} alt={restaurant.name} />
              <div className="restaurant-info">
                <h3>{restaurant.name}</h3>
                <p>{restaurant.cuisine}</p>
                <p>{restaurant.location}</p>
                <div className="document-links">
                  <a href={restaurant.license} target="_blank" rel="noopener noreferrer">License</a>
                  <a href={restaurant.permits} target="_blank" rel="noopener noreferrer">Permits</a>
                </div>
                <div className="actions">
                  <button
                    className="approve-btn"
                    onClick={() => handleApproval(restaurant.id, 'approved')}
                  >
                    Approve
                  </button>
                  <button
                    className="reject-btn"
                    onClick={() => handleApproval(restaurant.id, 'rejected')}
                  >
                    Reject
                  </button>
                  <button
                    className="details-btn"
                    onClick={() => {
                      setSelectedRestaurant(restaurant);
                      setIsModalOpen(true);
                    }}
                  >
                    View Details
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title="Restaurant Details"
      >
        {selectedRestaurant && (
          <div className="restaurant-details">
            <div className="details-grid">
              <div className="detail-section">
                <h3>Basic Information</h3>
                <p>Name: {selectedRestaurant.name}</p>
                <p>Cuisine: {selectedRestaurant.cuisine}</p>
                <p>Location: {selectedRestaurant.location}</p>
                <p>Owner: {selectedRestaurant.owner}</p>
              </div>
              <div className="detail-section">
                <h3>Documents</h3>
                <div className="document-preview">
                  {/* Document previews */}
                </div>
              </div>
            </div>
            <div className="approval-actions">
              <textarea
                placeholder="Add notes or reasons for approval/rejection..."
                className="approval-notes"
              />
              <div className="action-buttons">
                <button onClick={() => handleApproval(selectedRestaurant.id, 'approved')}>
                  Approve Restaurant
                </button>
                <button onClick={() => handleApproval(selectedRestaurant.id, 'rejected')}>
                  Reject Restaurant
                </button>
              </div>
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
};

export default RestaurantApprovals;
