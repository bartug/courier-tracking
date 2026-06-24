package com.migros.couriertracking.service.event;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <h1>Kurye Konum Publisher</h1>
 * <p>Observer Design Pattern'in subject tarafıdır. Seçtiğim patternlernden bir tanesi.  Gelen her kurye konum olayını, kayıtlı
 * tüm {@link CourierLocationListener} gerçekleştirimlerine iletir.</p>
 *
 * @author Bartug Sevindik
 * @since 24.06.2026
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CourierLocationPublisher {

    private final List<CourierLocationListener> listeners;

    /**
     * <h1>Olayı Yayınla</h1>
     * <p>Konum olayını tüm dinleyicilere sırayla iletir. Bir dinleyicide oluşan hatanın
     * diğerlerini etkilememesi için her dinleyici ayrı ayrı korunur.</p>
     *
     * @param event yayınlanacak konum olayı
     */
    public void publish(CourierLocationEvent event) {
        for (CourierLocationListener listener : listeners) {
            try {
                listener.onCourierLocation(event);
            } catch (Exception e) {
                log.error("Konum olayı '{}' dinleyicisinde işlenemedi.", listener.getClass().getSimpleName(), e);
            }
        }
    }
}
