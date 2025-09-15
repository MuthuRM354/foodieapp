package com.foodieapp.restaurant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RestaurantServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(RestaurantServiceApplication.class, args);
	}

	public void init() {
		// Initialize restaurant service components
		System.out.println("Restaurant Service initialized");
	}
}
