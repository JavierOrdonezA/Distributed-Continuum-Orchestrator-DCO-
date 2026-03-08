package com.example.dco.runtime;

import java.util.HashMap;
import java.util.Map;

public final class FunctionRegistry {

  private final Map<String, StateFunction> functions = new HashMap<>();

  public void register(final String functionName, final StateFunction function) {
    if (functionName == null || functionName.trim().isEmpty()) {
      throw new IllegalArgumentException("functionName must not be null or empty");
    }
    if (function == null) {
      throw new IllegalArgumentException("function must not be null");
    }
    functions.put(functionName.trim(), function);
  }

  public void invoke(final String functionName, final FunctionContext context) {
    final StateFunction function = functions.get(functionName);
    if (function == null) {
      throw new IllegalStateException("Function not registered: " + functionName);
    }
    function.execute(context);
  }
}
