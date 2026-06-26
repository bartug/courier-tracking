package com.migros.couriertracking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.migros.couriertracking.domain.CourierState;
import com.migros.couriertracking.dto.CourierLocationRequest;
import com.migros.couriertracking.exception.NotFoundException;
import com.migros.couriertracking.repository.CourierStateRepository;
import com.migros.couriertracking.repository.StoreEntranceRepository;
import com.migros.couriertracking.service.event.CourierLocationEvent;
import com.migros.couriertracking.service.event.CourierLocationPublisher;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * <h1>Kurye Takip Servisi Testleri</h1>
 * <p>Servisin; konum olayını doğru ürettiğini, zaman damgası yokken sunucu saatini kullandığını,
 * toplam mesafe sorgusunu döndürdüğünü ve kurye bulunamadığında {@link NotFoundException}
 * fırlattığını doğrular.</p>
 *
 * @author Bartug Sevindik
 * @since 24.06.2026
 */
@ExtendWith(MockitoExtension.class)
class CourierTrackingServiceImplTest {

    private static final Instant FIXED_NOW = Instant.parse("2026-06-22T16:00:00Z");

    @Mock
    private CourierLocationPublisher courierLocationPublisher;
    @Mock
    private CourierStateRepository courierStateRepository;
    @Mock
    private StoreEntranceRepository storeEntranceRepository;

    private CourierTrackingServiceImpl courierTrackingService;

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(FIXED_NOW, ZoneOffset.UTC);
        courierTrackingService = new CourierTrackingServiceImpl(
                courierLocationPublisher, courierStateRepository, storeEntranceRepository, fixedClock);
    }

    @Test
    void testTrackPublishesEventWithRequestData() {
        CourierLocationRequest request = CourierLocationRequest.builder()
                .courierId("courier-1")
                .latitude(40.9923307)
                .longitude(29.1244229)
                .timestamp(Instant.parse("2026-06-22T16:01:00Z"))
                .build();

        courierTrackingService.track(request);

        ArgumentCaptor<CourierLocationEvent> captor = ArgumentCaptor.forClass(CourierLocationEvent.class);
        verify(courierLocationPublisher).publish(captor.capture());

        CourierLocationEvent event = captor.getValue();
        assertEquals("courier-1", event.getCourierId());
        assertEquals(40.9923307, event.getLatitude());
        assertEquals(29.1244229, event.getLongitude());
        assertEquals(Instant.parse("2026-06-22T16:01:00Z"), event.getTimestamp());
    }

    @Test
    void testTrackUsesClockWhenTimestampMissing() {
        CourierLocationRequest request = CourierLocationRequest.builder()
                .courierId("courier-1")
                .latitude(40.0)
                .longitude(29.0)
                .build();

        courierTrackingService.track(request);

        ArgumentCaptor<CourierLocationEvent> captor = ArgumentCaptor.forClass(CourierLocationEvent.class);
        verify(courierLocationPublisher).publish(captor.capture());
        assertEquals(FIXED_NOW, captor.getValue().getTimestamp(), "Zaman damgası yoksa sunucu saati kullanılmalı");
    }

    @Test
    void testGetTotalTravelDistanceReturnsAccumulatedValue() {
        CourierState state = CourierState.initial(40.0, 29.0).advance(40.0, 29.0, 1234.5);
        when(courierStateRepository.findByCourierId("courier-1")).thenReturn(Optional.of(state));

        double total = courierTrackingService.getTotalTravelDistance("courier-1");

        assertEquals(1234.5, total, 1e-9);
    }

    @Test
    void testGetTotalTravelDistanceThrowsWhenCourierUnknown() {
        when(courierStateRepository.findByCourierId(anyString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> courierTrackingService.getTotalTravelDistance("unknown"));
    }
}
