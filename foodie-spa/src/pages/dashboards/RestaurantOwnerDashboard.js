import React, { useState, useEffect } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import LoadingSpinner from '../../components/LoadingSpinner';
import * as restaurantOwnerService from '../../services/restaurantOwnerService';

const RestaurantOwnerDashboard = () => {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState('overview');
  const [loading, setLoading] = useState(true);
  const [dashboardStats, setDashboardStats] = useState(null);
  const [restaurants, setRestaurants] = useState([]);
  const [orders, setOrders] = useState([]);
  const [selectedRestaurant, setSelectedRestaurant] = useState(null);
  const [menuItems, setMenuItems] = useState([]);
  const [showMenuForm, setShowMenuForm] = useState(false);
  const [showRestaurantForm, setShowRestaurantForm] = useState(false);
  const [editingMenuItem, setEditingMenuItem] = useState(null);
  const [editingRestaurant, setEditingRestaurant] = useState(null);

  // Form states
  const [menuItemForm, setMenuItemForm] = useState({
    name: '',
    description: '',
    price: '',
    category: '',
    available: true,
    ingredients: '',
    allergens: '',
    preparationTime: ''
  });

  const [restaurantForm, setRestaurantForm] = useState({
    name: '',
    cuisineType: '',
    description: '',
    phone: '',
    email: '',
    address: {
      street: '',
      city: '',
      state: '',
      zipCode: ''
    },
    openingHours: '',
    deliveryFee: '',
    minimumOrder: '',
    estimatedDeliveryTime: ''
  });

  useEffect(() => {
    loadDashboardData();
  }, []);

  useEffect(() => {
    if (selectedRestaurant) {
      loadMenuItems(selectedRestaurant.id);
      loadRestaurantOrders(selectedRestaurant.id);
    }
  }, [selectedRestaurant]);

  const loadDashboardData = async () => {
    try {
      setLoading(true);
      const restaurantsData = await restaurantOwnerService.getOwnerRestaurants();
      setRestaurants(restaurantsData);
      
      if (restaurantsData.length > 0) {
        setSelectedRestaurant(restaurantsData[0]);
        const stats = await restaurantOwnerService.getRestaurantDashboardStats(restaurantsData[0].id);
        setDashboardStats(stats);
      }
    } catch (error) {
      console.error('Error loading dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  const loadMenuItems = async (restaurantId) => {
    try {
      const menuData = await restaurantOwnerService.getRestaurantMenu(restaurantId);
      setMenuItems(menuData);
    } catch (error) {
      console.error('Error loading menu items:', error);
    }
  };

  const loadRestaurantOrders = async (restaurantId) => {
    try {
      const ordersData = await restaurantOwnerService.getRestaurantOrders(restaurantId);
      setOrders(ordersData);
    } catch (error) {
      console.error('Error loading orders:', error);
    }
  };

  const handleCreateRestaurant = async (e) => {
    e.preventDefault();
    try {
      const newRestaurant = await restaurantOwnerService.createRestaurant(restaurantForm);
      setRestaurants([...restaurants, newRestaurant]);
      setRestaurantForm({
        name: '',
        cuisineType: '',
        description: '',
        phone: '',
        email: '',
        address: { street: '', city: '', state: '', zipCode: '' },
        openingHours: '',
        deliveryFee: '',
        minimumOrder: '',
        estimatedDeliveryTime: ''
      });
      setShowRestaurantForm(false);
      alert('Restaurant created successfully!');
    } catch (error) {
      console.error('Error creating restaurant:', error);
      alert('Failed to create restaurant');
    }
  };

  const handleCreateMenuItem = async (e) => {
    e.preventDefault();
    try {
      const menuItemData = {
        ...menuItemForm,
        price: parseFloat(menuItemForm.price),
        ingredients: menuItemForm.ingredients.split(',').map(i => i.trim()),
        allergens: menuItemForm.allergens.split(',').map(a => a.trim()),
        preparationTime: parseInt(menuItemForm.preparationTime)
      };

      let result;
      if (editingMenuItem) {
        result = await restaurantOwnerService.updateMenuItem(selectedRestaurant.id, editingMenuItem.id, menuItemData);
        setMenuItems(menuItems.map(item => item.id === editingMenuItem.id ? { ...item, ...menuItemData } : item));
      } else {
        result = await restaurantOwnerService.addMenuItem(selectedRestaurant.id, menuItemData);
        setMenuItems([...menuItems, { ...menuItemData, id: result.id || Date.now().toString() }]);
      }

      setMenuItemForm({
        name: '', description: '', price: '', category: '', available: true,
        ingredients: '', allergens: '', preparationTime: ''
      });
      setShowMenuForm(false);
      setEditingMenuItem(null);
      alert(`Menu item ${editingMenuItem ? 'updated' : 'created'} successfully!`);
    } catch (error) {
      console.error('Error saving menu item:', error);
      alert('Failed to save menu item');
    }
  };

  const handleDeleteMenuItem = async (itemId) => {
    if (window.confirm('Are you sure you want to delete this menu item?')) {
      try {
        await restaurantOwnerService.deleteMenuItem(selectedRestaurant.id, itemId);
        setMenuItems(menuItems.filter(item => item.id !== itemId));
        alert('Menu item deleted successfully!');
      } catch (error) {
        console.error('Error deleting menu item:', error);
        alert('Failed to delete menu item');
      }
    }
  };

  const handleEditMenuItem = (item) => {
    setEditingMenuItem(item);
    setMenuItemForm({
      name: item.name,
      description: item.description,
      price: item.price.toString(),
      category: item.category,
      available: item.available,
      ingredients: item.ingredients ? item.ingredients.join(', ') : '',
      allergens: item.allergens ? item.allergens.join(', ') : '',
      preparationTime: item.preparationTime ? item.preparationTime.toString() : ''
    });
    setShowMenuForm(true);
  };

  const handleUpdateOrderStatus = async (orderId, newStatus) => {
    try {
      await restaurantOwnerService.updateOrderStatus(orderId, newStatus);
      setOrders(orders.map(order => 
        order.id === orderId ? { ...order, status: newStatus } : order
      ));
      alert('Order status updated successfully!');
    } catch (error) {
      console.error('Error updating order status:', error);
      alert('Failed to update order status');
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'PENDING': return 'border-yellow-400 text-yellow-600';
      case 'CONFIRMED': return 'border-blue-400 text-blue-600';
      case 'PREPARING': return 'border-orange-400 text-orange-600';
      case 'READY': return 'border-purple-400 text-purple-600';
      case 'OUT_FOR_DELIVERY': return 'border-indigo-400 text-indigo-600';
      case 'DELIVERED': return 'border-green-400 text-green-600';
      case 'CANCELLED': return 'border-red-400 text-red-600';
      default: return 'border-gray-400 text-gray-600';
    }
  };

  if (loading) {
    return <LoadingSpinner />;
  }

  return (
    <div className="min-h-screen bg-gray-50 p-8">
      <div className="max-w-7xl mx-auto">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900">Restaurant Owner Dashboard</h1>
          <p className="text-gray-600 mt-2">Welcome, {user?.username || 'Restaurant Owner'}</p>
        </div>

        {/* Tab Navigation */}
        <div className="border-b border-gray-200 mb-8">
          <nav className="-mb-px flex space-x-8">
            {[
              { id: 'overview', label: 'Overview', icon: '📊' },
              { id: 'restaurants', label: 'Restaurants', icon: '🏪' },
              { id: 'menu', label: 'Menu Management', icon: '🍽️' },
              { id: 'orders', label: 'Orders', icon: '📋' },
              { id: 'analytics', label: 'Analytics', icon: '📈' }
            ].map((tab) => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`whitespace-nowrap py-2 px-1 border-b-2 font-medium text-sm ${
                  activeTab === tab.id
                    ? 'border-blue-500 text-blue-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}
              >
                <span className="mr-2">{tab.icon}</span>
                {tab.label}
              </button>
            ))}
          </nav>
        </div>

        {/* Tab Content */}
        {activeTab === 'overview' && (
          <div>
            {/* Quick Stats */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
              <div className="bg-white rounded-lg shadow-sm p-6">
                <div className="flex items-center">
                  <div className="p-2 bg-blue-100 rounded-md">
                    <span className="text-2xl">🏪</span>
                  </div>
                  <div className="ml-4">
                    <p className="text-sm font-medium text-gray-600">Total Restaurants</p>
                    <p className="text-2xl font-bold text-gray-900">{restaurants.length}</p>
                  </div>
                </div>
              </div>

              <div className="bg-white rounded-lg shadow-sm p-6">
                <div className="flex items-center">
                  <div className="p-2 bg-green-100 rounded-md">
                    <span className="text-2xl">📋</span>
                  </div>
                  <div className="ml-4">
                    <p className="text-sm font-medium text-gray-600">Today's Orders</p>
                    <p className="text-2xl font-bold text-gray-900">{dashboardStats?.todayOrders || 0}</p>
                  </div>
                </div>
              </div>

              <div className="bg-white rounded-lg shadow-sm p-6">
                <div className="flex items-center">
                  <div className="p-2 bg-yellow-100 rounded-md">
                    <span className="text-2xl">💰</span>
                  </div>
                  <div className="ml-4">
                    <p className="text-sm font-medium text-gray-600">Today's Revenue</p>
                    <p className="text-2xl font-bold text-gray-900">${dashboardStats?.todayRevenue?.toFixed(2) || '0.00'}</p>
                  </div>
                </div>
              </div>

              <div className="bg-white rounded-lg shadow-sm p-6">
                <div className="flex items-center">
                  <div className="p-2 bg-purple-100 rounded-md">
                    <span className="text-2xl">⭐</span>
                  </div>
                  <div className="ml-4">
                    <p className="text-sm font-medium text-gray-600">Pending Orders</p>
                    <p className="text-2xl font-bold text-gray-900">{dashboardStats?.pendingOrders || 0}</p>
                  </div>
                </div>
              </div>
            </div>

            {/* Restaurant Selector */}
            {restaurants.length > 1 && (
              <div className="mb-6">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Select Restaurant
                </label>
                <select
                  value={selectedRestaurant?.id || ''}
                  onChange={(e) => {
                    const restaurant = restaurants.find(r => r.id === e.target.value);
                    setSelectedRestaurant(restaurant);
                  }}
                  className="block w-full max-w-xs px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                >
                  {restaurants.map((restaurant) => (
                    <option key={restaurant.id} value={restaurant.id}>
                      {restaurant.name}
                    </option>
                  ))}
                </select>
              </div>
            )}

            {/* Recent Orders and Popular Items */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
              <div className="bg-white rounded-lg shadow-sm p-6">
                <h2 className="text-xl font-semibold text-gray-900 mb-4">Recent Orders</h2>
                <div className="space-y-4">
                  {orders.slice(0, 5).map((order) => (
                    <div key={order.id} className={`border-l-4 pl-4 py-2 ${getStatusColor(order.status)}`}>
                      <p className="font-medium">Order #{order.id}</p>
                      <p className="text-sm text-gray-600">
                        {order.items.map(item => `${item.quantity}x ${item.name}`).join(', ')} - ${order.totalAmount.toFixed(2)}
                      </p>
                      <p className="text-sm">{order.status.replace('_', ' ')}</p>
                    </div>
                  ))}
                </div>
                
                <button
                  onClick={() => setActiveTab('orders')}
                  className="mt-4 w-full border border-gray-300 text-gray-700 py-2 px-4 rounded-md hover:bg-gray-50"
                >
                  View All Orders
                </button>
              </div>

              <div className="bg-white rounded-lg shadow-sm p-6">
                <h2 className="text-xl font-semibold text-gray-900 mb-4">Popular Items</h2>
                <div className="space-y-4">
                  {dashboardStats?.popularItems?.map((item, index) => (
                    <div key={index} className="flex justify-between items-center">
                      <div>
                        <p className="font-medium">{item.name}</p>
                        <p className="text-sm text-gray-600">{item.orders} orders</p>
                      </div>
                      <p className="font-semibold text-green-600">${item.revenue.toFixed(2)}</p>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>
        )}

        {activeTab === 'restaurants' && (
          <div>
            <div className="flex justify-between items-center mb-6">
              <h2 className="text-2xl font-bold text-gray-900">Your Restaurants</h2>
              <button
                onClick={() => setShowRestaurantForm(true)}
                className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700"
              >
                Add New Restaurant
              </button>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {restaurants.map((restaurant) => (
                <div key={restaurant.id} className="bg-white rounded-lg shadow-sm p-6">
                  <h3 className="font-semibold text-lg mb-2">{restaurant.name}</h3>
                  <p className="text-sm text-gray-600 mb-2">{restaurant.cuisineType} Cuisine</p>
                  {restaurant.rating && (
                    <p className="text-sm text-gray-600 mb-2">Rating: {restaurant.rating} ⭐</p>
                  )}
                  <p className={`text-sm mb-4 ${restaurant.verified ? 'text-green-600' : 'text-orange-600'}`}>
                    {restaurant.verified ? '✅ Verified' : '⏳ Pending Verification'}
                  </p>
                  
                  <div className="space-y-2">
                    <button
                      onClick={() => {
                        setSelectedRestaurant(restaurant);
                        setActiveTab('menu');
                      }}
                      className="w-full text-left text-blue-600 text-sm hover:underline"
                    >
                      Manage Menu
                    </button>
                    <button
                      onClick={() => {
                        setSelectedRestaurant(restaurant);
                        setActiveTab('orders');
                      }}
                      className="w-full text-left text-blue-600 text-sm hover:underline"
                    >
                      View Orders
                    </button>
                    <button
                      onClick={() => {
                        setEditingRestaurant(restaurant);
                        setRestaurantForm({
                          name: restaurant.name,
                          cuisineType: restaurant.cuisineType,
                          description: restaurant.description || '',
                          phone: restaurant.phone || '',
                          email: restaurant.email || '',
                          address: restaurant.address || { street: '', city: '', state: '', zipCode: '' },
                          openingHours: restaurant.openingHours || '',
                          deliveryFee: restaurant.deliveryFee ? restaurant.deliveryFee.toString() : '',
                          minimumOrder: restaurant.minimumOrder ? restaurant.minimumOrder.toString() : '',
                          estimatedDeliveryTime: restaurant.estimatedDeliveryTime || ''
                        });
                        setShowRestaurantForm(true);
                      }}
                      className="w-full text-left text-blue-600 text-sm hover:underline"
                    >
                      Edit Details
                    </button>
                  </div>
                </div>
              ))}
            </div>

            {/* Restaurant Form Modal */}
            {showRestaurantForm && (
              <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
                <div className="bg-white rounded-lg max-w-2xl w-full max-h-[90vh] overflow-y-auto">
                  <div className="p-6">
                    <h3 className="text-lg font-semibold mb-4">
                      {editingRestaurant ? 'Edit Restaurant' : 'Add New Restaurant'}
                    </h3>
                    
                    <form onSubmit={handleCreateRestaurant} className="space-y-4">
                      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div>
                          <label className="block text-sm font-medium text-gray-700 mb-1">
                            Restaurant Name *
                          </label>
                          <input
                            type="text"
                            required
                            value={restaurantForm.name}
                            onChange={(e) => setRestaurantForm({...restaurantForm, name: e.target.value})}
                            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                          />
                        </div>
                        
                        <div>
                          <label className="block text-sm font-medium text-gray-700 mb-1">
                            Cuisine Type *
                          </label>
                          <input
                            type="text"
                            required
                            value={restaurantForm.cuisineType}
                            onChange={(e) => setRestaurantForm({...restaurantForm, cuisineType: e.target.value})}
                            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                          />
                        </div>
                      </div>

                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                          Description
                        </label>
                        <textarea
                          value={restaurantForm.description}
                          onChange={(e) => setRestaurantForm({...restaurantForm, description: e.target.value})}
                          rows={3}
                          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                        />
                      </div>

                      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div>
                          <label className="block text-sm font-medium text-gray-700 mb-1">
                            Phone
                          </label>
                          <input
                            type="tel"
                            value={restaurantForm.phone}
                            onChange={(e) => setRestaurantForm({...restaurantForm, phone: e.target.value})}
                            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                          />
                        </div>
                        
                        <div>
                          <label className="block text-sm font-medium text-gray-700 mb-1">
                            Email
                          </label>
                          <input
                            type="email"
                            value={restaurantForm.email}
                            onChange={(e) => setRestaurantForm({...restaurantForm, email: e.target.value})}
                            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                          />
                        </div>
                      </div>

                      {/* Address Fields */}
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">Address</label>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                          <div className="md:col-span-2">
                            <input
                              type="text"
                              placeholder="Street Address"
                              value={restaurantForm.address.street}
                              onChange={(e) => setRestaurantForm({
                                ...restaurantForm,
                                address: {...restaurantForm.address, street: e.target.value}
                              })}
                              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                            />
                          </div>
                          <div>
                            <input
                              type="text"
                              placeholder="City"
                              value={restaurantForm.address.city}
                              onChange={(e) => setRestaurantForm({
                                ...restaurantForm,
                                address: {...restaurantForm.address, city: e.target.value}
                              })}
                              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                            />
                          </div>
                          <div>
                            <input
                              type="text"
                              placeholder="State"
                              value={restaurantForm.address.state}
                              onChange={(e) => setRestaurantForm({
                                ...restaurantForm,
                                address: {...restaurantForm.address, state: e.target.value}
                              })}
                              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                            />
                          </div>
                        </div>
                      </div>

                      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                        <div>
                          <label className="block text-sm font-medium text-gray-700 mb-1">
                            Delivery Fee ($)
                          </label>
                          <input
                            type="number"
                            step="0.01"
                            value={restaurantForm.deliveryFee}
                            onChange={(e) => setRestaurantForm({...restaurantForm, deliveryFee: e.target.value})}
                            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                          />
                        </div>
                        
                        <div>
                          <label className="block text-sm font-medium text-gray-700 mb-1">
                            Minimum Order ($)
                          </label>
                          <input
                            type="number"
                            step="0.01"
                            value={restaurantForm.minimumOrder}
                            onChange={(e) => setRestaurantForm({...restaurantForm, minimumOrder: e.target.value})}
                            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                          />
                        </div>
                        
                        <div>
                          <label className="block text-sm font-medium text-gray-700 mb-1">
                            Delivery Time
                          </label>
                          <input
                            type="text"
                            placeholder="e.g., 30-45 minutes"
                            value={restaurantForm.estimatedDeliveryTime}
                            onChange={(e) => setRestaurantForm({...restaurantForm, estimatedDeliveryTime: e.target.value})}
                            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                          />
                        </div>
                      </div>

                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                          Opening Hours
                        </label>
                        <input
                          type="text"
                          placeholder="e.g., 9:00 AM - 10:00 PM"
                          value={restaurantForm.openingHours}
                          onChange={(e) => setRestaurantForm({...restaurantForm, openingHours: e.target.value})}
                          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                        />
                      </div>

                      <div className="flex space-x-4 pt-4">
                        <button
                          type="submit"
                          className="flex-1 bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700"
                        >
                          {editingRestaurant ? 'Update Restaurant' : 'Create Restaurant'}
                        </button>
                        <button
                          type="button"
                          onClick={() => {
                            setShowRestaurantForm(false);
                            setEditingRestaurant(null);
                            setRestaurantForm({
                              name: '', cuisineType: '', description: '', phone: '', email: '',
                              address: { street: '', city: '', state: '', zipCode: '' },
                              openingHours: '', deliveryFee: '', minimumOrder: '', estimatedDeliveryTime: ''
                            });
                          }}
                          className="flex-1 border border-gray-300 text-gray-700 py-2 px-4 rounded-md hover:bg-gray-50"
                        >
                          Cancel
                        </button>
                      </div>
                    </form>
                  </div>
                </div>
              </div>
            )}
          </div>
        )}

        {activeTab === 'menu' && selectedRestaurant && (
          <div>
            <div className="flex justify-between items-center mb-6">
              <h2 className="text-2xl font-bold text-gray-900">
                Menu Management - {selectedRestaurant.name}
              </h2>
              <button
                onClick={() => setShowMenuForm(true)}
                className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700"
              >
                Add Menu Item
              </button>
            </div>

            <div className="bg-white rounded-lg shadow-sm">
              <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-gray-200">
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Item
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Category
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Price
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Status
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Actions
                      </th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-gray-200">
                    {menuItems.map((item) => (
                      <tr key={item.id}>
                        <td className="px-6 py-4 whitespace-nowrap">
                          <div>
                            <div className="text-sm font-medium text-gray-900">{item.name}</div>
                            <div className="text-sm text-gray-500">{item.description}</div>
                          </div>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                          {item.category}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                          ${item.price.toFixed(2)}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${
                            item.available 
                              ? 'bg-green-100 text-green-800' 
                              : 'bg-red-100 text-red-800'
                          }`}>
                            {item.available ? 'Available' : 'Unavailable'}
                          </span>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                          <button
                            onClick={() => handleEditMenuItem(item)}
                            className="text-blue-600 hover:text-blue-900 mr-4"
                          >
                            Edit
                          </button>
                          <button
                            onClick={() => handleDeleteMenuItem(item.id)}
                            className="text-red-600 hover:text-red-900"
                          >
                            Delete
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>

            {/* Menu Item Form Modal */}
            {showMenuForm && (
              <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
                <div className="bg-white rounded-lg max-w-2xl w-full max-h-[90vh] overflow-y-auto">
                  <div className="p-6">
                    <h3 className="text-lg font-semibold mb-4">
                      {editingMenuItem ? 'Edit Menu Item' : 'Add Menu Item'}
                    </h3>
                    
                    <form onSubmit={handleCreateMenuItem} className="space-y-4">
                      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div>
                          <label className="block text-sm font-medium text-gray-700 mb-1">
                            Item Name *
                          </label>
                          <input
                            type="text"
                            required
                            value={menuItemForm.name}
                            onChange={(e) => setMenuItemForm({...menuItemForm, name: e.target.value})}
                            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                          />
                        </div>
                        
                        <div>
                          <label className="block text-sm font-medium text-gray-700 mb-1">
                            Category *
                          </label>
                          <input
                            type="text"
                            required
                            value={menuItemForm.category}
                            onChange={(e) => setMenuItemForm({...menuItemForm, category: e.target.value})}
                            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                          />
                        </div>
                      </div>

                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                          Description
                        </label>
                        <textarea
                          value={menuItemForm.description}
                          onChange={(e) => setMenuItemForm({...menuItemForm, description: e.target.value})}
                          rows={3}
                          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                        />
                      </div>

                      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div>
                          <label className="block text-sm font-medium text-gray-700 mb-1">
                            Price ($) *
                          </label>
                          <input
                            type="number"
                            step="0.01"
                            required
                            value={menuItemForm.price}
                            onChange={(e) => setMenuItemForm({...menuItemForm, price: e.target.value})}
                            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                          />
                        </div>
                        
                        <div>
                          <label className="block text-sm font-medium text-gray-700 mb-1">
                            Preparation Time (minutes)
                          </label>
                          <input
                            type="number"
                            value={menuItemForm.preparationTime}
                            onChange={(e) => setMenuItemForm({...menuItemForm, preparationTime: e.target.value})}
                            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                          />
                        </div>
                      </div>

                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                          Ingredients (comma-separated)
                        </label>
                        <input
                          type="text"
                          value={menuItemForm.ingredients}
                          onChange={(e) => setMenuItemForm({...menuItemForm, ingredients: e.target.value})}
                          placeholder="e.g., Tomato sauce, Mozzarella, Basil"
                          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                        />
                      </div>

                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                          Allergens (comma-separated)
                        </label>
                        <input
                          type="text"
                          value={menuItemForm.allergens}
                          onChange={(e) => setMenuItemForm({...menuItemForm, allergens: e.target.value})}
                          placeholder="e.g., Dairy, Gluten, Nuts"
                          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                        />
                      </div>

                      <div>
                        <label className="flex items-center">
                          <input
                            type="checkbox"
                            checked={menuItemForm.available}
                            onChange={(e) => setMenuItemForm({...menuItemForm, available: e.target.checked})}
                            className="rounded border-gray-300 text-blue-600 shadow-sm focus:border-blue-500 focus:ring-blue-500"
                          />
                          <span className="ml-2 text-sm text-gray-700">Available for ordering</span>
                        </label>
                      </div>

                      <div className="flex space-x-4 pt-4">
                        <button
                          type="submit"
                          className="flex-1 bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700"
                        >
                          {editingMenuItem ? 'Update Item' : 'Add Item'}
                        </button>
                        <button
                          type="button"
                          onClick={() => {
                            setShowMenuForm(false);
                            setEditingMenuItem(null);
                            setMenuItemForm({
                              name: '', description: '', price: '', category: '', available: true,
                              ingredients: '', allergens: '', preparationTime: ''
                            });
                          }}
                          className="flex-1 border border-gray-300 text-gray-700 py-2 px-4 rounded-md hover:bg-gray-50"
                        >
                          Cancel
                        </button>
                      </div>
                    </form>
                  </div>
                </div>
              </div>
            )}
          </div>
        )}

        {activeTab === 'orders' && selectedRestaurant && (
          <div>
            <h2 className="text-2xl font-bold text-gray-900 mb-6">
              Orders - {selectedRestaurant.name}
            </h2>

            <div className="bg-white rounded-lg shadow-sm">
              <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-gray-200">
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Order ID
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Customer
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Items
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Total
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Status
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Actions
                      </th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-gray-200">
                    {orders.map((order) => (
                      <tr key={order.id}>
                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                          #{order.id}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          <div>
                            <div className="text-sm font-medium text-gray-900">{order.customerName}</div>
                            <div className="text-sm text-gray-500">{order.customerPhone}</div>
                          </div>
                        </td>
                        <td className="px-6 py-4">
                          <div className="text-sm text-gray-900">
                            {order.items.map(item => `${item.quantity}x ${item.name}`).join(', ')}
                          </div>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                          ${order.totalAmount.toFixed(2)}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${
                            order.status === 'PENDING' ? 'bg-yellow-100 text-yellow-800' :
                            order.status === 'CONFIRMED' ? 'bg-blue-100 text-blue-800' :
                            order.status === 'PREPARING' ? 'bg-orange-100 text-orange-800' :
                            order.status === 'READY' ? 'bg-purple-100 text-purple-800' :
                            order.status === 'OUT_FOR_DELIVERY' ? 'bg-indigo-100 text-indigo-800' :
                            order.status === 'DELIVERED' ? 'bg-green-100 text-green-800' :
                            'bg-red-100 text-red-800'
                          }`}>
                            {order.status.replace('_', ' ')}
                          </span>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                          <div className="flex space-x-2">
                            {order.status === 'PENDING' && (
                              <>
                                <button
                                  onClick={() => handleUpdateOrderStatus(order.id, 'CONFIRMED')}
                                  className="text-green-600 hover:text-green-900"
                                >
                                  Accept
                                </button>
                                <button
                                  onClick={() => handleUpdateOrderStatus(order.id, 'CANCELLED')}
                                  className="text-red-600 hover:text-red-900"
                                >
                                  Reject
                                </button>
                              </>
                            )}
                            {order.status === 'CONFIRMED' && (
                              <button
                                onClick={() => handleUpdateOrderStatus(order.id, 'PREPARING')}
                                className="text-blue-600 hover:text-blue-900"
                              >
                                Start Preparing
                              </button>
                            )}
                            {order.status === 'PREPARING' && (
                              <button
                                onClick={() => handleUpdateOrderStatus(order.id, 'READY')}
                                className="text-purple-600 hover:text-purple-900"
                              >
                                Mark Ready
                              </button>
                            )}
                            {order.status === 'READY' && (
                              <button
                                onClick={() => handleUpdateOrderStatus(order.id, 'OUT_FOR_DELIVERY')}
                                className="text-indigo-600 hover:text-indigo-900"
                              >
                                Out for Delivery
                              </button>
                            )}
                            {order.status === 'OUT_FOR_DELIVERY' && (
                              <button
                                onClick={() => handleUpdateOrderStatus(order.id, 'DELIVERED')}
                                className="text-green-600 hover:text-green-900"
                              >
                                Mark Delivered
                              </button>
                            )}
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        )}

        {activeTab === 'analytics' && selectedRestaurant && (
          <div>
            <h2 className="text-2xl font-bold text-gray-900 mb-6">
              Analytics - {selectedRestaurant.name}
            </h2>

            {/* Analytics Stats */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
              <div className="bg-white rounded-lg shadow-sm p-6">
                <div className="flex items-center">
                  <div className="p-2 bg-blue-100 rounded-md">
                    <span className="text-2xl">📊</span>
                  </div>
                  <div className="ml-4">
                    <p className="text-sm font-medium text-gray-600">Total Orders</p>
                    <p className="text-2xl font-bold text-gray-900">{dashboardStats?.totalOrders || 0}</p>
                  </div>
                </div>
              </div>

              <div className="bg-white rounded-lg shadow-sm p-6">
                <div className="flex items-center">
                  <div className="p-2 bg-green-100 rounded-md">
                    <span className="text-2xl">💰</span>
                  </div>
                  <div className="ml-4">
                    <p className="text-sm font-medium text-gray-600">Total Revenue</p>
                    <p className="text-2xl font-bold text-gray-900">${dashboardStats?.totalRevenue?.toFixed(2) || '0.00'}</p>
                  </div>
                </div>
              </div>

              <div className="bg-white rounded-lg shadow-sm p-6">
                <div className="flex items-center">
                  <div className="p-2 bg-yellow-100 rounded-md">
                    <span className="text-2xl">📈</span>
                  </div>
                  <div className="ml-4">
                    <p className="text-sm font-medium text-gray-600">Average Order</p>
                    <p className="text-2xl font-bold text-gray-900">${dashboardStats?.averageOrderValue?.toFixed(2) || '0.00'}</p>
                  </div>
                </div>
              </div>

              <div className="bg-white rounded-lg shadow-sm p-6">
                <div className="flex items-center">
                  <div className="p-2 bg-purple-100 rounded-md">
                    <span className="text-2xl">👥</span>
                  </div>
                  <div className="ml-4">
                    <p className="text-sm font-medium text-gray-600">Customers</p>
                    <p className="text-2xl font-bold text-gray-900">{dashboardStats?.customerCount || 0}</p>
                  </div>
                </div>
              </div>
            </div>

            {/* Popular Items */}
            <div className="bg-white rounded-lg shadow-sm p-6 mb-8">
              <h3 className="text-lg font-semibold text-gray-900 mb-4">Popular Menu Items</h3>
              <div className="space-y-4">
                {dashboardStats?.popularItems?.map((item, index) => (
                  <div key={index} className="flex justify-between items-center">
                    <div>
                      <p className="font-medium">{item.name}</p>
                      <p className="text-sm text-gray-600">{item.orders} orders</p>
                    </div>
                    <p className="font-semibold text-green-600">${item.revenue.toFixed(2)}</p>
                  </div>
                ))}
              </div>
            </div>

            {/* Recent Activity */}
            <div className="bg-white rounded-lg shadow-sm p-6">
              <h3 className="text-lg font-semibold text-gray-900 mb-4">Recent Activity</h3>
              <div className="space-y-4">
                <div className="border-l-4 border-blue-400 pl-4 py-2">
                  <p className="font-medium">New order received</p>
                  <p className="text-sm text-gray-600">Order #1234 - $36.00</p>
                  <p className="text-sm text-gray-500">2 minutes ago</p>
                </div>
                
                <div className="border-l-4 border-green-400 pl-4 py-2">
                  <p className="font-medium">Order completed</p>
                  <p className="text-sm text-gray-600">Order #1233 - $24.50</p>
                  <p className="text-sm text-gray-500">15 minutes ago</p>
                </div>
                
                <div className="border-l-4 border-yellow-400 pl-4 py-2">
                  <p className="font-medium">Menu item updated</p>
                  <p className="text-sm text-gray-600">Margherita Pizza price updated</p>
                  <p className="text-sm text-gray-500">1 hour ago</p>
                </div>
              </div>
            </div>
          </div>
        )}

        {!selectedRestaurant && activeTab !== 'overview' && activeTab !== 'restaurants' && (
          <div className="text-center py-8">
            <p className="text-gray-500">Please select a restaurant to view {activeTab} data.</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default RestaurantOwnerDashboard;
