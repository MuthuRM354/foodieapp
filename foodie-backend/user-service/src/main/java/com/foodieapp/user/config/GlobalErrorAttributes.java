package com.foodieapp.user.config;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);

        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", errorAttributes.getOrDefault("message", "An unexpected error occurred"));
        response.put("status", errorAttributes.get("status"));

        Map<String, Object> error = new HashMap<>();
        error.put("code", errorAttributes.getOrDefault("error", "INTERNAL_SERVER_ERROR"));
        error.put("description", errorAttributes.getOrDefault("message", "An unexpected error occurred"));

        response.put("error", error);

        return response;
    }
}