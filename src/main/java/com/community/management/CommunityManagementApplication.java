package com.community.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching; // Add this import
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // To enable automatic setting of created_at and updated_at
@EnableCaching // Enable caching
public class CommunityManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommunityManagementApplication.class, args);
    }

}