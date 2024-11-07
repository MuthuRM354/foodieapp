package com.foodieapp.location.service;

import com.foodieapp.location.model.Location;
import com.foodieapp.location.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ZomatoIntegrationService zomatoService;

    public Location getLocationByCity(String cityName) {
        Location location = locationRepository.findByCityName(cityName);
        if (location == null) {
            location = zomatoService.fetchLocationFromZomato(cityName);
            if (location != null) {
                locationRepository.save(location);
            }
        }
        return location;
    }
}