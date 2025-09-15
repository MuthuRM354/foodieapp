// Mock Data Service for Testing Dashboards
// This provides realistic data for all dashboard components

export const mockUsers = [
  {
    id: '1',
    username: 'john_doe',
    email: 'john.doe@example.com',
    firstName: 'John',
    lastName: 'Doe',
    phone: '+1234567890',
    role: 'ROLE_CUSTOMER',
    isActive: true,
    createdAt: '2024-01-15T08:30:00Z',
    lastLogin: '2024-01-28T18:30:00Z',
    totalOrders: 15,
    totalSpent: 320.50,
    address: {
      street: '123 Main St',
      city: 'New York',
      state: 'NY',
      zipCode: '10001'
    }
  },
  {
    id: '2',
    username: 'jane_smith',
    email: 'jane.smith@example.com',
    firstName: 'Jane',
    lastName: 'Smith',
    phone: '+1987654321',
    role: 'ROLE_CUSTOMER',
    isActive: true,
    createdAt: '2024-01-20T10:15:00Z',
    lastLogin: '2024-01-28T16:45:00Z',
    totalOrders: 8,
    totalSpent: 165.75,
    address: {
      street: '456 Oak Ave',
      city: 'Los Angeles',
      state: 'CA',
      zipCode: '90210'
    }
  },
  {
    id: '3',
    username: 'mario_rossi',
    email: 'mario@pizzapalace.com',
    firstName: 'Mario',
    lastName: 'Rossi',
    phone: '+1555123456',
    role: 'ROLE_RESTAURANT_OWNER',
    isActive: true,
    createdAt: '2024-01-10T09:00:00Z',
    lastLogin: '2024-01-28T19:30:00Z',
    restaurantCount: 2,
    totalRevenue: 15420.80
  },
  {
    id: '4',
    username: 'admin_user',
    email: 'admin@foodie.com',
    firstName: 'Admin',
    lastName: 'User',
    phone: '+1555999888',
    role: 'ROLE_ADMIN',
    isActive: true,
    createdAt: '2024-01-01T00:00:00Z',
    lastLogin: '2024-01-28T20:00:00Z'
  },
  {
    id: '5',
    username: 'sarah_wilson',
    email: 'sarah@burgerjoint.com',
    firstName: 'Sarah',
    lastName: 'Wilson',
    phone: '+1555777666',
    role: 'ROLE_RESTAURANT_OWNER',
    isActive: false,
    createdAt: '2024-01-25T14:20:00Z',
    lastLogin: '2024-01-27T12:30:00Z',
    restaurantCount: 1,
    totalRevenue: 2340.50,
    pendingApproval: true
  }
];

export const mockRestaurants = [
  {
    id: '1',
    name: 'Pizza Palace',
    ownerId: '3',
    ownerName: 'Mario Rossi',
    ownerEmail: 'mario@pizzapalace.com',
    cuisineType: 'Italian',
    rating: 4.5,
    reviewCount: 142,
    verified: true,
    isActive: true,
    address: {
      street: '123 Little Italy St',
      city: 'New York',
      state: 'NY',
      zipCode: '10013'
    },
    phone: '+1555123456',
    email: 'info@pizzapalace.com',
    description: 'Authentic Italian pizza and pasta made with fresh ingredients',
    openingHours: '11:00 AM - 11:00 PM',
    deliveryFee: 2.99,
    minimumOrder: 15.00,
    estimatedDeliveryTime: '25-35 minutes',
    totalOrders: 1250,
    monthlyRevenue: 8920.50,
    imageUrl: null,
    createdAt: '2024-01-10T10:00:00Z',
    verificationDate: '2024-01-12T15:30:00Z'
  },
  {
    id: '2',
    name: 'Burger Joint',
    ownerId: '5',
    ownerName: 'Sarah Wilson',
    ownerEmail: 'sarah@burgerjoint.com',
    cuisineType: 'American',
    rating: 4.2,
    reviewCount: 89,
    verified: false,
    isActive: true,
    address: {
      street: '456 Main Ave',
      city: 'Chicago',
      state: 'IL',
      zipCode: '60601'
    },
    phone: '+1555777666',
    email: 'info@burgerjoint.com',
    description: 'Gourmet burgers with premium ingredients and craft sauces',
    openingHours: '12:00 PM - 10:00 PM',
    deliveryFee: 3.50,
    minimumOrder: 12.00,
    estimatedDeliveryTime: '20-30 minutes',
    totalOrders: 89,
    monthlyRevenue: 2340.50,
    imageUrl: null,
    createdAt: '2024-01-25T14:20:00Z',
    pendingVerification: true
  },
  {
    id: '3',
    name: 'Taco Fiesta',
    ownerId: '3',
    ownerName: 'Mario Rossi',
    ownerEmail: 'mario@pizzapalace.com',
    cuisineType: 'Mexican',
    rating: 4.7,
    reviewCount: 203,
    verified: true,
    isActive: true,
    address: {
      street: '789 Sunset Blvd',
      city: 'Los Angeles',
      state: 'CA',
      zipCode: '90028'
    },
    phone: '+1555123457',
    email: 'info@tacofiesta.com',
    description: 'Authentic Mexican street food with bold flavors',
    openingHours: '10:00 AM - 12:00 AM',
    deliveryFee: 2.50,
    minimumOrder: 10.00,
    estimatedDeliveryTime: '15-25 minutes',
    totalOrders: 890,
    monthlyRevenue: 6500.30,
    imageUrl: null,
    createdAt: '2024-01-15T11:30:00Z',
    verificationDate: '2024-01-16T09:15:00Z'
  }
];

export const mockMenuItems = [
  {
    id: '1',
    restaurantId: '1',
    name: 'Margherita Pizza',
    description: 'Classic pizza with fresh tomato sauce, mozzarella, and basil',
    price: 18.99,
    category: 'Pizza',
    available: true,
    imageUrl: null,
    ingredients: ['Tomato sauce', 'Mozzarella cheese', 'Fresh basil', 'Olive oil'],
    allergens: ['Dairy', 'Gluten'],
    preparationTime: 15,
    popularity: 95,
    totalOrders: 245
  },
  {
    id: '2',
    restaurantId: '1',
    name: 'Pepperoni Pizza',
    description: 'Classic pepperoni pizza with mozzarella cheese',
    price: 21.99,
    category: 'Pizza',
    available: true,
    imageUrl: null,
    ingredients: ['Tomato sauce', 'Mozzarella cheese', 'Pepperoni'],
    allergens: ['Dairy', 'Gluten'],
    preparationTime: 15,
    popularity: 88,
    totalOrders: 198
  },
  {
    id: '3',
    restaurantId: '1',
    name: 'Caesar Salad',
    description: 'Fresh romaine lettuce with Caesar dressing and croutons',
    price: 12.99,
    category: 'Salads',
    available: true,
    imageUrl: null,
    ingredients: ['Romaine lettuce', 'Caesar dressing', 'Croutons', 'Parmesan cheese'],
    allergens: ['Dairy', 'Gluten'],
    preparationTime: 8,
    popularity: 75,
    totalOrders: 87
  },
  {
    id: '4',
    restaurantId: '2',
    name: 'Classic Burger',
    description: 'Beef patty with lettuce, tomato, onion, and special sauce',
    price: 14.99,
    category: 'Burgers',
    available: true,
    imageUrl: null,
    ingredients: ['Beef patty', 'Lettuce', 'Tomato', 'Onion', 'Special sauce', 'Sesame bun'],
    allergens: ['Gluten'],
    preparationTime: 12,
    popularity: 92,
    totalOrders: 156
  },
  {
    id: '5',
    restaurantId: '3',
    name: 'Chicken Tacos',
    description: 'Three soft tacos with grilled chicken and fresh toppings',
    price: 11.99,
    category: 'Tacos',
    available: true,
    imageUrl: null,
    ingredients: ['Grilled chicken', 'Soft tortillas', 'Lettuce', 'Tomato', 'Cheese', 'Salsa'],
    allergens: ['Dairy'],
    preparationTime: 10,
    popularity: 89,
    totalOrders: 234
  }
];

export const mockOrders = [
  {
    id: 'ORD_001',
    customerId: '1',
    customerName: 'John Doe',
    customerEmail: 'john.doe@example.com',
    customerPhone: '+1234567890',
    restaurantId: '1',
    restaurantName: 'Pizza Palace',
    items: [
      {
        id: '1',
        name: 'Margherita Pizza',
        quantity: 2,
        price: 18.99,
        specialInstructions: 'Extra cheese please'
      },
      {
        id: '3',
        name: 'Caesar Salad',
        quantity: 1,
        price: 12.99,
        specialInstructions: ''
      }
    ],
    subtotal: 50.97,
    deliveryFee: 2.99,
    tax: 4.33,
    totalAmount: 58.29,
    status: 'PENDING',
    paymentStatus: 'PAID',
    paymentMethod: 'CREDIT_CARD',
    deliveryAddress: {
      street: '123 Main St',
      city: 'New York',
      state: 'NY',
      zipCode: '10001'
    },
    specialInstructions: 'Ring doorbell twice',
    createdAt: '2024-01-28T18:30:00Z',
    estimatedDeliveryTime: '2024-01-28T19:15:00Z',
    actualDeliveryTime: null
  },
  {
    id: 'ORD_002',
    customerId: '2',
    customerName: 'Jane Smith',
    customerEmail: 'jane.smith@example.com',
    customerPhone: '+1987654321',
    restaurantId: '1',
    restaurantName: 'Pizza Palace',
    items: [
      {
        id: '2',
        name: 'Pepperoni Pizza',
        quantity: 1,
        price: 21.99,
        specialInstructions: ''
      }
    ],
    subtotal: 21.99,
    deliveryFee: 2.99,
    tax: 2.12,
    totalAmount: 27.10,
    status: 'DELIVERED',
    paymentStatus: 'PAID',
    paymentMethod: 'PAYPAL',
    deliveryAddress: {
      street: '456 Oak Ave',
      city: 'Los Angeles',
      state: 'CA',
      zipCode: '90210'
    },
    specialInstructions: '',
    createdAt: '2024-01-28T16:45:00Z',
    estimatedDeliveryTime: '2024-01-28T17:30:00Z',
    actualDeliveryTime: '2024-01-28T17:25:00Z'
  },
  {
    id: 'ORD_003',
    customerId: '1',
    customerName: 'John Doe',
    customerEmail: 'john.doe@example.com',
    customerPhone: '+1234567890',
    restaurantId: '3',
    restaurantName: 'Taco Fiesta',
    items: [
      {
        id: '5',
        name: 'Chicken Tacos',
        quantity: 3,
        price: 11.99,
        specialInstructions: 'Extra spicy sauce'
      }
    ],
    subtotal: 35.97,
    deliveryFee: 2.50,
    tax: 3.08,
    totalAmount: 41.55,
    status: 'PREPARING',
    paymentStatus: 'PAID',
    paymentMethod: 'CREDIT_CARD',
    deliveryAddress: {
      street: '123 Main St',
      city: 'New York',
      state: 'NY',
      zipCode: '10001'
    },
    specialInstructions: 'Leave at door',
    createdAt: '2024-01-28T19:00:00Z',
    estimatedDeliveryTime: '2024-01-28T19:45:00Z',
    actualDeliveryTime: null
  }
];

export const mockPayments = [
  {
    id: 'PAY_001',
    orderId: 'ORD_001',
    customerId: '1',
    restaurantId: '1',
    amount: 58.29,
    currency: 'USD',
    method: 'CREDIT_CARD',
    status: 'COMPLETED',
    transactionId: 'txn_abc123',
    gatewayResponse: 'SUCCESS',
    createdAt: '2024-01-28T18:31:00Z',
    completedAt: '2024-01-28T18:31:05Z',
    cardLast4: '4242',
    cardBrand: 'VISA'
  },
  {
    id: 'PAY_002',
    orderId: 'ORD_002',
    customerId: '2',
    restaurantId: '1',
    amount: 27.10,
    currency: 'USD',
    method: 'PAYPAL',
    status: 'COMPLETED',
    transactionId: 'pp_xyz789',
    gatewayResponse: 'SUCCESS',
    createdAt: '2024-01-28T16:46:00Z',
    completedAt: '2024-01-28T16:46:03Z',
    paypalEmail: 'jane.smith@example.com'
  },
  {
    id: 'PAY_003',
    orderId: 'ORD_003',
    customerId: '1',
    restaurantId: '3',
    amount: 41.55,
    currency: 'USD',
    method: 'CREDIT_CARD',
    status: 'COMPLETED',
    transactionId: 'txn_def456',
    gatewayResponse: 'SUCCESS',
    createdAt: '2024-01-28T19:01:00Z',
    completedAt: '2024-01-28T19:01:02Z',
    cardLast4: '1234',
    cardBrand: 'MASTERCARD'
  }
];

export const mockNotifications = [
  {
    id: 'NOT_001',
    userId: '1',
    type: 'ORDER_CONFIRMED',
    title: 'Order Confirmed',
    message: 'Your order #ORD_001 has been confirmed by Pizza Palace',
    isRead: false,
    priority: 'NORMAL',
    createdAt: '2024-01-28T18:32:00Z',
    relatedEntityId: 'ORD_001',
    relatedEntityType: 'ORDER'
  },
  {
    id: 'NOT_002',
    userId: '2',
    type: 'ORDER_DELIVERED',
    title: 'Order Delivered',
    message: 'Your order #ORD_002 has been delivered successfully',
    isRead: true,
    priority: 'NORMAL',
    createdAt: '2024-01-28T17:25:00Z',
    relatedEntityId: 'ORD_002',
    relatedEntityType: 'ORDER'
  },
  {
    id: 'NOT_003',
    userId: '3',
    type: 'NEW_ORDER',
    title: 'New Order Received',
    message: 'You have received a new order #ORD_003 for Taco Fiesta',
    isRead: false,
    priority: 'HIGH',
    createdAt: '2024-01-28T19:00:00Z',
    relatedEntityId: 'ORD_003',
    relatedEntityType: 'ORDER'
  },
  {
    id: 'NOT_004',
    userId: '4',
    type: 'RESTAURANT_VERIFICATION',
    title: 'Restaurant Pending Verification',
    message: 'Burger Joint is pending verification',
    isRead: false,
    priority: 'MEDIUM',
    createdAt: '2024-01-25T14:30:00Z',
    relatedEntityId: '2',
    relatedEntityType: 'RESTAURANT'
  }
];

export const mockDashboardStats = {
  admin: {
    totalUsers: 1250,
    totalCustomers: 1180,
    totalRestaurantOwners: 65,
    totalAdmins: 5,
    totalRestaurants: 89,
    verifiedRestaurants: 76,
    pendingVerifications: 13,
    totalOrders: 15420,
    completedOrders: 14890,
    cancelledOrders: 280,
    pendingOrders: 250,
    platformRevenue: 125420.50,
    monthlyRevenue: 25680.75,
    averageOrderValue: 32.45,
    activeUsers: 980,
    newUsersThisMonth: 45,
    topRestaurants: [
      { name: 'Pizza Palace', orders: 1250, revenue: 15420.80 },
      { name: 'Taco Fiesta', orders: 890, revenue: 12340.50 },
      { name: 'Burger Joint', orders: 560, revenue: 8970.25 }
    ],
    recentActivity: [
      { type: 'NEW_USER', message: 'New customer registered: john_new@email.com', timestamp: '2024-01-28T19:30:00Z' },
      { type: 'ORDER_COMPLETED', message: 'Order #ORD_999 completed at Pizza Palace', timestamp: '2024-01-28T19:25:00Z' },
      { type: 'RESTAURANT_VERIFIED', message: 'Taco Fiesta verification approved', timestamp: '2024-01-28T19:20:00Z' }
    ]
  },
  restaurantOwner: {
    totalRestaurants: 2,
    todayOrders: 12,
    todayRevenue: 248.50,
    pendingOrders: 3,
    totalOrders: 2140,
    totalRevenue: 21920.80,
    averageOrderValue: 24.77,
    customerCount: 189,
    popularItems: [
      { name: 'Margherita Pizza', orders: 245, revenue: 4655.55 },
      { name: 'Chicken Tacos', orders: 234, revenue: 2805.66 },
      { name: 'Pepperoni Pizza', orders: 198, revenue: 4353.02 }
    ],
    orderTrends: {
      labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
      data: [12, 15, 18, 22, 25, 35, 28]
    },
    revenueTrends: {
      labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
      data: [248, 310, 374, 456, 519, 725, 580]
    }
  },
  customer: {
    totalOrders: 23,
    totalSpent: 486.25,
    favoriteRestaurants: 5,
    rewardPoints: 145,
    recentOrders: [
      { id: 'ORD_001', restaurant: 'Pizza Palace', total: 58.29, status: 'PENDING', date: '2024-01-28' },
      { id: 'ORD_003', restaurant: 'Taco Fiesta', total: 41.55, status: 'PREPARING', date: '2024-01-28' }
    ],
    recommendedRestaurants: [
      { id: '1', name: 'Pizza Palace', cuisine: 'Italian', rating: 4.5, deliveryTime: '25-35 min' },
      { id: '3', name: 'Taco Fiesta', cuisine: 'Mexican', rating: 4.7, deliveryTime: '15-25 min' }
    ]
  }
};

export const mockAnalytics = {
  ordersByHour: {
    labels: ['9AM', '10AM', '11AM', '12PM', '1PM', '2PM', '3PM', '4PM', '5PM', '6PM', '7PM', '8PM', '9PM'],
    data: [2, 3, 5, 12, 18, 15, 8, 6, 14, 22, 28, 24, 15]
  },
  ordersByDay: {
    labels: ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'],
    data: [45, 52, 48, 61, 72, 89, 67]
  },
  revenueByMonth: {
    labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
    data: [15420, 18550, 21300, 19800, 22450, 25670, 28900, 26780, 24560, 27890, 30120, 32450]
  },
  cuisinePopularity: {
    labels: ['Italian', 'Mexican', 'American', 'Chinese', 'Indian', 'Thai'],
    data: [35, 28, 22, 18, 15, 12]
  }
};

// Helper functions to get mock data
export const getMockUsersByRole = (role) => {
  if (!role) return mockUsers;
  return mockUsers.filter(user => user.role === role);
};

export const getMockRestaurantsByOwner = (ownerId) => {
  return mockRestaurants.filter(restaurant => restaurant.ownerId === ownerId);
};

export const getMockOrdersByRestaurant = (restaurantId) => {
  return mockOrders.filter(order => order.restaurantId === restaurantId);
};

export const getMockOrdersByCustomer = (customerId) => {
  return mockOrders.filter(order => order.customerId === customerId);
};

export const getMockMenuByRestaurant = (restaurantId) => {
  return mockMenuItems.filter(item => item.restaurantId === restaurantId);
};

export const getMockNotificationsByUser = (userId) => {
  return mockNotifications.filter(notification => notification.userId === userId);
};
