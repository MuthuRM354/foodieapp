package com.foodieapp.order.util;

import com.foodieapp.order.model.CartItem;
import com.foodieapp.order.model.OrderItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service to consolidate price calculation logic for Cart and Order
 */
@Component
public class PriceCalculationService {

    private final PriceCalculationUtil priceCalculationUtil;

    public PriceCalculationService(PriceCalculationUtil priceCalculationUtil) {
        this.priceCalculationUtil = priceCalculationUtil;
    }

    /**
     * Calculate totals for a list of cart items
     */
    public PriceBreakdown calculateCartTotals(List<CartItem> items) {
        BigDecimal subtotal = items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return calculatePriceBreakdown(subtotal);
    }

    /**
     * Calculate totals for a list of order items
     */
    public PriceBreakdown calculateOrderTotals(List<OrderItem> items) {
        BigDecimal subtotal = items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return calculatePriceBreakdown(subtotal);
    }

    /**
     * Common method to calculate price breakdown
     */
    private PriceBreakdown calculatePriceBreakdown(BigDecimal subtotal) {
        BigDecimal tax = PriceCalculationUtil.calculateTax(subtotal);
        BigDecimal deliveryFee = PriceCalculationUtil.getDeliveryFee();
        BigDecimal total = PriceCalculationUtil.calculateTotal(subtotal, tax, deliveryFee, null);

        return new PriceBreakdown(subtotal, tax, deliveryFee, total);
    }

    /**
     * Class to hold price breakdown components
     */
    public static class PriceBreakdown {
        private final BigDecimal subtotal;
        private final BigDecimal tax;
        private final BigDecimal deliveryFee;
        private final BigDecimal totalAmount;

        public PriceBreakdown(BigDecimal subtotal, BigDecimal tax, BigDecimal deliveryFee, BigDecimal totalAmount) {
            this.subtotal = subtotal;
            this.tax = tax;
            this.deliveryFee = deliveryFee;
            this.totalAmount = totalAmount;
        }

        public BigDecimal getSubtotal() {
            return subtotal;
        }

        public BigDecimal getTax() {
            return tax;
        }

        public BigDecimal getDeliveryFee() {
            return deliveryFee;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }
    }
}