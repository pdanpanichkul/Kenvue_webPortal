package com.vendorportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Vendor Portal application.
 * Run this class (or `mvn spring-boot:run`) to start the server on http://localhost:8080
 */
@SpringBootApplication
public class VendorPortalApplication {
    public static void main(String[] args) {
        SpringApplication.run(VendorPortalApplication.class, args);
    }
}
