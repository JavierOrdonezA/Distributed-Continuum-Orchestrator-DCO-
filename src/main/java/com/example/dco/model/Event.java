package com.example.dco.model;

import java.util.Objects;

public final class Event {

  private final String name;

  public Event(final String name) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Event name must not be null or empty");
    }
    this.name = name.trim();
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean equals(final Object otherObject) {
    if (this == otherObject) {
      return true;
    }
    if (!(otherObject instanceof Event)) {
      return false;
    }
    final Event otherEvent = (Event) otherObject;
    return Objects.equals(name, otherEvent.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public String toString() {
    return "Event{name='" + name + "'}";
  }
}
