package com.example.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ControllerStateMachineTest {

  private ControllerStateMachine stateMachine;

  @BeforeEach
  void setUp() {
    stateMachine = new ControllerStateMachine();
  }

  @Test
  void testInitialState() {
    assertEquals("Away", stateMachine.getCurrentState(), "Initial state should be 'Away'");
  }

  @Test
  void testTransitionFromAwayToApproach() {
    stateMachine.processSensorInput(true); // Train detected
    assertEquals(
        "Approach",
        stateMachine.getCurrentState(),
        "State should transition to 'Approach' when train is seen");
  }

  @Test
  void testTransitionFromApproachToClose() {
    stateMachine.processSensorInput(true); // Train detected, transition to Approach
    stateMachine.processSensorInput(true); // Train still detected, transition to Close
    assertEquals(
        "Close",
        stateMachine.getCurrentState(),
        "State should transition to 'Close' when train is still seen in 'Approach'");
  }

  @Test
  void testTransitionFromCloseToPresent() {
    stateMachine.processSensorInput(true); // Train detected, transition to Approach
    stateMachine.processSensorInput(true); // Train still detected, transition to Close
    stateMachine.processSensorInput(true); // Train still detected, transition to Present
    assertEquals(
        "Present",
        stateMachine.getCurrentState(),
        "State should transition to 'Present' when train is still seen in 'Close'");
  }

  @Test
  void testTransitionFromPresentToLeaving() {
    stateMachine.processSensorInput(true); // Train detected, transition to Approach
    stateMachine.processSensorInput(true); // Train still detected, transition to Close
    stateMachine.processSensorInput(true); // Train still detected, transition to Present
    stateMachine.processSensorInput(false); // Train not detected, transition to Leaving
    assertEquals(
        "Leaving",
        stateMachine.getCurrentState(),
        "State should transition to 'Leaving' when train is no longer seen in 'Present'");
  }

  @Test
  void testTransitionFromLeavingToLeft() {
    stateMachine.processSensorInput(true); // Train detected, transition to Approach
    stateMachine.processSensorInput(true); // Train still detected, transition to Close
    stateMachine.processSensorInput(true); // Train still detected, transition to Present
    stateMachine.processSensorInput(false); // Train not detected, transition to Leaving
    stateMachine.processSensorInput(false); // Train still not detected, transition to Left
    assertEquals(
        "Left",
        stateMachine.getCurrentState(),
        "State should transition to 'Left' when train is still not seen in 'Leaving'");
  }

  @Test
  void testHandleNullEvent() {
    Exception exception =
        assertThrows(
            NullPointerException.class,
            () -> {
              stateMachine.handleEvent(null);
            });
    assertEquals(
        "Event must not be null",
        exception.getMessage(),
        "NullPointerException should be thrown with appropriate message when event is null");
  }

  @Test
  void testIllegalStateTransition() {
    // Verificar que la máquina de estados está en el estado "Away"
    assertEquals("Away", stateMachine.getCurrentState(), "Initial state should be 'Away'");

    // Probar un evento inválido en el estado "Away"
    Exception exception =
        assertThrows(
            IllegalStateException.class,
            () -> {
              stateMachine.handleEvent(new Event("Invalid"));
            });

    // Verificar que el mensaje de la excepción contiene la información esperada
    assertTrue(
        exception.getMessage().contains("Unexpected event 'Invalid' in state 'Away'"),
        "IllegalStateException should be thrown for unexpected event in state 'Away'");
  }
}
