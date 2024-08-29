package com.example.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EventTest {

  @Test
  void testEventCreationValidName() {
    Event event = new Event("TestEvent");
    assertEquals("TestEvent", event.getName(), "Event name should match the input name");
  }

  @Test
  void testEventCreationNullName() {
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              new Event(null);
            });
    assertEquals(
        "Event name must not be null or empty",
        exception.getMessage(),
        "Exception message should indicate that the name is null or empty");
  }

  @Test
  void testEventCreationEmptyName() {
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              new Event("");
            });
    assertEquals(
        "Event name must not be null or empty",
        exception.getMessage(),
        "Exception message should indicate that the name is null or empty");
  }

  @Test
  void testEqualsSameObject() {
    Event event = new Event("TestEvent");
    assertTrue(event.equals(event), "An event should be equal to itself");
  }

  @Test
  void testEqualsDifferentObjectSameName() {
    Event event1 = new Event("TestEvent");
    Event event2 = new Event("TestEvent");
    assertTrue(event1.equals(event2), "Events with the same name should be equal");
    assertEquals(
        event1.hashCode(),
        event2.hashCode(),
        "Events with the same name should have the same hash code");
  }

  @Test
  void testEqualsDifferentObjectDifferentName() {
    Event event1 = new Event("TestEvent1");
    Event event2 = new Event("TestEvent2");
    assertFalse(event1.equals(event2), "Events with different names should not be equal");
    assertNotEquals(
        event1.hashCode(),
        event2.hashCode(),
        "Events with different names should have different hash codes");
  }

  @Test
  void testEqualsNullObject() {
    Event event = new Event("TestEvent");
    assertFalse(event.equals(null), "An event should not be equal to null");
  }

  @Test
  void testEqualsDifferentClass() {
    Event event = new Event("TestEvent");
    Object other = new Object();
    assertFalse(
        event.equals(other), "An event should not be equal to an object of a different class");
  }

  @Test
  void testToString() {
    Event event = new Event("TestEvent");
    assertEquals(
        "Event{name='TestEvent'}",
        event.toString(),
        "toString should return the correct string representation");
  }
}
