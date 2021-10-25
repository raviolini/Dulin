package Dulin.engine.exceptions;

import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;

public class ShaderLinkageException extends ShaderException {
  public ShaderLinkageException(int programHandle) {
    super("\n" + glGetProgramInfoLog(programHandle));
  }
}
