package com.example.dco.functions;

import com.example.dco.runtime.FunctionRegistry;
import java.util.Objects;
import java.util.function.Consumer;

public final class RailCrossingFunctions {

  private RailCrossingFunctions() {}

  public static FunctionRegistry create(
      final String machineId, final Consumer<String> messageConsumer) {
    Objects.requireNonNull(messageConsumer, "messageConsumer must not be null");

    final FunctionRegistry registry = new FunctionRegistry();
    registry.register(
        "gate_up",
        context -> messageConsumer.accept(machineId + " -> call up"));
    registry.register(
        "gate_down",
        context -> messageConsumer.accept(machineId + " -> call down"));
    registry.register(
        "light_on",
        context -> messageConsumer.accept(machineId + " -> call on"));
    registry.register(
        "light_off",
        context -> messageConsumer.accept(machineId + " -> call off"));
    return registry;
  }
}
