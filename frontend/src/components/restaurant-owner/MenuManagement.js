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
  const [activeCategory, setActiveCategory] = useState('all');
  const { showToast } = useToast();

  const [formData, setFormData] = useState({
    name: '',
    description: '',
    price: '',
    category: '',
    image: '',
    available: true,
    preparationTime: '',
    allergens: [],
    spicyLevel: 'medium'
  });

  useEffect(() => {
    fetchMenuData();
  }, []);

  const fetchMenuData = async () => {
    setLoading(true);
    try {
      // API calls to fetch menu data
      setLoading(false);
    } catch (error) {
      showToast('Failed to fetch menu data', 'error');
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      // API call to add/update menu item
      showToast('Menu item saved successfully', 'success');
      setIsModalOpen(false);
      fetchMenuData();
    } catch (error) {
      showToast('Failed to save menu item', 'error');
    }
  };

  const handleCategoryAdd = async (categoryName) => {
    try {
      // API call to add new category
      setCategories([...categories, { id: Date.now(), name: categoryName }]);
      showToast('Category added successfully', 'success');
    } catch (error) {
      showToast('Failed to add category', 'error');
    }
  };

  const renderMenuItems = () => {
    const filteredItems = activeCategory === 'all'
      ? menuItems
      : menuItems.filter(item => item.category === activeCategory);

    return filteredItems.map(item => (
      <div key={item.id} className="menu-item-card">
        <div className="item-image">
          <img src={item.image} alt={item.name} loading="lazy" />
          {!item.available && <div className="unavailable-overlay">Unavailable</div>}
        </div>
        <div className="item-details">
          <h3>{item.name}</h3>
          <p className="description">{item.description}</p>
          <p className="price">${item.price}</p>
          <div className="item-meta">
            <span className="prep-time">
              <i className="fas fa-clock"></i> {item.preparationTime} mins
            </span>
            <span className={`spicy-level ${item.spicyLevel}`}>
              <i className="fas fa-pepper-hot"></i> {item.spicyLevel}
            </span>
          </div>
          <div className="actions">
            <button onClick={() => {
              setSelectedItem(item);
              setFormData(item);
              setIsModalOpen(true);
            }}>
              <i className="fas fa-edit"></i> Edit
            </button>
            <button onClick={() => handleToggleAvailability(item.id)}>
              <i className={`fas fa-${item.available ? 'times' : 'check'}`}></i>
              {item.available ? 'Disable' : 'Enable'}
            </button>
          </div>
        </div>
      </div>
    ));
  };

  return (
    <div className="menu-management">
      {/* Component implementation continues as in the original file */}
    </div>
  );
};

export default MenuManagement;
