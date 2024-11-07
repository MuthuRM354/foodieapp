package com.foodieapp.location.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "locations")
public class Location {
    @Id
    private String id;
    private String cityName;
    private String zomatoLocationId;
    private Double latitude;
    private Double longitude;

    // Constructor
    public Location(String cityName, String zomatoLocationId, Double latitude, Double longitude) {
        this.cityName = cityName;
        this.zomatoLocationId = zomatoLocationId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }
    public String getZomatoLocationId() { return zomatoLocationId; }
    public void setZomatoLocationId(String zomatoLocationId) { this.zomatoLocationId = zomatoLocationId; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}