package com.migros.couriertracking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * <h1>Genel Yanıt DTO</h1>
 * <p>Tüm REST mantıgında ortak dönüş tipidir</p>
 *
 * @author Bartug Sevindik
 * @since 24.06.2026
 */
@Schema(name = "ResponseMessage", description = "Tüm yanıtların sarmalandığı genel dönüş nesnesi.")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseMessage {

    @Schema(description = "İşlem başarılı mı?", example = "true")
    private Boolean success;

    @Schema(description = "Kullanıcıya gösterilecek mesaj.", example = "Kurye konumu başarıyla işlendi.")
    private String message;

    @Schema(description = "Varsa, yanıtın veri gövdesi.")
    private Object object;

    @Schema(description = "HTTP durumu.", example = "OK")
    private HttpStatus httpStatus;

    @Schema(description = "HTTP durum kodu.", example = "200")
    private Integer httpStatusCode;
}
