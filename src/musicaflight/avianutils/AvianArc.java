
package musicaflight.avianutils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class AvianArc {

	private float x, y, r;

	private static int vboVertexHandle, vboColorHandle;

	private static FloatBuffer vertexData, colorData;

	public AvianArc() {
		vboVertexHandle = glGenBuffers();
		vboColorHandle = glGenBuffers();
	}

	public AvianArc(float x, float y, float r) {
		this();
		this.x = x;
		this.y = y;
		this.r = r;
	}

	private float startAngle, endAngle;

	int iterations = 32;

	public void setLineWidth(float width) {
		glLineWidth(width);
	}

	public void setQuality(int iterations) {
		this.iterations = iterations;
	}

	public void createVertexData() {
		int iters = (int) Math.abs(((((endAngle - startAngle) / 360f)) * (this.iterations - 2))) + 2;
		vertexData = BufferUtils.createFloatBuffer(iters * 3);
		for (int i = 0; i < iters; i++) {
			float foo = (endAngle - startAngle) / (iters - 1f);
			float xx = (r) * AvianMath.sin((i * foo) + startAngle);
			float yy = (r) * -AvianMath.cos((i * foo) + startAngle);
			vertexData.put(new float[] { x + xx, y + yy, 0f });
		}
		vertexData.flip();

	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getRadius() {
		return r;
	}

	public float getStartAngle() {
		return startAngle;
	}

	public float getEndAngle() {
		return endAngle;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public void setRadius(float r) {
		this.r = r;
	}

	public void setXYR(float x, float y, float r) {
		this.x = x;
		this.y = y;
		this.r = r;
	}

	public void setStartAngle(float sa) {
		startAngle = sa;
	}

	public void setEndAngle(float ea) {
		endAngle = ea;
	}

	public void set(float x, float y, float r, float start, float end) {
		this.x = x;
		this.y = y;
		this.r = r;
		startAngle = start;
		endAngle = end;
	}

	public void render(float... color) {
		createVertexData();

		int iters = (int) Math.abs(((((endAngle - startAngle) / 360f)) * (this.iterations - 2))) + 2;

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

		glDrawArrays(GL_LINE_STRIP, 0, iters);
	}

	private void colorData(float[] color) {

		colorData = BufferUtils.createFloatBuffer(iterations * 4);

		float red, g, b, a;
		if (color.length == 0) {
			red = g = b = a = 1;
		} else if (color.length == 1) {
			red = g = b = 1;
			a = color[0];
		} else if (color.length == 2) {
			red = g = b = color[0];
			a = color[1];
		} else if (color.length == 3) {
			red = color[0];
			g = color[1];
			b = color[2];
			a = 1;
		} else {
			red = color[0];
			g = color[1];
			b = color[2];
			a = color[3];
		}

		colorData.clear();
		for (int i = 0; i < iterations; i++) {
			colorData.put(red).put(g).put(b).put(a);
		}
		colorData.flip();
	}

	public void destroy() {
		glDeleteBuffers(vboVertexHandle);
		glDeleteBuffers(vboColorHandle);
	}

}
