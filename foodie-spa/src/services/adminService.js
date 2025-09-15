import { 
  mockUsers, mockDashboardStats 
} from './mockDataService';

const API_BASE_URLS = {
  USER_SERVICE: 'http://localhost:8081/api/v1',
  RESTAURANT_SERVICE: 'http://localhost:8082/api/v1',
  ORDER_SERVICE: 'http://localhost:8083/api/v1',
  PAYMENT_SERVICE: 'http://localhost:8084/api/v1',
  NOTIFICATION_SERVICE: 'http://localhost:8085/api/v1'
};

// Helper function to get auth headers
const getAuthHeaders = () => {
  const token = localStorage.getItem('token');
  return {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  };
};

// Admin Dashboard Stats
export const getDashboardStats = async () => {
  try {
    const response = await fetch(`${API_BASE_URLS.USER_SERVICE}/admin/dashboard/stats`, {
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
  
  // Return mock data for development
  return mockDashboardStats.admin;
};

// User Management
export const getAllUsers = async (page = 0, size = 20, sort = 'id', direction = 'asc') => {
  try {
    const response = await fetch(
      `${API_BASE_URLS.USER_SERVICE}/admin/users?page=${page}&size=${size}&sort=${sort}&direction=${direction}`,
      {
        method: 'GET',
        headers: getAuthHeaders()
      }
    );
    
    if (response.ok) {
      const result = await response.json();
      return result.data || result;
    }
  } catch (error) {
    console.log('Backend not available, using mock data');
  }
  
  // Return mock data
  return {
    content: mockUsers,
    totalElements: mockUsers.length,
    totalPages: 1,
    number: page,
    size: size
  };
};

export const createAdmin = async (adminData) => {
  try {
    const response = await fetch(`${API_BASE_URLS.USER_SERVICE}/admin/create-admin`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify(adminData)
    });
    
    if (!response.ok) {
      throw new Error('Failed to create admin');
    }
    
    const result = await response.json();
    return result.data || result;
  } catch (error) {
    console.error('Error creating admin:', error);
    throw error;
  }
};

export const approveRestaurantOwner = async (userId) => {
  try {
    const response = await fetch(`${API_BASE_URLS.USER_SERVICE}/admin/approve-restaurant-owner/${userId}`, {
      method: 'POST',
      headers: getAuthHeaders()
    });
    
    if (!response.ok) {
      throw new Error('Failed to approve restaurant owner');
    }
    
    const result = await response.json();
    return result.data || result;
  } catch (error) {
    console.error('Error approving restaurant owner:', error);
    throw error;
  }
};

export const activateUser = async (userId) => {
  try {
    const response = await fetch(`${API_BASE_URLS.USER_SERVICE}/admin/activate-user/${userId}`, {
      method: 'POST',
      headers: getAuthHeaders()
    });
    
    if (!response.ok) {
      throw new Error('Failed to activate user');
    }
    
    const result = await response.json();
    return result.data || result;
  } catch (error) {
    console.error('Error activating user:', error);
    throw error;
  }
};

export const deactivateUser = async (userId) => {
  try {
    const response = await fetch(`${API_BASE_URLS.USER_SERVICE}/admin/deactivate-user/${userId}`, {
      method: 'POST',
      headers: getAuthHeaders()
    });
    
    if (!response.ok) {
      throw new Error('Failed to deactivate user');
    }
    
    const result = await response.json();
    return result.data || result;
  } catch (error) {
    console.error('Error deactivating user:', error);
    throw error;
  }
};

export const deleteUser = async (userId) => {
  try {
    const response = await fetch(`${API_BASE_URLS.USER_SERVICE}/admin/users/${userId}`, {
      method: 'DELETE',
      headers: getAuthHeaders()
    });
    
    if (!response.ok) {
      throw new Error('Failed to delete user');
    }
    
    return { success: true };
  } catch (error) {
    console.error('Error deleting user:', error);
    throw error;
  }
};

// Restaurant Management
export const getAllRestaurants = async () => {
  try {
    const response = await fetch(`${API_BASE_URLS.RESTAURANT_SERVICE}/restaurants`, {
      method: 'GET',
      headers: getAuthHeaders()
    });
    
    if (!response.ok) {
      throw new Error('Failed to fetch restaurants');
    }
    
    const result = await response.json();
    return result.data || result;
  } catch (error) {
    console.error('Error fetching restaurants:', error);
    // Return mock data for development
    return [
      {
        id: '1',
        name: 'Pizza Palace',
        cuisineType: 'Italian',
        rating: 4.5,
        verified: true,
        address: { city: 'New York' },
        imageUrl: null,
        createdAt: '2024-01-10T08:00:00Z'
      },
      {
        id: '2',
        name: 'Burger Haven',
        cuisineType: 'American',
        rating: 4.2,
        verified: false,
        address: { city: 'Los Angeles' },
        imageUrl: null,
        createdAt: '2024-01-25T12:30:00Z'
      }
    ];
  }
};

export const verifyRestaurant = async (restaurantId) => {
  try {
    const response = await fetch(`${API_BASE_URLS.RESTAURANT_SERVICE}/admin/restaurants/${restaurantId}/verify`, {
      method: 'PUT',
      headers: getAuthHeaders()
    });
    
    if (!response.ok) {
      throw new Error('Failed to verify restaurant');
    }
    
    const result = await response.json();
    return result.data || result;
  } catch (error) {
    console.error('Error verifying restaurant:', error);
    throw error;
  }
};

export const deleteRestaurant = async (restaurantId) => {
  try {
    const response = await fetch(`${API_BASE_URLS.RESTAURANT_SERVICE}/admin/restaurants/${restaurantId}`, {
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

// Order Management
export const getAllOrders = async (page = 0, size = 20) => {
  try {
    const response = await fetch(`${API_BASE_URLS.ORDER_SERVICE}/orders?page=${page}&size=${size}`, {
      method: 'GET',
      headers: getAuthHeaders()
    });
    
    if (!response.ok) {
      throw new Error('Failed to fetch orders');
    }
    
    const result = await response.json();
    return result.data || result;
  } catch (error) {
    console.error('Error fetching orders:', error);
    // Return mock data for development
    return [
      {
        id: 'order_123',
        userId: 'user_1',
        restaurantId: 'rest_1',
        customerName: 'John Doe',
        restaurantName: 'Pizza Palace',
        totalAmount: 25.99,
        status: 'DELIVERED',
        createdAt: '2024-01-28T18:30:00Z'
      },
      {
        id: 'order_124',
        userId: 'user_2',
        restaurantId: 'rest_2',
        customerName: 'Jane Smith',
        restaurantName: 'Burger Haven',
        totalAmount: 18.50,
        status: 'PENDING',
        createdAt: '2024-01-28T19:15:00Z'
      }
    ];
  }
};

export const updateOrderStatus = async (orderId, status, notes = '') => {
  try {
    const response = await fetch(`${API_BASE_URLS.ORDER_SERVICE}/orders/${orderId}/status`, {
      method: 'PUT',
      headers: getAuthHeaders(),
      body: JSON.stringify({ status, notes })
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

// Payment Management
export const getPaymentLogs = async () => {
  try {
    // Note: Payment service doesn't have admin endpoint for all payments in the provided code
    // This would need to be implemented in the backend
    console.log('Payment logs endpoint not available in current backend implementation');
    
    // Return mock data for development
    return [
      {
        id: 'pay_123',
        orderId: 'order_123',
        amount: 25.99,
        paymentMethod: 'Credit Card',
        status: 'COMPLETED',
        createdAt: '2024-01-28T18:35:00Z'
      },
      {
        id: 'pay_124',
        orderId: 'order_124',
        amount: 18.50,
        paymentMethod: 'PayPal',
        status: 'PENDING',
        createdAt: '2024-01-28T19:20:00Z'
      }
    ];
  } catch (error) {
    console.error('Error fetching payment logs:', error);
    return [];
  }
};

// Notification Management
export const getNotificationLogs = async (page = 0, size = 20, recipient = null, type = null) => {
  try {
    let url = `${API_BASE_URLS.NOTIFICATION_SERVICE}/notifications/logs?page=${page}&size=${size}`;
    
    if (recipient) {
      url += `&recipient=${recipient}`;
    }
    
    if (type) {
      url += `&type=${type}`;
    }
    
    const response = await fetch(url, {
      method: 'GET',
      headers: getAuthHeaders()
    });
    
    if (!response.ok) {
      throw new Error('Failed to fetch notification logs');
    }
    
    const result = await response.json();
    return result.data || result;
  } catch (error) {
    console.error('Error fetching notification logs:', error);
    // Return mock data for development
    return {
      content: [
        {
          id: '1',
          type: 'EMAIL',
          recipient: 'john.doe@email.com',
          subject: 'Welcome to Foodie App',
          status: 'DELIVERED',
          sentAt: '2024-01-28T10:00:00Z'
        },
        {
          id: '2',
          type: 'SMS',
          recipient: '+1234567890',
          message: 'Your order is ready for pickup',
          status: 'SENT',
          sentAt: '2024-01-28T18:45:00Z'
        },
        {
          id: '3',
          type: 'EMAIL',
          recipient: 'jane.smith@restaurant.com',
          subject: 'Restaurant Registration Approved',
          status: 'DELIVERED',
          sentAt: '2024-01-25T14:30:00Z'
        }
      ],
      totalElements: 3,
      totalPages: 1,
      number: 0
    };
  }
};

// Analytics and Reports
export const getSystemAnalytics = async (timeRange = '7d') => {
  try {
    // This endpoint would need to be implemented in the backend
    // For now, return mock analytics data
    return {
      userGrowth: {
        labels: ['Jan 22', 'Jan 23', 'Jan 24', 'Jan 25', 'Jan 26', 'Jan 27', 'Jan 28'],
        data: [1200, 1215, 1230, 1235, 1240, 1245, 1247]
      },
      orderVolume: {
        labels: ['Jan 22', 'Jan 23', 'Jan 24', 'Jan 25', 'Jan 26', 'Jan 27', 'Jan 28'],
        data: [45, 52, 48, 61, 55, 67, 58]
      },
      revenue: {
        labels: ['Jan 22', 'Jan 23', 'Jan 24', 'Jan 25', 'Jan 26', 'Jan 27', 'Jan 28'],
        data: [2340, 2650, 2280, 3120, 2890, 3450, 2980]
      },
      topRestaurants: [
        { name: 'Pizza Palace', orders: 156, revenue: 3240 },
        { name: 'Burger Haven', orders: 134, revenue: 2890 },
        { name: 'Sushi Central', orders: 98, revenue: 4560 }
      ]
    };
  } catch (error) {
    console.error('Error fetching system analytics:', error);
    throw error;
  }
};

// Platform Settings
export const updatePlatformSettings = async (settings) => {
  try {
    // This endpoint would need to be implemented in the backend
    console.log('Platform settings update:', settings);
    return { success: true, message: 'Settings updated successfully' };
  } catch (error) {
    console.error('Error updating platform settings:', error);
    throw error;
  }
};

// Export Data
export const exportUserData = async (format = 'csv') => {
  try {
    // This endpoint would need to be implemented in the backend
    console.log(`Exporting user data in ${format} format`);
    return { downloadUrl: '/api/exports/users.csv' };
  } catch (error) {
    console.error('Error exporting user data:', error);
    throw error;
  }
};

export const exportRestaurantData = async (format = 'csv') => {
  try {
    // This endpoint would need to be implemented in the backend
    console.log(`Exporting restaurant data in ${format} format`);
    return { downloadUrl: '/api/exports/restaurants.csv' };
  } catch (error) {
    console.error('Error exporting restaurant data:', error);
    throw error;
  }
};

export const exportOrderData = async (format = 'csv') => {
  try {
    // This endpoint would need to be implemented in the backend
    console.log(`Exporting order data in ${format} format`);
    return { downloadUrl: '/api/exports/orders.csv' };
  } catch (error) {
    console.error('Error exporting order data:', error);
    throw error;
  }
};

// System Health
export const getSystemHealth = async () => {
  try {
    // This would typically check health endpoints for each service
    const services = [
      { name: 'User Service', status: 'healthy', url: API_BASE_URLS.USER_SERVICE },
      { name: 'Restaurant Service', status: 'healthy', url: API_BASE_URLS.RESTAURANT_SERVICE },
      { name: 'Order Service', status: 'healthy', url: API_BASE_URLS.ORDER_SERVICE },
      { name: 'Payment Service', status: 'healthy', url: API_BASE_URLS.PAYMENT_SERVICE },
      { name: 'Notification Service', status: 'healthy', url: API_BASE_URLS.NOTIFICATION_SERVICE }
    ];
    
    // In a real implementation, you would ping each service's health endpoint
    return {
      overall: 'healthy',
      services,
      uptime: '99.9%',
      lastChecked: new Date().toISOString()
    };
  } catch (error) {
    console.error('Error checking system health:', error);
    throw error;
  }
};
