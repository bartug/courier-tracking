package com.migros.couriertracking.service.distance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * <h1>Haversine Mesafe Hesaplayıcı Testleri</h1>
 * <p>{@link HaversineDistanceCalculator} için birim testleri. Bilinen referans değerlerle formülün
 * doğruluğu; ayrıca simetri ve uç durumlar (aynı nokta, kısa mesafe) doğrulanır. Referans değerler
 * bağımsız bir Haversine gerçekleştirimi ile (R = 6.371.000 m) çapraz kontrol edilmiştir.</p>
 *
 * @author Bartug Sevindik
 * @since 24.06.2026
 */
class HaversineDistanceCalculatorTest {

    private static final double DELTA_METERS = 0.5;

    private DistanceCalculator distanceCalculator;

    @BeforeEach
    void setUp() {
        distanceCalculator = new HaversineDistanceCalculator();
    }

    @Test
    void testSamePointReturnsZeroDistance() {
        double distance = distanceCalculator.distanceInMeters(40.9923307, 29.1244229, 40.9923307, 29.1244229);
        assertEquals(0.0, distance, 1e-6, "Aynı nokta için mesafe sıfır olmalı");
    }

    @Test
    void testOneDegreeOfLongitudeAtEquator() {
        // 1 degree of longitude at the equator is ~111.195 km.
        double distance = distanceCalculator.distanceInMeters(0.0, 0.0, 0.0, 1.0);
        assertEquals(111_194.9266, distance, DELTA_METERS);
    }

    @Test
    void testOneDegreeOfLatitude() {
        // 1 degree of latitude is ~111.195 km anywhere on the globe.
        double distance = distanceCalculator.distanceInMeters(0.0, 0.0, 1.0, 0.0);
        assertEquals(111_194.9266, distance, DELTA_METERS);
    }

    @Test
    void testKnownDistanceBetweenTwoStores() {
        // Ataşehir MMM Migros -> Novada MMM Migros
        double distance = distanceCalculator.distanceInMeters(40.9923307, 29.1244229, 40.986106, 29.1161293);
        assertEquals(981.6568, distance, 1.0);
    }

    @Test
    void testDistanceIsSymmetric() {
        double forward = distanceCalculator.distanceInMeters(40.9923307, 29.1244229, 41.055783, 29.0210292);
        double backward = distanceCalculator.distanceInMeters(41.055783, 29.0210292, 40.9923307, 29.1244229);
        assertEquals(forward, backward, 1e-9, "Mesafe iki yönde de aynı olmalı");
    }

    @Test
    void testShortDistanceStaysWithinHundredMeters() {
        // ~55.6 m north of the Ataşehir store (+0.0005 deg latitude).
        double distance = distanceCalculator.distanceInMeters(40.9923307, 29.1244229, 40.9928307, 29.1244229);
        assertTrue(distance < 100.0, "Beklenen mesafe 100 metrenin altında olmalı");
        assertEquals(55.5975, distance, DELTA_METERS);
    }
}
