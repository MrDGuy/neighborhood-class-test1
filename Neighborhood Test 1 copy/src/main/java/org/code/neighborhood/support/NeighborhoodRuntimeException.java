package org.code.neighborhood.support;


public class NeighborhoodRuntimeException extends RuntimeException {
  private final ExceptionKeys key;

  public NeighborhoodRuntimeException(ExceptionKeys key) {
    super(key.toString());
    this.key = key;
  }

  public NeighborhoodRuntimeException(ExceptionKeys key, Throwable cause) {
    super(key.toString(), cause);
    this.key = key;
  }

  public ExceptionKeys getKey() {
    return this.key;
  }
}
