package com.example.dco;

import com.example.dco.adapters.http.HttpBroadcastPublisher;
import com.example.dco.adapters.http.HttpEventServer;
import com.example.dco.application.RailCrossingApplication;
import com.example.dco.functions.RailCrossingFunctions;
import com.example.dco.model.Event;
import com.example.dco.model.StateMachineDefinition;
import com.example.dco.runtime.EventPublisher;
import com.example.dco.runtime.NoOpEventPublisher;
import com.example.dco.runtime.StateMachineRuntime;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class Main {

  private static final String DEFAULT_SENSOR_SEQUENCE =
      "seen,not_seen,seen,not_seen,seen,not_seen";

  private Main() {}

  public static void main(final String[] args) throws Exception {
    final Config config = Config.from(parseArgs(args));

    final StateMachineDefinition definition = definitionForRole(config.role);
    final EventPublisher publisher =
        "controller".equals(config.role)
            ? new HttpBroadcastPublisher(config.peers)
            : new NoOpEventPublisher();

    final StateMachineRuntime runtime =
        new StateMachineRuntime(
            definition,
            RailCrossingFunctions.create(config.role, Main::log),
            publisher);
    runtime.start();

    try (HttpEventServer server =
        new HttpEventServer(
            config.port,
            event -> {
              final Event normalized = normalizeEvent(event);
              final boolean transitioned = runtime.handleEvent(normalized);
              log(
                  ""
                      + runtime.getMachineId()
                      + " received event="
                      + normalized.getName()
                      + " transitioned="
                      + transitioned
                      + " currentState="
                      + runtime.getCurrentState());
            },
            runtime::getCurrentState)) {
      server.start();
      log(config.role + " node listening on port " + config.port);

      if ("controller".equals(config.role) && config.autoSimulate) {
        runSimulation(runtime, config.sensorSequence, config.simulationDelayMs);
        if (config.exitAfterSimulation) {
          return;
        }
      }

      Thread.currentThread().join();
    }
  }

  private static void runSimulation(
      final StateMachineRuntime runtime,
      final List<String> sensorSequence,
      final long delayMs)
      throws InterruptedException {
    for (final String rawEvent : sensorSequence) {
      final Event event = normalizeEvent(new Event(rawEvent));
      runtime.handleEvent(event);
      log(
          "controller simulation event="
              + event.getName()
              + " currentState="
              + runtime.getCurrentState());
      Thread.sleep(delayMs);
    }
  }

  private static Event normalizeEvent(final Event event) {
    final String normalized = event.getName().trim().toLowerCase(Locale.ROOT);
    switch (normalized) {
      case "seen":
        return new Event(RailCrossingApplication.SEEN);
      case "¬seen":
      case "not_seen":
      case "not-seen":
      case "notseen":
        return new Event(RailCrossingApplication.NOT_SEEN);
      case "approaching":
        return new Event(RailCrossingApplication.APPROACHING);
      case "leaving":
        return new Event(RailCrossingApplication.LEAVING);
      default:
        return new Event(event.getName());
    }
  }

  private static StateMachineDefinition definitionForRole(final String role) {
    switch (role) {
      case "controller":
        return RailCrossingApplication.controllerDefinition();
      case "gate":
        return RailCrossingApplication.gateDefinition();
      case "light":
        return RailCrossingApplication.lightDefinition();
      default:
        throw new IllegalArgumentException("Unsupported role: " + role);
    }
  }

  private static void log(final String message) {
    System.out.println("[DCO] " + message);
  }

  private static Map<String, String> parseArgs(final String[] args) {
    final Map<String, String> parsed = new HashMap<>();
    for (final String arg : args) {
      if (!arg.startsWith("--")) {
        continue;
      }
      final int index = arg.indexOf('=');
      if (index <= 2 || index == arg.length() - 1) {
        continue;
      }
      final String key = arg.substring(2, index);
      final String value = arg.substring(index + 1);
      parsed.put(key, value);
    }
    return parsed;
  }

  private static final class Config {

    private final String role;
    private final int port;
    private final boolean autoSimulate;
    private final boolean exitAfterSimulation;
    private final List<String> sensorSequence;
    private final List<URI> peers;
    private final long simulationDelayMs;

    private Config(
        final String role,
        final int port,
        final boolean autoSimulate,
        final boolean exitAfterSimulation,
        final List<String> sensorSequence,
        final List<URI> peers,
        final long simulationDelayMs) {
      this.role = role;
      this.port = port;
      this.autoSimulate = autoSimulate;
      this.exitAfterSimulation = exitAfterSimulation;
      this.sensorSequence = sensorSequence;
      this.peers = peers;
      this.simulationDelayMs = simulationDelayMs;
    }

    private static Config from(final Map<String, String> args) {
      final String role = value(args, "role", "NODE_ROLE", "controller");
      final int port = Integer.parseInt(value(args, "port", "PORT", "8080"));
      final boolean autoSimulate =
          Boolean.parseBoolean(value(args, "auto-simulate", "AUTO_SIMULATE", "false"));
      final boolean exitAfterSimulation =
          Boolean.parseBoolean(
              value(args, "exit-after-simulation", "EXIT_AFTER_SIMULATION", "false"));
      final String sequence =
          value(args, "sensor-sequence", "SENSOR_SEQUENCE", DEFAULT_SENSOR_SEQUENCE);
      final long delayMs =
          Long.parseLong(
              value(args, "simulation-delay-ms", "SIMULATION_DELAY_MS", "250"));
      final String peers = value(args, "peers", "PEERS", "");

      return new Config(
          role,
          port,
          autoSimulate,
          exitAfterSimulation,
          parseCsv(sequence),
          parseUris(peers),
          delayMs);
    }

    private static String value(
        final Map<String, String> args,
        final String argKey,
        final String envKey,
        final String defaultValue) {
      if (args.containsKey(argKey)) {
        return args.get(argKey);
      }
      final String envValue = System.getenv(envKey);
      if (envValue != null && !envValue.trim().isEmpty()) {
        return envValue;
      }
      return defaultValue;
    }

    private static List<String> parseCsv(final String csv) {
      final List<String> values = new ArrayList<>();
      Arrays.stream(csv.split(","))
          .map(String::trim)
          .filter(value -> !value.isEmpty())
          .forEach(values::add);
      return values;
    }

    private static List<URI> parseUris(final String csv) {
      final List<URI> uris = new ArrayList<>();
      for (final String value : parseCsv(csv)) {
        uris.add(URI.create(value));
      }
      return uris;
    }
  }
}
