import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RailCrossingSystemTest {

  private ControllerStateMachine controller;
  private GateStateMachine gate;
  private LightStateMachine light;

  @BeforeEach
  public void setUp() {
    controller = new ControllerStateMachine();
    gate = new GateStateMachine();
    light = new LightStateMachine();

    controller.addListener(gate);
    controller.addListener(light);
  }

  @Test
  public void testInitialState() {
    assertEquals("Away", controller.currentState);
    assertEquals("Up", gate.currentState);
    assertEquals("Off", light.currentState);
  }

  @Test
  public void testApproachingTrain() {
    controller.processSensorInput(true); // Seen
    assertEquals("Approach", controller.currentState);
    assertEquals("Down", gate.currentState); // Gate should be down after train is approaching
    assertEquals("On", light.currentState); // Light should be on after train is approaching

    controller.processSensorInput(true); // Seen
    assertEquals("Close", controller.currentState);

    controller.processSensorInput(true); // Seen
    assertEquals("Present", controller.currentState);
  }

  @Test
  public void testTrainLeaving() {
    // Simulate train approaching
    controller.processSensorInput(true); // Seen
    controller.processSensorInput(true); // Seen
    controller.processSensorInput(true); // Seen
    controller.processSensorInput(true); // Seen

    // Simulate train leaving
    controller.processSensorInput(false); // ¬Seen
    assertEquals("Leaving", controller.currentState);

    controller.processSensorInput(false); // ¬Seen
    assertEquals("Left", controller.currentState);
    assertEquals("Up", gate.currentState); // Gate should be up after train leaves
    assertEquals("Off", light.currentState); // Light should be off after train leaves

    controller.processSensorInput(false); // ¬Seen
    assertEquals("Away", controller.currentState);
  }
}
