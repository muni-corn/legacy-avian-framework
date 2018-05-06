
package musicaflight.avianutils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class AvianCircle {

	float x, y, d;

	int vboVertexHandle, vboColorHandle;

	FloatBuffer vertexData, colorData;

	public AvianCircle() {
		vboVertexHandle = glGenBuffers();
		vboColorHandle = glGenBuffers();
	}

	public AvianCircle(float x, float y, float d) {
		this();
		this.x = x;
		this.y = y;
		this.d = d;
	}

	int vertices = 32;

	public void createVertexData(boolean filled) {
		if (filled) {
			vertexData = BufferUtils.createFloatBuffer((vertices + 2) * 3);
			vertexData.put(x);
			vertexData.put(y);
			vertexData.put(0);
			for (int i = 0; i < vertices + 1; i++) {
				float foo = 360f / (vertices);
				float xx = (d / 2f) * AvianMath.sin(i * foo);
				float yy = (d / 2f) * AvianMath.cos(i * foo);
				vertexData.put(x + xx);
				vertexData.put(y + yy);
				vertexData.put(0);
			}

		} else {
			vertexData = BufferUtils.createFloatBuffer(vertices * 3);
			for (int i = 0; i < vertices; i++) {
				float foo = 360f / (vertices - 1);
				float xx = (d / 2f) * AvianMath.sin(i * foo);
				float yy = (d / 2f) * AvianMath.cos(i * foo);
				vertexData.put(x + xx);
				vertexData.put(y + yy);
				vertexData.put(0);
			}
		}
		vertexData.flip();

	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getDiameter() {
		return d;
	}

	public void setX(float fx) {
		x = fx;
	}

	public void setY(float fy) {
		y = fy;
	}

	public void setDiameter(float fd) {
		d = fd;
	}

	public void set(float x, float y, float d) {
		this.x = x;
		this.y = y;
		this.d = d;
	}

	public void render(boolean filled, float... color) {
		createVertexData(filled);

		glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
		glBufferData(GL_ARRAY_BUFFER, vertexData, GL_STATIC_DRAW);
		glVertexPointer(3, GL_FLOAT, 0, 0);

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
		if (filled) {
			colorData = BufferUtils.createFloatBuffer((vertices + 2) * 4);
			for (int i = 0; i < vertices + 2; i++)
				colorData.put(new float[] { r, g, b, a });
		} else {
			colorData = BufferUtils.createFloatBuffer(vertices * 4);
			for (int i = 0; i < vertices; i++)
				colorData.put(new float[] { r, g, b, a });
		}
		colorData.flip();

		glBindBuffer(GL_ARRAY_BUFFER, vboColorHandle);
		glBufferData(GL_ARRAY_BUFFER, colorData, GL_STATIC_DRAW);
		glColorPointer(4, GL_FLOAT, 0, 0);

		if (glGetBoolean(GL_VERTEX_ARRAY) == GL_FALSE)
			glEnableClientState(GL_VERTEX_ARRAY);
		if (glGetBoolean(GL_COLOR_ARRAY) == GL_FALSE)
			glEnableClientState(GL_COLOR_ARRAY);

		if (filled)
			glDrawArrays(GL_TRIANGLE_FAN, 0, vertices + 2);
		else
			glDrawArrays(GL_LINE_LOOP, 0, vertices);

	}

	public void destroy() {
		glDeleteBuffers(vboVertexHandle);
		glDeleteBuffers(vboColorHandle);
	}

	public void setQuality(int i) {
		this.vertices = i;
	}

}
