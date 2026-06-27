# Proje Sunum Notları — Courier Tracking

> Bu belge, projeyi anlatırken kullanacağım nottur: **ne yaptım ve neden böyle yaptım.** Kodun nasıl çalıştırılacağı için [README.md](README.md)'ye bakılabilir.

---

## 1. Özetle ne istendi, ne yaptım

İstenen; akan kurye konumlarını (zaman, kurye, enlem, boylam) işleyen bir REST servisti. Üç temel beklenti vardı: bir kurye bir Migros mağazasının **100 m** çevresine girince bunu loglamak (ama aynı mağazaya **1 dakika içindeki** tekrar girişleri saymamak), her kuryenin **toplam kat ettiği mesafeyi** sorgulatabilmek ve **en az iki tasarım örüntüsü** kullanmak.

Ben de olayı şöyle kurguladım: tek bir konum bildirimi geldiğinde bunu bir **olaya** çevirip yayınlıyorum; bu olayı iki ayrı bileşen dinliyor — biri mağaza girişini, diğeri toplam mesafeyi hesaplıyor. Mesafe hesabını ayrı bir **strateji** arkasına aldım. Mağaza listesini açılışta bir kez okuyup bellekte tutuyorum. Durumu (mesafe, son giriş zamanları) eşzamanlı erişime uygun yapılarda saklıyorum.

---

## 2. Bir konum geldiğinde ne oluyor? (akış)

```
POST /api/v1/couriers/locations
        │
        ▼
CourierTrackingService  ──►  olay üret (CourierLocationEvent)  ──►  CourierLocationPublisher.publish(event)
                                                                          │
                                        ┌─────────────────────────────────┴─────────────────────────────────┐
                                        ▼                                                                     ▼
                              StoreEntranceListener                                            TravelDistanceListener
                              "100 m içinde mi? 1 dk geçti mi?"                                  "önceki konumla arasındaki
                               → girişi logla + kaydet                                             mesafeyi topla"
```

Servis katmanı yalnızca olayı üretip yayınlar; **iş kuralları dinleyicilerde**. Bu, servisi ince ve test edilebilir tutuyor.

---

## 3. Tasarım kararları (neden böyle?)

### 3.1 Mesafeyi neden Haversine ile hesapladım?

İki koordinat arası mesafede düz (Öklit/Pisagor) hesap yanıltıcıdır, çünkü dünya küresel. Bu yüzden iki nokta arası kuş uçuşu mesafeyi, dünyanın eğriliğini hesaba katan **Haversine formülü** ile hesaplıyorum. 100 m gibi küçük bir eşikte bile doğru sonuç vermesi önemliydi.

### 3.2 Tasarım Örüntüsü #1 — Strategy (Mesafe Hesabı)

Mesafe hesabını doğrudan iş koduna gömmedim; `DistanceCalculator` adında bir **arayüz** tanımlayıp `HaversineDistanceCalculator` ile gerçekledim. Gerekçem: yarın "kuş uçuşu değil, gerçek yol mesafesini istiyoruz" denirse (örneğin bir harita servisiyle), kodun geri kalanına dokunmadan sadece yeni bir gerçekleştirim yazıp geçebilirim. Yani mesafe **algoritması** değişebilir, kullanan kod sabit kalır. Bu, hem Open/Closed hem de Dependency Inversion prensiplerine uyuyor.

### 3.3 Tasarım Örüntüsü #2 — Observer (Konum Olayı)

Tek bir konum geldiğinde aslında **iki bağımsız iş** yapılması gerekiyor: (1) mağaza girişi var mı diye bakmak, (2) toplam mesafeyi güncellemek. Bunları tek bir metoda doldurmak yerine, bir **olay** yayınlayıp iki ayrı **dinleyiciye** dağıttım:

- `CourierLocationPublisher` → olayı yayınlayan taraf (subject)
- `StoreEntranceListener` ve `TravelDistanceListener` → olaya tepki veren gözlemciler

Bunun iki faydası var: Her dinleyici **tek bir işten** sorumlu (Single Responsibility), birbirlerinden habersizler. Ayrıca yarın "bir de şu olunca bildirim gönderelim" denirse, mevcut kodu hiç ellemeden yeni bir `@Component` dinleyici eklemek yetiyor. Bunu bilerek Spring'in hazır event mekanizması yerine elle kurdum; çünkü mülakatın amacı örüntüyü kendimin kurgulayabildiğini göstermek.

> Not: `StoreCatalog` da etkin biçimde bir **Singleton** (mağazalar bir kez yükleniyor) ve JSON okuma `loadStores()` içinde küçük bir **fabrika** gibi toplanmış durumda. Yani örüntüler iki ile sınırlı değil.

### 3.4 1 dakika kuralı — projedeki en kritik kararım

Burada iki yol vardı:

- **Yol A (yaygın):** Anahtarı `kurye + mağaza` olan, **1 dakika TTL** ile kendini boşaltan bir bellek cache (ör. Caffeine `expireAfterWrite`). Giriş olunca yaz, 1 dk sonra otomatik silinsin.
- **Yol B (benim seçimim):** Kararı **olayın kendi zaman damgasına** göre vermek; duvar saatine (wall-clock) göre değil.

Ben Yol B'yi seçtim. Çünkü bu bir **akış (stream)** ve akışlar yeniden oynatılabilir (replay), toplu (batch) ya da gecikmeli gelebilir. Cache'in TTL'i sunucunun o anki saatine bakar; oysa veride zaten olayın gerçekleştiği zaman var. Geçmişe ait kayıtları işlerken duvar saati yanlış karar verdirir. Olay zamanına bakınca kural hem **doğru** hem de **deterministik** oluyor — testlerde 1 dakika beklemek (`Thread.sleep`) gerekmiyor, sadece zaman damgalarını veriyorum.

Bu yüzden son sayılan giriş zamanlarını `ConcurrentHashMap<"kurye::mağaza", Instant>` içinde tutuyorum; her bildirimde olay zamanı ile son giriş arasındaki fark 1 dakikadan küçükse girişi yok sayıyorum. (Caffeine de kullanılabilirdi; ama yukarıdaki nedenlerle olay-zamanlı yaklaşım bu vaka için daha doğru.)

### 3.5 stores.json'u neden bir kez okuyorum?

Dosyayı her konum bildiriminde okumak büyük israf olurdu. `@PostConstruct` ile uygulama açılırken bir kez okuyup **değişmez bir listede** tutuyorum. Sıcak yolda (her konum işlenirken) disk/IO maliyeti olmuyor.

### 3.6 Eşikleri neden konfigürasyona aldım?

100 m yarıçap ve 60 sn pencere koda gömülü değil; `application.yml` içinde `courier-tracking.store.*` altından geliyor. Böylece değerler yeniden derlemeden değişebilir. (Üretimdeki sistemlerimizde bu tür eşikleri bir config kaynağından okuduğum yaklaşımın sade hali.)

---

## 4. Eşzamanlılık (Thread Safety)

Akan veride aynı anda çok sayıda kuryeden bildirim gelebilir; üstelik aynı kuryeye ait ardışık bildirimler farklı iş parçacıklarında işlenebilir. Yarış durumlarını (race condition) önlemek için:

- **Toplam mesafe:** `ConcurrentHashMap.compute(...)` ile okuma-hesaplama-yazma adımı tek bir kurye anahtarı üzerinde **atomik** yürütülür. `CourierState` **değişmez (immutable)** olduğundan her güncelleme yeni bir nesne üretir; kilit alanı dar kalır.
- **1 dakika kuralı:** Son giriş zamanı yine `ConcurrentHashMap.compute(...)` ile atomik kontrol edilip güncellenir; "kontrol et ve yaz" arasına başka bir iş parçacığının girmesi mümkün değildir.
- **Giriş kayıtları:** Eşzamanlı yazmaya uygun `ConcurrentLinkedQueue` kullanılır.
- **Mağaza kataloğu:** Yüklemeden sonra değişmez (`List.copyOf`), bu yüzden eşzamanlı okuma güvenlidir.

---

## 5. Ölçeklenebilirlik Notu

Şu an 5 mağaza olduğu için her konum bildiriminde mağazaları tek tek dolaşmak (`O(N)`) tamamen sorunsuz. Ama mağaza sayısı **50.000** olsaydı, her kurye hareketinde hepsini taramak sistemi yorardı. O ölçekte coğrafi indeksleme kullanırdım:

- **Redis GEO** (`GEOSEARCH` / `GEORADIUS`) — dakikada milyonlarca konum sorgusu için in-memory ve hızlı.
- **Geohash** ile ön filtreleme (yalnızca komşu hücrelere bakmak).
- **PostGIS** / **R-Tree** tabanlı uzamsal indeksler (kalıcı, SQL ile sorgulanabilir).

Mesafe hesabını `DistanceCalculator` arkasına aldığım için, böyle bir geçişte iş mantığının geri kalanı değişmeden kalır.

---

## 6. Daha önce yaptığım benzer çalışmalar

Bu vaka, ürünlerimizde üzerinde çalıştığım coğrafi konum problemlerine çok benziyor; yukarıdaki tercihlerim de bu deneyimden geliyor:

- **Yakındaki durakları bulma (Redis GEO).** Bir backoffice servisinde, kullanıcının enlem/boylamına göre belirli bir yarıçap içindeki taksi duraklarını döndüren bir uç geliştirdim. Yarıçap eşiği bir app-config değerinden (`station-visit.distance.threshold.meters`) okunuyor; yakınlık sorgusu Redis GEO (`opsForGeo().radius(...)`, `Circle`/`Point`/`Distance`/`Metrics`) ile yapılıyordu. Sürücü yalnızca **yakınındaki** bir durağa ziyaret açabiliyordu — yani buradaki "100 m yarıçap" mantığının üretimdeki bir karşılığı.
- **Gerçek zamanlı en yakın durak/şube tespiti (Go).** Başka bir serviste (Go + WebSocket), araçtan akan konum verisinde 1 km içindeki en yakın şubeyi Redis `GeoSearch` ile mesafeye göre sıralayıp bularak olay yayınladım. Buradaki "akan konum + yakınlık + mesafeye göre değerlendirme" deseni, bu vakadaki streaming yaklaşımıyla birebir örtüşüyor.
- **Gerçek zamanlı ısı haritası + konum tabanlı kampanya (geopos).** Her başarılı POS işleminin GPS konumunu, dış kaynaklı etkinlik verileriyle (konser, maç vb.) birleştirip anlık bir ısı haritası (heat map) üreten bir akış servisi geliştirdim. Statik raporlar yerine canlı veri akışıyla çalışıyordu — ısı haritaları, tahmin modelleri, anomali tespiti; bir kullanıcı/araç belirli bir yoğunluk bölgesine girince bildirim ve kampanya tetikleniyordu. Buradaki "akan konum + bir alana girince aksiyon üret" deseni, bu vakadaki 100 m yarıçapına girince giriş tetikleme mantığıyla doğrudan örtüşüyor.

Bu yüzden bu projede de mesafe hesabını soyutladım, eşiği konfigürasyona taşıdım ve "ölçeklenince Redis GEO'ya geçerim" notunu bilerek ekledim.

---

## 7. Varsayımlar

- Bir kuryeye ait bildirimler **sıralı** gelir (aynı cihazdan akış). Sıra garanti edilmeseydi, mesafe birikiminden önce zaman damgasına göre sıralama yapardım.
- "Giriş" anı olarak olay zaman damgası esas alınır; bildirim zaman damgasız gelirse sunucu saati kullanılır.
- Toplam mesafe, ardışık konumlar arası Haversine mesafelerinin toplamıdır (segment yaklaşımı).
- Kalıcılık vaka kapsamı dışında olduğundan durum bellekte tutulur; uygulama yeniden başlarsa sıfırlanır.
- Streaming girişi REST `POST` ile modellenmiştir. Üretimde bu büyük olasılıkla bir Kafka tüketicisi olurdu; servis katmanı zaten bundan bağımsız tasarlandı.

---

