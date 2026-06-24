package com.migros.couriertracking.service.event;

/**
 * <h1>Kurye Konum Listener</h1>
 * <p>Observer Design Pattern'in observe tarafıdır. Yeni bir konum olayı geldiğinde
 * tetiklenir.</p>
 *
 * @author Bartug Sevindik
 * @since 24.06.2026
 */
public interface CourierLocationListener {

    /**
     * <h1>Konum Olayını İşle</h1>
     * <p>Yeni gelen kurye konum olayına tepki verir.</p>
     *
     * @param event işlenecek konum olayı
     */
    void onCourierLocation(CourierLocationEvent event);
}
