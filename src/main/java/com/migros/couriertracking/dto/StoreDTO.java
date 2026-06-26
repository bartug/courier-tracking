package com.migros.couriertracking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h1>Mağaza DTO</h1>
 * <p>Yüklenen mağaza kataloğunun gösterimidir.</p>
 *
 * @author Bartug Sevindik
 * @since 24.06.2026
 */
@Schema(name = "StoreDTO", description = "Mağaza bilgisi.")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreDTO {

    @Schema(description = "Mağaza adı.", example = "Ataşehir MMM Migros")
    private String name;

    @Schema(description = "Enlem.", example = "40.9923307")
    private double lat;

    @Schema(description = "Boylam.", example = "29.1244229")
    private double lng;
}
