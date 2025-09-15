package com.foodieapp.order.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Component
public class PriceCalculationUtil {

    private static BigDecimal taxPercentage;
    private static BigDecimal deliveryFee;

    @Value("${order.tax.percentage}")
    public void setTaxPercentage(double taxPercentage) {
        PriceCalculationUtil.taxPercentage = BigDecimal.valueOf(taxPercentage).divide(
                BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    @Value("${order.delivery.fee}")
    public void setDeliveryFee(BigDecimal deliveryFee) {
        PriceCalculationUtil.deliveryFee = deliveryFee;
    }

    /**
     * Calculate subtotal from price and quantity
     */
    public static BigDecimal calculateSubtotal(BigDecimal unitPrice, int quantity) {
        if (unitPrice == null) {
            return BigDecimal.ZERO;
        }
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    /**
     * Calculate subtotal including add-ons
     */
    public static BigDecimal calculateSubtotalWithAddons(BigDecimal unitPrice, int quantity, Map<String, BigDecimal> addOns) {
        BigDecimal addOnsTotal = BigDecimal.ZERO;

        if (addOns != null && !addOns.isEmpty()) {
            addOnsTotal = addOns.values().stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        return unitPrice.add(addOnsTotal).multiply(BigDecimal.valueOf(quantity));
    }

    /**
     * Calculate tax for a given amount
     */
    public static BigDecimal calculateTax(BigDecimal amount) {
        if (taxPercentage == null) {
            // Default to 10% if not configured
            return amount.multiply(new BigDecimal("0.10"));
        }
        return amount.multiply(taxPercentage);
    }

    /**
     * Get configured delivery fee
     */
    public static BigDecimal getDeliveryFee() {
        return deliveryFee != null ? deliveryFee : new BigDecimal("2.99");
    }

    /**
     * Calculate total order price
     */
    public static BigDecimal calculateTotal(BigDecimal subtotal, BigDecimal tax, BigDecimal deliveryFee, BigDecimal discount) {
        BigDecimal total = subtotal.add(tax).add(deliveryFee);

        if (discount != null) {
            total = total.subtract(discount);
        }

        return total;
    }
}
