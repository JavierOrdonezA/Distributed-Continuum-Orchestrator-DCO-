package com.example.dco.runtime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.dco.model.Action;
import com.example.dco.model.Event;
import com.example.dco.model.StateDefinition;
import com.example.dco.model.StateMachineDefinition;
import com.example.dco.model.Transition;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class StateMachineRuntimeTest {

  @Test
  void executesMooreActionsOnInitialAndTargetStateEntry() {
    final List<String> functionCalls = new ArrayList<>();
    final List<String> raisedEvents = new ArrayList<>();

    final FunctionRegistry registry = new FunctionRegistry();
    registry.register("enter_idle", context -> functionCalls.add(context.getFunctionName()));
    registry.register("enter_active", context -> functionCalls.add(context.getFunctionName()));

    final StateMachineDefinition definition =
        new StateMachineDefinition(
            "demo",
            "idle",
            Map.of(
                "idle", new StateDefinition("idle", List.of(Action.callFunction("enter_idle"))),
                "active",
                    new StateDefinition(
                        "active",
                        List.of(
                            Action.callFunction("enter_active"),
                            Action.raiseEvent("activated")))),
            List.of(new Transition("idle", "go", "active")));

    final StateMachineRuntime runtime =
        new StateMachineRuntime(
            definition,
            registry,
            (sourceMachineId, event) -> raisedEvents.add(event.getName()));

    runtime.start();
    assertEquals("idle", runtime.getCurrentState());
    assertEquals(List.of("enter_idle"), functionCalls);

    final boolean transitioned = runtime.handleEvent(new Event("go"));
    assertTrue(transitioned);
    assertEquals("active", runtime.getCurrentState());
    assertEquals(List.of("enter_idle", "enter_active"), functionCalls);
    assertEquals(List.of("activated"), raisedEvents);
  }

  @Test
  void keepsStateWhenTransitionDoesNotExist() {
    final FunctionRegistry registry = new FunctionRegistry();
    final StateMachineDefinition definition =
        new StateMachineDefinition(
            "demo",
            "only",
            Map.of("only", StateDefinition.withoutActions("only")),
            List.of());

    final StateMachineRuntime runtime =
        new StateMachineRuntime(definition, registry, new NoOpEventPublisher());

    final boolean transitioned = runtime.handleEvent(new Event("unknown"));
    assertFalse(transitioned);
    assertEquals("only", runtime.getCurrentState());
  }
}
