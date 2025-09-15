package com.foodieapp.user.service.address;

import com.foodieapp.user.dto.request.AddressRequest;
import com.foodieapp.user.dto.response.AddressDTO;
import com.foodieapp.user.model.User;
import java.util.List;

public interface AddressService {
    AddressDTO createAddress(User user, AddressRequest addressRequest);
    AddressDTO updateAddress(User user, String addressId, AddressRequest addressRequest);
    void deleteAddress(User user, String addressId);
    AddressDTO getAddress(User user, String addressId);
    List<AddressDTO> getAllAddressesByUser(User user);
    AddressDTO setDefaultAddress(User user, String addressId);
}
