
package musicaflight.avianutils;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

import java.io.*;

public class AvianMaterial {


	private int shader;
	
	/** Load a vertex and fragment shader for use with OpenGL. */
	
//	private float shininess = .5f;
//	private AvianColor ambient, diffuse, specular;
	
	public AvianMaterial(String vertexShaderLocation, String fragmentShaderLocation) {
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
			shader = -1;
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
			shader =  -1;
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
			shader = -1;
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
			shader = -1;
		}
		glDeleteShader(vertexShader);
		glDeleteShader(fragmentShader);
		shader = shaderProgram;

	}
	
	public int getShaderID() {
		return shader;
	}

}
