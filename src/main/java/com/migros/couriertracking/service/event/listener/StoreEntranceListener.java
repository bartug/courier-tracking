package com.migros.couriertracking.service.event.listener;

import com.migros.couriertracking.config.CourierTrackingProperties;
import com.migros.couriertracking.domain.Store;
import com.migros.couriertracking.domain.StoreEntrance;
import com.migros.couriertracking.repository.StoreEntranceRepository;
import com.migros.couriertracking.service.catalog.StoreCatalog;
import com.migros.couriertracking.service.distance.DistanceCalculator;
import com.migros.couriertracking.service.event.CourierLocationEvent;
import com.migros.couriertracking.service.event.CourierLocationListener;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <h1>Mağaza Giriş Dinleyicisi</h1>
 * <p>Observer Design Pattern dinleyicisidir. Gelen her konum olayında, kuryenin konfigüre edilen
 * yarıçap (varsayılan 100 m) içine girdiği mağazaları tespit eder, girişi loglar ve depoya kaydeder.</p>
 *
 * <p><b>Tekrar giriş kuralı:</b> Aynı kurye-mağaza ikilisi için bir giriş sayıldıktan sonra, tekrar
 * giriş penceresi (varsayılan 60 sn) dolmadan gelen bildirimler yeni giriş sayılmaz. Karar; cüzdan
 * saatine (wall-clock) değil, olayın <i>kendi zaman damgasına</i> göre verilir; böylece akış yeniden
 * oynatıldığında (replay) veya gecikmeli geldiğinde de sonuç tutarlı kalır. Son sayılan giriş
 * zamanları, eşzamanlı bildirimlere karşı {@link ConcurrentHashMap} üzerinde atomik {@code compute(...)}
 * ile güncellenir.</p>
 *
 * @author Bartug Sevindik
 * @since 23.06.2026
 */
@Slf4j
@Component
public class StoreEntranceListener implements CourierLocationListener {

    private final StoreCatalog storeCatalog;
    private final DistanceCalculator distanceCalculator;
    private final StoreEntranceRepository storeEntranceRepository;
    private final double entranceRadiusMeters;
    private final Duration reentryWindow;

    /** Anahtar: "courierId::storeName", Değer: son sayılan girişin zaman damgası. */
    private final Map<String, Instant> lastEntranceByKey = new ConcurrentHashMap<>();

    public StoreEntranceListener(StoreCatalog storeCatalog,
                                 DistanceCalculator distanceCalculator,
                                 StoreEntranceRepository storeEntranceRepository,
                                 CourierTrackingProperties properties) {
        this.storeCatalog = storeCatalog;
        this.distanceCalculator = distanceCalculator;
        this.storeEntranceRepository = storeEntranceRepository;
        this.entranceRadiusMeters = properties.getStore().getEntranceRadiusMeters();
        this.reentryWindow = Duration.ofSeconds(properties.getStore().getReentryWindowSeconds());
    }

    @Override
    public void onCourierLocation(CourierLocationEvent event) {
        for (Store store : storeCatalog.getStores()) {
            double distance = distanceCalculator.distanceInMeters(
                    event.getLatitude(), event.getLongitude(), store.getLat(), store.getLng());

            if (distance <= entranceRadiusMeters) {
                registerEntranceIfNotDebounced(event, store, distance);
            }
        }
    }

    /**
     * <h1>Girişi Değerlendir</h1>
     * <p>Kurye-mağaza ikilisi için tekrar giriş penceresini kontrol eder; pencere dolmuşsa (ya da
     * ilk giriş ise) girişi sayar, loglar ve depoya kaydeder. Kontrol ve güncelleme tek adımda,
     * {@code compute} ile atomik olarak yapılır.</p>
     *
     * @param event    konum olayı
     * @param store    yarıçap içine girilen mağaza
     * @param distance kuryenin mağazaya uzaklığı (metre)
     */
    private void registerEntranceIfNotDebounced(CourierLocationEvent event, Store store, double distance) {
        String key = event.getCourierId() + "::" + store.getName();
        Instant eventTime = event.getTimestamp();
        boolean[] counted = {false};

        lastEntranceByKey.compute(key, (k, lastTime) -> {
            if (lastTime == null || Duration.between(lastTime, eventTime).compareTo(reentryWindow) >= 0) {
                counted[0] = true;
                return eventTime; // yeni giriş sayıldı
            }
            return lastTime; // pencere içinde -> yok say
        });

        if (counted[0]) {
            log.info("GİRİŞ | kurye={} mağaza='{}' mesafe={} m an={}",
                    event.getCourierId(), store.getName(), Math.round(distance), eventTime);
            storeEntranceRepository.save(StoreEntrance.builder()
                    .courierId(event.getCourierId())
                    .storeName(store.getName())
                    .distanceMeters(distance)
                    .enteredAt(eventTime)
                    .build());
        }
    }
}
