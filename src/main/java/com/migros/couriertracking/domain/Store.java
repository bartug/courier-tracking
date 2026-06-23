package com.migros.couriertracking.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h1>Mağaza</h1>
 * <p><code>stores.json</code> dosyasından okunan Migros mağazasını temsil eder. Uygulama ayağa
 * kalkarken bir kez yüklenir ve bellekte salt-okunur olarak tutulur.</p>
 *
 * @author Bartug Sevindik
 * @since 23.06.2026
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Store {

    /** Mağaza adı (ör. "Ataşehir MMM Migros"). */
    private String name;

    /** Enlem (latitude). */
    private double lat;

    /** Boylam (longitude). */
    private double lng;
}
