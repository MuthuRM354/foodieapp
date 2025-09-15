package com.foodieapp.user.service.address;

import com.foodieapp.user.dto.request.AddressRequest;
import com.foodieapp.user.dto.response.AddressDTO;
import com.foodieapp.user.exception.ResourceNotFoundException;
import com.foodieapp.user.model.Address;
import com.foodieapp.user.model.User;
import com.foodieapp.user.repository.AddressRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService {
    private static final Logger logger = LoggerFactory.getLogger(AddressServiceImpl.class);

    private final AddressRepository addressRepository;

    public AddressServiceImpl(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    /**
     * Clear existing default addresses for a user
     */
    private void clearExistingDefaultAddresses(User user) {
        List<Address> defaultAddresses = addressRepository.findByUserAndDefaultAddressTrue(user);
        defaultAddresses.forEach(a -> a.setDefaultAddress(false));
        addressRepository.saveAll(defaultAddresses);
    }

    /**
     * Helper method to find an address by ID and user
     */
    private Address findAddressByIdAndUser(String addressId, User user) {
        Address address = addressRepository.findByIdAndUser(addressId, user);
        if (address == null) {
            throw new ResourceNotFoundException("Address not found or you don't have permission to access it");
        }
        return address;
    }

    @Override
    @Transactional
    public AddressDTO createAddress(User user, AddressRequest addressRequest) {
        Address address = new Address();
        address.setUser(user);
        address.setStreetAddress(addressRequest.getStreetAddress());
        address.setCity(addressRequest.getCity());
        address.setState(addressRequest.getState());
        address.setPostalCode(addressRequest.getPostalCode());
        address.setLandmark(addressRequest.getLandmark());
        address.setAddressType(addressRequest.getAddressType());
        address.setRecipientName(addressRequest.getRecipientName());
        address.setRecipientPhoneNumber(addressRequest.getRecipientPhoneNumber());
        address.setDeliveryInstructions(addressRequest.getDeliveryInstructions());

        // If this is the first address or if requested as default, make it default
        boolean isDefault = addressRequest.isDefaultAddress();
        if (isDefault) {
            clearExistingDefaultAddresses(user);
        }
        address.setDefaultAddress(isDefault);

        Address savedAddress = addressRepository.save(address);
        logger.info("Created new address for user: {}", user.getId());
        return new AddressDTO(savedAddress);
    }

    @Override
    @Transactional
    public AddressDTO updateAddress(User user, String addressId, AddressRequest addressRequest) {
        Address address = findAddressByIdAndUser(addressId, user);

        address.setStreetAddress(addressRequest.getStreetAddress());
        address.setCity(addressRequest.getCity());
        address.setState(addressRequest.getState());
        address.setPostalCode(addressRequest.getPostalCode());
        address.setLandmark(addressRequest.getLandmark());
        address.setAddressType(addressRequest.getAddressType());
        address.setRecipientName(addressRequest.getRecipientName());
        address.setRecipientPhoneNumber(addressRequest.getRecipientPhoneNumber());
        address.setDeliveryInstructions(addressRequest.getDeliveryInstructions());

        boolean isDefault = addressRequest.isDefaultAddress();
        if (isDefault && !address.isDefaultAddress()) {
            // Setting as default when it wasn't before
            clearExistingDefaultAddresses(user);
            address.setDefaultAddress(true);
        } else if (!isDefault && address.isDefaultAddress()) {
            // Check if this is the only address
            List<Address> userAddresses = addressRepository.findByUser(user);
            // Don't allow removing default status from the only address
            address.setDefaultAddress(userAddresses.size() == 1);
        }

        Address savedAddress = addressRepository.save(address);
        logger.info("Updated address {} for user: {}", addressId, user.getId());
        return new AddressDTO(savedAddress);
    }

    @Override
    @Transactional
    public void deleteAddress(User user, String addressId) {
        Address address = findAddressByIdAndUser(addressId, user);

        // If this is a default address and there are other addresses, make another one default
        if (address.isDefaultAddress()) {
            List<Address> userAddresses = addressRepository.findByUser(user);
            if (userAddresses.size() > 1) {
                // Find another address to make default
                userAddresses.stream()
                        .filter(a -> !a.getId().equals(addressId))
                        .findFirst()
                        .ifPresent(a -> {
                            a.setDefaultAddress(true);
                            addressRepository.save(a);
                        });
            }
        }

        addressRepository.delete(address);
        logger.info("Deleted address {} for user: {}", addressId, user.getId());
    }

    @Override
    public AddressDTO getAddress(User user, String addressId) {
        Address address = findAddressByIdAndUser(addressId, user);
        return new AddressDTO(address);
    }

    @Override
    public List<AddressDTO> getAllAddressesByUser(User user) {
        List<Address> addresses = addressRepository.findByUser(user);
        return addresses.stream()
                .map(AddressDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AddressDTO setDefaultAddress(User user, String addressId) {
        Address address = findAddressByIdAndUser(addressId, user);

        // Already default, no action needed
        if (address.isDefaultAddress()) {
            return new AddressDTO(address);
        }

        // Clear existing default addresses
        clearExistingDefaultAddresses(user);

        // Set new default
        address.setDefaultAddress(true);
        Address savedAddress = addressRepository.save(address);
        logger.info("Set address {} as default for user: {}", addressId, user.getId());

        return new AddressDTO(savedAddress);
    }
}
