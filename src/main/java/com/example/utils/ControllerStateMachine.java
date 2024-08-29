package com.example.utils;

public class ControllerStateMachine extends StateMachine {

  public ControllerStateMachine() {
    super("Away"); // Pasa el estado inicial al constructor de StateMachine
  }

  public void processSensorInput(boolean seen) {
    handleEvent(new Event(seen ? "Seen" : "¬Seen"));
  }

  @Override
  public void handleEvent(Event event) {
    switch (currentState) {
      case "Away":
        if (event.getName().equals("Seen")) {
          currentState = "Approach";
          notifyListeners(new Event("Approaching", 1));
        }
        break;
      case "Approach":
        if (event.getName().equals("Seen")) {
          currentState = "Close";
        }
        break;
      case "Close":
        if (event.getName().equals("Seen")) {
          currentState = "Present";
        }
        break;
      case "Present":
        if (event.getName().equals("¬Seen")) {
          currentState = "Leaving";
        }
        break;
      case "Leaving":
        if (event.getName().equals("¬Seen")) {
          currentState = "Left";
          notifyListeners(new Event("Leaving", 1));
        }
        break;
      case "Left":
        if (event.getName().equals("¬Seen")) {
          currentState = "Away";
        }
        break;
    }
    System.out.println("Controller state: " + currentState);
  }
}
