package com.migros.couriertracking.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <h1>Courier Tracking Ayarları</h1>
 * <p><code>courier-tracking.*</code> ön eki ile <code>application.yml</code> üzerinden yönetilen
 * uygulama ayarlarını taşır. Giriş yarıçapının ve tekrar giriş penceresinin koda gömülmeyip dış
 * konfigürasyondan okunmasını sağlar; böylece eşik değerleri yeniden derleme olmadan
 * değiştirilebilir.</p>
 *
 * @author Bartug Sevindik
 * @since 23.06.2026
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "courier-tracking")
public class CourierTrackingProperties {

    /** Mağaza giriş/yakınlık kurallarının ayarları. */
    private Store store = new Store();

    /** Mağaza konumlarının okunacağı kaynak (Spring Resource ifadesi, ör. classpath:stores.json). */
    private String storesFile = "classpath:stores.json";

    /**
     * <h1>Mağaza Kuralları</h1>
     * <p>Giriş yarıçapı ve tekrar giriş penceresi gibi mağaza bazlı eşik değerleri.</p>
     */
    @Getter
    @Setter
    public static class Store {

        /** Kuryenin mağazaya "girdi" sayılması için gereken azami yarıçap (metre). */
        private double entranceRadiusMeters = 100.0;

        /** Aynı mağazaya art arda girişlerde, yeni giriş sayılmaması gereken pencere (saniye). */
        private long reentryWindowSeconds = 60L;
    }
}
