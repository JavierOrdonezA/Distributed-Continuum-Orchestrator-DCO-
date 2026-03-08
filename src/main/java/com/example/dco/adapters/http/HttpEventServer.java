package com.example.dco.adapters.http;

import com.example.dco.model.Event;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class HttpEventServer implements AutoCloseable {

  private final HttpServer httpServer;
  private final ObjectMapper objectMapper;
  private final Consumer<Event> eventHandler;
  private final Supplier<String> stateSupplier;

  public HttpEventServer(
      final int port,
      final Consumer<Event> eventHandler,
      final Supplier<String> stateSupplier)
      throws IOException {
    this.eventHandler = Objects.requireNonNull(eventHandler, "eventHandler must not be null");
    this.stateSupplier = Objects.requireNonNull(stateSupplier, "stateSupplier must not be null");
    this.objectMapper = new ObjectMapper();
    this.httpServer = HttpServer.create(new InetSocketAddress(port), 0);
    this.httpServer.createContext("/event", this::handleEvent);
    this.httpServer.createContext("/state", this::handleState);
  }

  private void handleEvent(final HttpExchange exchange) throws IOException {
    if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
      sendResponse(exchange, 405, "Method Not Allowed");
      return;
    }

    final JsonNode body = objectMapper.readTree(exchange.getRequestBody());
    final JsonNode nameNode = body.get("name");
    if (nameNode == null || nameNode.asText().trim().isEmpty()) {
      sendResponse(exchange, 400, "Missing event name");
      return;
    }

    eventHandler.accept(new Event(nameNode.asText()));
    sendResponse(exchange, 200, "OK");
  }

  private void handleState(final HttpExchange exchange) throws IOException {
    if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
      sendResponse(exchange, 405, "Method Not Allowed");
      return;
    }
    final String payload = "{\"state\":\"" + stateSupplier.get() + "\"}";
    sendResponse(exchange, 200, payload);
  }

  private static void sendResponse(
      final HttpExchange exchange, final int code, final String payload) throws IOException {
    final byte[] bytes = payload.getBytes(StandardCharsets.UTF_8);
    exchange.getResponseHeaders().add("Content-Type", "application/json");
    exchange.sendResponseHeaders(code, bytes.length);
    exchange.getResponseBody().write(bytes);
    exchange.getResponseBody().close();
  }

  public void start() {
    httpServer.start();
  }

  @Override
  public void close() {
    httpServer.stop(0);
  }
}
