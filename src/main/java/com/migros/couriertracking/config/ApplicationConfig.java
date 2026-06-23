package com.migros.couriertracking.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <h1>Uygulama Geneli Bean Tanımları</h1>
 * <p>Test edilebilirliği artırmak amacıyla zaman erişimini soyutlayan {@link Clock} gibi altyapı
 * bean'lerinin tanımlandığı konfigürasyon sınıfıdır.</p>
 *
 * @author Bartug Sevindik
 * @since 23.06.2026
 */
@Configuration
public class ApplicationConfig {

    /**
     * <h1>Sistem Saati</h1>
     * <p>Konum isteğinde zaman damgası (timestamp) gelmediği durumlarda kullanılacak UTC saat
     * kaynağını sağlar. Testlerde sabit bir {@link Clock} ile değiştirilebilir.</p>
     *
     * @return UTC tabanlı sistem saati
     * @since 23.06.2026
     */
    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
