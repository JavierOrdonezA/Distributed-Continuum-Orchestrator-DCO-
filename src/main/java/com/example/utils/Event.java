package com.example.utils;

public final class Event {

  private final String name;
  private final int port;

  public Event(final String name, final int port) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Event name must not be null or empty");
    }
    if (port < 0 || port > 65535) {
      throw new IllegalArgumentException("Port must be between 0 and 65535");
    }
    this.name = name;
    this.port = port;
  }

  public String getName() {
    return name;
  }

  public int getPort() {
    return port;
  }

  @Override
  public String toString() {
    return "Event{name='" + name + "', port=" + port + "}";
  }

  @Override
  public boolean equals(final Object otherObject) {
    if (otherObject == null || getClass() != otherObject.getClass()) {
      return false;
    }
    if (this == otherObject) {
      return true;
    }

    final Event otherEvent = (Event) otherObject;
    return name.equals(otherEvent.name) && port == otherEvent.port;
  }

  @Override
  public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + port;
    return result;
  }
}
