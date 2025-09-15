package com.foodieapp.order.service;

import com.foodieapp.order.client.NotificationServiceClient;
import com.foodieapp.order.client.PaymentServiceClient;
import com.foodieapp.order.client.RestaurantServiceClient;
import com.foodieapp.order.client.UserServiceClient;
import com.foodieapp.order.dto.request.OrderRequest;
import com.foodieapp.order.dto.request.PaymentRequest;
import com.foodieapp.order.dto.response.OrderResponse;
import com.foodieapp.order.exception.ErrorCodes;
import com.foodieapp.order.exception.OrderServiceException;
import com.foodieapp.order.exception.ResourceNotFoundException;
import com.foodieapp.order.exception.UnauthorizedException;
import com.foodieapp.order.mapper.ItemMapper;
import com.foodieapp.order.model.Cart;
import com.foodieapp.order.model.CartItem;
import com.foodieapp.order.model.Order;
import com.foodieapp.order.model.OrderItem;
import com.foodieapp.order.model.OrderStatus;
import com.foodieapp.order.repository.CartRepository;
import com.foodieapp.order.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private RestaurantServiceClient restaurantServiceClient;

    @Autowired
    private PaymentServiceClient paymentServiceClient;

    @Autowired
    private NotificationServiceClient notificationServiceClient;

    @Autowired
    private ItemMapper itemMapper;

    @Override
    @Transactional
    public Order createOrderFromCart(OrderRequest orderRequest, String authToken) {
        logger.info("Creating order from cart for user: {}", orderRequest.getUserId());

        // Common validation and preparation
        Order order = prepareOrderBase(orderRequest, authToken);

        // Cart-specific processing
        Cart cart = cartRepository.findByUserId(orderRequest.getUserId())
                .orElseThrow(() -> new OrderServiceException(
                        "Cart not found for user: " + orderRequest.getUserId(),
                        ErrorCodes.CART_NOT_FOUND));

        // Ensure cart is not empty
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new OrderServiceException(
                    "Cannot create order from empty cart",
                    ErrorCodes.EMPTY_CART);
        }

        // Set restaurant info from cart
        order.setRestaurantId(cart.getRestaurantId());

        // Look up restaurant name
        setRestaurantName(order, authToken);

        // Convert cart items to order items
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(this::convertCartItemToOrderItem)
                .collect(Collectors.toList());

        order.setItems(orderItems);

        // Set totals from cart
        order.setSubtotal(cart.getSubtotal());
        order.setTax(cart.getTax());
        order.setDeliveryFee(cart.getDeliveryFee());
        order.calculateTotals();

        // Save the order
        Order savedOrder = completeOrderCreation(order, orderRequest, authToken);

        // Clear the cart after successful order creation
        cartService.clearCart(orderRequest.getUserId());

        logger.info("Order created successfully from cart. Order ID: {}", savedOrder.getId());
        return savedOrder;
    }

    @Override
    @Transactional
    public Order createDirectOrder(OrderRequest orderRequest, String authToken) {
        logger.info("Creating direct order for user: {}", orderRequest.getUserId());

        // Common validation and preparation
        Order order = prepareOrderBase(orderRequest, authToken);

        // Direct order specific validation
        if (orderRequest.getItems() == null || orderRequest.getItems().isEmpty()) {
            throw new OrderServiceException(
                    "Cannot create order without items",
                    ErrorCodes.VALIDATION_ERROR);
        }

        // Set restaurant info
        order.setRestaurantId(orderRequest.getRestaurantId());
        order.setRestaurantName(orderRequest.getRestaurantName());

        // Validate restaurant exists if not already set
        if (order.getRestaurantName() == null || order.getRestaurantName().isEmpty()) {
            setRestaurantName(order, authToken);
        }

        // Convert request items to order items
        List<OrderItem> orderItems = orderRequest.getItems().stream()
                .map(itemRequest -> {
                    OrderItem item = new OrderItem();
                    item.setItemId(itemRequest.getItemId());
                    item.setName(itemRequest.getName());
                    item.setQuantity(itemRequest.getQuantity());
                    item.setPrice(itemRequest.getPrice());
                    item.setSpecialInstructions(itemRequest.getSpecialInstructions());
                    item.setCategory(itemRequest.getCategory());
                    item.setSize(itemRequest.getItemSize());
                    return item;
                })
                .collect(Collectors.toList());

        order.setItems(orderItems);

        // Calculate totals
        order.calculateTotals();

        // Save the order
        Order savedOrder = completeOrderCreation(order, orderRequest, authToken);

        logger.info("Direct order created successfully. Order ID: {}", savedOrder.getId());
        return savedOrder;
    }

    private boolean isOnlinePaymentMethod(String paymentMethod) {
        // Define which payment methods require online payment processing
        return "CREDIT_CARD".equals(paymentMethod) ||
                "DEBIT_CARD".equals(paymentMethod) ||
                "UPI".equals(paymentMethod) ||
                "WALLET".equals(paymentMethod) ||
                "RAZORPAY".equals(paymentMethod);
    }

    /**
     * Check if the authenticated user is authorized for the specified restaurant
     * @param restaurantId Restaurant ID to check authorization for
     * @param authToken Authentication token
     * @return true if user is authorized for the restaurant
     */
    private boolean isUserAuthorizedForRestaurant(String restaurantId, String authToken) {
        try {
            // Admins are authorized for all restaurants
            if (isUserAdmin(authToken)) {
                return true;
            }
            String userId = extractUserIdFromToken(authToken);
            if (userId == null) {
                return false;
            }

            Map<String, Object> ownershipInfo = restaurantServiceClient.verifyOwnership(
                    restaurantId, userId, authToken);

            return Boolean.TRUE.equals(ownershipInfo.get("isOwner"));
        } catch (Exception e) {
            logger.error("Error verifying restaurant ownership: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Comprehensive authorization check for order access
     * @param order The order to check authorization for
     * @param authToken Authentication token
     * @return true if the user is authorized to access this order
     */
    private boolean isAuthorizedForOrder(Order order, String authToken) {
        if (order == null) {
            return false;
        }

        // Admins can access all orders
        if (isUserAdmin(authToken)) {
            return true;
        }

        String userId = extractUserIdFromToken(authToken);
        if (userId == null) {
            return false;
        }

        // Order owners can access their orders
        if (order.getUserId().equals(userId)) {
            return true;
        }

        // Restaurant owners can access orders for their restaurants
        return isUserAuthorizedForRestaurant(order.getRestaurantId(), authToken);
    }

    /**
     * Verify a restaurant exists and is active
     * @param restaurantId Restaurant ID to check
     * @param authToken Authentication token
     * @throws ResourceNotFoundException if restaurant doesn't exist
     */
    private void verifyRestaurantExists(String restaurantId, String authToken) {
        Map<String, Object> restaurantInfo = restaurantServiceClient.getRestaurantInfo(
                restaurantId, authToken);

        if (restaurantInfo == null || !Boolean.TRUE.equals(restaurantInfo.get("exists"))) {
            throw new ResourceNotFoundException("Restaurant", restaurantId);
        }
    }

    /**
     * Create the base Order object with common fields
     * @param orderRequest Request containing order details
     * @param authToken Authentication token
     * @return Partially initialized Order
     */
    private Order prepareOrderBase(OrderRequest orderRequest, String authToken) {
        // Verify user identity
        verifyUserIdentity(orderRequest.getUserId(), authToken);

        // Create new order
        Order order = new Order();

        // Set basic properties
        order.setUserId(orderRequest.getUserId());
        order.setDeliveryAddress(orderRequest.getDeliveryAddress());
        order.setCustomerName(orderRequest.getCustomerName());
        order.setCustomerPhone(orderRequest.getCustomerPhone());
        order.setPaymentMethod(orderRequest.getPaymentMethod());
        order.setSpecialInstructions(orderRequest.getSpecialInstructions());
        order.setOrderTime(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        return order;
    }

    /**
     * Set restaurant name by looking it up from the restaurant service
     * @param order Order to set restaurant name for
     * @param authToken Authentication token
     */
    private void setRestaurantName(Order order, String authToken) {
        try {
            Map<String, Object> restaurantInfo = restaurantServiceClient.getRestaurantInfo(
                    order.getRestaurantId(), authToken);

            if (restaurantInfo != null && restaurantInfo.containsKey("name")) {
                order.setRestaurantName(restaurantInfo.get("name").toString());
            }
        } catch (Exception e) {
            logger.warn("Could not retrieve restaurant name: {}", e.getMessage());
        }
    }

    /**
     * Complete order creation by saving, handling payment and returning the result
     * @param order Prepared order
     * @param orderRequest Original order request
     * @param authToken Authentication token
     * @return Saved order
     */
    private Order completeOrderCreation(Order order, OrderRequest orderRequest, String authToken) {
        // Save the order
        Order savedOrder = orderRepository.save(order);

        // Now initiate payment if this is an online payment method
        if (isOnlinePaymentMethod(orderRequest.getPaymentMethod())) {
            try {
                PaymentRequest paymentRequest = PaymentRequest.builder()
                        .userId(orderRequest.getUserId())
                        .orderId(savedOrder.getId())
                        .amount(savedOrder.getTotalAmount())
                        .currency("INR")
                        .paymentMethod(orderRequest.getPaymentMethod())
                        .build();

                Map<String, Object> paymentResult = paymentServiceClient.createPayment(paymentRequest, authToken);

                logger.info("Payment initiated for order {}: {}", savedOrder.getId(), paymentResult);

                // You might want to store the payment ID in the order
                if (paymentResult != null && paymentResult.containsKey("paymentId")) {
                    savedOrder.setOrderNotes("Payment ID: " + paymentResult.get("paymentId"));
                    savedOrder = orderRepository.save(savedOrder);
                }
            } catch (Exception e) {
                logger.error("Failed to initiate payment for order {}: {}", savedOrder.getId(), e.getMessage());
                // Handle payment initiation failure
                // You might want to mark the order as payment_pending or similar
            }
        }

        return savedOrder;
    }

    @Override
    public Order getOrderById(String orderId, String authToken) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        // Verify the requester has permission to view this order
        if (!isAuthorizedForOrder(order, authToken)) {
            throw new UnauthorizedException("You are not authorized to view this order");
        }

        return order;
    }

    @Override
    public List<Order> getUserOrders(String userId, String authToken) {
        // Verify the requester has permission to view these orders
        verifyUserIdentity(userId, authToken);

        return orderRepository.findByUserId(userId,
                Sort.by(Sort.Direction.DESC, "orderTime"));
    }

    @Override
    public List<Order> getRestaurantOrders(String restaurantId, String authToken) {
        // Verify the requester has permission to view these orders
        if (!isUserAuthorizedForRestaurant(restaurantId, authToken) && !isUserAdmin(authToken)) {
            throw new UnauthorizedException(
                    "You are not authorized to view orders for this restaurant");
        }

        return orderRepository.findByRestaurantId(restaurantId,
                Sort.by(Sort.Direction.DESC, "orderTime"));
    }

    @Override
    public List<Order> getAllOrders(int page, int size, String authToken) {
        // Verify the requester is an admin
        if (!isUserAdmin(authToken)) {
            throw new UnauthorizedException("Only admins can view all orders");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "orderTime"));
        return orderRepository.findAll(pageable).getContent();
    }

    @Override
    public Order updateOrderStatus(String orderId, OrderStatus status, String notes, String authToken) {
        // Verify order exists
        Order order = getOrderById(orderId, authToken);

        // Store original status
        OrderStatus oldStatus = order.getStatus();

        // Perform authorization checks
        boolean authorized = false;

        // If it's a customer cancellation (only allowed for certain statuses)
        if (status == OrderStatus.CANCELLED &&
                (order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.CONFIRMED)) {
            // Check if the authenticated user is the order owner
            String userId = extractUserIdFromToken(authToken);
            authorized = userId != null && userId.equals(order.getUserId());
        }

        // If it's a restaurant operation
        if ((status == OrderStatus.CONFIRMED || status == OrderStatus.PREPARING ||
                status == OrderStatus.READY || status == OrderStatus.OUT_FOR_DELIVERY)) {
            // Check if the authenticated user is associated with the restaurant
            String restaurantId = order.getRestaurantId();
            authorized = isUserAuthorizedForRestaurant(restaurantId, authToken);
        }

        // If it's an admin operation
        if (!authorized) {
            authorized = isUserAdmin(authToken);
        }

        if (!authorized) {
            throw new UnauthorizedException("You are not authorized to update this order status");
        }

        // Validate state transition
        validateStatusTransition(order.getStatus(), status);

        // Update the order
        order.setStatus(status);

        // Handle status-specific logic
        switch (status) {
            case CONFIRMED:
                order.setConfirmedTime(LocalDateTime.now());
                break;
            case CANCELLED:
                // Add cancellation logic
                break;
            case DELIVERED:
                // Add delivery confirmation logic
                break;
        }

        // Add notes if provided
        if (notes != null && !notes.isEmpty()) {
            order.setOrderNotes(notes);
        }

        // Save the order
        Order updatedOrder = orderRepository.save(order);

        // Send notifications about status change
        sendOrderStatusNotifications(updatedOrder, oldStatus);

        return updatedOrder;
    }

    @Override
    public OrderResponse convertToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();

        response.setId(order.getId());
        response.setUserId(order.getUserId());
        response.setRestaurantId(order.getRestaurantId());
        response.setRestaurantName(order.getRestaurantName());
        response.setItems(order.getItems());
        response.setTotalAmount(order.getTotalAmount());
        response.setSubtotal(order.getSubtotal());
        response.setTax(order.getTax());
        response.setDeliveryFee(order.getDeliveryFee());
        response.setStatus(order.getStatus());
        response.setStatusNotes(order.getOrderNotes());
        response.setDeliveryAddress(order.getDeliveryAddress());
        response.setPaymentMethod(order.getPaymentMethod());
        response.setOrderTime(order.getOrderTime());
        response.setSpecialInstructions(order.getSpecialInstructions());
        response.setCustomerName(order.getCustomerName());
        response.setCustomerPhone(order.getCustomerPhone());

        return response;
    }

    // Helper methods
    private OrderItem convertCartItemToOrderItem(CartItem cartItem) {
        return itemMapper.cartItemToOrderItem(cartItem);
    }

    private void verifyUserIdentity(String userId, String authToken) {
        // Check if the authenticated user matches the requested user ID
        String authenticatedUserId = extractUserIdFromToken(authToken);
        boolean isAdmin = isUserAdmin(authToken);

        if (!isAdmin && (authenticatedUserId == null || !authenticatedUserId.equals(userId))) {
            throw new UnauthorizedException("You are not authorized to perform this action");
        }
    }

    private String extractUserIdFromToken(String authToken) {
        Map<String, Object> tokenInfo = userServiceClient.validateToken(authToken);

        if (Boolean.TRUE.equals(tokenInfo.get("valid"))) {
            return tokenInfo.get("userId").toString();
        }

        return null;
    }

    private boolean isUserAdmin(String authToken) {
        try {
            Map<String, Object> tokenInfo = userServiceClient.validateToken(authToken);

            if (Boolean.TRUE.equals(tokenInfo.get("valid"))) {
                @SuppressWarnings("unchecked")
                List<String> roles = (List<String>) tokenInfo.get("roles");
                return roles != null && roles.contains("ROLE_ADMIN");
            }

            return false;
        } catch (Exception e) {
            logger.error("Error checking admin status: {}", e.getMessage());
            return false;
        }
    }

    // Helper method to validate status transitions
    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        // Define valid transitions
        switch (currentStatus) {
            case PENDING:
                if (newStatus != OrderStatus.CONFIRMED &&
                        newStatus != OrderStatus.CANCELLED) {
                    throw new IllegalStateException("Cannot transition from PENDING to " + newStatus);
                }
                break;
            case CONFIRMED:
                if (newStatus != OrderStatus.PREPARING &&
                        newStatus != OrderStatus.CANCELLED) {
                    throw new IllegalStateException("Cannot transition from CONFIRMED to " + newStatus);
                }
                break;
            case PREPARING:
                if (newStatus != OrderStatus.READY &&
                        newStatus != OrderStatus.CANCELLED) {
                    throw new IllegalStateException("Cannot transition from PREPARING to " + newStatus);
                }
                break;
            case READY:
                if (newStatus != OrderStatus.OUT_FOR_DELIVERY &&
                        newStatus != OrderStatus.COMPLETED &&
                        newStatus != OrderStatus.CANCELLED) {
                    throw new IllegalStateException("Cannot transition from READY to " + newStatus);
                }
                break;
            case OUT_FOR_DELIVERY:
                if (newStatus != OrderStatus.DELIVERED &&
                        newStatus != OrderStatus.CANCELLED) {
                    throw new IllegalStateException("Cannot transition from OUT_FOR_DELIVERY to " + newStatus);
                }
                break;
            case DELIVERED:
                if (newStatus != OrderStatus.COMPLETED) {
                    throw new IllegalStateException("Cannot transition from DELIVERED to " + newStatus);
                }
                break;
            case COMPLETED:
            case CANCELLED:
                throw new IllegalStateException("Cannot change status once order is " + currentStatus);
        }
    }

    @Override
    @Transactional
    public Order updateOrderPaymentStatus(String orderId, String paymentStatus) {
        logger.info("Updating payment status for order {} to: {}", orderId, paymentStatus);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        // Map payment status to order status if needed
        if ("COMPLETED".equals(paymentStatus)) {
            // If payment is completed, update order status to CONFIRMED if it's still PENDING
            if (order.getStatus() == OrderStatus.PENDING) {
                order.setStatus(OrderStatus.CONFIRMED);
                order.setConfirmedTime(LocalDateTime.now());
            }
        } else if ("FAILED".equals(paymentStatus) || "CANCELLED".equals(paymentStatus)) {
            // Handle payment failure or cancellation
            // You may want to cancel the order or keep it in a special state
            order.setOrderNotes("Payment " + paymentStatus.toLowerCase() + ": " +
                    (order.getOrderNotes() != null ? order.getOrderNotes() : ""));
        }

        // Save and return the updated order
        return orderRepository.save(order);
    }

    /**
     * Send order status notifications
     */
    private void sendOrderStatusNotifications(Order order, OrderStatus oldStatus) {
        try {
            // Skip notification if status didn't change
            if (oldStatus == order.getStatus()) {
                return;
            }

            // Get user details from user service
            Map<String, Object> userDetails = userServiceClient.getUserDetails(order.getUserId(), "");

            if (userDetails == null || !Boolean.TRUE.equals(userDetails.get("exists"))) {
                logger.warn("User details not found for order status notification. UserId: {}", order.getUserId());
                return;
            }

            // Extract user contact info
            String email = (String) userDetails.get("email");
            String phoneNumber = (String) userDetails.get("phoneNumber");

            // Send email notification
            if (email != null && !email.isEmpty()) {
                notificationServiceClient.sendOrderStatusEmail(
                        email,
                        order.getId(),
                        order.getStatus().toString(),
                        order.getRestaurantName());
            }

            // Send SMS for important status changes
            if (phoneNumber != null && !phoneNumber.isEmpty() &&
                    (order.getStatus() == OrderStatus.CONFIRMED ||
                            order.getStatus() == OrderStatus.OUT_FOR_DELIVERY ||
                            order.getStatus() == OrderStatus.DELIVERED ||
                            order.getStatus() == OrderStatus.CANCELLED)) {

                notificationServiceClient.sendOrderStatusSms(
                        phoneNumber,
                        order.getId(),
                        order.getStatus().toString(),
                        order.getRestaurantName());
            }

            // If this is a new order (changing from PENDING to CONFIRMED), notify restaurant
            if (oldStatus == OrderStatus.PENDING && order.getStatus() == OrderStatus.CONFIRMED) {
                notifyRestaurantAboutOrder(order);
            }

            logger.info("Order status notifications sent for order: {}", order.getId());
        } catch (Exception e) {
            logger.error("Failed to send order status notifications: {}", e.getMessage());
        }
    }

    /**
     * Notify restaurant about new order
     */
    private void notifyRestaurantAboutOrder(Order order) {
        try {
            // Get restaurant details from restaurant service
            Map<String, Object> restaurantInfo = restaurantServiceClient.getRestaurantInfo(
                    order.getRestaurantId(), "");

            if (restaurantInfo == null || !Boolean.TRUE.equals(restaurantInfo.get("exists"))) {
                logger.warn("Restaurant details not found for notification. RestaurantId: {}", order.getRestaurantId());
                return;
            }

            // Get restaurant owner ID
            String ownerId = (String) restaurantInfo.get("ownerId");

            // Get owner details from user service
            Map<String, Object> ownerDetails = userServiceClient.getUserDetails(ownerId, "");

            if (ownerDetails == null || !Boolean.TRUE.equals(ownerDetails.get("exists"))) {
                logger.warn("Restaurant owner details not found. OwnerId: {}", ownerId);
                return;
            }

            // Extract owner email
            String ownerEmail = (String) ownerDetails.get("email");

            if (ownerEmail != null && !ownerEmail.isEmpty()) {
                notificationServiceClient.notifyRestaurantAboutNewOrder(
                        ownerEmail,
                        order.getId(),
                        order.getCustomerName());
            }
        } catch (Exception e) {
            logger.error("Failed to notify restaurant about order: {}", e.getMessage());
        }
    }
}
