import React, { useState, useEffect } from 'react';
import Modal from '../common/Modal';
import LoadingStates from '../common/LoadingStates';
import { useToast } from '../../hooks/useToast';
import ImageUpload from '../common/ImageUpload';

const RestaurantApprovals = () => {
  const [pendingRestaurants, setPendingRestaurants] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedRestaurant, setSelectedRestaurant] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const { showToast } = useToast();

  const handleApproval = async (restaurantId, status) => {
    try {
      // API call to update restaurant status
      showToast(`Restaurant ${status === 'approved' ? 'approved' : 'rejected'} successfully`, 'success');
    } catch (error) {
      showToast('Failed to update restaurant status', 'error');
    }
  };

  return (
    <div className="restaurant-approvals">
      <h2>Restaurant Approvals</h2>

      <div className="filters">
        <select>
          <option value="pending">Pending</option>
          <option value="approved">Approved</option>
          <option value="rejected">Rejected</option>
        </select>
        <input type="search" placeholder="Search restaurants..." />
      </div>

      {loading ? (
        <LoadingStates />
      ) : (
        <div className="restaurants-grid">
          {pendingRestaurants.map(restaurant => (
            <div key={restaurant.id} className="restaurant-card">
              <img src={restaurant.image} alt={restaurant.name} />
              <div className="restaurant-info">
                <h3>{restaurant.name}</h3>
                <p>{restaurant.cuisine}</p>
                <p>{restaurant.location}</p>
                <div className="documents">
                  <a href={restaurant.license}>View License</a>
                  <a href={restaurant.permits}>View Permits</a>
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
            {/* Detailed restaurant information */}
          </div>
        )}
      </Modal>
    </div>
  );
};

export default RestaurantApprovals;