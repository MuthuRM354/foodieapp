package com.foodieapp.order.service;

import com.foodieapp.order.dto.request.CartItemRequest;
import com.foodieapp.order.dto.response.CartResponse;
import com.foodieapp.order.model.Cart;

public interface CartService {
    CartResponse getCart(String userId);
    CartResponse addItemToCart(String userId, CartItemRequest itemRequest);
    CartResponse removeItemFromCart(String userId, String itemId);
    CartResponse updateItemQuantity(String userId, String itemId, int quantity);
    void clearCart(String userId);
    CartResponse updateCartInstructions(String userId, String instructions);

    // Add this method to the interface
    Cart getOrCreateCart(String userId);
}
