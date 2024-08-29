package com.example.utils;

import java.io.*;
import java.net.*;

public class GateStateMachine extends StateMachine {

  public GateStateMachine() {
    super("Up"); // Llama al constructor de StateMachine con el estado inicial "Up"
  }

  @Override
  protected void notifyListeners(Event event) {
    // Not needed for Gate
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

  public static void main(String[] args) {
    GateStateMachine gate = new GateStateMachine();
    try (ServerSocket serverSocket = new ServerSocket(8080)) {
      System.out.println("Gate waiting for connection...");
      Socket clientSocket = serverSocket.accept();
      System.out.println("Gate connected to Controller");
      BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      String inputLine;
      while ((inputLine = in.readLine()) != null) {
        gate.handleEvent(new Event(inputLine));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
