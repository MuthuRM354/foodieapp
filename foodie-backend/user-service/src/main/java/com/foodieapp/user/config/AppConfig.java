package com.foodieapp.user.config;

import com.foodieapp.user.repository.UserRepository;
import com.foodieapp.user.service.auth.PasswordService;
import com.foodieapp.user.service.auth.PasswordServiceImpl;
import com.foodieapp.user.service.email.EmailService;
import com.foodieapp.user.service.verification.OtpService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Value("${app.rest-template.connect-timeout:5000}")
    private int connectTimeout;

    @Value("${app.rest-template.read-timeout:8000}")
    private int readTimeout;

    @Bean
    public PasswordService passwordService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            OtpService otpService,
            EmailService emailService) {
        return new PasswordServiceImpl(userRepository, passwordEncoder, otpService, emailService);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(clientHttpRequestFactory());
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);
        return factory;
    }
}
