package com.migros.couriertracking.controller;

import com.migros.couriertracking.dto.CourierLocationRequest;
import com.migros.couriertracking.dto.ResponseMessage;
import com.migros.couriertracking.dto.StoreEntranceDTO;
import com.migros.couriertracking.dto.TotalDistanceResponse;
import com.migros.couriertracking.helper.ResponseHelper;
import com.migros.couriertracking.service.CourierTrackingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h1>Kurye İşlemleri Controller</h1>
 * <p>Kurye konum akışının alındığı toplam mesafe v mağaza girişi sorgularının yapıldığı REST
 * controller.</p>
 *
 * @author Bartug Sevindik
 * @since 23.06.2026
 */
@Tag(name = "Kurye İşlemleri", description = "Kurye konum akışı, toplam mesafe ve mağaza girişi sorguları.")
@RestController
@RequestMapping("/api/v1/couriers")
@RequiredArgsConstructor
public class CourierController {

    private final CourierTrackingService courierTrackingService;

    @Operation(summary = "Kurye konumu bildir",
            description = "Akan veriden gelen tek bir kurye konumunu işler; mağaza girişi tespiti ve " +
                    "toplam mesafe birikimi tetiklenir.")
    @ApiResponses(value = {
            @ApiResponse(description = "Konum başarıyla işlendi.", responseCode = "200",
                    content = @Content(schema = @Schema(implementation = ResponseMessage.class))),
            @ApiResponse(description = "Geçersiz istek gövdesi.", responseCode = "400")
    })
    @PostMapping("/locations")
    public ResponseEntity<ResponseMessage> trackLocation(@Valid @RequestBody CourierLocationRequest request) {
        courierTrackingService.track(request);
        return ResponseEntity.ok(ResponseHelper.success("Kurye konumu başarıyla işlendi.", null));
    }

    @Operation(summary = "Toplam mesafe sorgula",
            description = "Verilen kuryenin o ana kadar kat ettiği toplam mesafeyi (metre) döndürür.")
    @ApiResponses(value = {
            @ApiResponse(description = "Toplam mesafe getirildi.", responseCode = "200",
                    content = @Content(schema = @Schema(implementation = TotalDistanceResponse.class))),
            @ApiResponse(description = "Kurye bulunamadı.", responseCode = "404")
    })
    @GetMapping("/{courierId}/total-distance")
    public ResponseEntity<ResponseMessage> getTotalDistance(@PathVariable String courierId) {
        double totalDistance = courierTrackingService.getTotalTravelDistance(courierId);
        TotalDistanceResponse response = TotalDistanceResponse.builder()
                .courierId(courierId)
                .totalDistanceMeters(totalDistance)
                .build();
        return ResponseEntity.ok(ResponseHelper.success("Kurye toplam mesafesi getirildi.", response));
    }

    @Operation(summary = "Mağaza girişlerini listele",
            description = "Verilen kuryeye ait, sayılmış (loglanmış) mağaza girişlerini döndürür.")
    @ApiResponses(value = {
            @ApiResponse(description = "Girişler getirildi.", responseCode = "200",
                    content = @Content(schema = @Schema(implementation = StoreEntranceDTO.class)))
    })
    @GetMapping("/{courierId}/entrances")
    public ResponseEntity<ResponseMessage> getEntrances(@PathVariable String courierId) {
        List<StoreEntranceDTO> entrances = courierTrackingService.getEntrances(courierId);
        return ResponseEntity.ok(ResponseHelper.success("Kurye mağaza girişleri getirildi.", entrances));
    }
}
