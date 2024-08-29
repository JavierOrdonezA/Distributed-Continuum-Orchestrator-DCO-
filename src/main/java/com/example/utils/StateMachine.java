package com.example.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Abstract class representing a state machine. This class provides basic
 * functionalities to manage
 * state transitions and notify listeners.
 */
public abstract class StateMachine {

  protected String currentState; // NOPMD
  private final List<StateMachine> listeners; // NOPMD

  /**
   * Constructs a StateMachine with an initial state.
   *
   * @param initialState the initial state of the state machine, must not be null
   *                     or empty
   * @throws IllegalArgumentException if initialState is null or empty
   */
  protected StateMachine(final String initialState) {
    if (initialState == null || initialState.trim().isEmpty()) {
      throw new IllegalArgumentException("Initial state must not be null or empty");
    }
    this.currentState = initialState;
    this.listeners = new ArrayList<>();
  }

  /**
   * Adds a listener to be notified when this state machine transitions.
   *
   * @param listener the state machine listener to add, must not be null
   * @throws NullPointerException if listener is null
   */
  public void addListener(final StateMachine listener) {
    if (listener == null) {
      throw new NullPointerException("Listener must not be null");
    }
    listeners.add(listener);
  }

  /**
   * Notifies all listeners of an event.
   *
   * @param event the event to notify listeners of, must not be null
   */
  protected void notifyListeners(final Event event) {
    if (event == null) {
      throw new NullPointerException("Event must not be null");
    }
    for (final StateMachine listener : listeners) {
      listener.handleEvent(event);
    }
  }

  /**
   * Handles an incoming event, causing the state machine to transition if
   * necessary.
   *
   * @param event the event to handle, must not be null
   */
  public abstract void handleEvent(Event event);

  /**
   * Returns the current state of the state machine.
   *
   * @return the current state, never null or empty
   */
  public String getCurrentState() {
    return currentState;
  }

  @Override
  public String toString() {
    return "StateMachine{currentState='" + currentState + "'}";
  }

  @Override
  public boolean equals(final Object otherObject) {
    if (this == otherObject) {
      return true; // NOPMD - Multiple return statements improve readability
    }
    if (otherObject == null || getClass() != otherObject.getClass()) {
      return false; // NOPMD - Multiple return statements improve readability
    }
    final StateMachine otherStateMachine = (StateMachine) otherObject;
    return Objects.equals(currentState, otherStateMachine.currentState)
        && Objects.equals(listeners, otherStateMachine.listeners);
  }

  @Override
  public int hashCode() {
    return Objects.hash(currentState, listeners);
  }
}
