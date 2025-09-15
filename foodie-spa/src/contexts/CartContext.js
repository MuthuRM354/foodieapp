import React, { createContext, useContext, useReducer } from 'react';
import orderService from '../services/orderService';

const CartContext = createContext();

const initialState = {
  items: [],
  total: 0,
  itemCount: 0,
  restaurantId: null,
  isLoading: false,
};

const cartReducer = (state, action) => {
  switch (action.type) {
    case 'SET_LOADING':
      return {
        ...state,
        isLoading: action.payload,
      };
    case 'LOAD_CART':
      return {
        ...state,
        items: action.payload.items || [],
        total: action.payload.total || 0,
        itemCount: action.payload.itemCount || 0,
        restaurantId: action.payload.restaurantId || null,
        isLoading: false,
      };
    case 'ADD_ITEM':
      const existingItemIndex = state.items.findIndex(
        item => item.id === action.payload.id
      );
      
      let newItems;
      if (existingItemIndex >= 0) {
        newItems = state.items.map((item, index) =>
          index === existingItemIndex
            ? { ...item, quantity: item.quantity + action.payload.quantity }
            : item
        );
      } else {
        newItems = [...state.items, action.payload];
      }
      
      const newTotal = newItems.reduce((sum, item) => sum + (item.price * item.quantity), 0);
      const newItemCount = newItems.reduce((sum, item) => sum + item.quantity, 0);
      
      return {
        ...state,
        items: newItems,
        total: newTotal,
        itemCount: newItemCount,
        restaurantId: action.payload.restaurantId,
      };
    case 'UPDATE_ITEM':
      const updatedItems = state.items.map(item =>
        item.id === action.payload.id
          ? { ...item, quantity: action.payload.quantity }
          : item
      ).filter(item => item.quantity > 0);
      
      const updatedTotal = updatedItems.reduce((sum, item) => sum + (item.price * item.quantity), 0);
      const updatedItemCount = updatedItems.reduce((sum, item) => sum + item.quantity, 0);
      
      return {
        ...state,
        items: updatedItems,
        total: updatedTotal,
        itemCount: updatedItemCount,
        restaurantId: updatedItems.length > 0 ? state.restaurantId : null,
      };
    case 'REMOVE_ITEM':
      const filteredItems = state.items.filter(item => item.id !== action.payload);
      const filteredTotal = filteredItems.reduce((sum, item) => sum + (item.price * item.quantity), 0);
      const filteredItemCount = filteredItems.reduce((sum, item) => sum + item.quantity, 0);
      
      return {
        ...state,
        items: filteredItems,
        total: filteredTotal,
        itemCount: filteredItemCount,
        restaurantId: filteredItems.length > 0 ? state.restaurantId : null,
      };
    case 'CLEAR_CART':
      return {
        ...initialState,
      };
    default:
      return state;
  }
};

export const CartProvider = ({ children }) => {
  const [state, dispatch] = useReducer(cartReducer, initialState);

  const loadCart = async () => {
    dispatch({ type: 'SET_LOADING', payload: true });
    try {
      const cart = await orderService.getCart();
      dispatch({ type: 'LOAD_CART', payload: cart });
    } catch (error) {
      console.error('Failed to load cart:', error);
      dispatch({ type: 'SET_LOADING', payload: false });
    }
  };

  const addToCart = async (item) => {
    // Check if item is from a different restaurant
    if (state.restaurantId && state.restaurantId !== item.restaurantId && state.items.length > 0) {
      throw new Error('You can only order from one restaurant at a time. Please clear your cart first.');
    }

    dispatch({ type: 'ADD_ITEM', payload: item });
    
    try {
      await orderService.addToCart(item);
    } catch (error) {
      console.error('Failed to sync cart with server:', error);
    }
  };

  const updateCartItem = async (itemId, quantity) => {
    dispatch({ type: 'UPDATE_ITEM', payload: { id: itemId, quantity } });
    
    try {
      await orderService.updateCartItem(itemId, quantity);
    } catch (error) {
      console.error('Failed to update cart item on server:', error);
    }
  };

  const removeFromCart = async (itemId) => {
    dispatch({ type: 'REMOVE_ITEM', payload: itemId });
    
    try {
      await orderService.removeFromCart(itemId);
    } catch (error) {
      console.error('Failed to remove item from server cart:', error);
    }
  };

  const clearCart = async () => {
    dispatch({ type: 'CLEAR_CART' });
    
    try {
      await orderService.clearCart();
    } catch (error) {
      console.error('Failed to clear cart on server:', error);
    }
  };

  const value = {
    ...state,
    loadCart,
    addToCart,
    updateCartItem,
    removeFromCart,
    clearCart,
  };

  return (
    <CartContext.Provider value={value}>
      {children}
    </CartContext.Provider>
  );
};

export const useCart = () => {
  const context = useContext(CartContext);
  if (!context) {
    throw new Error('useCart must be used within a CartProvider');
  }
  return context;
};
