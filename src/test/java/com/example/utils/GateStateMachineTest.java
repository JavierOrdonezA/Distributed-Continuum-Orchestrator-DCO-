import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GateStateMachineTest {

  private GateStateMachine gate;

  @BeforeEach
  public void setUp() {
    gate = new GateStateMachine();
  }

  @Test
  public void testInitialState() {
    assertEquals("Up", gate.currentState);
  }

  @Test
  public void testApproachingEvent() {
    gate.handleEvent(new Event("Approaching"));
    assertEquals("Down", gate.currentState);
  }

  @Test
  public void testLeavingEvent() {
    // First, transition to Down state by sending "Approaching" event
    gate.handleEvent(new Event("Approaching"));
    assertEquals("Down", gate.currentState);

    // Then, send "Leaving" event to raise the gate
    gate.handleEvent(new Event("Leaving"));
    assertEquals("Up", gate.currentState);
  }
}
