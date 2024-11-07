import React, { useState, useEffect } from 'react';
import ImageUpload from '../common/ImageUpload';
import Modal from '../common/Modal';
import LoadingStates from '../common/LoadingStates';
import { useToast } from '../../hooks/useToast';

const MenuManagement = () => {
  const [menuItems, setMenuItems] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedItem, setSelectedItem] = useState(null);
  const { showToast } = useToast();

  const [formData, setFormData] = useState({
    name: '',
    description: '',
    price: '',
    category: '',
    image: '',
    available: true
  });

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      // API call to add/update menu item
      showToast('Menu item saved successfully', 'success');
      setIsModalOpen(false);
    } catch (error) {
      showToast('Failed to save menu item', 'error');
    }
  };

  return (
    <div className="menu-management">
      <div className="header">
        <h2>Menu Management</h2>
        <button onClick={() => {
          setSelectedItem(null);
          setIsModalOpen(true);
        }}>
          Add New Item
        </button>
      </div>

      {loading ? (
        <LoadingStates />
      ) : (
        <div className="menu-grid">
          {menuItems.map(item => (
            <div key={item.id} className="menu-item-card">
              <img src={item.image} alt={item.name} />
              <div className="item-details">
                <h3>{item.name}</h3>
                <p>{item.description}</p>
                <p className="price">${item.price}</p>
                <div className="actions">
                  <button onClick={() => {
                    setSelectedItem(item);
                    setIsModalOpen(true);
                  }}>Edit</button>
                  <button>Delete</button>
                  <button>{item.available ? 'Disable' : 'Enable'}</button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title={selectedItem ? 'Edit Menu Item' : 'Add Menu Item'}
      >
        <form onSubmit={handleSubmit}>
          <ImageUpload
            onImageUpload={(url) => setFormData({...formData, image: url})}
          />
          {/* Form fields */}
        </form>
      </Modal>
    </div>
  );
};

export default MenuManagement;