package com.example.dco.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.dco.functions.RailCrossingFunctions;
import com.example.dco.model.Event;
import com.example.dco.runtime.EventPublisher;
import com.example.dco.runtime.NoOpEventPublisher;
import com.example.dco.runtime.StateMachineRuntime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class RailCrossingIntegrationTest {

  @Test
  void trainPassageSequenceTransitionsAllThreeMachinesAsExpected() {
    final List<String> raisedByController = new ArrayList<>();
    final List<String> gateCalls = new ArrayList<>();
    final List<String> lightCalls = new ArrayList<>();

    final StateMachineRuntime gate =
        new StateMachineRuntime(
            RailCrossingApplication.gateDefinition(),
            RailCrossingFunctions.create("gate", gateCalls::add),
            new NoOpEventPublisher());

    final StateMachineRuntime light =
        new StateMachineRuntime(
            RailCrossingApplication.lightDefinition(),
            RailCrossingFunctions.create("light", lightCalls::add),
            new NoOpEventPublisher());

    final EventPublisher controllerPublisher =
        (sourceMachineId, event) -> {
          raisedByController.add(event.getName());
          gate.handleEvent(event);
          light.handleEvent(event);
        };

    final StateMachineRuntime controller =
        new StateMachineRuntime(
            RailCrossingApplication.controllerDefinition(),
            RailCrossingFunctions.create("controller", message -> {}),
            controllerPublisher);

    gate.start();
    light.start();
    controller.start();

    controller.handleEvent(new Event(RailCrossingApplication.SEEN));
    assertEquals("approach", controller.getCurrentState());
    assertEquals("down", gate.getCurrentState());
    assertEquals("on", light.getCurrentState());

    controller.handleEvent(new Event(RailCrossingApplication.NOT_SEEN));
    assertEquals("close", controller.getCurrentState());

    controller.handleEvent(new Event(RailCrossingApplication.SEEN));
    assertEquals("present", controller.getCurrentState());

    controller.handleEvent(new Event(RailCrossingApplication.NOT_SEEN));
    assertEquals("leaving", controller.getCurrentState());
    assertEquals("up", gate.getCurrentState());
    assertEquals("off", light.getCurrentState());

    controller.handleEvent(new Event(RailCrossingApplication.SEEN));
    assertEquals("left", controller.getCurrentState());

    controller.handleEvent(new Event(RailCrossingApplication.NOT_SEEN));
    assertEquals("away", controller.getCurrentState());

    assertEquals(
        List.of(RailCrossingApplication.APPROACHING, RailCrossingApplication.LEAVING),
        raisedByController);
    assertEquals(List.of("gate -> call up", "gate -> call down", "gate -> call up"), gateCalls);
    assertEquals(
        List.of("light -> call off", "light -> call on", "light -> call off"),
        lightCalls);
  }
}
