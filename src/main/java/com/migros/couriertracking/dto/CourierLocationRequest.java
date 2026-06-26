package com.migros.couriertracking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h1>Kurye Konum İsteği</h1>
 * <p>Streaming esnasında kurye konum verisinin tek bir kaydını temsil ediyor. hangi kurye, hangi an,
 * hangi enlem/boylamda gibi. Toplam mesafe hesabı ve mağaza giriş tespiti bu kayıtlar üzerinden
 * yürütülür.</p>
 *
 * @author Bartug Sevindik
 * @since 24.06.2026
 */
@Schema(name = "CourierLocationRequest", description = "Bir kuryenin tek bir konum bildirimi.")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourierLocationRequest {

    @NotBlank(message = "Kurye kimliği (courierId) zorunludur.")
    @Schema(description = "Kurye kimliği.", example = "courier-1", requiredMode = Schema.RequiredMode.REQUIRED)
    private String courierId;

    @NotNull(message = "Enlem (latitude) zorunludur.")
    @Schema(description = "Enlem (latitude).", example = "40.9923307", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double latitude;

    @NotNull(message = "Boylam (longitude) zorunludur.")
    @Schema(description = "Boylam (longitude).", example = "29.1244229", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double longitude;

    @Schema(description = "Konumun ölçüldüğü an (ISO-8601). Boş bırakılırsa sunucu saati kullanılır.",
            example = "2026-06-22T16:01:00Z", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Instant timestamp;
}
