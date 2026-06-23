package com.migros.couriertracking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <h1>Swagger / OpenAPI Konfigürasyonu</h1>
 * <p>API dokümantasyonunun başlık, açıklama ve sürüm bilgilerini tanımlar. Uygulama ayağa
 * kalktıktan sonra arayüze <code>/swagger-ui.html</code> adresinden erişilebilir.</p>
 *
 * @author Bartug Sevindik
 * @since 23.06.2026
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI courierTrackingOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Migros Courier Tracking API")
                        .description("Kurye konum akışını işleyen; mağaza giriş tespiti ve toplam mesafe " +
                                "takibi yapan servisin REST dokümantasyonu.")
                        .version("1.0.0")
                        .contact(new Contact().name("Bartug Sevindik")));
    }
}
