package com.migros.couriertracking.service.event.listener;

import com.migros.couriertracking.domain.CourierState;
import com.migros.couriertracking.repository.CourierStateRepository;
import com.migros.couriertracking.service.distance.DistanceCalculator;
import com.migros.couriertracking.service.event.CourierLocationEvent;
import com.migros.couriertracking.service.event.CourierLocationListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * <h1>Toplam Mesafe Dinleyicisi</h1>
 * <p>Observer Design Pattern dinleyicisidir. Gelen her konum olayında, kuryenin bir önceki konumu
 * ile yeni konumu arasındaki mesafeyi {@link DistanceCalculator} ile hesaplar ve kuryenin toplam
 * mesafesine ekler. Okuma-hesaplama-yazma adımı {@link CourierStateRepository#update} aracılığıyla
 * atomik olarak yürütülür; böylece eşzamanlı bildirimlerde mesafe kaybı yaşanmaz.</p>
 *
 * @author Bartug Sevindik
 * @since 23.06.2026
 */
@Component
@RequiredArgsConstructor
public class TravelDistanceListener implements CourierLocationListener {

    private final CourierStateRepository courierStateRepository;
    private final DistanceCalculator distanceCalculator;

    @Override
    public void onCourierLocation(CourierLocationEvent event) {
        courierStateRepository.update(event.getCourierId(), current -> {
            if (current == null) {
                return CourierState.initial(event.getLatitude(), event.getLongitude());
            }
            double segment = distanceCalculator.distanceInMeters(
                    current.getLastLatitude(), current.getLastLongitude(),
                    event.getLatitude(), event.getLongitude());
            return current.advance(event.getLatitude(), event.getLongitude(), segment);
        });
    }
}
