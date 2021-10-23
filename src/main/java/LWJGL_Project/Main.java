/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package LWJGL_Project;

/**
 *
 * @author zndf
 */
import java.io.IOException;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray;
import static org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays;
import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_FALSE;
import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11C.glClear;
import static org.lwjgl.opengl.GL11C.glClearColor;
import static org.lwjgl.opengl.GL11C.glDrawArrays;
import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15C.glBindBuffer;
import static org.lwjgl.opengl.GL15C.glBufferData;
import static org.lwjgl.opengl.GL15C.glDeleteBuffers;
import static org.lwjgl.opengl.GL15C.glGenBuffers;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgramiv;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderiv;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {

	// The window handle
	private long window;

	public void run() {
		System.out.println("Hello LWJGL " + Version.getVersion() + "!");

		init();
		loop();

		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	private void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

		// Create the window
		window = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL);
		if (window == NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
			}
		});

		// Get the thread stack and push a new frame
		try (MemoryStack stack = stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
					window,
					(vidmode.width() - pWidth.get(0)) / 2,
					(vidmode.height() - pHeight.get(0)) / 2
					);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);
	}

	private void loop() {
		GL.createCapabilities();

		/********** STRT TEXTURE **********
		int textureHandle = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureHandle);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

		stbi_set_flip_vertically_on_load(true);

		int[] width = new int[1], height = new int[1], nrChannels = new int[1];
		ByteBuffer data = stbi_load("assets/images/container.jpg", width, height, nrChannels, 3);

		if (data == null) {
		throw new RuntimeException("Failed to load a texture file!"
		+ System.lineSeparator() + stbi_failure_reason());
		}

		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width[0], height[0], 0, GL_RGB, GL_UNSIGNED_BYTE, data);
		glGenerateMipmap(GL_TEXTURE_2D);

		glBindTexture(GL_TEXTURE_2D, 0);

		stbi_image_free(data);
		******** END' TEXTURE ***********/

		/******************** Triangle ********************/
		float vertices[] = {
			-0.5f, -0.5f, 0.0f,
			0.5f, -0.5f, 0.0f,
			0.0f,  0.5f, 0.0f
		};

		int VBO = glGenBuffers();
		int VAO = glGenVertexArrays();

		glBindVertexArray(VAO);

		glBindBuffer(GL_ARRAY_BUFFER, VBO);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

		glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * 4, 0l);
		glEnableVertexAttribArray(0);

		glBindVertexArray(0);
		/******************** Triangle ********************/

		/******************** SHADER ********************/
		int shaderProgramHandle = -1;
		try {
			String shaderSource;
			shaderSource = new String(Files.readAllBytes(Paths.get("assets/shaders/vertexShader.glsl")), StandardCharsets.UTF_8);

			System.out.println(shaderSource);

			int[] success = new int[1];

			int vertexShaderHandle = glCreateShader(GL_VERTEX_SHADER);
			int fragmentShaderHandle = glCreateShader(GL_FRAGMENT_SHADER);

			glShaderSource(vertexShaderHandle, shaderSource);
			glCompileShader(vertexShaderHandle);

			glGetShaderiv(vertexShaderHandle, GL_COMPILE_STATUS, success);
			if (success[0] == GL_FALSE) {
				String errorLog = glGetShaderInfoLog(vertexShaderHandle);
				System.out.println("Error compiling vertex shader: " + errorLog);
			}

			shaderSource = new String(Files.readAllBytes(Paths.get("assets/shaders/fragmentShader.glsl")), StandardCharsets.UTF_8);

			System.out.println(shaderSource);

			glShaderSource(fragmentShaderHandle, shaderSource);
			glCompileShader(fragmentShaderHandle);

			glGetShaderiv(fragmentShaderHandle, GL_COMPILE_STATUS, success);
			if (success[0] == GL_FALSE) {
				String errorLog = glGetShaderInfoLog(fragmentShaderHandle);
				System.out.println("Error compiling fragment shader: " + errorLog);
			}

			shaderProgramHandle = glCreateProgram();
			glAttachShader(shaderProgramHandle, vertexShaderHandle);
			glAttachShader(shaderProgramHandle, fragmentShaderHandle);
			glLinkProgram(shaderProgramHandle);

			glGetProgramiv(shaderProgramHandle, GL_LINK_STATUS, success);
			if (success[0] == GL_FALSE) {
				String errorLog = glGetProgramInfoLog(shaderProgramHandle);
				System.out.println("Error linking shader program: " + errorLog);
			}

			glDeleteShader(vertexShaderHandle);
			glDeleteShader(fragmentShaderHandle);
		} catch (IOException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
		}
		/******************** SHADER ********************/

		glClearColor(0.2f, 0.3f, 0.3f, 1.0f);

		while (!glfwWindowShouldClose(window)) {
			glClear(GL_COLOR_BUFFER_BIT); // clear the framebuffer

			glBindVertexArray(VAO);
			glUseProgram(shaderProgramHandle);

			glDrawArrays(GL_TRIANGLES, 0, 3);

			glfwSwapBuffers(window);

			glfwPollEvents();
		}

		glDeleteBuffers(VBO);
	}

	public static void main(String[] args) {
		new Main().run();
	}

}
