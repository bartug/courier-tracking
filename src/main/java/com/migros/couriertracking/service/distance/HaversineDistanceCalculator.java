package com.migros.couriertracking.service.distance;

import org.springframework.stereotype.Component;

/**
 * <h1>Haversine Mesafe Hesaplayıcı</h1>
 * <p>{@link DistanceCalculator} arayüzünün Haversine formülü ile gerçekleştirimidir. İki nokta
 * arasındaki kuş uçuşu mesafeyi, dünyanın küreselliğini hesaba katarak hesaplar; bu sayede düz
 * (Öklit) mesafenin aksine coğrafi koordinatlarda doğru sonuç verir.</p>
 *
 * @author Bartug Sevindik
 * @since 23.06.2026
 */
@Component
public class HaversineDistanceCalculator implements DistanceCalculator {

    /** Dünyanın ortalama yarıçapı (metre). */
    private static final double EARTH_RADIUS_METERS = 6_371_000.0;

    /**
     * {@inheritDoc}
     * <p>Enlem/boylam farkları radyana çevrilir ve Haversine formülü uygulanır. {@code sqrt}
     * sonucu, kayan nokta hatalarına karşı {@code [0,1]} aralığına sıkıştırılır.</p>
     */
    @Override
    public double distanceInMeters(double latitude1, double longitude1, double latitude2, double longitude2) {
        double lat1Rad = Math.toRadians(latitude1);
        double lat2Rad = Math.toRadians(latitude2);
        double deltaLat = Math.toRadians(latitude2 - latitude1);
        double deltaLon = Math.toRadians(longitude2 - longitude1);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.asin(Math.min(1.0, Math.sqrt(a)));

        return EARTH_RADIUS_METERS * c;
    }
}
