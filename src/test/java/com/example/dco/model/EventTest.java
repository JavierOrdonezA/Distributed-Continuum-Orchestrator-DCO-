package com.example.dco.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class EventTest {

  @Test
  void createsEventWithValidName() {
    final Event event = new Event("seen");
    assertEquals("seen", event.getName());
  }

  @Test
  void rejectsNullName() {
    final IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> new Event(null));
    assertEquals("Event name must not be null or empty", exception.getMessage());
  }

  @Test
  void rejectsBlankName() {
    final IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> new Event("   "));
    assertEquals("Event name must not be null or empty", exception.getMessage());
  }

  @Test
  void supportsEqualsHashCodeAndToString() {
    final Event event1 = new Event("approaching");
    final Event event2 = new Event("approaching");
    final Event event3 = new Event("leaving");

    assertEquals(event1, event2);
    assertEquals(event1.hashCode(), event2.hashCode());
    assertNotEquals(event1, event3);
    assertFalse(event1.equals(null));
    assertTrue(event1.toString().contains("approaching"));
  }
}
