package com.migros.couriertracking.repository;

import com.migros.couriertracking.domain.CourierState;
import java.util.Optional;
import java.util.function.UnaryOperator;

/**
 * <h1>Kurye Durumu Deposu</h1>
 * <p>Kuryelerin son konumunu ve toplam mesafesini tutan deponun arayüzüdür.</p>
 *
 * @author Bartug Sevindik
 * @since 24.06.2026
 */
public interface CourierStateRepository {

    /**
     * <h1>Atomik Güncelleme</h1>
     * <p>Verilen kuryenin durumunu; mevcut durumu (yoksa {@code null}) girdi alıp yeni durumu
     * döndüren {@code updater} fonksiyonu ile atomik olarak günceller. Okuma-değiştirme-yazma
     * adımının tamamı tek bir kurye anahtarı üzerinde yarışsız çalışır.</p>
     *
     * @param courierId güncellenecek kuryenin kimliği
     * @param updater   mevcut durumu yeni duruma dönüştüren fonksiyon
     * @return güncelleme sonrası oluşan yeni durum
     */
    CourierState update(String courierId, UnaryOperator<CourierState> updater);

    /**
     * <h1>Kurye Durumunu Getir</h1>
     * <p>Verilen kuryenin mevcut durumunu döndürür; kayıt yoksa boş {@link Optional}.</p>
     *
     * @param courierId kurye kimliği
     * @return varsa kurye durumu
     */
    Optional<CourierState> findByCourierId(String courierId);
}
