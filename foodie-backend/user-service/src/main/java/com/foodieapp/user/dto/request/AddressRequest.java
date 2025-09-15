package com.foodieapp.user.dto.request;

import com.foodieapp.user.validation.ValidationGroups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class AddressRequest {
    @NotBlank(message = "Street address is required", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String streetAddress;

    @NotBlank(message = "City is required", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String city;

    @NotBlank(message = "State is required", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String state;

    @NotBlank(message = "Postal code is required", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String postalCode;

    private String landmark;

    @NotBlank(message = "Address type is required", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String addressType; // HOME, WORK, FRIEND, FAMILY, OTHER

    private String recipientName; // Required for FRIEND, FAMILY addresses

    @Pattern(regexp = "^$|^\\+?[0-9]{10,15}$", message = "Phone number should be between 10 and 15 digits",
            groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String recipientPhoneNumber; // Required for FRIEND, FAMILY addresses

    private String deliveryInstructions;

    private boolean defaultAddress;

    // Default constructor
    public AddressRequest() {
    }

    // Getters and setters
    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientPhoneNumber() {
        return recipientPhoneNumber;
    }

    public void setRecipientPhoneNumber(String recipientPhoneNumber) {
        this.recipientPhoneNumber = recipientPhoneNumber;
    }

    public String getDeliveryInstructions() {
        return deliveryInstructions;
    }

    public void setDeliveryInstructions(String deliveryInstructions) {
        this.deliveryInstructions = deliveryInstructions;
    }

    public boolean isDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(boolean defaultAddress) {
        this.defaultAddress = defaultAddress;
    }
}
