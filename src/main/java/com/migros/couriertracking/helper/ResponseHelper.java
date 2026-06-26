package com.migros.couriertracking.helper;

import com.migros.couriertracking.dto.ResponseMessage;
import org.springframework.http.HttpStatus;

/**
 * <h1>Yanıt Yardımcısı</h1>
 * <p>{@link ResponseMessage} nesnelerini standart bir biçimde üretmek için kullanılan yardımcı
 * sınıftır. Örnek oluşturulmasını engellemek amacıyla boş gövdeli bir enum olarak tanımlanmıştır.</p>
 *
 * @author Bartug Sevindik
 * @since 24.06.2026
 */
public enum ResponseHelper {
    ;

    /**
     * <h1>Başarılı Yanıt</h1>
     * <p>200 OK durumu ile başarılı bir yanıt nesnesi oluşturur.</p>
     *
     * @param message kullanıcıya gösterilecek mesaj
     * @param object  dönen veri gövdesi (opsiyonel)
     * @return başarılı {@link ResponseMessage}
     */
    public static ResponseMessage success(String message, Object object) {
        return ResponseMessage.builder()
                .success(true)
                .message(message)
                .object(object)
                .httpStatus(HttpStatus.OK)
                .httpStatusCode(HttpStatus.OK.value())
                .build();
    }

    /**
     * <h1>Hatalı İstek Yanıtı</h1>
     * <p>400 Bad Request durumu ile hatalı istek yanıtı oluşturur.</p>
     *
     * @param message hata mesajı
     * @return hatalı {@link ResponseMessage}
     */
    public static ResponseMessage badRequest(String message) {
        return ResponseMessage.builder()
                .success(false)
                .message(message)
                .httpStatus(HttpStatus.BAD_REQUEST)
                .httpStatusCode(HttpStatus.BAD_REQUEST.value())
                .build();
    }

    /**
     * <h1>Bulunamadı Yanıtı</h1>
     * <p>404 Not Found durumu ile kayıt bulunamadı yanıtı oluşturur.</p>
     *
     * @param message hata mesajı
     * @return bulunamadı {@link ResponseMessage}
     */
    public static ResponseMessage notFound(String message) {
        return ResponseMessage.builder()
                .success(false)
                .message(message)
                .httpStatus(HttpStatus.NOT_FOUND)
                .httpStatusCode(HttpStatus.NOT_FOUND.value())
                .build();
    }
}
