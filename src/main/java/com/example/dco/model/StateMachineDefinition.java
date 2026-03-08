package com.example.dco.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class StateMachineDefinition {

  private final String machineId;
  private final String initialState;
  private final Map<String, StateDefinition> states;
  private final List<Transition> transitions;

  public StateMachineDefinition(
      final String machineId,
      final String initialState,
      final Map<String, StateDefinition> states,
      final List<Transition> transitions) {
    this.machineId = requireValue(machineId, "machineId");
    this.initialState = requireValue(initialState, "initialState");

    if (states == null || states.isEmpty()) {
      throw new IllegalArgumentException("states must not be null or empty");
    }
    final Map<String, StateDefinition> copyStates = new HashMap<>(states);
    if (!copyStates.containsKey(this.initialState)) {
      throw new IllegalArgumentException(
          "initialState must exist in states: " + this.initialState);
    }
    this.states = Collections.unmodifiableMap(copyStates);

    if (transitions == null) {
      throw new IllegalArgumentException("transitions must not be null");
    }
    final List<Transition> copyTransitions = new ArrayList<>(transitions);
    validateTransitions(copyTransitions, this.states);
    this.transitions = Collections.unmodifiableList(copyTransitions);
  }

  private static String requireValue(final String value, final String fieldName) {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException(fieldName + " must not be null or empty");
    }
    return value.trim();
  }

  private static void validateTransitions(
      final List<Transition> transitions, final Map<String, StateDefinition> states) {
    final Set<String> keys = new HashSet<>();
    for (final Transition transition : transitions) {
      if (!states.containsKey(transition.getFromState())) {
        throw new IllegalArgumentException(
            "Unknown fromState in transition: " + transition.getFromState());
      }
      if (!states.containsKey(transition.getToState())) {
        throw new IllegalArgumentException(
            "Unknown toState in transition: " + transition.getToState());
      }
      final String key = transition.getFromState() + "|" + transition.getEventName();
      if (!keys.add(key)) {
        throw new IllegalArgumentException("Duplicate transition for key: " + key);
      }
    }
  }

  public String getMachineId() {
    return machineId;
  }

  public String getInitialState() {
    return initialState;
  }

  public Map<String, StateDefinition> getStates() {
    return states;
  }

  public List<Transition> getTransitions() {
    return transitions;
  }

  public StateDefinition getState(final String stateName) {
    final StateDefinition state = states.get(stateName);
    if (state == null) {
      throw new IllegalArgumentException("Unknown state: " + stateName);
    }
    return state;
  }

  public Optional<Transition> findTransition(final String fromState, final String eventName) {
    return transitions.stream()
        .filter(
            transition ->
                transition.getFromState().equals(fromState)
                    && transition.getEventName().equals(eventName))
        .findFirst();
  }

  @Override
  public boolean equals(final Object otherObject) {
    if (this == otherObject) {
      return true;
    }
    if (!(otherObject instanceof StateMachineDefinition)) {
      return false;
    }
    final StateMachineDefinition otherDefinition = (StateMachineDefinition) otherObject;
    return Objects.equals(machineId, otherDefinition.machineId)
        && Objects.equals(initialState, otherDefinition.initialState)
        && Objects.equals(states, otherDefinition.states)
        && Objects.equals(transitions, otherDefinition.transitions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(machineId, initialState, states, transitions);
  }
}
