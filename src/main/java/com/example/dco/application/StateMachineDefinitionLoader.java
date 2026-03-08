package com.example.dco.application;

import com.example.dco.model.Action;
import com.example.dco.model.StateDefinition;
import com.example.dco.model.StateMachineDefinition;
import com.example.dco.model.Transition;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class StateMachineDefinitionLoader {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private StateMachineDefinitionLoader() {}

  public static Map<String, StateMachineDefinition> loadFromResource(final String resourcePath) {
    try (InputStream inputStream =
        StateMachineDefinitionLoader.class.getResourceAsStream(resourcePath)) {
      if (inputStream == null) {
        throw new IllegalStateException("Resource not found: " + resourcePath);
      }

      final JsonNode root = OBJECT_MAPPER.readTree(inputStream);
      final JsonNode machinesNode = root.path("machines");
      if (!machinesNode.isArray()) {
        throw new IllegalStateException("Expected 'machines' array in " + resourcePath);
      }

      final Map<String, StateMachineDefinition> definitions = new HashMap<>();
      for (final JsonNode machineNode : machinesNode) {
        final StateMachineDefinition definition = parseMachine(machineNode);
        definitions.put(definition.getMachineId(), definition);
      }
      return definitions;
    } catch (final IOException exception) {
      throw new IllegalStateException(
          "Failed to load state machine definitions from " + resourcePath, exception);
    }
  }

  private static StateMachineDefinition parseMachine(final JsonNode machineNode) {
    final String machineId = text(machineNode, "machineId");
    final String initialState = text(machineNode, "initialState");

    final Map<String, StateDefinition> states = new HashMap<>();
    for (final JsonNode stateNode : machineNode.path("states")) {
      final String stateName = text(stateNode, "name");
      final List<Action> actions = new ArrayList<>();
      for (final JsonNode actionNode : stateNode.path("entryActions")) {
        final String actionType = text(actionNode, "type");
        final String target = text(actionNode, "target");
        if ("RAISE_EVENT".equalsIgnoreCase(actionType)) {
          actions.add(Action.raiseEvent(target));
        } else if ("CALL_FUNCTION".equalsIgnoreCase(actionType)) {
          actions.add(Action.callFunction(target));
        } else {
          throw new IllegalStateException("Unsupported action type: " + actionType);
        }
      }
      states.put(stateName, new StateDefinition(stateName, actions));
    }

    final List<Transition> transitions = new ArrayList<>();
    for (final JsonNode transitionNode : machineNode.path("transitions")) {
      transitions.add(
          new Transition(
              text(transitionNode, "from"),
              text(transitionNode, "event"),
              text(transitionNode, "to")));
    }

    return new StateMachineDefinition(machineId, initialState, states, transitions);
  }

  private static String text(final JsonNode node, final String field) {
    final JsonNode value = node.get(field);
    if (value == null || value.asText().trim().isEmpty()) {
      throw new IllegalStateException("Missing or empty field: " + field);
    }
    return value.asText().trim();
  }
}
