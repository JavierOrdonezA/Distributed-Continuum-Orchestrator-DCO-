package com.example.dco.model;

import java.util.Objects;

public final class Transition {

  private final String fromState;
  private final String eventName;
  private final String toState;

  public Transition(final String fromState, final String eventName, final String toState) {
    this.fromState = requireValue(fromState, "fromState");
    this.eventName = requireValue(eventName, "eventName");
    this.toState = requireValue(toState, "toState");
  }

  private static String requireValue(final String value, final String fieldName) {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException(fieldName + " must not be null or empty");
    }
    return value.trim();
  }

  public String getFromState() {
    return fromState;
  }

  public String getEventName() {
    return eventName;
  }

  public String getToState() {
    return toState;
  }

  @Override
  public boolean equals(final Object otherObject) {
    if (this == otherObject) {
      return true;
    }
    if (!(otherObject instanceof Transition)) {
      return false;
    }
    final Transition otherTransition = (Transition) otherObject;
    return Objects.equals(fromState, otherTransition.fromState)
        && Objects.equals(eventName, otherTransition.eventName)
        && Objects.equals(toState, otherTransition.toState);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fromState, eventName, toState);
  }

  @Override
  public String toString() {
    return "Transition{from='"
        + fromState
        + "', event='"
        + eventName
        + "', to='"
        + toState
        + "'}";
  }
}
