FROM gradle:8.10.2-jdk17 AS builder
WORKDIR /workspace
COPY . .
RUN gradle clean installDist -x test --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /workspace/build/install/distributed-continuum-orchestrator-dco/lib /app/lib
EXPOSE 8080
ENTRYPOINT ["java", "-cp", "/app/lib/*", "com.example.dco.Main"]
