#!/usr/bin/env bash
#
# Courier Tracking - DEMO 1: Tek kurye, zengin rota.
# Bir kuryenin uzun bir rota boyunca birden cok magazaya girisini, 1 dakika tekrar
# giris kuralini ve toplam mesafe birikimini gosterir.
#
# Once baska bir terminalde "./run.sh" ile uygulamayi baslatin, sonra: ./demo1.sh
#
set -euo pipefail
BASE="${BASE:-http://localhost:8080}"
COURIER="courier-1"

pretty() { if command -v python3 >/dev/null 2>&1; then python3 -m json.tool; else cat; fi; }

echo "Uygulamanin hazir olmasi bekleniyor ($BASE) ..."
curl -s --retry 60 --retry-delay 1 --retry-connrefused -o /dev/null "$BASE/api/v1/stores"
echo "Hazir."; echo

post() {
  curl -s -X POST "$BASE/api/v1/couriers/locations" -H 'Content-Type: application/json' \
    -d "{\"courierId\":\"$COURIER\",\"latitude\":$1,\"longitude\":$2,\"timestamp\":\"$3\"}" \
    -o /dev/null -w "  $3  ($1, $2) -> HTTP %{http_code}\n"
}

echo "== $COURIER rota akisi (Anadolu yakasi) =="
post 40.9800000 29.1300000 "2026-06-27T09:00:00Z"   # uzak baslangic
post 40.9900000 29.1260000 "2026-06-27T09:02:00Z"   # yaklasiyor
post 40.9923307 29.1244229 "2026-06-27T09:05:00Z"   # Atasehir MMM (GIRIS)
post 40.9924000 29.1244500 "2026-06-27T09:05:30Z"   # 30 sn sonra ayni yer (SAYILMAZ)
post 40.9861060 29.1161293 "2026-06-27T09:10:00Z"   # Novada MMM (GIRIS)
post 40.9700000 29.0900000 "2026-06-27T09:20:00Z"   # yolda
post 40.9632463 29.0630908 "2026-06-27T09:30:00Z"   # Caddebostan MMM (GIRIS)
post 40.9500000 29.0400000 "2026-06-27T09:35:00Z"   # uzaklasiyor
echo

echo "== Toplam mesafe =="
curl -s "$BASE/api/v1/couriers/$COURIER/total-distance" | pretty
echo

echo "== Magaza girisleri (3 giris beklenir: Atasehir, Novada, Caddebostan; 09:05:30 SAYILMAZ) =="
curl -s "$BASE/api/v1/couriers/$COURIER/entrances" | pretty
