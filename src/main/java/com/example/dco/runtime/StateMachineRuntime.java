package com.example.dco.runtime;

import com.example.dco.model.Action;
import com.example.dco.model.ActionType;
import com.example.dco.model.Event;
import com.example.dco.model.StateMachineDefinition;
import com.example.dco.model.Transition;
import java.util.Objects;
import java.util.Optional;

public final class StateMachineRuntime {

  private final StateMachineDefinition definition;
  private final FunctionRegistry functionRegistry;
  private final EventPublisher eventPublisher;
  private String currentState;
  private boolean started;

  public StateMachineRuntime(
      final StateMachineDefinition definition,
      final FunctionRegistry functionRegistry,
      final EventPublisher eventPublisher) {
    this.definition = Objects.requireNonNull(definition, "definition must not be null");
    this.functionRegistry =
        Objects.requireNonNull(functionRegistry, "functionRegistry must not be null");
    this.eventPublisher =
        Objects.requireNonNull(eventPublisher, "eventPublisher must not be null");
    this.currentState = definition.getInitialState();
    this.started = false;
  }

  public void start() {
    if (started) {
      return;
    }
    started = true;
    executeEntryActions(currentState);
  }

  public boolean handleEvent(final Event event) {
    Objects.requireNonNull(event, "event must not be null");
    if (!started) {
      start();
    }

    final Optional<Transition> transition =
        definition.findTransition(currentState, event.getName());
    if (!transition.isPresent()) {
      return false;
    }

    currentState = transition.get().getToState();
    executeEntryActions(currentState);
    return true;
  }

  private void executeEntryActions(final String stateName) {
    for (final Action action : definition.getState(stateName).getEntryActions()) {
      if (action.getType() == ActionType.CALL_FUNCTION) {
        functionRegistry.invoke(
            action.getTargetName(),
            new FunctionContext(
                definition.getMachineId(), currentState, action.getTargetName()));
      } else if (action.getType() == ActionType.RAISE_EVENT) {
        eventPublisher.publish(definition.getMachineId(), new Event(action.getTargetName()));
      }
    }
  }

  public String getMachineId() {
    return definition.getMachineId();
  }

  public String getCurrentState() {
    return currentState;
  }
}
