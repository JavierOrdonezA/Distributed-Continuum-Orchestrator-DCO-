# Distributed Continuum Orchestrator (DCO)

This repository implements the railway-crossing scenario from `DPS_CSM_Application_Test_v2.pdf` using a model-driven finite state machine runtime.

## What is implemented

- Generic model for states, events, transitions, and entry actions.
- Runtime engine that executes transitions and Moore semantics (actions run when entering states).
- Rail crossing application made of three state machines:
  - `controller`
  - `gate`
  - `light`
- External application description loaded from `src/main/resources/fsm/rail_crossing.json`.
- Distributed event exchange through HTTP (`/event`).
- Unit and integration tests that verify the PDF transition logic.

## Controller transitions (aligned with PDF)

- `away --seen--> approach`
- `approach --¬seen--> close`
- `close --seen--> present`
- `present --¬seen--> leaving`
- `leaving --seen--> left`
- `left --¬seen--> away`

Entry actions:

- Entering `approach` raises `approaching`.
- Entering `leaving` raises `leaving`.

## Project structure

- `src/main/java/com/example/dco/model`: model abstractions.
- `src/main/java/com/example/dco/runtime`: FSM engine and execution support.
- `src/main/java/com/example/dco/application`: rail crossing FSM definitions.
- `src/main/java/com/example/dco/adapters/http`: HTTP adapter for distributed communication.
- `src/main/java/com/example/dco/functions`: callable functions for Moore actions.
- `src/test/java/com/example/dco`: tests.

## Run locally

Requirements:

- Java 17+
- `curl` and `unzip` (the included `gradlew` downloads Gradle automatically)

Commands:

```bash
./gradlew clean test
./gradlew run --args='--role=controller --auto-simulate=true --exit-after-simulation=true --peers=http://localhost:8080/event,http://localhost:8081/event'
```

## Run distributed with Docker Compose

```bash
docker compose up --build
```

Health/state checks:

```bash
curl http://localhost:8080/state
curl http://localhost:8081/state
curl http://localhost:8082/state
```

Send manual sensor events to controller:

```bash
curl -X POST http://localhost:8082/event -H 'Content-Type: application/json' -d '{"name":"seen"}'
curl -X POST http://localhost:8082/event -H 'Content-Type: application/json' -d '{"name":"not_seen"}'
```

## CI

Workflow: `.github/workflows/main.yml`

- Build + tests + JaCoCo report
- Docker image smoke build

## Notes on Gradle Wrapper

This repository includes a lightweight wrapper (`gradlew` / `gradlew.bat`) that downloads and uses Gradle `8.10.2`.
You can replace it with the official Gradle Wrapper (`gradle wrapper`) if you prefer standard wrapper files.
