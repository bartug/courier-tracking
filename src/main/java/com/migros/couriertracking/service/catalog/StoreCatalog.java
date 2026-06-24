package com.migros.couriertracking.service.catalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migros.couriertracking.config.CourierTrackingProperties;
import com.migros.couriertracking.domain.Store;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

/**
 * <h1>Mağaza Kataloğu</h1>
 * <p>Mağaza listesini, uygulama ayağa kalkarken <code>stores.json</code> dosyasından bir kez okuyup
 * bellekte salt-okunur olarak tutar (Singleton). Her konum bildiriminde dosyanın yeniden okunmasını
 * engelleyerek performansı korur. JSON ayrıştırma, basit bir fabrika (factory) yöntemi olan
 * {@link #loadStores()} içinde toplanmıştır.</p>
 *
 * @author Bartug Sevindik
 * @since 23.06.2026
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StoreCatalog {

    private final CourierTrackingProperties properties;
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;

    /** Uygulama başlangıcında yüklenen, değişmez mağaza listesi. */
    private List<Store> stores = List.of();

    /**
     * <h1>Kataloğu Yükle</h1>
     * <p>Konfigürasyonda belirtilen kaynaktan mağazaları okur ve bellekte tutar. Bean oluşturulduktan
     * sonra Spring tarafından bir kez çağrılır. Dosya okunamazsa uygulamanın hatalı bir durumla
     * ayağa kalkmaması için {@link IllegalStateException} fırlatılır.</p>
     */
    @PostConstruct
    public void loadStores() {
        Resource resource = resourceLoader.getResource(properties.getStoresFile());
        try (InputStream inputStream = resource.getInputStream()) {
            Store[] loaded = objectMapper.readValue(inputStream, Store[].class);
            this.stores = List.of(loaded);
            log.info("{} mağaza '{}' kaynağından yüklendi.", stores.size(), properties.getStoresFile());
        } catch (IOException e) {
            throw new IllegalStateException("Mağaza listesi okunamadı: " + properties.getStoresFile(), e);
        }
    }

    /**
     * <h1>Tüm Mağazalar</h1>
     * <p>Yüklenmiş mağazaların değişmez listesini döndürür.</p>
     *
     * @return mağaza listesi
     */
    public List<Store> getStores() {
        return stores;
    }
}
