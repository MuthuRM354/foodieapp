package com.foodieapp.location.service;

import com.foodieapp.location.model.Location;
import com.foodieapp.location.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LocationService {
    @Autowired
    private LocationRepository locationRepository;

    public Location createLocation(Location location) {
        return locationRepository.save(location);
    }

    public Location getLocationById(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found"));
    }

    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    public List<Location> searchLocations(String query) {
        return locationRepository.findByNameContainingOrAddressContainingOrCityContaining(
                query, query, query);
    }

    public Location updateLocation(Long id, Location locationDetails) {
        Location location = getLocationById(id);
        location.setName(locationDetails.getName());
        location.setAddress(locationDetails.getAddress());
        location.setArea(locationDetails.getArea());
        location.setLatitude(locationDetails.getLatitude());
        location.setLongitude(locationDetails.getLongitude());
        location.setCity(locationDetails.getCity());
        location.setState(locationDetails.getState());
        location.setCountry(locationDetails.getCountry());
        location.setpinCode(locationDetails.getpinCode());
        return locationRepository.save(location);
    }

    public void deleteLocation(Long id) {
        locationRepository.deleteById(id);
    }

    public List<Location> searchNearbyLocations(Double latitude, Double longitude, Double radius) {
        return locationRepository.findNearbyLocations(longitude, latitude, radius);
    }

    public List<Location> getLocationsByCity(String city) {
        return locationRepository.findByCity(city);
    }

    public List<Location> getLocationsBypinCode(String pinCode) {
        return locationRepository.findBypinCode(pinCode);
    }

    public List<Location> getActiveLocations() {
        return locationRepository.findByIsActiveTrue();
    }
}
