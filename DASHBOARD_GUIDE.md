# Role-Based Landing Pages Documentation

## Overview
The Foodie application now features different landing pages (dashboards) based on user roles:
- **Public Landing Page** - For non-authenticated users
- **Customer Dashboard** - For authenticated customers (ROLE_CUSTOMER)
- **Restaurant Owner Dashboard** - For restaurant owners (ROLE_RESTAURANT_OWNER)
- **Admin Dashboard** - For administrators (ROLE_ADMIN)

## Dashboard Features

### 🌟 Public Landing Page (Non-authenticated users)
**URL**: `http://localhost:3000`

**Features**:
- Hero section with search functionality
- Popular cuisines showcase
- Featured restaurants display
- Why Choose Foodie section
- Business partnership information
- Call-to-action buttons for registration

**Target Audience**: Visitors who haven't signed up yet

### 👤 Customer Dashboard (ROLE_CUSTOMER)
**Features**:
- Personalized welcome message
- Quick search functionality
- Quick action buttons (Browse Restaurants, My Orders, My Cart, Favorites)
- Recent orders display with reorder functionality
- Popular restaurants showcase
- Pro tips for better ordering experience

**Navigation**:
- Browse restaurants
- View order history
- Access cart
- Manage favorites

### 🍽️ Restaurant Owner Dashboard (ROLE_RESTAURANT_OWNER)
**Features**:
- Business metrics overview (Today's Revenue, Total Orders, Pending Orders, Avg Rating)
- Quick actions (Add Restaurant, Manage Menu, View Orders, Analytics)
- Restaurant management interface
- Recent orders table
- Performance insights
- Success tips for restaurant growth

**Key Capabilities**:
- Add and manage restaurants
- Menu management
- Order processing
- Performance analytics
- Business insights

### 🛡️ Admin Dashboard (ROLE_ADMIN)
**Features**:
- Platform-wide statistics (Total Users, Orders, Revenue, Pending Approvals)
- User management quick access
- Restaurant approval interface
- System monitoring
- Recent platform activities
- Pending approvals management
- User statistics breakdown
- System health indicators

**Administrative Functions**:
- User management
- Restaurant approvals
- Platform monitoring
- Analytics and reporting
- System administration

## How Role Detection Works

The application uses the `useAuth` context to determine user roles:

```javascript
const hasRole = (role) => {
  if (!user || !user.roles) return false;
  return user.roles.includes(role) || user.roles.some(r => r.name === role);
};
```

Based on the user's roles, the appropriate dashboard is rendered:
1. **Admin** users see the Admin Dashboard
2. **Restaurant Owner** users see the Restaurant Owner Dashboard  
3. **Customer** users (or default) see the Customer Dashboard
4. **Non-authenticated** users see the Public Landing Page

## Testing the Dashboards

### Testing Customer Dashboard
1. Register as a customer at `/register`
2. Login with customer credentials
3. Home page will show Customer Dashboard

### Testing Restaurant Owner Dashboard
1. Register as a restaurant owner at `/register-restaurant`
2. Wait for admin approval (or manually approve in backend)
3. Login with restaurant owner credentials
4. Home page will show Restaurant Owner Dashboard

### Testing Admin Dashboard
1. Create an admin user through backend API
2. Login with admin credentials
3. Home page will show Admin Dashboard

### Testing Public Landing Page
1. Logout if currently authenticated
2. Visit home page (`/`)
3. Will show public landing page

## Benefits

1. **Personalized Experience**: Each user type gets relevant information and actions
2. **Role-Based Access**: Users only see features they can use
3. **Improved Navigation**: Quick access to role-specific functions
4. **Better User Experience**: Tailored content based on user needs
5. **Professional Interface**: Each role gets a dashboard suited to their workflow

## Future Enhancements

- Real-time data updates
- More detailed analytics
- Notification systems
- Advanced filtering and search
- Mobile-responsive improvements
- Integration with notification service
- Advanced reporting features
