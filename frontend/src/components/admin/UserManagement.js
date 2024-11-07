import React, { useState, useEffect } from 'react';
import Modal from '../common/Modal';
import LoadingStates from '../common/LoadingStates';
import { useToast } from '../../hooks/useToast';

const UserManagement = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedUser, setSelectedUser] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const { showToast } = useToast();

  const handleUserAction = async (userId, action) => {
    try {
      switch(action) {
        case 'verify':
          // Handle verification
          break;
        case 'suspend':
          // Handle suspension
          break;
        case 'delete':
          // Handle deletion
          break;
      }
      showToast('User updated successfully', 'success');
    } catch (error) {
      showToast('Failed to update user', 'error');
    }
  };

  return (
    <div className="user-management">
      <h2>User Management</h2>

      <div className="filters">
        <select>
          <option value="all">All Users</option>
          <option value="customer">Customers</option>
          <option value="restaurant">Restaurant Owners</option>
          <option value="admin">Admins</option>
        </select>
        <input type="search" placeholder="Search users..." />
      </div>

      {loading ? (
        <LoadingStates />
      ) : (
        <table className="users-table">
          <thead>
            <tr>
              <th>Name</th>
              <th>Email</th>
              <th>Role</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {users.map(user => (
              <tr key={user.id}>
                <td>{user.name}</td>
                <td>{user.email}</td>
                <td>{user.role}</td>
                <td>{user.status}</td>
                <td>
                  <button onClick={() => handleUserAction(user.id, 'verify')}>
                    Verify
                  </button>
                  <button onClick={() => handleUserAction(user.id, 'suspend')}>
                    Suspend
                  </button>
                  <button onClick={() => handleUserAction(user.id, 'delete')}>
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title="User Details"
      >
        {selectedUser && (
          <div className="user-details">
            {/* User details form */}
          </div>
        )}
      </Modal>
    </div>
  );
};

export default UserManagement;