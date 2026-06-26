package com.migros.couriertracking.exception;

import com.migros.couriertracking.dto.ResponseMessage;
import com.migros.couriertracking.helper.ResponseHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * <h1>Global Hata Yöneticisi</h1>
 * <p>Uygulama genelinde fırlatılan istisnaları yakalayıp standart {@link ResponseMessage} yanıtına
 * dönüştürür. Böylece her uçta tekrarlı hata mesajını önlemiş oluyorum.</p>
 *
 * @author Bartug Sevindik
 * @since 24.06.2026
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * <h1>Bulunamadı</h1>
     * <p>{@link NotFoundException} durumunu 404 yanıtına dönüştürür.</p>
     *
     * @param exception yakalanan istisna
     * @return 404 yanıtı
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ResponseMessage> handleNotFound(NotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseHelper.notFound(exception.getMessage()));
    }

    /**
     * <h1>Doğrulama Hatası</h1>
     * <p>İstek gövdesindeki doğrulama (validation) hatalarını 400 yanıtına dönüştürür; ilk hatalı
     * alanın mesajını kullanıcıya iletir.</p>
     *
     * @param exception yakalanan doğrulama istisnası
     * @return 400 yanıtı
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseMessage> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Doğrulama hatası.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseHelper.badRequest(message));
    }
}
