package com.migros.couriertracking.controller;

import com.migros.couriertracking.dto.ResponseMessage;
import com.migros.couriertracking.dto.StoreDTO;
import com.migros.couriertracking.helper.ResponseHelper;
import com.migros.couriertracking.service.catalog.StoreCatalog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h1>Mağaza İşlemleri Controller</h1>
 * <p>Uygulama başlangıcında sadece 1 kere yüklenen Migros mağaza kataloğunu sorgulamak için kullanılan REST
 * controller.</p>
 *
 * @author Bartug Sevindik
 * @since 23.06.2026
 */
@Tag(name = "Mağaza İşlemleri", description = "Yüklenen mağaza kataloğunu listeler.")
@RestController
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreCatalog storeCatalog;

    @Operation(summary = "Mağazaları listele",
            description = "stores.json dosyasından yüklenen tüm mağazaları döndürür.")
    @ApiResponses(value = {
            @ApiResponse(description = "Mağaza listesi getirildi.", responseCode = "200",
                    content = @Content(schema = @Schema(implementation = StoreDTO.class)))
    })
    @GetMapping
    public ResponseEntity<ResponseMessage> getStores() {
        List<StoreDTO> stores = storeCatalog.getStores().stream()
                .map(store -> StoreDTO.builder()
                        .name(store.getName())
                        .lat(store.getLat())
                        .lng(store.getLng())
                        .build())
                .toList();
        return ResponseEntity.ok(ResponseHelper.success("Mağaza listesi getirildi.", stores));
    }
}
