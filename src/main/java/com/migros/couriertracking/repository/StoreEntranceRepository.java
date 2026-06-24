package com.migros.couriertracking.repository;

import com.migros.couriertracking.domain.StoreEntrance;
import java.util.List;

/**
 * <h1>Mağaza Giriş Deposu</h1>
 * <p>Loglanan mağaza girişlerini saklayan deponun arayüzüdür.</p>
 *
 * @author Bartug Sevindik
 * @since 24.06.2026
 */
public interface StoreEntranceRepository {

    /**
     * <h1>Giriş Kaydet</h1>
     * <p>Yeni bir mağaza girişini depoya ekler.</p>
     *
     * @param entrance kaydedilecek giriş
     */
    void save(StoreEntrance entrance);

    /**
     * <h1>Tüm Girişler</h1>
     * <p>O ana kadar kaydedilmiş tüm girişleri döndürür.</p>
     *
     * @return giriş kayıtları
     */
    List<StoreEntrance> findAll();

    /**
     * <h1>Kuryeye Göre Girişler</h1>
     * <p>Belirtilen kuryeye ait giriş kayıtlarını döndürür.</p>
     *
     * @param courierId kurye kimliği
     * @return kuryenin giriş kayıtları
     */
    List<StoreEntrance> findByCourierId(String courierId);
}
