package com.migros.couriertracking.service;

import com.migros.couriertracking.domain.CourierState;
import com.migros.couriertracking.dto.CourierLocationRequest;
import com.migros.couriertracking.dto.StoreEntranceDTO;
import com.migros.couriertracking.exception.NotFoundException;
import com.migros.couriertracking.repository.CourierStateRepository;
import com.migros.couriertracking.repository.StoreEntranceRepository;
import com.migros.couriertracking.service.event.CourierLocationEvent;
import com.migros.couriertracking.service.event.CourierLocationPublisher;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * <h1>Kurye Takip Servisi implemantasyonu</h1>
 * <p>Gelen konum bildirimini bir {@link CourierLocationEvent}'e dönüştürüp
 * {@link CourierLocationPublisher} ile yayınlar; toplam mesafe ve giriş sorgularını ilgili
 * depolardan karşılar. </p>
 *
 * @author Bartug Sevindik
 * @since 24.06.2026
 */
@Service
@RequiredArgsConstructor
public class CourierTrackingServiceImpl implements CourierTrackingService {

    private final CourierLocationPublisher courierLocationPublisher;
    private final CourierStateRepository courierStateRepository;
    private final StoreEntranceRepository storeEntranceRepository;
    private final Clock clock;

    /**
     * {@inheritDoc}
     * <p>Zaman damgası gelmemişse {@link Clock} üzerinden sunucu saati kullanılır.</p>
     */
    @Override
    public void track(CourierLocationRequest request) {
        Instant timestamp = request.getTimestamp() != null ? request.getTimestamp() : Instant.now(clock);
        CourierLocationEvent event = new CourierLocationEvent(
                request.getCourierId(), request.getLatitude(), request.getLongitude(), timestamp);
        courierLocationPublisher.publish(event);
    }

    @Override
    public double getTotalTravelDistance(String courierId) {
        return courierStateRepository.findByCourierId(courierId)
                .map(CourierState::getTotalDistanceMeters)
                .orElseThrow(() -> new NotFoundException("Kurye bulunamadı: " + courierId));
    }

    @Override
    public List<StoreEntranceDTO> getEntrances(String courierId) {
        return storeEntranceRepository.findByCourierId(courierId).stream()
                .map(entrance -> StoreEntranceDTO.builder()
                        .courierId(entrance.getCourierId())
                        .storeName(entrance.getStoreName())
                        .distanceMeters(entrance.getDistanceMeters())
                        .enteredAt(entrance.getEnteredAt())
                        .build())
                .toList();
    }
}
