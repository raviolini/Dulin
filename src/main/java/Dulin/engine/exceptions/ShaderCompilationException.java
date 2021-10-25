package Dulin.engine.exceptions;

import java.nio.IntBuffer;
import java.nio.file.Path;

import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL33.*;

public class ShaderCompilationException extends ShaderException {
  public ShaderCompilationException(Path shaderPath, int shaderHandle) {
    super(shaderPath.toString() + " (" + getShaderType(shaderHandle) + "):\n" + glGetShaderInfoLog(shaderHandle));
  }

  private static String getShaderType(int shaderHandle) {
    IntBuffer shaderType = BufferUtils.createIntBuffer(1);
    glGetShaderiv(shaderHandle, GL_SHADER_TYPE, shaderType);

    if (shaderType.get(0) == GL_VERTEX_SHADER) {
      return "GL_VERTEX_SHADER";
    } else if (shaderType.get(0) == GL_FRAGMENT_SHADER) {
      return "GL_FRAGMENT_SHADER";
    } else if (shaderType.get(0) == GL_GEOMETRY_SHADER) {
      return "GL_GEOMETRY_SHADER";
    } else {
      return "UNKNOWN_SHADER";
    }
  }
}
