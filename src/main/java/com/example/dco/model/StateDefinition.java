package com.example.dco.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class StateDefinition {

  private final String name;
  private final List<Action> entryActions;

  public StateDefinition(final String name, final List<Action> entryActions) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("State name must not be null or empty");
    }
    this.name = name.trim();
    if (entryActions == null) {
      this.entryActions = Collections.emptyList();
    } else {
      this.entryActions = Collections.unmodifiableList(new ArrayList<>(entryActions));
    }
  }

  public static StateDefinition withoutActions(final String name) {
    return new StateDefinition(name, Collections.emptyList());
  }

  public String getName() {
    return name;
  }

  public List<Action> getEntryActions() {
    return entryActions;
  }

  @Override
  public boolean equals(final Object otherObject) {
    if (this == otherObject) {
      return true;
    }
    if (!(otherObject instanceof StateDefinition)) {
      return false;
    }
    final StateDefinition otherState = (StateDefinition) otherObject;
    return Objects.equals(name, otherState.name)
        && Objects.equals(entryActions, otherState.entryActions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, entryActions);
  }
}
