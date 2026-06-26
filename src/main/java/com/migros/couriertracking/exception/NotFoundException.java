package com.migros.couriertracking.exception;

/**
 * <h1>Bulunamadı İstisnası</h1>
 * <p>İstenen kaynağın (ör. henüz hiç konum bildirmemiş bir kurye) bulunamadığı durumlarda  bu hata çalısır.
 *
 * @author Bartug Sevindik
 * @since 24.06.2026
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
