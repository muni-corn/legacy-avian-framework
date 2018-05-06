
package musicaflight.avianutils;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

import java.io.*;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;

import org.lwjgl.BufferUtils;

public class AvianUtils {

	static double timeI, timeF;

	public static void startNanoWatch() {
		timeI = System.nanoTime();
	}

	public static void stopNanoWatch() {
		timeF = System.nanoTime();
	}

	public static long getNanoDifference() {
		return (long) (timeF - timeI);
	}

	public static String getNanoDifferenceString() {
		DecimalFormat format = new DecimalFormat("0");

		return format.format((timeF - timeI));
	}

	public static FloatBuffer asFlippedFloatBuffer(float... values) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(values.length);
		buffer.put(values);
		buffer.flip();
		return buffer;
	}

	/** Load a vertex and fragment shader for use with OpenGL. */

	public static int loadShaders(String vertexShaderLocation, String fragmentShaderLocation) {
		int shaderProgram = glCreateProgram();
		int vertexShader = glCreateShader(GL_VERTEX_SHADER);
		int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
		StringBuilder vertexShaderSource = new StringBuilder();
		StringBuilder fragmentShaderSource = new StringBuilder();
		BufferedReader vertexShaderFileReader = null;
		try {
			vertexShaderFileReader = new BufferedReader(new InputStreamReader(AvianUtils.class.getResourceAsStream(vertexShaderLocation)));
			String line;
			while ((line = vertexShaderFileReader.readLine()) != null) {
				vertexShaderSource.append(line).append('\n');
			}
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		} finally {
			if (vertexShaderFileReader != null) {
				try {
					vertexShaderFileReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		BufferedReader fragmentShaderFileReader = null;
		try {
			fragmentShaderFileReader = new BufferedReader(new InputStreamReader(AvianUtils.class.getResourceAsStream(fragmentShaderLocation)));
			String line;
			while ((line = fragmentShaderFileReader.readLine()) != null) {
				fragmentShaderSource.append(line).append('\n');
			}
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		} finally {
			if (fragmentShaderFileReader != null) {
				try {
					fragmentShaderFileReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		glShaderSource(vertexShader, vertexShaderSource);
		glCompileShader(vertexShader);
		if (glGetShaderi(vertexShader, GL_COMPILE_STATUS) == GL_FALSE) {
			System.err.println("Vertex shader wasn't able to be compiled correctly. Error log:");
			System.err.println(glGetShaderInfoLog(vertexShader, 1024));
			return -1;
		}
		glShaderSource(fragmentShader, fragmentShaderSource);
		glCompileShader(fragmentShader);
		if (glGetShaderi(fragmentShader, GL_COMPILE_STATUS) == GL_FALSE) {
			System.err.println("Fragment shader wasn't able to be compiled correctly. Error log:");
			System.err.println(glGetShaderInfoLog(fragmentShader, 1024));
		}
		glAttachShader(shaderProgram, vertexShader);
		glAttachShader(shaderProgram, fragmentShader);
		glLinkProgram(shaderProgram);
		if (glGetProgrami(shaderProgram, GL_LINK_STATUS) == GL_FALSE) {
			System.err.println("Shader program wasn't linked correctly.");
			System.err.println(glGetProgramInfoLog(shaderProgram, 1024));
			return -1;
		}
		glDeleteShader(vertexShader);
		glDeleteShader(fragmentShader);
		return shaderProgram;
	}
}
