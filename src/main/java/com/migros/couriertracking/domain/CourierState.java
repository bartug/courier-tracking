package com.migros.couriertracking.domain;

import lombok.Getter;
import lombok.ToString;

/**
 * <h1>Kurye Durumu</h1>
 * <p>Bir kuryenin son bilinen konumunu ve o ana kadar kat ettiği toplam mesafeyi (metre) tutar.
 * Nesne değişmezdir (immutable); her yeni konumda {@link #advance(double, double, double)} ile
 * yeni bir örnek üretilir. Bu değişmezlik sayesinde {@code ConcurrentHashMap.compute(...)} içinde
 * yarış durumu (race condition) oluşmadan güncellenebilir.</p>
 *
 * @author Bartug Sevindik
 * @since 23.06.2026
 */
@Getter
@ToString
public class CourierState {

    private final double lastLatitude;
    private final double lastLongitude;
    private final double totalDistanceMeters;

    private CourierState(double lastLatitude, double lastLongitude, double totalDistanceMeters) {
        this.lastLatitude = lastLatitude;
        this.lastLongitude = lastLongitude;
        this.totalDistanceMeters = totalDistanceMeters;
    }

    /**
     * <h1>İlk Durum</h1>
     * <p>Kuryenin ilk konum kaydını, toplam mesafesi 0 olacak şekilde oluşturur.</p>
     *
     * @param latitude  ilk enlem
     * @param longitude ilk boylam
     * @return başlangıç durumu
     */
    public static CourierState initial(double latitude, double longitude) {
        return new CourierState(latitude, longitude, 0.0);
    }

    /**
     * <h1>Yeni Konuma İlerle</h1>
     * <p>Son konumu yeni konumla günceller ve verilen ara mesafeyi toplam mesafeye ekleyerek yeni
     * bir durum nesnesi döndürür.</p>
     *
     * @param latitude      yeni enlem
     * @param longitude     yeni boylam
     * @param segmentMeters bir önceki konum ile yeni konum arasındaki mesafe (metre)
     * @return güncellenmiş yeni durum
     */
    public CourierState advance(double latitude, double longitude, double segmentMeters) {
        return new CourierState(latitude, longitude, this.totalDistanceMeters + segmentMeters);
    }
}
