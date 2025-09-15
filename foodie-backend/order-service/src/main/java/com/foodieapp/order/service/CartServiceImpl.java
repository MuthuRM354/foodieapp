package com.foodieapp.order.service;

import com.foodieapp.order.dto.request.CartItemRequest;
import com.foodieapp.order.dto.response.CartItemResponse;
import com.foodieapp.order.dto.response.CartResponse;
import com.foodieapp.order.model.Cart;
import com.foodieapp.order.model.CartItem;
import com.foodieapp.order.repository.CartRepository;
import com.foodieapp.order.util.PriceCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private PriceCalculationService priceCalculationService;

    @Override
    public CartResponse getCart(String userId) {
        Cart cart = getOrCreateCart(userId);
        return convertToCartResponse(cart);
    }

    @Override
    public CartResponse addItemToCart(String userId, CartItemRequest itemRequest) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = convertToCartItem(itemRequest);

        // Check if cart has items from a different restaurant
        if (cart.getRestaurantId() != null && !cart.getRestaurantId().equals(item.getRestaurantId())) {
            cart.getItems().clear();
        }

        cart.setRestaurantId(item.getRestaurantId());

        // Add or update item
        boolean itemExists = false;
        for (CartItem existingItem : cart.getItems()) {
            if (existingItem.getItemId().equals(item.getItemId())) {
                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                itemExists = true;
                break;
            }
        }

        if (!itemExists) {
            cart.getItems().add(item);
        }

        // Set price calculation service and calculate total
        cart.setPriceCalculationService(priceCalculationService);
        cart.calculateTotal();

        cart.setLastUpdated(LocalDateTime.now());
        Cart updatedCart = cartRepository.save(cart);
        return convertToCartResponse(updatedCart);
    }

    @Override
    public CartResponse removeItemFromCart(String userId, String itemId) {
        Cart cart = getOrCreateCart(userId);
        if (cart.getItems().removeIf(item -> item.getItemId().equals(itemId))) {
            if (cart.getItems().isEmpty()) {
                cart.setRestaurantId(null);
            }

            cart.setPriceCalculationService(priceCalculationService);
            cart.calculateTotal();
            cart.setLastUpdated(LocalDateTime.now());
            Cart updatedCart = cartRepository.save(cart);
            return convertToCartResponse(updatedCart);
        } else {
            throw new IllegalArgumentException("Item not found in cart");
        }
    }

    @Override
    public CartResponse updateItemQuantity(String userId, String itemId, int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }

        Cart cart = getOrCreateCart(userId);
        CartItem itemToUpdate = cart.getItems().stream()
                .filter(item -> item.getItemId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item not found in cart"));

        itemToUpdate.setQuantity(quantity);

        cart.setPriceCalculationService(priceCalculationService);
        cart.calculateTotal();
        cart.setLastUpdated(LocalDateTime.now());
        Cart updatedCart = cartRepository.save(cart);
        return convertToCartResponse(updatedCart);
    }

    @Override
    @Transactional
    public void clearCart(String userId) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().clear();
        cart.setRestaurantId(null);

        cart.setPriceCalculationService(priceCalculationService);
        cart.calculateTotal();
        cart.setLastUpdated(LocalDateTime.now());
        cartRepository.save(cart);
    }

    @Override
    public CartResponse updateCartInstructions(String userId, String instructions) {
        Cart cart = getOrCreateCart(userId);
        cart.setSpecialInstructions(instructions);
        cart.setLastUpdated(LocalDateTime.now());
        Cart updatedCart = cartRepository.save(cart);
        return convertToCartResponse(updatedCart);
    }

    @Override
    public Cart getOrCreateCart(String userId) {
        return cartRepository.findByUserIdAndIsActiveTrue(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    newCart.setActive(true);
                    newCart.setLastUpdated(LocalDateTime.now());
                    return cartRepository.save(newCart);
                });
    }

    private CartItem convertToCartItem(CartItemRequest request) {
        CartItem item = new CartItem();
        item.setItemId(request.getItemId());
        item.setName(request.getName());
        item.setPrice(request.getPrice());
        item.setQuantity(request.getQuantity());
        item.setRestaurantId(request.getRestaurantId());
        item.setSpecialInstructions(request.getSpecialInstructions());
        item.setCategory(request.getCategory());
        item.setSize(request.getItemSize());
        return item;
    }

    private CartResponse convertToCartResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setUserId(cart.getUserId());
        response.setRestaurantId(cart.getRestaurantId());
        response.setItems(cart.getItems().stream()
                .map(this::convertToCartItemResponse)
                .collect(Collectors.toList()));
        response.setSubtotal(cart.getSubtotal());
        response.setTax(cart.getTax());
        response.setDeliveryFee(cart.getDeliveryFee());
        response.setTotalAmount(cart.getTotalAmount());
        response.setItemCount(cart.getItemCount());
        response.setSpecialInstructions(cart.getSpecialInstructions());
        response.setLastUpdated(cart.getLastUpdated());
        return response;
    }

    private CartItemResponse convertToCartItemResponse(CartItem item) {
        CartItemResponse response = new CartItemResponse();
        response.setItemId(item.getItemId());
        response.setName(item.getName());
        response.setQuantity(item.getQuantity());
        response.setPrice(item.getPrice());
        response.setSubtotal(item.getSubtotal());
        response.setSpecialInstructions(item.getSpecialInstructions());
        response.setCategory(item.getCategory());
        response.setSize(item.getSize());
        response.setRestaurantId(item.getRestaurantId());
        return response;
    }
}
