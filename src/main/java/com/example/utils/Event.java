package com.example.utils;

public class Event {
  private final String name;
  private final int priority; // Este es el segundo parámetro si es necesario

  // Constructor que acepta dos parámetros
  public Event(String name, int priority) {
    this.name = name;
    this.priority = priority;
  }

  // Constructor que acepta solo un parámetro si lo necesitas
  public Event(String name) {
    this.name = name;
    this.priority = 0; // O un valor predeterminado si es necesario
  }

  public String getName() {
    return name;
  }

  public int getPriority() {
    return priority;
  }
}
