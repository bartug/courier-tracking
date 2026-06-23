package com.migros.couriertracking.domain;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * <h1>Mağaza Giriş Kaydı</h1>
 * <p>Bir kuryenin belirli bir mağazanın giriş yarıçapına girdiği anın kaydıdır. Tekrar giriş
 * kuralı gereği sayılan (loglanan) her giriş için bir kez oluşturulur.</p>
 *
 * @author Bartug Sevindik
 * @since 23.06.2026
 */
@Getter
@Builder
@AllArgsConstructor
@ToString
public class StoreEntrance {

    /** Giriş yapan kuryenin kimliği. */
    private final String courierId;

    /** Girilen mağazanın adı. */
    private final String storeName;

    /** Giriş anında kuryenin mağazaya uzaklığı (metre). */
    private final double distanceMeters;

    /** Girişin gerçekleştiği an (olay zaman damgası). */
    private final Instant enteredAt;
}
