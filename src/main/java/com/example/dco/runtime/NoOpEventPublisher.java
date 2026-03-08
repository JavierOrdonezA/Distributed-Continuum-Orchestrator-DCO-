package com.example.dco.runtime;

import com.example.dco.model.Event;

public final class NoOpEventPublisher implements EventPublisher {
  @Override
  public void publish(final String sourceMachineId, final Event event) {
    // Intentionally left blank.
  }
}
