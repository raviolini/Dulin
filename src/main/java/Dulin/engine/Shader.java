package Dulin.engine;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;

import static org.lwjgl.opengl.GL33.*;

import Dulin.engine.exceptions.ShaderCompilationException;
import Dulin.engine.exceptions.ShaderLinkageException;
import Dulin.engine.exceptions.ShaderException;

public class Shader {
  public Shader() {}

  public Shader(String vertexShaderPath, String fragmentShaderPath) throws IOException, ShaderException {
    this(Paths.get(vertexShaderPath), Paths.get(fragmentShaderPath));
  }

  public Shader(Path vertexShaderPath, Path fragmentShaderPath) throws IOException, ShaderException {
    loadFromFile(vertexShaderPath, fragmentShaderPath);
  }

  public void loadFromFile(Path vertexShaderPath, Path fragmentShaderPath) throws IOException, ShaderException {
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
      glDeleteProgram(programHandle);
      throw new ShaderLinkageException(programHandle);
    }

    glDeleteShader(vertexShaderHandle);
    glDeleteShader(fragmentShaderHandle);

    initialized = true;
  }

  public void use() {
    assert(initialized);
    glUseProgram(programHandle);
  }

  public void setInt(String uniformName, int value) {
    assert(initialized);
    glUniform1i(glGetUniformLocation(programHandle, uniformName), value);
  }

  private int programHandle = -1;
  private boolean initialized = false;
}
