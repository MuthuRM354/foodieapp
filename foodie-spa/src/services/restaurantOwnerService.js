import { 
  mockDashboardStats,
  getMockRestaurantsByOwner, getMockOrdersByRestaurant, getMockMenuByRestaurant 
} from './mockDataService';

const API_BASE_URLS = {
  USER_SERVICE: 'http://localhost:8081/api/v1',
  RESTAURANT_SERVICE: 'http://localhost:8082/api/v1',
  ORDER_SERVICE: 'http://localhost:8083/api/v1'
};

// Helper function to get auth headers
const getAuthHeaders = () => {
  const token = localStorage.getItem('authToken');
  return {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  };
};

// Restaurant Management
export const createRestaurant = async (restaurantData) => {
  try {
    const response = await fetch(`${API_BASE_URLS.RESTAURANT_SERVICE}/owner/restaurants`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify(restaurantData)
    });
    
    if (!response.ok) {
      throw new Error('Failed to create restaurant');
    }
    
    const result = await response.json();
    return result.data || result;
  } catch (error) {
    console.error('Error creating restaurant:', error);
    throw error;
  }
};

export const getOwnerRestaurants = async () => {
  try {
    const response = await fetch(`${API_BASE_URLS.RESTAURANT_SERVICE}/owner/restaurants`, {
      method: 'GET',
      headers: getAuthHeaders()
    });
    
    if (response.ok) {
      const result = await response.json();
      return result.data || result;
    }
  } catch (error) {
    console.log('Backend not available, using mock data');
  }
  
  // Return mock data - assuming current user is owner with ID '3'
  return getMockRestaurantsByOwner('3');
};

export const updateRestaurant = async (restaurantId, restaurantData) => {
  try {
    const response = await fetch(`${API_BASE_URLS.RESTAURANT_SERVICE}/owner/restaurants/${restaurantId}`, {
      method: 'PUT',
      headers: getAuthHeaders(),
      body: JSON.stringify(restaurantData)
    });
    
    if (!response.ok) {
      throw new Error('Failed to update restaurant');
    }
    
    const result = await response.json();
    return result.data || result;
  } catch (error) {
    console.error('Error updating restaurant:', error);
    throw error;
  }
};

export const deleteRestaurant = async (restaurantId) => {
  try {
    const response = await fetch(`${API_BASE_URLS.RESTAURANT_SERVICE}/owner/restaurants/${restaurantId}`, {
      method: 'DELETE',
      headers: getAuthHeaders()
    });
    
    if (!response.ok) {
      throw new Error('Failed to delete restaurant');
    }
    
    return { success: true };
  } catch (error) {
    console.error('Error deleting restaurant:', error);
    throw error;
  }
};

// Menu Management
export const getRestaurantMenu = async (restaurantId) => {
  try {
    const response = await fetch(`${API_BASE_URLS.RESTAURANT_SERVICE}/owner/restaurants/${restaurantId}/menu`, {
      method: 'GET',
      headers: getAuthHeaders()
    });
    
    if (response.ok) {
      const result = await response.json();
      return result.data || result;
    }
  } catch (error) {
    console.log('Backend not available, using mock data');
  }
  
  // Return mock data
  return getMockMenuByRestaurant(restaurantId);
};

export const addMenuItem = async (restaurantId, menuItem) => {
  try {
    const response = await fetch(`${API_BASE_URLS.RESTAURANT_SERVICE}/owner/restaurants/${restaurantId}/menu`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify(menuItem)
    });
    
    if (!response.ok) {
      throw new Error('Failed to add menu item');
    }
    
    const result = await response.json();
    return result.data || result;
  } catch (error) {
    console.error('Error adding menu item:', error);
    throw error;
  }
};

export const updateMenuItem = async (restaurantId, menuItemId, menuItem) => {
  try {
    const response = await fetch(`${API_BASE_URLS.RESTAURANT_SERVICE}/owner/restaurants/${restaurantId}/menu/${menuItemId}`, {
      method: 'PUT',
      headers: getAuthHeaders(),
      body: JSON.stringify(menuItem)
    });
    
    if (!response.ok) {
      throw new Error('Failed to update menu item');
    }
    
    const result = await response.json();
    return result.data || result;
  } catch (error) {
    console.error('Error updating menu item:', error);
    throw error;
  }
};

export const deleteMenuItem = async (restaurantId, menuItemId) => {
  try {
    const response = await fetch(`${API_BASE_URLS.RESTAURANT_SERVICE}/owner/restaurants/${restaurantId}/menu/${menuItemId}`, {
      method: 'DELETE',
      headers: getAuthHeaders()
    });
    
    if (!response.ok) {
      throw new Error('Failed to delete menu item');
    }
    
    return { success: true };
  } catch (error) {
    console.error('Error deleting menu item:', error);
    throw error;
  }
};

// Order Management
export const getRestaurantOrders = async (restaurantId) => {
  try {
    const response = await fetch(`${API_BASE_URLS.ORDER_SERVICE}/owner/restaurants/${restaurantId}/orders`, {
      method: 'GET',
      headers: getAuthHeaders()
    });
    
    if (response.ok) {
      const result = await response.json();
      return result.data || result;
    }
  } catch (error) {
    console.log('Backend not available, using mock data');
  }
  
  // Return mock data
  return getMockOrdersByRestaurant(restaurantId);
};

export const updateOrderStatus = async (orderId, status) => {
  try {
    const response = await fetch(`${API_BASE_URLS.ORDER_SERVICE}/owner/orders/${orderId}/status`, {
      method: 'PUT',
      headers: getAuthHeaders(),
      body: JSON.stringify({ status })
    });
    
    if (!response.ok) {
      throw new Error('Failed to update order status');
    }
    
    const result = await response.json();
    return result.data || result;
  } catch (error) {
    console.error('Error updating order status:', error);
    throw error;
  }
};

// Analytics
export const getRestaurantAnalytics = async (restaurantId) => {
  try {
    const response = await fetch(`${API_BASE_URLS.RESTAURANT_SERVICE}/owner/restaurants/${restaurantId}/analytics`, {
      method: 'GET',
      headers: getAuthHeaders()
    });
    
    if (response.ok) {
      const result = await response.json();
      return result.data || result;
    }
  } catch (error) {
    console.log('Backend not available, using mock data');
  }
  
  // Return mock analytics data
  return {
    totalOrders: 150,
    totalRevenue: 4500.75,
    averageOrderValue: 30.00,
    customerCount: 120,
    popularItems: [
      { name: 'Margherita Pizza', orderCount: 45, revenue: 675.00 },
      { name: 'Pepperoni Pizza', orderCount: 38, revenue: 665.00 },
      { name: 'Caesar Salad', orderCount: 25, revenue: 300.00 }
    ]
  };
};

// Restaurant Dashboard Stats
export const getRestaurantDashboardStats = async (restaurantId) => {
  try {
    const [orders, analytics] = await Promise.all([
      getRestaurantOrders(restaurantId),
      getRestaurantAnalytics(restaurantId)
    ]);
    
    const today = new Date().toDateString();
    const todayOrders = orders.filter(order => 
      new Date(order.createdAt).toDateString() === today
    );
    
    const pendingOrders = orders.filter(order => order.status === 'PENDING');
    
    return {
      todayOrders: todayOrders.length,
      todayRevenue: todayOrders.reduce((sum, order) => sum + order.totalAmount, 0),
      pendingOrders: pendingOrders.length,
      totalOrders: analytics.totalOrders,
      totalRevenue: analytics.totalRevenue,
      averageOrderValue: analytics.averageOrderValue,
      customerCount: analytics.customerCount,
      popularItems: analytics.popularItems
    };
  } catch (error) {
    console.log('Error fetching restaurant dashboard stats, using mock data');
    // Return mock data
    return mockDashboardStats.restaurantOwner;
  }
};

// Current user debug info
export const getCurrentUser = async () => {
  try {
    const response = await fetch(`${API_BASE_URLS.RESTAURANT_SERVICE}/owner/restaurants/debug/current-user`, {
      method: 'GET',
      headers: getAuthHeaders()
    });
    
    if (!response.ok) {
      throw new Error('Failed to get current user info');
    }
    
    const result = await response.json();
    return result;
  } catch (error) {
    console.error('Error getting current user info:', error);
    throw error;
  }
};
