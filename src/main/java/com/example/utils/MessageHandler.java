package com.example.utils;

import java.io.Console;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MessageHandler { // NOPMD

  // Logger instance
  private static final Logger LOGGER = LogManager.getLogger(MessageHandler.class);

  // ANSI escape codes for colors and styles
  private static final String BOLD = "\033[1m";
  private static final String RESET = "\033[0m";
  private static final String RED = "\033[31m";
  private static final String GREEN = "\033[32m";
  private static final String YELLOW = "\033[33m";

  private static final Console CONSOLE = System.console();

  // Method to print messages with specified color
  private static void printMessage(final String color, final String type, final String message) {
    final String formattedMessage =
        String.format("%s%s[%s]%s %s", BOLD, color, type, RESET, message);
    if (CONSOLE == null) {
      LOGGER.info(formattedMessage);
    } else {
      CONSOLE.printf("%s%n", formattedMessage);
    }
  }

  public static void info(final String message) {
    printMessage(GREEN, "INFO", message);
  }

  public static void warning(final String message) {
    printMessage(YELLOW, "WARNING", message);
  }

  public static void error(final String message) {
    printMessage(RED, "ERROR", message);
  }

  public static void errorExit(final String message) {
    error(message);
    System.exit(1); // NOPMD
  }
}
