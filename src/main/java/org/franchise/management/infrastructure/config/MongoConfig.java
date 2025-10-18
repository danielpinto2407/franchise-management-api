package org.franchise.management.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
@EnableReactiveMongoAuditing
public class MongoConfig {

    @Bean
    CommandLineRunner testConnection(ReactiveMongoTemplate mongoTemplate) {
        return args -> {
            log.info("🔄 Testing MongoDB connection...");
            mongoTemplate.getCollectionNames()
                    .doOnNext(name -> log.info("Collection found: {}", name))
                    .doOnComplete(() -> log.info("MongoDB Atlas connection successful!"))
                    .doOnError(error -> log.error("MongoDB connection failed: {}", error.getMessage()))
                    .subscribe();
        };
    }

    @Bean
    public ReactiveAuditorAware<String> auditorProvider() {
        return () -> Mono.just("system");
    }
}