package com.foodieapp.restaurant.config;

import com.foodieapp.restaurant.interceptor.RestaurantOwnerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final RestaurantOwnerInterceptor restaurantOwnerInterceptor;

    @Autowired
    public WebMvcConfig(RestaurantOwnerInterceptor restaurantOwnerInterceptor) {
        this.restaurantOwnerInterceptor = restaurantOwnerInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Only apply to owner-specific endpoints
        registry.addInterceptor(restaurantOwnerInterceptor)
                .addPathPatterns("/api/v1/owner/restaurants/**");
    }
}
