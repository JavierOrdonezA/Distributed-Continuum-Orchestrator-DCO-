package com.example.dco.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.dco.model.Action;
import com.example.dco.model.StateMachineDefinition;
import org.junit.jupiter.api.Test;

class RailCrossingDefinitionTest {

  @Test
  void controllerTransitionsMatchPdfSpecification() {
    final StateMachineDefinition controller = RailCrossingApplication.controllerDefinition();

    assertTransition(controller, "away", RailCrossingApplication.SEEN, "approach");
    assertTransition(controller, "approach", RailCrossingApplication.NOT_SEEN, "close");
    assertTransition(controller, "close", RailCrossingApplication.SEEN, "present");
    assertTransition(controller, "present", RailCrossingApplication.NOT_SEEN, "leaving");
    assertTransition(controller, "leaving", RailCrossingApplication.SEEN, "left");
    assertTransition(controller, "left", RailCrossingApplication.NOT_SEEN, "away");
  }

  @Test
  void controllerRaisesApproachingAndLeavingOnStateEntry() {
    final StateMachineDefinition controller = RailCrossingApplication.controllerDefinition();

    assertEquals(
        Action.raiseEvent(RailCrossingApplication.APPROACHING),
        controller.getState("approach").getEntryActions().get(0));
    assertEquals(
        Action.raiseEvent(RailCrossingApplication.LEAVING),
        controller.getState("leaving").getEntryActions().get(0));
  }

  private static void assertTransition(
      final StateMachineDefinition definition,
      final String from,
      final String event,
      final String to) {
    assertTrue(
        definition
            .findTransition(from, event)
            .map(transition -> transition.getToState().equals(to))
            .orElse(false),
        "Missing transition: " + from + " --" + event + "--> " + to);
  }
}
