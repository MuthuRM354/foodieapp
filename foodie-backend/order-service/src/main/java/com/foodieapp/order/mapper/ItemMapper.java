package com.foodieapp.order.mapper;

import com.foodieapp.order.dto.ItemDto;
import com.foodieapp.order.dto.request.BaseItemRequest;
import com.foodieapp.order.dto.request.CartItemRequest;
import com.foodieapp.order.dto.response.CartItemResponse;
import com.foodieapp.order.model.CartItem;
import com.foodieapp.order.model.OrderItem;
import org.springframework.stereotype.Component;

@Component
public class ItemMapper {

    // Convert BaseItemRequest to ItemDto
    public ItemDto toItemDto(BaseItemRequest request) {
        ItemDto dto = new ItemDto();
        dto.setItemId(request.getItemId());
        dto.setName(request.getName());
        dto.setQuantity(request.getQuantity());
        dto.setPrice(request.getPrice());
        dto.setSpecialInstructions(request.getSpecialInstructions());
        dto.setCategory(request.getCategory());
        dto.setSize(request.getItemSize());

        // Handle CartItemRequest specific fields
        if (request instanceof CartItemRequest) {
            dto.setRestaurantId(((CartItemRequest) request).getRestaurantId());
            dto.setType(ItemDto.ItemType.CART_ITEM);
        } else {
            dto.setType(ItemDto.ItemType.ORDER_ITEM);
        }

        return dto;
    }

    // Convert ItemDto to CartItem
    public CartItem toCartItem(ItemDto dto) {
        CartItem item = new CartItem();
        item.setItemId(dto.getItemId());
        item.setName(dto.getName());
        item.setQuantity(dto.getQuantity());
        item.setPrice(dto.getPrice());
        item.setSpecialInstructions(dto.getSpecialInstructions());
        item.setCategory(dto.getCategory());
        item.setSize(dto.getSize());
        item.setRestaurantId(dto.getRestaurantId());

        return item;
    }

    // Convert ItemDto to OrderItem
    public OrderItem toOrderItem(ItemDto dto) {
        OrderItem item = new OrderItem();
        item.setItemId(dto.getItemId());
        item.setName(dto.getName());
        item.setQuantity(dto.getQuantity());
        item.setPrice(dto.getPrice());
        item.setSpecialInstructions(dto.getSpecialInstructions());
        item.setCategory(dto.getCategory());
        item.setSize(dto.getSize());

        return item;
    }

    // Convert CartItem to CartItemResponse
    public CartItemResponse toCartItemResponse(CartItem item) {
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

    // Convert CartItem to OrderItem
    public OrderItem cartItemToOrderItem(CartItem cartItem) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItemId(cartItem.getItemId());
        orderItem.setName(cartItem.getName());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setPrice(cartItem.getPrice());
        orderItem.setSpecialInstructions(cartItem.getSpecialInstructions());
        orderItem.setCategory(cartItem.getCategory());
        orderItem.setSize(cartItem.getSize());

        return orderItem;
    }
}