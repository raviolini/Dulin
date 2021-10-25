package Dulin.engine.exceptions;

import java.lang.Exception;

public class ShaderException extends Exception
{
  public ShaderException() {
    super();
  }

  public ShaderException(String message) {
    super(message);
  }

  public ShaderException(Throwable throwable) {
    super(throwable);
  }

  public ShaderException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
