package com.example.dco.adapters.http;

import com.example.dco.model.Event;
import com.example.dco.runtime.EventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class HttpBroadcastPublisher implements EventPublisher {

  private final List<URI> targets;
  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;

  public HttpBroadcastPublisher(final List<URI> targets) {
    this.targets = List.copyOf(Objects.requireNonNull(targets, "targets must not be null"));
    this.httpClient = HttpClient.newHttpClient();
    this.objectMapper = new ObjectMapper();
  }

  @Override
  public void publish(final String sourceMachineId, final Event event) {
    for (final URI target : targets) {
      sendEvent(target, event);
    }
  }

  private void sendEvent(final URI target, final Event event) {
    try {
      final String payload =
          objectMapper.writeValueAsString(Map.of("name", event.getName()));

      final HttpRequest request =
          HttpRequest.newBuilder(target)
              .header("Content-Type", "application/json")
              .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
              .build();

      final HttpResponse<Void> response =
          httpClient.send(request, HttpResponse.BodyHandlers.discarding());

      if (response.statusCode() >= 300) {
        throw new IllegalStateException(
            "Failed to publish event to "
                + target
                + ", statusCode="
                + response.statusCode());
      }
    } catch (final Exception exception) {
      throw new IllegalStateException(
          "Failed to publish event to " + target + ": " + exception.getMessage(), exception);
    }
  }
}
