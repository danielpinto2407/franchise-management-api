package org.franchise.management.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

@Slf4j
@Configuration
public class MongoConfig {

    @Bean
    CommandLineRunner testConnection(ReactiveMongoTemplate mongoTemplate) {
        return args -> {
            log.info("🔄 Testing MongoDB connection...");
            mongoTemplate.getCollectionNames()
                    .doOnNext(name -> log.info("📦 Collection found: {}", name))
                    .doOnComplete(() -> log.info("✅ MongoDB Atlas connection successful!"))
                    .doOnError(error -> log.error("❌ MongoDB connection failed: {}", error.getMessage()))
                    .subscribe();
        };
    }
}