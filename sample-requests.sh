#!/usr/bin/env bash
#
# Courier Tracking - calisan uygulamayi ornek isteklerle deneme scripti.
# Once baska bir terminalde "./run.sh" ile uygulamayi baslatin, ardindan: ./sample-requests.sh
#
set -euo pipefail
BASE="${BASE:-http://localhost:8080}"

echo "Uygulamanin hazir olmasi bekleniyor ($BASE) ..."
curl -s --retry 60 --retry-delay 1 --retry-connrefused -o /dev/null "$BASE/api/v1/stores"
echo "Hazir."
echo

post() {
  curl -s -X POST "$BASE/api/v1/couriers/locations" \
    -H 'Content-Type: application/json' -d "$1" -o /dev/null -w "  konum gonderildi -> HTTP %{http_code}\n"
}

echo "== 1) Yuklu magazalar =="
curl -s "$BASE/api/v1/stores"; echo; echo

echo "== 2) courier-1 icin konum akisi =="
# Atasehir magazasina 16:00:10'da girilir; 16:00:40'taki tekrar giris (30 sn sonra) SAYILMAZ; ardindan Beylikduzu.
post '{"courierId":"courier-1","latitude":40.9900000,"longitude":29.1200000,"timestamp":"2026-06-22T16:00:00Z"}'
post '{"courierId":"courier-1","latitude":40.9923307,"longitude":29.1244229,"timestamp":"2026-06-22T16:00:10Z"}'
post '{"courierId":"courier-1","latitude":40.9923307,"longitude":29.1244229,"timestamp":"2026-06-22T16:00:40Z"}'
post '{"courierId":"courier-1","latitude":41.0066851,"longitude":28.6552262,"timestamp":"2026-06-22T16:30:00Z"}'
echo

echo "== 3) courier-1 toplam mesafe =="
curl -s "$BASE/api/v1/couriers/courier-1/total-distance"; echo; echo

echo "== 4) courier-1 magaza girisleri (Atasehir tek giris + Beylikduzu beklenir) =="
curl -s "$BASE/api/v1/couriers/courier-1/entrances"; echo; echo

echo "== 5) Bilinmeyen kurye -> 404 =="
curl -s "$BASE/api/v1/couriers/ghost/total-distance"; echo
