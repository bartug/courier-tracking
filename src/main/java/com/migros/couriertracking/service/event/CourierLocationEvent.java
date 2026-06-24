package com.migros.couriertracking.service.event;

import java.time.Instant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * <h1>Kurye Konum eventi</h1>
 * <p>Bir kuryeden yeni bir konum geldiğinde üretilen, değişmezolay nesnesidir.
 * {@link CourierLocationPublisher} bu olayı, ilgilenen tüm dinleyicilere observe eder.</p>
 *
 * @author Bartug Sevindik
 * @since 23.06.2026
 */
@Getter
@ToString
@RequiredArgsConstructor
public class CourierLocationEvent {

    /** Konumu bildiren kuryenin kimliği. */
    private final String courierId;

    /** Enlem (latitude). */
    private final double latitude;

    /** Boylam (longitude). */
    private final double longitude;

    /** Konumun ölçüldüğü an. */
    private final Instant timestamp;
}
