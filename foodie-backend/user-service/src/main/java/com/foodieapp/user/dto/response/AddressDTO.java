package com.foodieapp.user.dto.response;

import com.foodieapp.user.model.Address;

public class AddressDTO {
    private String id;  // Changed from Long to String
    private String streetAddress;
    private String city;
    private String state;
    private String postalCode;
    private String landmark;
    private String addressType;
    private String recipientName;
    private String recipientPhoneNumber;
    private String deliveryInstructions;
    private boolean defaultAddress;

    // Default constructor
    public AddressDTO() {
    }

    // Constructor from Address entity
    public AddressDTO(Address address) {
        this.id = address.getId().toString();  // Convert Long to String
        this.streetAddress = address.getStreetAddress();
        this.city = address.getCity();
        this.state = address.getState();
        this.postalCode = address.getPostalCode();
        this.landmark = address.getLandmark();
        this.addressType = address.getAddressType();
        this.recipientName = address.getRecipientName();
        this.recipientPhoneNumber = address.getRecipientPhoneNumber();
        this.deliveryInstructions = address.getDeliveryInstructions();
        this.defaultAddress = address.isDefaultAddress();
    }

    // Getters and setters
    public String getId() {  // Changed return type from Long to String
        return id;
    }

    public void setId(String id) {  // Changed parameter type from Long to String
        this.id = id;
    }

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
