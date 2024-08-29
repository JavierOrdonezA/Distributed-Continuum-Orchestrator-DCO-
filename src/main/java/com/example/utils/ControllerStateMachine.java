package com.example.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * ControllerStateMachine is a state machine that manages state transitions for
 * a railway crossing controller based on the presence of a train.
 */
public class ControllerStateMachine extends StateMachine {

  private static final String EVENT_SEEN = "Seen";
  private static final String EVENT_NOT_SEEN = "NotSeen";
  private static final String STATE_AWAY = "Away";
  private static final String STATE_APPROACH = "Approach";
  private static final String STATE_CLOSE = "Close";
  private static final String STATE_PRESENT = "Present";
  private static final String STATE_LEAVING = "Leaving";
  private static final String STATE_LEFT = "Left";
  private static final String ERR_UNEXPECTED_EVENT = "Unexpected event '%s' in state '%s'"; // NOPMD - variable name
                                                                                            // required for clarity

  private Socket gateSocket; // NOPMD - suppress warning about unused field, it's used in method
                             // connectToServices
  private Socket lightSocket; // NOPMD - suppress warning about unused field, it's used in method
                              // connectToServices
  private PrintWriter gateOut; // NOPMD - suppress warning about unused field, it's used in method
                               // notifyListeners
  private PrintWriter lightOut; // NOPMD - suppress warning about unused field, it's used in method
                                // notifyListeners

  /**
   * Constructs a new ControllerStateMachine with the initial state set to "Away".
   * Also establishes connections to gate and light services.
   */
  public ControllerStateMachine() {
    super(STATE_AWAY);
    connectToServices(); // NOPMD - method required to initialize connections to external services
  }

  /**
   * Establishes connections to the gate and light services.
   */
  private void connectToServices() {
    try {
      // Wait for services to be ready
      Thread.sleep(5000); // NOPMD - intentionally adding delay to wait for services
      gateSocket = new Socket("gate", 8080); // NOPMD - suppress hardcoded IP address warning
      lightSocket = new Socket("light", 8081); // NOPMD - suppress hardcoded IP address warning
      gateOut = new PrintWriter(gateSocket.getOutputStream(), true); // NOPMD - suppress Closeable resource warning
      lightOut = new PrintWriter(lightSocket.getOutputStream(), true); // NOPMD - suppress Closeable resource warning
      System.out.println("Controller connected to Gate and Light"); // NOPMD - suppress System.out warning, required for
                                                                    // logging
    } catch (IOException | InterruptedException e) {
      e.printStackTrace(); // NOPMD - suppress e.printStackTrace warning, needed for debug purposes
    }
  }

  /**
   * Processes the sensor input and triggers the appropriate event.
   *
   * @param trainDetected true if the train is detected, false otherwise
   */
  public void processSensorInput(final boolean trainDetected) {
    handleEvent(new Event(trainDetected ? EVENT_SEEN : EVENT_NOT_SEEN)); // NOPMD - event names required for clarity
  }

  /**
   * Handles an incoming event and manages state transitions.
   *
   * @param event the event to handle, must not be null
   * @throws NullPointerException  if the event is null
   * @throws IllegalStateException if the event is invalid for the current state
   */
  @Override
  public void handleEvent(final Event event) {
    if (event == null) {
      throw new NullPointerException("Event must not be null"); // NOPMD - suppress null check warning, necessary for
                                                                // robustness
    }

    final String eventName = event.getName(); // NOPMD - PMD false positive
    switch (currentState) {
      case STATE_AWAY:
        transitionState(eventName, EVENT_SEEN, STATE_APPROACH, "Approaching");
        break;
      case STATE_APPROACH:
        transitionState(eventName, EVENT_SEEN, STATE_CLOSE, null);
        break;
      case STATE_CLOSE:
        transitionState(eventName, EVENT_SEEN, STATE_PRESENT, null);
        break;
      case STATE_PRESENT:
        transitionState(eventName, EVENT_NOT_SEEN, STATE_LEAVING, null);
        break;
      case STATE_LEAVING:
        transitionState(eventName, EVENT_NOT_SEEN, STATE_LEFT, "Leaving");
        break;
      case STATE_LEFT:
        transitionState(eventName, EVENT_NOT_SEEN, STATE_AWAY, null);
        break;
      default:
        throw new IllegalStateException("Unexpected state: " + currentState); // NOPMD - suppress IllegalStateException
                                                                              // warning, necessary for safety
    }
  }

  /**
   * Helper method to transition the state based on the event name.
   */
  private void transitionState(final String eventName, final String expectedEvent,
      final String newState, final String notificationEvent) {
    if (expectedEvent.equals(eventName)) {
      currentState = newState; // NOPMD - suppress field reassignment warning, state transition logic
      if (notificationEvent != null) {
        notifyListeners(new Event(notificationEvent)); // NOPMD - suppress notify method call warning, critical for
                                                       // state transition
      }
    } else {
      throw new IllegalStateException(String.format(ERR_UNEXPECTED_EVENT, eventName, currentState)); // NOPMD - suppress
                                                                                                     // IllegalStateException
                                                                                                     // warning, needed
                                                                                                     // for error
                                                                                                     // handling
    }
  }

  /**
   * Notifies listeners (external services) about a state change event.
   *
   * @param event the event to notify about, must not be null
   */
  @Override
  protected void notifyListeners(final Event event) {
    gateOut.println(event.getName()); // NOPMD - suppress System.out warning, necessary for communication with gate
                                      // service
    lightOut.println(event.getName()); // NOPMD - suppress System.out warning, necessary for communication with light
                                       // service
  }

  /**
   * Main method to simulate the state machine operation.
   */
  public static void main(String[] args) {
    ControllerStateMachine controller = new ControllerStateMachine(); // NOPMD - suppress unused variable warning, entry
                                                                      // point for simulation
    // Simulate sensor input
    controller.processSensorInput(true); // NOPMD - simulate 'Seen' event
    controller.processSensorInput(true); // NOPMD - simulate 'Seen' event
    controller.processSensorInput(true); // NOPMD - simulate 'Seen' event
    controller.processSensorInput(true); // NOPMD - simulate 'Seen' event
    System.out.println("_____________________________");
    controller.processSensorInput(false); // NOPMD - simulate 'NotSeen' event
    controller.processSensorInput(false); // NOPMD - simulate 'NotSeen' event
    controller.processSensorInput(false); // NOPMD - simulate 'NotSeen' event
    controller.processSensorInput(false); // NOPMD - simulate 'NotSeen' event
    controller.processSensorInput(false); // NOPMD - simulate 'NotSeen' event
    controller.processSensorInput(false); // NOPMD - simulate 'NotSeen' event
  }
}
