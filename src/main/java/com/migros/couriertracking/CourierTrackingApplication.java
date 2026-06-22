package com.migros.couriertracking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <h1>Courier Tracking Uygulaması</h1>
 * <p>Kuryelerin akan (streaming) konum verilerini işleyecek olan Spring Boot uygulamasının giriş
 * noktasıdır.</p>
 *
 * @author Bartug Sevindik
 * @since 22.06.2026
 */
@SpringBootApplication
public class CourierTrackingApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourierTrackingApplication.class, args);
    }
}
