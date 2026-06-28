#!/usr/bin/env bash
#
# Courier Tracking - DEMO 2: Cok kurye, bagimsiz takip.
# Uc kuryenin farkli magazalara girisini, kuryelerin birbirinden bagimsiz takip
# edildigini ve 1 dakika kuralini (courier-2 ornegi) gosterir.
#
# Once baska bir terminalde "./run.sh" ile uygulamayi baslatin, sonra: ./demo2.sh
#
set -euo pipefail
BASE="${BASE:-http://localhost:8080}"

pretty() { if command -v python3 >/dev/null 2>&1; then python3 -m json.tool; else cat; fi; }

echo "Uygulamanin hazir olmasi bekleniyor ($BASE) ..."
curl -s --retry 60 --retry-delay 1 --retry-connrefused -o /dev/null "$BASE/api/v1/stores"
echo "Hazir."; echo

post() {
  curl -s -X POST "$BASE/api/v1/couriers/locations" -H 'Content-Type: application/json' \
    -d "{\"courierId\":\"$1\",\"latitude\":$2,\"longitude\":$3,\"timestamp\":\"$4\"}" \
    -o /dev/null -w "  [$1] $4 -> HTTP %{http_code}\n"
}

echo "== courier-2 : Ortakoy -- 1 dakika kurali ornegi =="
post courier-2 41.0560000 29.0230000 "2026-06-28T10:00:00Z"   # yakin ama disarida
post courier-2 41.0557830 29.0210292 "2026-06-28T10:02:00Z"   # Ortakoy (GIRIS)
post courier-2 41.0557830 29.0210292 "2026-06-28T10:02:40Z"   # 40 sn sonra (SAYILMAZ)
post courier-2 41.0557830 29.0210292 "2026-06-28T10:05:00Z"   # 3 dk sonra (tekrar GIRIS)
echo

echo "== courier-3 : Beylikduzu =="
post courier-3 41.0100000 28.6600000 "2026-06-28T10:00:00Z"
post courier-3 41.0066851 28.6552262 "2026-06-28T10:06:00Z"   # Beylikduzu (GIRIS)
echo

echo "== courier-4 : Atasehir cevresi =="
post courier-4 40.9850000 29.1200000 "2026-06-28T10:00:00Z"
post courier-4 40.9923307 29.1244229 "2026-06-28T10:04:00Z"   # Atasehir (GIRIS)
echo

echo "== Kurye basina toplam mesafe =="
for c in courier-2 courier-3 courier-4; do
  echo "  --- $c ---"
  curl -s "$BASE/api/v1/couriers/$c/total-distance" | pretty
done
echo

echo "== Kurye basina magaza girisleri (courier-2'de Ortakoy 2 kez beklenir) =="
for c in courier-2 courier-3 courier-4; do
  echo "  --- $c ---"
  curl -s "$BASE/api/v1/couriers/$c/entrances" | pretty
done
