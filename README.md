# Courier Tracking — Kurye Takip Servisi

Kuryelerin akan (streaming) konum verilerini işleyen RESTful servis. İki işi yapar:

1. **Mağaza girişi tespiti** — Bir kurye herhangi bir Migros mağazasının **100 metre** yarıçapına girdiğinde kurye + mağaza loglanır. Aynı mağazaya **1 dakika içinde** gelen tekrar girişler yeni giriş sayılmaz.
2. **Toplam mesafe takibi** — Her kuryenin kat ettiği toplam mesafe sorgulanabilir (`double getTotalTravelDistance(courierId)`).

Mağaza konumları `src/main/resources/stores.json` dosyasından, uygulama açılırken **bir kez** okunur.

> Bu alanda sadece açıklama ve çalıştırma notları vardır. Tasarım kararlarının gerekçeler ve notları ayrı bir belgeye aldım: **[PROJE_SUNUM.md](PROJE_SUNUM.md)**

---

## Proje Yapısı

```
src/main/java/com/migros/couriertracking
├── config/        # OpenAPI, ConfigurationProperties, Clock bean
├── controller/    # REST uçları (Kurye, Mağaza)
├── domain/        # Store, CourierState (immutable), StoreEntrance
├── dto/           # İstek/yanıt nesneleri + ResponseMessage zarfı
├── exception/     # NotFoundException + global hata yöneticisi
├── helper/        # ResponseHelper
├── repository/    # Bellek içi depolar (state + entrance)
└── service/
    ├── catalog/   # StoreCatalog (stores.json'u bir kez yükler)
    ├── distance/  # DistanceCalculator (Strategy) + Haversine
    └── event/     # Observer: event, publisher, listener + dinleyiciler
```

Akış: `CourierController → CourierTrackingService → CourierLocationPublisher` (Observer "subject") →
`StoreEntranceListener` (100 m + 1 dk kuralı) ve `TravelDistanceListener` (toplam mesafe birikimi).

---

## Teknoloji

- Java 21, Spring Boot 3.4 (Web, Validation)
- springdoc-openapi (Swagger UI)
- Lombok
- Maven (Maven Wrapper dahil)
- JUnit 5, Mockito, Spring MockMvc

---

## Hızlı Başlangıç

Gereksinim: **JDK 21+** (Maven kurulu olmasa da depodaki Wrapper ile çalışır).

**Script ile:**

```bash
./run.sh
```

**Docker ile:**

```bash
docker compose up --build
```

Uygulama `http://localhost:8080` adresinde çalışır:

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

---

## Bu script'ler ne yapıyor? (benden not)

Vakada "kolay çalıştırıp test edebilmek için README ya da çalıştırılabilir bir script olsa iyi olur" deniyordu; ben de ikisini birden koydum. Kısaca anlatayım:

- **`run.sh`** — Uygulamayı tek komutla ayağa kaldırıyorum. Makinemde varsayılan JDK bazen eski kalıyor, o yüzden script'in içinde 21+ bir JDK'yı kendim seçtiriyorum ki uğraşmadan çalışsın.
- **`sample-requests.sh`** — Çalışan uygulamaya gerçekçi bir istek dizisini sırayla atıp cevapları ekrana basıyorum: önce mağazaları listeliyorum, sonra `courier-1` için birkaç konum gönderiyorum, ardından toplam mesafeyi ve mağaza girişlerini soruyorum, en sonda da olmayan bir kuryeyle 404'ü gösteriyorum. Yani elle tek tek `curl` yazmadan veya Swagger'da tıklamadan, her şeyin uçtan uca çalıştığını tek seferde görüyorum. (Not: durum bellekte tutulduğu için aynı kuryeyle script'i tekrar çalıştırırsam toplam mesafe üst üste birikir; temiz sonuç için uygulamayı yeniden başlatıyorum.)
- **Swagger / OpenAPI** — API dökümantasyonunu Swagger ile verdim. Uygulama ayaktayken `/swagger-ui.html` üzerinden bütün uçları görüp doğrudan deneyebilirsin; ham hâli de `/v3/api-docs` altında.

---

## Test

```bash
./mvnw test
```

21 test: mesafe formülü (referans değerlerle), 100 m + 1 dakika kuralı, mesafe birikimi, servis orkestrasyonu ve web katmanı (MockMvc).

Çalışan uygulamayı uçtan uca denemek için (uygulama ayaktayken **başka bir terminalde**):

```bash
./sample-requests.sh
```

Senaryo, Ataşehir mağazasına 30 saniye arayla iki bildirim içerir; çıktıda **yalnızca tek bir giriş** görülmesi 1 dakika kuralının çalıştığını gösterir.

---

## API

Tüm yanıtlar ortak bir `ResponseMessage` zarfı ile döner: `{ success, message, object, httpStatus, httpStatusCode }`.

| Metot | Yol | Açıklama |
|---|---|---|
| `POST` | `/api/v1/couriers/locations` | Tek bir kurye konumunu işler (akış girişi) |
| `GET` | `/api/v1/couriers/{courierId}/total-distance` | Kuryenin toplam kat ettiği mesafe (metre) |
| `GET` | `/api/v1/couriers/{courierId}/entrances` | Kuryenin sayılan mağaza girişleri |
| `GET` | `/api/v1/stores` | Yüklü mağaza kataloğu |

**Konum bildir**

```bash
curl -X POST http://localhost:8080/api/v1/couriers/locations \
  -H 'Content-Type: application/json' \
  -d '{"courierId":"courier-1","latitude":40.9923307,"longitude":29.1244229,"timestamp":"2026-06-22T16:00:10Z"}'
```

`timestamp` opsiyoneldir; gönderilmezse sunucu saati kullanılır.

**Toplam mesafe**

```bash
curl http://localhost:8080/api/v1/couriers/courier-1/total-distance
```

```json
{
  "success": true,
  "message": "Kurye toplam mesafesi getirildi.",
  "object": { "courierId": "courier-1", "totalDistanceMeters": 39860.25 },
  "httpStatus": "OK",
  "httpStatusCode": 200
}
```
