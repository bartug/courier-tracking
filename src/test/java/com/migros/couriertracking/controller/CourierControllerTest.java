package com.migros.couriertracking.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migros.couriertracking.dto.CourierLocationRequest;
import com.migros.couriertracking.exception.NotFoundException;
import com.migros.couriertracking.service.CourierTrackingService;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * <h1>Kurye Kontrolcüsü Web Katmanı Testleri</h1>
 * <p>{@link CourierController} uçlarının HTTP davranışını {@link MockMvc} ile doğrular: başarılı
 * konum bildirimi, doğrulama hatası (400) ve kurye bulunamadı (404) senaryoları.</p>
 *
 * @author Bartug Sevindik
 * @since 24.06.2026
 */
@WebMvcTest(CourierController.class)
class CourierControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CourierTrackingService courierTrackingService;

    @Test
    void testTrackLocationReturnsOk() throws Exception {
        CourierLocationRequest request = CourierLocationRequest.builder()
                .courierId("courier-1")
                .latitude(40.9923307)
                .longitude(29.1244229)
                .timestamp(Instant.parse("2026-06-22T16:00:00Z"))
                .build();

        mockMvc.perform(post("/api/v1/couriers/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(courierTrackingService).track(any(CourierLocationRequest.class));
    }

    @Test
    void testTrackLocationWithMissingCourierIdReturnsBadRequest() throws Exception {
        // courierId is absent -> @NotBlank validation must fail.
        String body = "{\"latitude\": 40.99, \"longitude\": 29.12}";

        mockMvc.perform(post("/api/v1/couriers/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testGetTotalDistanceReturnsValue() throws Exception {
        when(courierTrackingService.getTotalTravelDistance("courier-1")).thenReturn(40_183.02);

        mockMvc.perform(get("/api/v1/couriers/{courierId}/total-distance", "courier-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.object.courierId").value("courier-1"))
                .andExpect(jsonPath("$.object.totalDistanceMeters").value(40_183.02));
    }

    @Test
    void testGetTotalDistanceForUnknownCourierReturnsNotFound() throws Exception {
        when(courierTrackingService.getTotalTravelDistance("unknown"))
                .thenThrow(new NotFoundException("Kurye bulunamadı: unknown"));

        mockMvc.perform(get("/api/v1/couriers/{courierId}/total-distance", "unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }
}
