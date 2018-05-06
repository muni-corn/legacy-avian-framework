
package musicaflight.avianutils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class AvianLine {

	float x1, y1, z1, x2, y2, z2;

	static int vboVertexHandle;
	static int vboColorHandle;
	boolean glLoaded;

	static FloatBuffer vertexData = BufferUtils.createFloatBuffer(6),
			colorData = BufferUtils.createFloatBuffer(8);

	public AvianLine() {
		vboVertexHandle = glGenBuffers();
		vboColorHandle = glGenBuffers();
	}

	public AvianLine(float x1, float y1, float x2, float y2) {
		this();
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	public void setLineWidth(float w) {
		glLineWidth(w);
	}

	public void createVertexData() {
		if (vertexData == null) {} else {
			vertexData.clear();
		}
		vertexData.put(new float[] { x1, y1, z1, x2, y2, z2 });
		vertexData.flip();
	}

	public void set(float x1, float y1, float z1, float x2, float y2, float z2) {
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
	}

	public void set(float x1, float y1, float x2, float y2) {
		this.x1 = x1;
		this.y1 = y1;
		z1 = 0;
		this.x2 = x2;
		this.y2 = y2;
		z2 = 0;
	}

	public void setFirstCoordinate(float x, float y) {
		x1 = x;
		y1 = y;
		z1 = 0;
	}

	public void setSecondCoordinate(float x, float y) {
		x2 = x;
		y2 = y;
		z2 = 0;
	}

	public void setFirstCoordinate(float x, float y, float z) {
		x1 = x;
		y1 = y;
		z1 = z;
	}

	public void setSecondCoordinate(float x, float y, float z) {
		x1 = x;
		y1 = y;
		z1 = z;
	}

	public void render(float... color) {
		createVertexData();

		glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
		glBufferData(GL_ARRAY_BUFFER, vertexData, GL_STATIC_DRAW);
		glVertexPointer(3, GL_FLOAT, 0, 0);

		colorData(color);

		glBindBuffer(GL_ARRAY_BUFFER, vboColorHandle);
		glBufferData(GL_ARRAY_BUFFER, colorData, GL_STATIC_DRAW);
		glColorPointer(4, GL_FLOAT, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		if (glGetBoolean(GL_VERTEX_ARRAY) == GL_FALSE)
			glEnableClientState(GL_VERTEX_ARRAY);
		if (glGetBoolean(GL_COLOR_ARRAY) == GL_FALSE)
			glEnableClientState(GL_COLOR_ARRAY);

		glDrawArrays(GL_LINES, 0, 2);

	}

	private void colorData(float[] color) {

		float r, g, b, a;
		if (color.length == 0) {
			r = g = b = a = 1;
		} else if (color.length == 1) {
			r = g = b = 1;
			a = color[0];
		} else if (color.length == 2) {
			r = g = b = color[0];
			a = color[1];
		} else if (color.length == 3) {
			r = color[0];
			g = color[1];
			b = color[2];
			a = 1;
		} else {
			r = color[0];
			g = color[1];
			b = color[2];
			a = color[3];
		}

		colorData.clear();
		for (int i = 0; i < 2; i++) {
			colorData.put(r).put(g).put(b).put(a);
		}
		colorData.flip();
	}

	public void destroy() {
		glDeleteBuffers(vboVertexHandle);
		glDeleteBuffers(vboColorHandle);
	}
}
