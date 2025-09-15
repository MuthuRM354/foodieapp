import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { FiStar, FiClock, FiMapPin, FiPhone, FiMinus, FiPlus } from 'react-icons/fi';
import restaurantService from '../services/restaurantService';
import { useCart } from '../contexts/CartContext';
import { useAuth } from '../contexts/AuthContext';
import LoadingSpinner from '../components/LoadingSpinner';
import toast from 'react-hot-toast';

const RestaurantDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { addToCart } = useCart();
  const { user } = useAuth();
  
  const [restaurant, setRestaurant] = useState(null);
  const [menu, setMenu] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedCategory, setSelectedCategory] = useState('all');
  const [itemQuantities, setItemQuantities] = useState({});

  useEffect(() => {
    loadRestaurantData();
  }, [id]);

  const loadRestaurantData = async () => {
    try {
      const [restaurantData, menuData] = await Promise.all([
        restaurantService.getRestaurantById(id),
        restaurantService.getRestaurantMenu(id),
      ]);
      setRestaurant(restaurantData);
      setMenu(menuData);
    } catch (error) {
      console.error('Error loading restaurant:', error);
      toast.error('Failed to load restaurant details');
      navigate('/restaurants');
    } finally {
      setLoading(false);
    }
  };

  const handleAddToCart = (item) => {
    if (!user) {
      toast.error('Please login to add items to cart');
      navigate('/login');
      return;
    }

    const cartItem = {
      id: item.id,
      name: item.name,
      price: item.price,
      image: item.imageUrl,
      restaurantId: restaurant.id,
      restaurantName: restaurant.name,
      quantity: itemQuantities[item.id] || 1,
    };

    addToCart(cartItem);
    toast.success(`${item.name} added to cart!`);
  };

  const updateQuantity = (itemId, quantity) => {
    if (quantity < 1) quantity = 1;
    if (quantity > 10) quantity = 10;
    
    setItemQuantities(prev => ({
      ...prev,
      [itemId]: quantity
    }));
  };

  const getFilteredMenu = () => {
    if (selectedCategory === 'all') return menu;
    return menu.filter(item => item.category === selectedCategory);
  };

  const getCategories = () => {
    const categories = [...new Set(menu.map(item => item.category))];
    return ['all', ...categories];
  };

  if (loading) {
    return <LoadingSpinner />;
  }

  if (!restaurant) {
    return (
      <div className="container text-center" style={{ padding: '3rem 20px' }}>
        <h2>Restaurant not found</h2>
        <button 
          onClick={() => navigate('/restaurants')}
          className="btn btn-primary"
          style={{ marginTop: '1rem' }}
        >
          Back to Restaurants
        </button>
      </div>
    );
  }

  return (
    <div>
      {/* Restaurant Hero */}
      <div style={{
        height: '300px',
        backgroundImage: `url(${restaurant.imageUrl || '/api/placeholder/800/300'})`,
        backgroundSize: 'cover',
        backgroundPosition: 'center',
        position: 'relative'
      }}>
        <div style={{
          position: 'absolute',
          bottom: 0,
          left: 0,
          right: 0,
          background: 'linear-gradient(transparent, rgba(0,0,0,0.8))',
          padding: '2rem 0'
        }}>
          <div className="container" style={{ color: 'white' }}>
            <h1 style={{ fontSize: '2.5rem', marginBottom: '0.5rem' }}>
              {restaurant.name}
            </h1>
            <div style={{ display: 'flex', alignItems: 'center', gap: '2rem', flexWrap: 'wrap' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <FiStar color="#fbbf24" />
                <span>{restaurant.averageRating?.toFixed(1) || 'New'} Rating</span>
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <FiClock />
                <span>{restaurant.estimatedDeliveryTime || 30}-{(restaurant.estimatedDeliveryTime || 30) + 15} min</span>
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <FiMapPin />
                <span>{restaurant.deliveryFee ? `$${restaurant.deliveryFee}` : 'Free'} delivery</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="container" style={{ padding: '2rem 20px' }}>
        {/* Restaurant Info */}
        <div className="card" style={{ marginBottom: '2rem', padding: '1.5rem' }}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr auto', gap: '2rem', alignItems: 'start' }}>
            <div>
              <h2 style={{ marginBottom: '0.5rem' }}>About {restaurant.name}</h2>
              {restaurant.description && (
                <p style={{ color: '#666', marginBottom: '1rem' }}>{restaurant.description}</p>
              )}
              
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem', marginBottom: '1rem' }}>
                {restaurant.cuisineTypes?.map(cuisine => (
                  <span 
                    key={cuisine}
                    style={{
                      backgroundColor: '#667eea',
                      color: 'white',
                      padding: '0.25rem 0.75rem',
                      borderRadius: '20px',
                      fontSize: '0.9rem'
                    }}
                  >
                    {cuisine}
                  </span>
                ))}
              </div>

              {restaurant.address && (
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: '#666' }}>
                  <FiMapPin />
                  <span>{restaurant.address}</span>
                </div>
              )}
              
              {restaurant.phoneNumber && (
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: '#666', marginTop: '0.5rem' }}>
                  <FiPhone />
                  <span>{restaurant.phoneNumber}</span>
                </div>
              )}
            </div>
            
            <div style={{ textAlign: 'center' }}>
              <div style={{ 
                backgroundColor: restaurant.isOpen ? '#10b981' : '#ef4444',
                color: 'white',
                padding: '0.5rem 1rem',
                borderRadius: '20px',
                fontSize: '0.9rem',
                fontWeight: '500'
              }}>
                {restaurant.isOpen ? 'Open Now' : 'Closed'}
              </div>
              {restaurant.openingHours && (
                <p style={{ color: '#666', fontSize: '0.9rem', marginTop: '0.5rem' }}>
                  {restaurant.openingHours}
                </p>
              )}
            </div>
          </div>
        </div>

        {/* Menu Section */}
        <div>
          <h2 style={{ marginBottom: '1.5rem' }}>Menu</h2>
          
          {/* Category Filter */}
          {getCategories().length > 1 && (
            <div style={{ 
              display: 'flex', 
              gap: '1rem', 
              marginBottom: '2rem',
              overflowX: 'auto',
              paddingBottom: '0.5rem'
            }}>
              {getCategories().map(category => (
                <button
                  key={category}
                  onClick={() => setSelectedCategory(category)}
                  style={{
                    backgroundColor: selectedCategory === category ? '#667eea' : 'white',
                    color: selectedCategory === category ? 'white' : '#333',
                    border: '2px solid #667eea',
                    padding: '0.5rem 1rem',
                    borderRadius: '25px',
                    cursor: 'pointer',
                    whiteSpace: 'nowrap',
                    textTransform: 'capitalize',
                    transition: 'all 0.2s ease'
                  }}
                >
                  {category === 'all' ? 'All Items' : category}
                </button>
              ))}
            </div>
          )}

          {/* Menu Items */}
          {getFilteredMenu().length === 0 ? (
            <div className="card text-center" style={{ padding: '3rem' }}>
              <h3>No menu items available</h3>
              <p style={{ color: '#666' }}>Check back later for updates</p>
            </div>
          ) : (
            <div style={{ 
              display: 'grid', 
              gap: '1.5rem' 
            }}>
              {getFilteredMenu().map(item => (
                <MenuItemCard 
                  key={item.id} 
                  item={item}
                  quantity={itemQuantities[item.id] || 1}
                  onQuantityChange={(quantity) => updateQuantity(item.id, quantity)}
                  onAddToCart={() => handleAddToCart(item)}
                />
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

const MenuItemCard = ({ item, quantity, onQuantityChange, onAddToCart }) => {
  return (
    <div className="card" style={{ padding: '1.5rem' }}>
      <div style={{ 
        display: 'grid', 
        gridTemplateColumns: 'auto 1fr auto', 
        gap: '1.5rem',
        alignItems: 'center'
      }}>
        {/* Item Image */}
        <div style={{
          width: '100px',
          height: '100px',
          borderRadius: '8px',
          backgroundImage: `url(${item.imageUrl || '/api/placeholder/100/100'})`,
          backgroundSize: 'cover',
          backgroundPosition: 'center',
          backgroundColor: '#f3f4f6'
        }} />
        
        {/* Item Details */}
        <div>
          <h3 style={{ margin: '0 0 0.5rem 0' }}>{item.name}</h3>
          {item.description && (
            <p style={{ color: '#666', margin: '0 0 0.5rem 0', fontSize: '0.9rem' }}>
              {item.description}
            </p>
          )}
          <div style={{ 
            display: 'flex', 
            alignItems: 'center', 
            gap: '1rem',
            fontSize: '0.9rem',
            color: '#666'
          }}>
            <span style={{ 
              fontSize: '1.2rem', 
              fontWeight: 'bold', 
              color: '#667eea' 
            }}>
              ${item.price?.toFixed(2)}
            </span>
            {item.category && (
              <span style={{
                backgroundColor: '#f3f4f6',
                padding: '0.25rem 0.5rem',
                borderRadius: '12px',
                fontSize: '0.8rem',
                textTransform: 'capitalize'
              }}>
                {item.category}
              </span>
            )}
          </div>
        </div>
        
        {/* Add to Cart Controls */}
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '1rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <button
              onClick={() => onQuantityChange(quantity - 1)}
              style={{
                backgroundColor: '#f3f4f6',
                border: 'none',
                borderRadius: '50%',
                width: '32px',
                height: '32px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                cursor: 'pointer'
              }}
            >
              <FiMinus size={16} />
            </button>
            <span style={{ 
              minWidth: '30px', 
              textAlign: 'center',
              fontWeight: '500',
              fontSize: '1rem'
            }}>
              {quantity}
            </span>
            <button
              onClick={() => onQuantityChange(quantity + 1)}
              style={{
                backgroundColor: '#f3f4f6',
                border: 'none',
                borderRadius: '50%',
                width: '32px',
                height: '32px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                cursor: 'pointer'
              }}
            >
              <FiPlus size={16} />
            </button>
          </div>
          
          <button
            onClick={onAddToCart}
            className="btn btn-primary"
            style={{ fontSize: '0.9rem', padding: '0.5rem 1rem' }}
          >
            Add to Cart
          </button>
        </div>
      </div>
    </div>
  );
};

export default RestaurantDetail;
