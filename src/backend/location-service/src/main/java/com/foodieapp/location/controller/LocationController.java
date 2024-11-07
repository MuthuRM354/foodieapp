package com.foodieapp.location.controller;

import com.foodieapp.location.model.Location;
import com.foodieapp.location.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @GetMapping("/{city}")
    public ResponseEntity<Location> getLocationByCity(@PathVariable String city) {
        Location location = locationService.getLocationByCity(city);
        if (location != null) {
            return ResponseEntity.ok(location);
        }
        return ResponseEntity.notFound().build();
    }
}