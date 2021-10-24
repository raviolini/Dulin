package LWJGL_Project;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;

import static org.lwjgl.opengl.GL33.*;

import LWJGL_Project.Exceptions.ShaderCompilationException;

public class Shader {
  public Shader() {}

  public Shader(String vertexShaderPath, String fragmentShaderPath) throws IOException, ShaderCompilationException {
    this(Paths.get(vertexShaderPath), Paths.get(fragmentShaderPath));
  }

  public Shader(Path vertexShaderPath, Path fragmentShaderPath) throws IOException, ShaderCompilationException {
    loadFromFile(vertexShaderPath, fragmentShaderPath);
  }

  public void loadFromFile(Path vertexShaderPath, Path fragmentShaderPath) throws IOException, ShaderCompilationException {
    String shaderSource;

    int[] success = new int[1];

    int vertexShaderHandle = glCreateShader(GL_VERTEX_SHADER);
    int fragmentShaderHandle = glCreateShader(GL_FRAGMENT_SHADER);

    try {
      shaderSource = Files.readString(vertexShaderPath);

      glShaderSource(vertexShaderHandle, shaderSource);
      glCompileShader(vertexShaderHandle);

      glGetShaderiv(vertexShaderHandle, GL_COMPILE_STATUS, success);

      if (success[0] == GL_FALSE) {
        throw new ShaderCompilationException(vertexShaderPath, vertexShaderHandle);
      }
    } catch(IOException e) {
      glDeleteShader(vertexShaderHandle);
      throw e;
    }

    try {
      shaderSource = Files.readString(fragmentShaderPath);

      glShaderSource(fragmentShaderHandle, shaderSource);
      glCompileShader(fragmentShaderHandle);

      glGetShaderiv(fragmentShaderHandle, GL_COMPILE_STATUS, success);

      if (success[0] == GL_FALSE) {
        throw new ShaderCompilationException(fragmentShaderPath, fragmentShaderHandle);
      }
    } catch (IOException e) {
      glDeleteShader(fragmentShaderHandle);
      throw e;
    }

    programHandle = glCreateProgram();
    glAttachShader(programHandle, vertexShaderHandle);
    glAttachShader(programHandle, fragmentShaderHandle);
    glLinkProgram(programHandle);

    glGetProgramiv(programHandle, GL_LINK_STATUS, success);

    if (success[0] == GL_FALSE) {
      String errorLog = glGetProgramInfoLog(programHandle);
      System.out.println("Error linking shader program: " + errorLog);
    }

    glDeleteShader(vertexShaderHandle);
    glDeleteShader(fragmentShaderHandle);

    initialized = true;
  }

  public void use() {
    assert(initialized);
    glUseProgram(programHandle);
  }

  private int programHandle = -1;
  private boolean initialized = false;
}
