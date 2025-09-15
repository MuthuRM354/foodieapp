package com.foodieapp.restaurant.config;

import com.foodieapp.restaurant.model.Restaurant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableMongoRepositories(basePackages = "com.foodieapp.restaurant.repository")
public class MongoConfig {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        // Add any custom converters here
        return new MongoCustomConversions(converters);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initIndicesAfterStartup() {
        // Get mongoTemplate from application context after it's fully initialized
        MongoTemplate mongoTemplate = applicationContext.getBean(MongoTemplate.class);

        // Create a 2dsphere index on the location field for geospatial queries
        mongoTemplate.indexOps(Restaurant.class)
            .ensureIndex(new GeospatialIndex("location").typed(GeoSpatialIndexType.GEO_2DSPHERE));

        // Create indices for other common queries
        mongoTemplate.indexOps(Restaurant.class)
            .ensureIndex(new Index().on("isActive", Sort.Direction.ASC).on("isVerified", Sort.Direction.ASC));

        mongoTemplate.indexOps(Restaurant.class)
            .ensureIndex(new Index().on("avgRating", Sort.Direction.DESC));

        mongoTemplate.indexOps(Restaurant.class)
            .ensureIndex(new Index().on("createdAt", Sort.Direction.DESC));
    }
}
