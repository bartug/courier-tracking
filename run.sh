#!/usr/bin/env bash
#
# Courier Tracking - uygulamayi kolayca ayaga kaldirma scripti.
# Kullanim: ./run.sh
#
set -euo pipefail
cd "$(dirname "$0")"

# Proje Java 21 ile derlenir. Kabugun varsayilan JDK'si daha eski olabilecegi icin
# (UnsupportedClassVersionError'i onlemek adina) macOS'ta 21+ bir JDK secip JAVA_HOME'a atariz.
if [ -x /usr/libexec/java_home ]; then
  if JH="$(/usr/libexec/java_home -v 21 2>/dev/null)"; then
    export JAVA_HOME="$JH"
  else
    echo "UYARI: Java 21+ bir JDK bulunamadi. Lutfen JDK 21 (veya ustu) kurun." >&2
  fi
fi

echo "Courier Tracking baslatiliyor -> http://localhost:8080"
echo "Swagger UI                    -> http://localhost:8080/swagger-ui.html"
echo "JAVA_HOME                     -> ${JAVA_HOME:-kabuk varsayilani}"
echo

# Sistemde Maven kurulu olmasa da calissin diye once Maven Wrapper kullanilir.
if [ -x "./mvnw" ]; then
  ./mvnw spring-boot:run
else
  mvn spring-boot:run
fi
