package com.example.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StateMachineTest {

  private TestStateMachine testStateMachine;

  // A simple concrete implementation of StateMachine for testing purposes
  private static class TestStateMachine extends StateMachine {

    private final List<Event> receivedEvents = new ArrayList<>();

    public TestStateMachine(String initialState) {
      super(initialState);
    }

    @Override
    public void handleEvent(final Event event) {
      if (event != null) {
        receivedEvents.add(event);
      }
    }

    public List<Event> getReceivedEvents() {
      return receivedEvents;
    }
  }

  @BeforeEach
  void setUp() {
    testStateMachine = new TestStateMachine("Initial");
  }

  @Test
  void testInitialState() {
    assertEquals(
        "Initial",
        testStateMachine.getCurrentState(),
        "Initial state should match the input state");
  }

  @Test
  void testAddListenerAndNotify() {
    TestStateMachine listener = new TestStateMachine("Listener");
    testStateMachine.addListener(listener);

    Event event = new Event("TestEvent");
    testStateMachine.notifyListeners(event);

    assertEquals(1, listener.getReceivedEvents().size(), "Listener should receive one event");
    assertEquals(
        event, listener.getReceivedEvents().get(0), "Listener should receive the correct event");
  }

  @Test
  void testAddNullListener() {
    Exception exception =
        assertThrows(
            NullPointerException.class,
            () -> {
              testStateMachine.addListener(null);
            });
    assertEquals(
        "Listener must not be null",
        exception.getMessage(),
        "NullPointerException should be thrown when adding a null listener");
  }

  @Test
  void testNotifyListenersWithNullEvent() {
    Exception exception =
        assertThrows(
            NullPointerException.class,
            () -> {
              testStateMachine.notifyListeners(null);
            });
    assertEquals(
        "Event must not be null",
        exception.getMessage(),
        "NullPointerException should be thrown when notifying listeners with a null event");
  }

  @Test
  void testEqualsAndHashCode() {
    TestStateMachine stateMachine1 = new TestStateMachine("State1");
    TestStateMachine stateMachine2 = new TestStateMachine("State1");
    TestStateMachine stateMachine3 = new TestStateMachine("State2");

    assertTrue(
        stateMachine1.equals(stateMachine2), "State machines with the same state should be equal");
    assertEquals(
        stateMachine1.hashCode(),
        stateMachine2.hashCode(),
        "State machines with the same state should have the same hash code");

    assertFalse(
        stateMachine1.equals(stateMachine3),
        "State machines with different states should not be equal");
    assertNotEquals(
        stateMachine1.hashCode(),
        stateMachine3.hashCode(),
        "State machines with different states should not have the same hash code");
  }

  @Test
  void testToString() {
    assertEquals(
        "StateMachine{currentState='Initial'}",
        testStateMachine.toString(),
        "toString should return the correct string representation");
  }
}
