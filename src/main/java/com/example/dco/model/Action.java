package com.example.dco.model;

import java.util.Objects;

public final class Action {

  private final ActionType type;
  private final String targetName;

  private Action(final ActionType type, final String targetName) {
    if (type == null) {
      throw new IllegalArgumentException("Action type must not be null");
    }
    if (targetName == null || targetName.trim().isEmpty()) {
      throw new IllegalArgumentException("Action target must not be null or empty");
    }
    this.type = type;
    this.targetName = targetName.trim();
  }

  public static Action raiseEvent(final String eventName) {
    return new Action(ActionType.RAISE_EVENT, eventName);
  }

  public static Action callFunction(final String functionName) {
    return new Action(ActionType.CALL_FUNCTION, functionName);
  }

  public ActionType getType() {
    return type;
  }

  public String getTargetName() {
    return targetName;
  }

  @Override
  public boolean equals(final Object otherObject) {
    if (this == otherObject) {
      return true;
    }
    if (!(otherObject instanceof Action)) {
      return false;
    }
    final Action otherAction = (Action) otherObject;
    return type == otherAction.type && Objects.equals(targetName, otherAction.targetName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, targetName);
  }

  @Override
  public String toString() {
    return "Action{type=" + type + ", target='" + targetName + "'}";
  }
}
