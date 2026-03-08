package com.example.dco.application;

import com.example.dco.model.StateMachineDefinition;
import java.util.Map;

public final class RailCrossingApplication {

  public static final String SEEN = "seen";
  public static final String NOT_SEEN = "¬seen";
  public static final String APPROACHING = "approaching";
  public static final String LEAVING = "leaving";

  private static final Map<String, StateMachineDefinition> DEFINITIONS =
      StateMachineDefinitionLoader.loadFromResource("/fsm/rail_crossing.json");

  private RailCrossingApplication() {}

  public static StateMachineDefinition controllerDefinition() {
    return definition("controller");
  }

  public static StateMachineDefinition gateDefinition() {
    return definition("gate");
  }

  public static StateMachineDefinition lightDefinition() {
    return definition("light");
  }

  private static StateMachineDefinition definition(final String machineId) {
    final StateMachineDefinition definition = DEFINITIONS.get(machineId);
    if (definition == null) {
      throw new IllegalStateException("Missing machine definition: " + machineId);
    }
    return definition;
  }
}
