
package musicaflight.avianutils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class AvianRectangle {

	private float x, y, w, h;

	private int vboVertexHandle, vboColorHandle;

	private FloatBuffer vertexData = BufferUtils.createFloatBuffer(12),
			colorData = BufferUtils.createFloatBuffer(16);

	public AvianRectangle() {
		vboVertexHandle = glGenBuffers();
		vboColorHandle = glGenBuffers();
	}

	public AvianRectangle(float x, float y, float w, float h) {
		this();
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getW() {
		return w;
	}

	public float getH() {
		return h;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public void setW(float w) {
		this.w = w;
	}

	public void setH(float h) {
		this.h = h;
	}

	public void set(float x, float y, float w, float h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	public void render(float... colors) {
		vertexData();

		glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
		glBufferData(GL_ARRAY_BUFFER, vertexData, GL_STATIC_DRAW);
		glVertexPointer(3, GL_FLOAT, 0, 0);

		colorData(colors);

		glBindBuffer(GL_ARRAY_BUFFER, vboColorHandle);
		glBufferData(GL_ARRAY_BUFFER, colorData, GL_STATIC_DRAW);
		glColorPointer(4, GL_FLOAT, 0, 0);

		glBindBuffer(GL_ARRAY_BUFFER, 0);

		if (glGetBoolean(GL_VERTEX_ARRAY)==GL_FALSE)
			glEnableClientState(GL_VERTEX_ARRAY);
		if (glGetBoolean(GL_COLOR_ARRAY)==GL_FALSE)
			glEnableClientState(GL_COLOR_ARRAY);

		glDrawArrays(GL_QUADS, 0, 4);
	}

	public void render(AvianColor c) {
		render(c, c, c, c);
	}
	
	public void render(AvianColor topleft, AvianColor topright, AvianColor bottomright, AvianColor bottomleft) {
		vertexData();

		glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
		glBufferData(GL_ARRAY_BUFFER, vertexData, GL_STATIC_DRAW);
		glVertexPointer(3, GL_FLOAT, 0, 0);

		colorData.clear();

		colorData.put(topleft.getR() / 255f).put(topleft.getG() / 255f).put(topleft.getB() / 255f).put(topleft.getA() / 255f);
		colorData.put(topright.getR() / 255f).put(topright.getG() / 255f).put(topright.getB() / 255f).put(topright.getA() / 255f);
		colorData.put(bottomright.getR() / 255f).put(bottomright.getG() / 255f).put(bottomright.getB() / 255f).put(bottomright.getA() / 255f);
		colorData.put(bottomleft.getR() / 255f).put(bottomleft.getG() / 255f).put(bottomleft.getB() / 255f).put(bottomleft.getA() / 255f);

		colorData.flip();

		glBindBuffer(GL_ARRAY_BUFFER, vboColorHandle);
		glBufferData(GL_ARRAY_BUFFER, colorData, GL_STATIC_DRAW);
		glColorPointer(4, GL_FLOAT, 0, 0);

		glBindBuffer(GL_ARRAY_BUFFER, 0);

		if (glGetBoolean(GL_VERTEX_ARRAY)==GL_FALSE)
			glEnableClientState(GL_VERTEX_ARRAY);
		if (glGetBoolean(GL_COLOR_ARRAY)==GL_FALSE)
			glEnableClientState(GL_COLOR_ARRAY);

		glDrawArrays(GL_QUADS, 0, 4);
	}

	private void vertexData() {
		vertexData.clear();

		vertexData.put(x).put(y).put(0);

		vertexData.put(x + w).put(y).put(0);

		vertexData.put(x + w).put(y + h).put(0);

		vertexData.put(x).put(y + h).put(0);

		vertexData.flip();
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
		for (int i = 0; i < 4; i++) {
			colorData.put(r).put(g).put(b).put(a);
		}
		colorData.flip();
	}

}
