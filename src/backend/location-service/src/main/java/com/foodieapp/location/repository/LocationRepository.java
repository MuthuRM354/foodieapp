package com.foodieapp.location.repository;

import com.foodieapp.location.model.Location;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LocationRepository extends MongoRepository<Location, String> {
    Location findByCityName(String cityName);
}
