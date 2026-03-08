package com.example.dco.runtime;

public final class FunctionContext {

  private final String machineId;
  private final String stateName;
  private final String functionName;

  public FunctionContext(
      final String machineId, final String stateName, final String functionName) {
    this.machineId = machineId;
    this.stateName = stateName;
    this.functionName = functionName;
  }

  public String getMachineId() {
    return machineId;
  }

  public String getStateName() {
    return stateName;
  }

  public String getFunctionName() {
    return functionName;
  }
}
