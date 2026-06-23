package com.migros.couriertracking;

import com.migros.couriertracking.config.CourierTrackingProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * <h1>Courier Tracking Uygulaması</h1>
 * <p>Kuryelerin akan (streaming) konum verilerini işleyen Spring Boot uygulamasının giriş
 * noktasıdır. Uygulama; Migros mağazalarına 100 metre yaklaşıldığında giriş kaydı oluşturur ve
 * her kuryenin kat ettiği toplam mesafeyi bellekte tutar.</p>
 *
 * @author Bartug Sevindik
 * @since 22.06.2026
 */
@SpringBootApplication
@EnableConfigurationProperties(CourierTrackingProperties.class)
public class CourierTrackingApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourierTrackingApplication.class, args);
    }
}
