package com.foodieapp.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class OrderServiceApplication {
	private static final Logger logger = LoggerFactory.getLogger(OrderServiceApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

	@Component
	static class StartupLogger {
		private final Environment environment;

		StartupLogger(Environment environment) {
			this.environment = environment;
		}

		@EventListener
		public void onApplicationStarted(ApplicationStartedEvent event) {
			logger.info("===============================================================");
			logger.info("              ORDER SERVICE STARTED SUCCESSFULLY              ");
			logger.info("===============================================================");
			logger.info("Service Configuration:");
			logger.info("User Service URL: {}", environment.getProperty("user.service.url"));
			logger.info("Restaurant Service URL: {}", environment.getProperty("restaurant.service.url"));
			logger.info("MongoDB Host: {}", environment.getProperty("spring.data.mongodb.host"));
			logger.info("MongoDB Database: {}", environment.getProperty("spring.data.mongodb.database"));
			logger.info("Server Port: {}", environment.getProperty("server.port"));
			logger.info("===============================================================");
		}
	}
}
