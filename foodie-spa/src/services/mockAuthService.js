// Mock Authentication Service for Testing
// This provides login functionality with mock users

import { mockUsers } from './mockDataService';

export const mockLogin = async (email, password) => {
  // Simple mock authentication - in real app this would be validated against backend
  const user = mockUsers.find(u => u.email === email);
  
  if (!user) {
    throw new Error('Invalid credentials');
  }
  
  // Mock successful login - generate fake JWT token
  const mockToken = btoa(JSON.stringify({
    userId: user.id,
    email: user.email,
    role: user.role,
    exp: Date.now() + (24 * 60 * 60 * 1000) // 24 hours
  }));
  
  // Create user object in the format expected by AuthContext
  const authUser = {
    id: user.id,
    username: user.username || user.firstName + user.lastName,
    email: user.email,
    firstName: user.firstName,
    lastName: user.lastName,
    role: user.role, // Single role for mock
    roles: [user.role], // Also provide as array for compatibility
    isActive: user.isActive
  };
  
  // Store in localStorage using the same keys as the real authService
  localStorage.setItem('authToken', mockToken);
  localStorage.setItem('user', JSON.stringify(authUser));
  
  return {
    success: true,
    data: {
      accessToken: mockToken,
      user: authUser
    }
  };
};

export const mockLogout = () => {
  localStorage.removeItem('authToken');
  localStorage.removeItem('user');
};

export const getMockCurrentUser = () => {
  const userStr = localStorage.getItem('user');
  return userStr ? JSON.parse(userStr) : null;
};

export const mockTestUsers = {
  customer: {
    email: 'john.doe@example.com',
    password: 'password123',
    role: 'ROLE_CUSTOMER'
  },
  restaurantOwner: {
    email: 'mario@pizzapalace.com', 
    password: 'password123',
    role: 'ROLE_RESTAURANT_OWNER'
  },
  admin: {
    email: 'admin@foodie.com',
    password: 'password123', 
    role: 'ROLE_ADMIN'
  }
};

// Default export for compatibility
const mockAuthService = {
  mockLogin,
  mockLogout,
  getMockCurrentUser,
  mockTestUsers
};

export default mockAuthService;
