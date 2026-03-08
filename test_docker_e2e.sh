#!/usr/bin/env bash                                              # Ejecuta con bash
set -euo pipefail                                                # Falla en errores, vars no definidas y pipes

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"         # Carpeta del script
cd "$ROOT_DIR"                                                   # Ir a la raíz del proyecto

OVERRIDE_FILE="$ROOT_DIR/docker-compose.test.override.yml"       # Archivo temporal de override

cat > "$OVERRIDE_FILE" <<'YAML'                                  # Crea override para tests
services:
  controller-state-machine:
    command:
      [
        "--role=controller",
        "--port=8080",
        "--auto-simulate=false",
        "--peers=http://gate-state-machine:8080/event,http://light-state-machine:8080/event"
      ]
YAML

cleanup() { rm -f "$OVERRIDE_FILE"; }                            # Borra override al salir
trap cleanup EXIT                                                # Ejecuta cleanup siempre al terminar

compose() { docker compose -f docker-compose.yml -f "$OVERRIDE_FILE" "$@"; }  # Helper compose + override

compose down --remove-orphans                                    # Limpia entorno previo
compose up --build -d                                            # Build + arranque en background

wait_http() {                                                    # Espera a que un endpoint responda
  local url="$1"                                                 # URL objetivo
  local retries="${2:-30}"                                       # Reintentos (default 30)
  local delay="${3:-1}"                                          # Espera entre intentos (default 1s)
  local i                                                        # Contador
  for ((i=1; i<=retries; i++)); do                               # Bucle de espera
    curl -fsS "$url" >/dev/null && return 0                      # Si responde, OK
    sleep "$delay"                                               # Si no, espera y reintenta
  done
  echo "ERROR: $url no respondió"                                # Mensaje de timeout
  return 1                                                       # Error
}

state() {                                                        # Lee estado JSON y devuelve solo valor
  local port="$1"                                                # Puerto (8080/8081/8082)
  curl -fsS "http://localhost:${port}/state" | sed -n 's/.*"state":"\([^"]*\)".*/\1/p'  # Parse simple
}

post_event() {                                                   # Envía evento al controller
  local name="$1"                                                # Nombre del evento
  curl -fsS -X POST "http://localhost:8082/event" \
    -H "Content-Type: application/json" \
    -d "{\"name\":\"${name}\"}" >/dev/null                       # POST JSON
  sleep 0.2                                                      # Espera propagación a peers
}

expect_state() {                                                 # Aserción de estado esperado
  local port="$1"                                                # Puerto a comprobar
  local expected="$2"                                            # Estado esperado
  local got                                                      # Estado actual
  got="$(state "$port")"                                         # Leer estado
  if [[ "$got" == "$expected" ]]; then                           # Comparar
    echo "PASS port=${port} state=${got}"                        # Éxito
  else
    echo "FAIL port=${port} expected=${expected} got=${got}"     # Falla
    return 1                                                     # Marca error
  fi
}

wait_http "http://localhost:8080/state"                          # Espera gate
wait_http "http://localhost:8081/state"                          # Espera light
wait_http "http://localhost:8082/state"                          # Espera controller

fails=0                                                          # Contador de fallos

expect_state 8080 up || ((fails++))                              # Inicial gate
expect_state 8081 off || ((fails++))                             # Inicial light
expect_state 8082 away || ((fails++))                            # Inicial controller

post_event seen                                                  # away->approach + emite approaching
expect_state 8080 down || ((fails++))                            # Gate debe bajar
expect_state 8081 on || ((fails++))                              # Light debe encender
expect_state 8082 approach || ((fails++))                        # Controller en approach

post_event not_seen                                              # approach->close
expect_state 8082 close || ((fails++))                           # Controller en close

post_event seen                                                  # close->present
expect_state 8082 present || ((fails++))                         # Controller en present

post_event not_seen                                              # present->leaving + emite leaving
expect_state 8080 up || ((fails++))                              # Gate debe subir
expect_state 8081 off || ((fails++))                             # Light debe apagar
expect_state 8082 leaving || ((fails++))                         # Controller en leaving

post_event seen                                                  # leaving->left
expect_state 8082 left || ((fails++))                            # Controller en left

post_event not_seen                                              # left->away
expect_state 8082 away || ((fails++))                            # Controller vuelve a away

compose ps                                                       # Estado final contenedores

if [[ "$fails" -gt 0 ]]; then                                    # Si hubo fallos
  echo "RESULTADO: FAIL ($fails fallos)"                         # Resumen fail
  compose logs --tail=120 controller-state-machine gate-state-machine light-state-machine  # Logs
  exit 1                                                         # Código error
fi

echo "RESULTADO: PASS"                                           # Resumen OK
