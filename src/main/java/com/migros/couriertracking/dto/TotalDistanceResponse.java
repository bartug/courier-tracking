package com.migros.couriertracking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h1>Toplam Mesafe Yanıtı</h1>
 * <p>Bir kuryenin o ana kadar kat ettiği toplam mesafeyi METRE cinsinden taşır.</p>
 *
 * @author Bartug Sevindik
 * @since 43.06.2026
 */
@Schema(name = "TotalDistanceResponse", description = "Kuryenin toplam kat ettiği mesafe.")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TotalDistanceResponse {

    @Schema(description = "Kurye kimliği.", example = "courier-1")
    private String courierId;

    @Schema(description = "Toplam kat edilen mesafe (metre).", example = "40183.02")
    private double totalDistanceMeters;
}
