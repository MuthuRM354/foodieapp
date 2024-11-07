package com.foodieapp.location.repository;

import com.foodieapp.location.model.Location;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LocationRepository extends MongoRepository<Location, Long> {
    List<Location> findByNameContainingOrAddressContainingOrCityContaining(
            String name, String address, String city);

    List<Location> findByCity(String city);

    List<Location> findBypinCode(String pinCode);

    @Query("{'location': {$near: {$geometry: {type: 'Point', coordinates: [?0, ?1]}, $maxDistance: ?2}}}")
    List<Location> findNearbyLocations(Double longitude, Double latitude, Double radius);

    List<Location> findByIsActiveTrue();
}
