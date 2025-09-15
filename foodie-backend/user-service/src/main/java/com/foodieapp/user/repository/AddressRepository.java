package com.foodieapp.user.repository;

import com.foodieapp.user.model.Address;
import com.foodieapp.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, String> {
    List<Address> findByUser(User user);
    List<Address> findByUserAndDefaultAddressTrue(User user);
    Address findByIdAndUser(String id, User user);
}
