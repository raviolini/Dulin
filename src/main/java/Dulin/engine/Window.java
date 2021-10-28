package Dulin.engine;

import Dulin.engine.exceptions.R;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Paths;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window{
  private int width, height;
  private String title;
  private long glfwWindow;

  private static Window window = null;

  private Window(){
    this.height = 300;
    this.width = 300;
    this.title = "Dulin";
  }

  // Ensure that Only 1 Window Object Is Created
  public static Window get(){
    if(Window.window == null){
      Window.window = new Window();
    }
    return Window.window;
  }

  public void run(){
    System.out.println("--=== Dulin ===--");

    init();
    loop();

    // Free the window callbacks and destroy the window
    glfwFreeCallbacks(glfwWindow);
    glfwDestroyWindow(glfwWindow);

    // Terminate GLFW and free the error callback
    glfwTerminate();
    Objects.requireNonNull(glfwSetErrorCallback(null)).free();
  }

  public void init(){

    // Setup an error callback
    GLFWErrorCallback.createPrint(System.err).set();

    // Initialize GLFW. Most GLFW functions will not work before doing this.
    if (!glfwInit()) {
      throw new IllegalStateException("Unable to initialize GLFW");
    }

    // Configure GLFW
    glfwDefaultWindowHints();
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

    // Create the window
    glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
    if (glfwWindow == NULL) {
      throw new RuntimeException("Failed to create the GLFW window");
    }

    // Setup a key callback. It will be called every time a key is pressed, repeated, or released
    glfwSetKeyCallback(glfwWindow, (window, key, scancode, action, mods) -> {
      if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
        glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
      }
    });

    // Get the thread stack and push a new frame
    try (MemoryStack stack = stackPush()) {
      IntBuffer pWidth = stack.mallocInt(1); // int*
      IntBuffer pHeight = stack.mallocInt(1); // int*

      // Get the window size passed to glfwCreateWindow
      glfwGetWindowSize(glfwWindow, pWidth, pHeight);

      // Get the resolution of the primary monitor
      GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

      // Center the window
      assert vidmode != null;
      glfwSetWindowPos(glfwWindow, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
    } // the stack frame is popped automatically

    // Make the OpenGL context current
    glfwMakeContextCurrent(glfwWindow);
    // Enable v-sync
    glfwSwapInterval(1);

    // Make the window visible
    glfwShowWindow(glfwWindow);
  }

  public void loop(){
    GL.createCapabilities();

    // Texture
    int textureHandle = glGenTextures();
    glBindTexture(GL_TEXTURE_2D, textureHandle);

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

    STBImage.stbi_set_flip_vertically_on_load(true);

    int[] width = new int[1], height = new int[1], nrChannels = new int[1];
    ByteBuffer data = STBImage.stbi_load(R.getImagesPathByName("container.jpg"), width, height, nrChannels, 3);

    if (data == null) {
      throw new RuntimeException("Failed to load a texture file!" + System.lineSeparator() + STBImage.stbi_failure_reason());
    }

    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width[0], height[0], 0, GL_RGB, GL_UNSIGNED_BYTE, data);
    glGenerateMipmap(GL_TEXTURE_2D);

    glBindTexture(GL_TEXTURE_2D, 0);

    STBImage.stbi_image_free(data);
    // Texture End

    // Triangle
    float[] vertices = {
      // Positions        Colors          TexCoords
      -0.5f, -0.5f, 0.0f, 1f, 0f, 0f, 1f, 0.5f, 1f,
      0.5f, -0.5f, 0.0f,  0f, 1f, 0f, 1f, 0f, 0f,
      0.0f, 0.5f, 0.0f,   0f, 0f, 1f, 1f, 1f, 1f
    };

    int VBO = glGenBuffers();
    int VAO = glGenVertexArrays();

    glBindVertexArray(VAO);

    glBindBuffer(GL_ARRAY_BUFFER, VBO);
    glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

    glVertexAttribPointer(0, 3, GL_FLOAT, false, 9 * 4, 0L);
    glEnableVertexAttribArray(0);

    glVertexAttribPointer(1, 4, GL_FLOAT, false, 9 * 4, 3L * 4L);
    glEnableVertexAttribArray(1);

    glVertexAttribPointer(2, 2, GL_FLOAT, false, 9 * 4, 7L * 4L);
    glEnableVertexAttribArray(2);

    glBindVertexArray(0);
    // Triangle End

    // Shader
    Shader shader = null;
    try {
      shader = new Shader(
        Paths.get("src/main/resources/shaders/vertexShader.glsl"),
        Paths.get("src/main/resources/shaders/fragmentShader.glsl"));
    } catch (Exception e) {
      e.printStackTrace();
    }
    // Shader End

    // Render Bindings
    assert shader != null;
    shader.use();

    glBindVertexArray(VAO);

    shader.setInt("uni_texture", 0);
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, textureHandle);
    // Render Bindings End

    glClearColor(0.2f, 0.3f, 0.3f, 1.0f);

    while (!glfwWindowShouldClose(glfwWindow)) {
      glClear(GL_COLOR_BUFFER_BIT);

      glDrawArrays(GL_TRIANGLES, 0, 3);

      glfwSwapBuffers(glfwWindow);

      glfwPollEvents();
    }

    glDeleteBuffers(VBO);
  }
}
