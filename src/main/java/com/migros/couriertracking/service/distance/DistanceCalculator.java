package com.migros.couriertracking.service.distance;

/**
 * <h1>Mesafe Hesaplama Stratejisi</h1>
 * <p>İki coğrafi nokta arasındaki mesafeyi metre cinsinden hesaplayan strateji arayüzüdür
 * (Strategy Design Pattern). Varsayılan gerçekleştirim {@link HaversineDistanceCalculator}'dir;
 * ileride gerçek yol mesafesi gerekirse (ör. bir harita servisi tabanlı hesaplama) kodun geri
 * kalanı değişmeden yeni bir gerçekleştirim eklenebilir.</p>
 *
 * @author Bartug Sevindik
 * @since 23.06.2026
 */
public interface DistanceCalculator {

    /**
     * <h1>İki Nokta Arası Mesafe</h1>
     * <p>Verilen enlem/boylam çiftleri arasındaki mesafeyi metre cinsinden döndürür.</p>
     *
     * @param latitude1  birinci noktanın enlemi
     * @param longitude1 birinci noktanın boylamı
     * @param latitude2  ikinci noktanın enlemi
     * @param longitude2 ikinci noktanın boylamı
     * @return iki nokta arasındaki mesafe (metre)
     */
    double distanceInMeters(double latitude1, double longitude1, double latitude2, double longitude2);
}
