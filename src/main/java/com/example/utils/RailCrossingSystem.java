import java.util.ArrayList;
import java.util.List;

// Event class to represent different events in the system
class Event {
  private String name;

  public Event(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}

// Abstract State Machine class
abstract class StateMachine {
  protected String currentState;
  protected List<StateMachine> listeners = new ArrayList<>();

  public void addListener(StateMachine listener) {
    listeners.add(listener);
  }

  protected void notifyListeners(Event event) {
    for (StateMachine listener : listeners) {
      listener.handleEvent(event);
    }
  }

  public abstract void handleEvent(Event event);
}

// Controller State Machine
class ControllerStateMachine extends StateMachine {
  public ControllerStateMachine() {
    currentState = "Away";
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
          notifyListeners(new Event("Approaching"));
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
          notifyListeners(new Event("Leaving"));
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

// Gate State Machine
class GateStateMachine extends StateMachine {
  public GateStateMachine() {
    currentState = "Up";
  }

  @Override
  public void handleEvent(Event event) {
    switch (currentState) {
      case "Up":
        if (event.getName().equals("Approaching")) {
          currentState = "Down";
          lowerGate();
        }
        break;
      case "Down":
        if (event.getName().equals("Leaving")) {
          currentState = "Up";
          raiseGate();
        }
        break;
    }
    System.out.println("Gate state: " + currentState);
  }

  private void lowerGate() {
    System.out.println("Lowering the gate");
  }

  private void raiseGate() {
    System.out.println("Raising the gate");
  }
}

// Light State Machine
class LightStateMachine extends StateMachine {
  public LightStateMachine() {
    currentState = "Off";
  }

  @Override
  public void handleEvent(Event event) {
    switch (currentState) {
      case "Off":
        if (event.getName().equals("Approaching")) {
          currentState = "On";
          turnOn();
        }
        break;
      case "On":
        if (event.getName().equals("Leaving")) {
          currentState = "Off";
          turnOff();
        }
        break;
    }
    System.out.println("Light state: " + currentState);
  }

  private void turnOn() {
    System.out.println("Turning the lights on");
  }

  private void turnOff() {
    System.out.println("Turning the lights off");
  }
}

// Main class to demonstrate the system
public class RailCrossingSystem {
  public static void main(String[] args) {
    ControllerStateMachine controller = new ControllerStateMachine();
    GateStateMachine gate = new GateStateMachine();
    LightStateMachine light = new LightStateMachine();

    controller.addListener(gate);
    controller.addListener(light);

    // Simulate a train approaching and leaving
    // Initial State of controller = away
    controller.processSensorInput(true); // Seen
    // assert(gate.currentState).equals("up");
    controller.processSensorInput(true); // Seen
    // assert(gate.currentState).equals("down");
    controller.processSensorInput(true); // Seen
    controller.processSensorInput(true); // Seen
    System.out.println("_____________________________");
    controller.processSensorInput(false); // ¬Seen
    controller.processSensorInput(false); // ¬Seen
    controller.processSensorInput(false); // ¬Seen
    controller.processSensorInput(false); // ¬Seen
    controller.processSensorInput(false); // ¬Seen
    controller.processSensorInput(false); // ¬Seen
  }
}
