# 🍔 Foodie App - Testing Guide

## 📋 Overview
The Foodie application now includes comprehensive mock data and test users for testing all dashboard functionalities without requiring backend services.

## 🚀 Quick Start

1. **Start the Application**
   ```bash
   cd foodie-spa
   npm start
   ```
   Application will be available at: http://localhost:3000

2. **Test Users Available**
   - **Customer**: `john.doe@example.com` / `password123`
   - **Restaurant Owner**: `mario@pizzapalace.com` / `password123`  
   - **Admin**: `admin@foodie.com` / `password123`

## 🎯 Testing Each Dashboard

### 1. 👤 Customer Dashboard
**Login as**: `john.doe@example.com`

**Features to Test**:
- ✅ Order history with real order data
- ✅ Favorite restaurants display
- ✅ Reward points and customer stats
- ✅ Recommended restaurants
- ✅ Profile management interface
- ✅ Recent order tracking with status

**Mock Data Includes**:
- 23 total orders
- $486.25 total spent
- 145 reward points
- 5 favorite restaurants

### 2. 🏪 Restaurant Owner Dashboard
**Login as**: `mario@pizzapalace.com`

**Features to Test**:
- ✅ **Overview Tab**: Dashboard stats, recent orders, popular items
- ✅ **Restaurants Tab**: Manage 2 restaurants (Pizza Palace & Taco Fiesta)
- ✅ **Menu Management**: Add/edit/delete menu items with full details
- ✅ **Orders Tab**: Process orders through status workflow
- ✅ **Analytics Tab**: Revenue trends and business insights

**Mock Data Includes**:
- 2 restaurants owned by Mario
- 12 today's orders, $248.50 today's revenue
- 3 pending orders to process
- Complete menu items for each restaurant
- Order workflow: PENDING → CONFIRMED → PREPARING → READY → OUT_FOR_DELIVERY → DELIVERED

**Test Workflows**:
1. Create new restaurant with full details
2. Add menu items with ingredients, allergens, prices
3. Process orders by updating status
4. View analytics and performance metrics

### 3. ⚙️ Admin Dashboard
**Login as**: `admin@foodie.com`

**Features to Test**:
- ✅ **Overview Tab**: Platform-wide statistics and metrics
- ✅ **Users Tab**: Manage customers, restaurant owners, and admins
- ✅ **Restaurants Tab**: Verify restaurants and manage platform restaurants
- ✅ **Orders Tab**: Monitor all platform orders
- ✅ **Payments Tab**: Track payment transactions
- ✅ **Notifications Tab**: System notification logs
- ✅ **Settings Tab**: Platform configuration

**Mock Data Includes**:
- 1,250 total users (1,180 customers, 65 restaurant owners)
- 89 restaurants (76 verified, 13 pending)
- 15,420 total orders, $125,420.50 platform revenue
- Complete user management capabilities
- Restaurant verification workflows

**Test Workflows**:
1. Create new admin users
2. Verify pending restaurants
3. Activate/deactivate users
4. Monitor order and payment logs
5. Export data functionality

## 📊 Mock Data Features

### Realistic Data Structure
- **Users**: Complete profiles with roles, contact info, order history
- **Restaurants**: Full restaurant details with owners, verification status
- **Orders**: Complete order lifecycle with items, pricing, delivery info
- **Menu Items**: Detailed items with ingredients, allergens, pricing
- **Payments**: Transaction logs with different payment methods
- **Analytics**: Trends, popular items, revenue data

### Automatic Fallbacks
- Services automatically use mock data when backend is unavailable
- No network errors - seamless testing experience
- Consistent data relationships across all services

## 🧪 Test Scenarios

### Customer Journey
1. Login as customer → View dashboard stats
2. Browse order history → Check recent orders
3. View recommended restaurants → See ratings and delivery times
4. Track active orders → See real-time status updates

### Restaurant Owner Journey
1. Login as owner → View business overview
2. Manage restaurants → Add new restaurant with full details
3. Update menu → Add items with pricing, ingredients, allergens
4. Process orders → Move through complete status workflow
5. View analytics → Check revenue trends and popular items

### Admin Management
1. Login as admin → View platform overview
2. Manage users → Create admins, activate/deactivate accounts
3. Verify restaurants → Approve pending restaurant applications
4. Monitor orders → Track all platform activity
5. Review payments → Check transaction logs

## 🔄 Data Relationships

The mock data maintains proper relationships:
- **Users** → linked to their **Orders** and **Restaurants**
- **Restaurants** → linked to **Owners**, **Menu Items**, and **Orders**
- **Orders** → linked to **Customers**, **Restaurants**, and **Payments**
- **Menu Items** → linked to specific **Restaurants**

## 💡 Development Notes

- Mock data is stored in `src/services/mockDataService.js`
- Services automatically fallback to mock data on API failures
- Test user credentials are in `src/services/mockAuthService.js`
- All dashboard components work fully with mock data
- No backend dependencies required for frontend testing

## 🎨 UI Features Tested

- ✅ Responsive design across all screen sizes
- ✅ Tabbed navigation with active states
- ✅ Modal forms for creating/editing
- ✅ Data tables with sorting and filtering
- ✅ Status indicators with color coding
- ✅ Loading states and error handling
- ✅ Interactive buttons and workflows

## 🚀 Ready for Testing!

The application is now fully functional with comprehensive mock data. You can test all dashboard features, user workflows, and UI components without any backend dependencies.

**Start testing by visiting**: http://localhost:3000 and clicking the test user buttons on the login page!
