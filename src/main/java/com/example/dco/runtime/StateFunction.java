package com.example.dco.runtime;

@FunctionalInterface
public interface StateFunction {
  void execute(FunctionContext context);
}
