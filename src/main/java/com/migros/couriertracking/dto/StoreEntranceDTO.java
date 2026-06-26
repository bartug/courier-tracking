package com.migros.couriertracking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h1>Mağaza Giriş DTO</h1>
 * <p>Bir kuryenin bir mağazaya girişinin gösterimidir.</p>
 *
 * @author Bartug Sevindik
 * @since 24.06.2026
 */
@Schema(name = "StoreEntranceDTO", description = "Kuryenin bir mağazaya giriş kaydı.")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreEntranceDTO {

    @Schema(description = "Kurye kimliği.", example = "courier-1")
    private String courierId;

    @Schema(description = "Mağaza adı.", example = "Ataşehir MMM Migros")
    private String storeName;

    @Schema(description = "Giriş anında mağazaya uzaklık (metre).", example = "42.7")
    private double distanceMeters;

    @Schema(description = "Giriş anı (ISO-8601).", example = "2026-06-22T16:01:00Z")
    private Instant enteredAt;
}
