package com.foodieapp.order.dto.request;

import com.foodieapp.order.validation.ValidationConstants;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public abstract class BaseItemRequest {
  @NotBlank(message = ValidationConstants.ITEM_ID_REQUIRED)
  private String itemId;

  @NotBlank(message = ValidationConstants.NAME_REQUIRED)
  @Size(min = ValidationConstants.MIN_NAME_LENGTH, max = ValidationConstants.MAX_NAME_LENGTH,
        message = ValidationConstants.NAME_SIZE)
  private String name;

  @NotNull(message = "Quantity is required")
  @Min(value = ValidationConstants.MIN_QUANTITY, message = ValidationConstants.QUANTITY_POSITIVE)
  private Integer quantity;

  @NotNull(message = "Price is required")
  @DecimalMin(value = "0.0", inclusive = true, message = ValidationConstants.PRICE_POSITIVE)
  private BigDecimal price;

  @ValidationConstants.SpecialInstructions
  private String specialInstructions;

  private String category;
  private String itemSize;

  // Getters and Setters
  public String getItemId() { return itemId; }
  public void setItemId(String itemId) { this.itemId = itemId; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public Integer getQuantity() { return quantity; }
  public void setQuantity(Integer quantity) { this.quantity = quantity; }

  public BigDecimal getPrice() { return price; }
  public void setPrice(BigDecimal price) { this.price = price; }

  public String getSpecialInstructions() { return specialInstructions; }
  public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }

  public String getCategory() { return category; }
  public void setCategory(String category) { this.category = category; }

  public String getItemSize() { return itemSize; }
  public void setItemSize(String itemSize) { this.itemSize = itemSize; }
}