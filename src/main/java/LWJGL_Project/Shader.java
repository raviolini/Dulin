package LWJGL_Project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL33.*;

public class Shader {
  public Shader() {}

  public Shader(Path vertexShaderPath, Path fragmentShaderPath) throws IOException {
    loadFromFile(vertexShaderPath, fragmentShaderPath);
  }

  public void loadFromFile(Path vertexShaderPath, Path fragmentShaderPath) throws IOException {
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
        String errorLog = glGetShaderInfoLog(vertexShaderHandle);
        System.out.println("Error compiling vertex shader: " + errorLog);
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
        String errorLog = glGetShaderInfoLog(fragmentShaderHandle);
        System.out.println("Error compiling fragment shader: " + errorLog);
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
