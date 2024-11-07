import React, { useState, useEffect } from 'react';
import Modal from '../common/Modal';
import LoadingStates from '../common/LoadingStates';
import { useToast } from '../../hooks/useToast';

const UserManagement = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedUser, setSelectedUser] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [filters, setFilters] = useState({
    role: 'all',
    status: 'all',
    search: ''
  });
  const { showToast } = useToast();

  useEffect(() => {
    fetchUsers();
  }, [filters]);

  const fetchUsers = async () => {
    setLoading(true);
    try {
      // API call to fetch users with filters
      setLoading(false);
    } catch (error) {
      showToast('Failed to fetch users', 'error');
      setLoading(false);
    }
  };

  const handleUserAction = async (userId, action) => {
    try {
      switch(action) {
        case 'verify':
          // API call to verify user
          showToast('User verified successfully', 'success');
          break;
        case 'suspend':
          // API call to suspend user
          showToast('User suspended successfully', 'warning');
          break;
        case 'delete':
          // API call to delete user
          showToast('User deleted successfully', 'success');
          break;
        default:
          break;
      }
      fetchUsers();
    } catch (error) {
      showToast('Action failed: ' + error.message, 'error');
    }
  };

  const renderUserTable = () => (
    <div className="users-table-container">
      <table className="users-table">
        <thead>
          <tr>
            <th>Name</th>
            <th>Email</th>
            <th>Role</th>
            <th>Status</th>
            <th>Joined Date</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {users.map(user => (
            <tr key={user.id}>
              <td>
                <div className="user-info">
                  <img src={user.avatar || '/assets/images/default-avatar.png'} alt={user.name} />
                  <span>{user.name}</span>
                </div>
              </td>
              <td>{user.email}</td>
              <td>
                <span className={`role-badge ${user.role.toLowerCase()}`}>
                  {user.role}
                </span>
              </td>
              <td>
                <span className={`status-badge ${user.status}`}>
                  {user.status}
                </span>
              </td>
              <td>{new Date(user.joinedDate).toLocaleDateString()}</td>
              <td className="actions">
                <button
                  onClick={() => handleUserAction(user.id, 'verify')}
                  className="verify-btn"
                  disabled={user.status === 'verified'}
                >
                  <i className="fas fa-check"></i>
                </button>
                <button
                  onClick={() => handleUserAction(user.id, 'suspend')}
                  className="suspend-btn"
                >
                  <i className="fas fa-ban"></i>
                </button>
                <button
                  onClick={() => {
                    setSelectedUser(user);
                    setIsModalOpen(true);
                  }}
                  className="details-btn"
                >
                  <i className="fas fa-info-circle"></i>
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );

  return (
    <div className="user-management">
      <div className="management-header">
        <h2>User Management</h2>
        <div className="filters">
          <select
            value={filters.role}
            onChange={(e) => setFilters({...filters, role: e.target.value})}
          >
            <option value="all">All Roles</option>
            <option value="customer">Customers</option>
            <option value="restaurant_owner">Restaurant Owners</option>
            <option value="admin">Admins</option>
          </select>
          <select
            value={filters.status}
            onChange={(e) => setFilters({...filters, status: e.target.value})}
          >
            <option value="all">All Status</option>
            <option value="active">Active</option>
            <option value="suspended">Suspended</option>
            <option value="pending">Pending</option>
          </select>
          <input
            type="search"
            placeholder="Search users..."
            value={filters.search}
            onChange={(e) => setFilters({...filters, search: e.target.value})}
          />
        </div>
      </div>

      {loading ? <LoadingStates /> : renderUserTable()}

      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title="User Details"
      >
        {selectedUser && (
          <div className="user-details">
            <div className="user-profile">
              <img src={selectedUser.avatar || '/assets/images/default-avatar.png'} alt={selectedUser.name} />
              <h3>{selectedUser.name}</h3>
              <p>{selectedUser.email}</p>
            </div>
            <div className="user-info-grid">
              <div className="info-item">
                <label>Role:</label>
                <span>{selectedUser.role}</span>
              </div>
              <div className="info-item">
                <label>Status:</label>
                <span>{selectedUser.status}</span>
              </div>
              <div className="info-item">
                <label>Joined:</label>
                <span>{new Date(selectedUser.joinedDate).toLocaleDateString()}</span>
              </div>
              <div className="info-item">
                <label>Last Login:</label>
                <span>{new Date(selectedUser.lastLogin).toLocaleDateString()}</span>
              </div>
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
};

export default UserManagement;
