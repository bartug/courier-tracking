package com.migros.couriertracking.service.event.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.migros.couriertracking.config.CourierTrackingProperties;
import com.migros.couriertracking.domain.Store;
import com.migros.couriertracking.repository.InMemoryStoreEntranceRepository;
import com.migros.couriertracking.repository.StoreEntranceRepository;
import com.migros.couriertracking.service.catalog.StoreCatalog;
import com.migros.couriertracking.service.distance.HaversineDistanceCalculator;
import com.migros.couriertracking.service.event.CourierLocationEvent;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * <h1>Mağaza Giriş Dinleyicisi Testleri</h1>
 * <p>100 metre giriş yarıçapının ve 1 dakikalık tekrar giriş kuralının doğru çalıştığını doğrular.
 * Tekrar giriş kararı olayın zaman damgasına göre verildiğinden, testler gerçek zamanı beklemeden
 * (sleep'siz), tamamen deterministik biçimde zaman damgaları üzerinden çalışır.</p>
 *
 * @author Bartug Sevindik
 * @since 24.06.2026
 */
@ExtendWith(MockitoExtension.class)
class StoreEntranceListenerTest {

    private static final String COURIER_ID = "courier-1";
    private static final Store ATASEHIR = Store.builder()
            .name("Ataşehir MMM Migros").lat(40.9923307).lng(29.1244229).build();

    @Mock
    private StoreCatalog storeCatalog;

    private StoreEntranceRepository storeEntranceRepository;
    private StoreEntranceListener storeEntranceListener;

    @BeforeEach
    void setUp() {
        storeEntranceRepository = new InMemoryStoreEntranceRepository();

        CourierTrackingProperties properties = new CourierTrackingProperties();
        properties.getStore().setEntranceRadiusMeters(100.0);
        properties.getStore().setReentryWindowSeconds(60L);

        storeEntranceListener = new StoreEntranceListener(
                storeCatalog, new HaversineDistanceCalculator(), storeEntranceRepository, properties);
    }

    @Test
    void testEntranceIsLoggedWhenWithinRadius() {
        when(storeCatalog.getStores()).thenReturn(List.of(ATASEHIR));

        // Right on top of the store -> clearly within the 100 m radius.
        storeEntranceListener.onCourierLocation(event(ATASEHIR.getLat(), ATASEHIR.getLng(), "2026-06-22T16:00:00Z"));

        assertEquals(1, storeEntranceRepository.findByCourierId(COURIER_ID).size());
    }

    @Test
    void testNoEntranceWhenOutsideRadius() {
        when(storeCatalog.getStores()).thenReturn(List.of(ATASEHIR));

        // ~111 m north of the store (+0.001 deg latitude) -> outside the 100 m radius.
        storeEntranceListener.onCourierLocation(event(ATASEHIR.getLat() + 0.001, ATASEHIR.getLng(), "2026-06-22T16:00:00Z"));

        assertEquals(0, storeEntranceRepository.findByCourierId(COURIER_ID).size());
    }

    @Test
    void testReentryWithinOneMinuteIsNotCounted() {
        when(storeCatalog.getStores()).thenReturn(List.of(ATASEHIR));

        // First entrance at 16:00:00, second one 30 seconds later -> still a single entrance.
        storeEntranceListener.onCourierLocation(event(ATASEHIR.getLat(), ATASEHIR.getLng(), "2026-06-22T16:00:00Z"));
        storeEntranceListener.onCourierLocation(event(ATASEHIR.getLat(), ATASEHIR.getLng(), "2026-06-22T16:00:30Z"));

        assertEquals(1, storeEntranceRepository.findByCourierId(COURIER_ID).size(),
                "1 dakika içindeki tekrar giriş sayılmamalı");
    }

    @Test
    void testReentryAfterOneMinuteIsCounted() {
        when(storeCatalog.getStores()).thenReturn(List.of(ATASEHIR));

        // Two entrances 61 seconds apart -> both are counted.
        storeEntranceListener.onCourierLocation(event(ATASEHIR.getLat(), ATASEHIR.getLng(), "2026-06-22T16:00:00Z"));
        storeEntranceListener.onCourierLocation(event(ATASEHIR.getLat(), ATASEHIR.getLng(), "2026-06-22T16:01:01Z"));

        assertEquals(2, storeEntranceRepository.findByCourierId(COURIER_ID).size(),
                "1 dakikadan sonraki tekrar giriş yeni giriş sayılmalı");
    }

    private CourierLocationEvent event(double lat, double lng, String isoTimestamp) {
        return new CourierLocationEvent(COURIER_ID, lat, lng, Instant.parse(isoTimestamp));
    }
}
