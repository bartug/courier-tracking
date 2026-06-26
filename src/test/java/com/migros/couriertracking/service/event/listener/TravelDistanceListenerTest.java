package com.migros.couriertracking.service.event.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.migros.couriertracking.repository.CourierStateRepository;
import com.migros.couriertracking.repository.InMemoryCourierStateRepository;
import com.migros.couriertracking.service.distance.HaversineDistanceCalculator;
import com.migros.couriertracking.service.event.CourierLocationEvent;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * <h1>Toplam Mesafe Dinleyicisi Testleri</h1>
 * <p>Ardışık konumlar arasındaki mesafenin doğru biriktiğini ve farklı kuryelerin birbirinden
 * bağımsız takip edildiğini doğrular. Beklenen toplam değerler bağımsız olarak hesaplanmıştır.</p>
 *
 * @author Bartug Sevindik
 * @since 24.06.2026
 */
class TravelDistanceListenerTest {

    private CourierStateRepository courierStateRepository;
    private TravelDistanceListener travelDistanceListener;

    @BeforeEach
    void setUp() {
        courierStateRepository = new InMemoryCourierStateRepository();
        travelDistanceListener = new TravelDistanceListener(courierStateRepository, new HaversineDistanceCalculator());
    }

    @Test
    void testFirstLocationHasZeroDistance() {
        travelDistanceListener.onCourierLocation(event("courier-1", 40.9900000, 29.1200000));

        double total = courierStateRepository.findByCourierId("courier-1").orElseThrow().getTotalDistanceMeters();
        assertEquals(0.0, total, 1e-9, "İlk konumda toplam mesafe sıfır olmalı");
    }

    @Test
    void testAccumulatesDistanceOverPath() {
        // A 4-point path; the total (~40183.02 m) is cross-checked independently.
        travelDistanceListener.onCourierLocation(event("courier-1", 40.9900000, 29.1200000));
        travelDistanceListener.onCourierLocation(event("courier-1", 40.9923307, 29.1244229));
        travelDistanceListener.onCourierLocation(event("courier-1", 40.9861060, 29.1161293));
        travelDistanceListener.onCourierLocation(event("courier-1", 41.0066851, 28.6552262));

        double total = courierStateRepository.findByCourierId("courier-1").orElseThrow().getTotalDistanceMeters();
        assertEquals(40_183.0183, total, 2.0);
    }

    @Test
    void testCouriersAreTrackedIndependently() {
        travelDistanceListener.onCourierLocation(event("courier-1", 40.9900000, 29.1200000));
        travelDistanceListener.onCourierLocation(event("courier-1", 40.9923307, 29.1244229));
        travelDistanceListener.onCourierLocation(event("courier-2", 41.0066851, 28.6552262));

        double courier1 = courierStateRepository.findByCourierId("courier-1").orElseThrow().getTotalDistanceMeters();
        double courier2 = courierStateRepository.findByCourierId("courier-2").orElseThrow().getTotalDistanceMeters();

        assertEquals(452.7344, courier1, 1.0);
        assertEquals(0.0, courier2, 1e-9, "Yeni kurye sıfır mesafe ile başlamalı");
    }

    private CourierLocationEvent event(String courierId, double lat, double lng) {
        return new CourierLocationEvent(courierId, lat, lng, Instant.parse("2026-06-22T16:00:00Z"));
    }
}
