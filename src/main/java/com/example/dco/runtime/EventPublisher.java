package com.example.dco.runtime;

import com.example.dco.model.Event;

@FunctionalInterface
public interface EventPublisher {
  void publish(String sourceMachineId, Event event);
}
