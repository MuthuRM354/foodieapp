package com.foodieapp.location.service;

import com.foodieapp.location.model.Location;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

@Service
public class ZomatoIntegrationService {

    @Value("${zomato.api.key}")
    private String apiKey;

    private final String ZOMATO_LOCATION_URL = "https://developers.zomato.com/api/v2.1/locations";
    private final RestTemplate restTemplate = new RestTemplate();

    public Location fetchLocationFromZomato(String cityName) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("user-key", apiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            String url = ZOMATO_LOCATION_URL + "?query=" + cityName;
            // Call Zomato API and map response
            // This is a simplified version - you'll need to map the actual Zomato response
            return restTemplate.exchange(url, HttpMethod.GET, entity, Location.class).getBody();
        } catch (Exception e) {
            return null;
        }
    }
}