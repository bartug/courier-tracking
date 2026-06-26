package com.migros.couriertracking.service;

import com.migros.couriertracking.dto.CourierLocationRequest;
import com.migros.couriertracking.dto.StoreEntranceDTO;
import java.util.List;

/**
 * <h1>Kurye Takip Servisi</h1>
 * <p>Kurye konum akışının işlenmesini ve toplam mesafe / mağaza girişi sorgularını yöneten servis
 * arayüzüdür.</p>
 *
 * @author Bartug Sevindik
 * @since 24.06.2026
 */
public interface CourierTrackingService {

    /**
     * <h1>Konum İşle</h1>
     * <p>Akan veriden gelen tek bir kurye konumunu işler: ilgili olayı üretip yayıncıya iletir.
     * Mağaza giriş tespiti ve toplam mesafe birikimi, olayı dinleyen gözlemciler tarafından yapılır.</p>
     *
     * @param request işlenecek kurye konum bildirimi
     */
    void track(CourierLocationRequest request);

    /**
     * <h1>Toplam Kat Edilen Mesafe</h1>
     * <p>Verilen kuryenin o ana kadar kat ettiği toplam mesafeyi metre cinsinden döndürür. Vaka
     * metnindeki <code>Double getTotalTravelDistance(courierId)</code> imzasının karşılığıdır.</p>
     *
     * @param courierId kurye kimliği
     * @return toplam mesafe (metre)
     * @throws com.migros.couriertracking.exception.NotFoundException kurye için kayıt yoksa
     */
    double getTotalTravelDistance(String courierId);

    /**
     * <h1>Mağaza Girişleri</h1>
     * <p>Verilen kuryeye ait, sayılmış (loglanmış) mağaza girişlerini döndürür.</p>
     *
     * @param courierId kurye kimliği
     * @return giriş kayıtları
     */
    List<StoreEntranceDTO> getEntrances(String courierId);
}
