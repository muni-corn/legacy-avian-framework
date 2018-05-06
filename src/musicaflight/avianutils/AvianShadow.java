
package musicaflight.avianutils;

import static org.lwjgl.opengl.GL11.*;

public class AvianShadow {

	private float x, y, w, h;

	public AvianShadow(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.w = width;
		this.h = height;
	}

	public AvianShadow() {
		this(0, 0, 0, 0);
	}

	public void render() {
		render(1f);

	}

	public void render(float alpha) {
		if (glGetBoolean(GL_VERTEX_ARRAY)==GL_FALSE)
			glEnableClientState(GL_VERTEX_ARRAY);
		if (glGetBoolean(GL_COLOR_ARRAY)==GL_FALSE)
			glEnableClientState(GL_COLOR_ARRAY);
		if (glGetBoolean(GL_TEXTURE_COORD_ARRAY)==GL_FALSE)
			glEnableClientState(GL_TEXTURE_COORD_ARRAY);

		if (w > 512) {
			AvianApp.shadowLeft.render(x, y + h, alpha * 255f);
			AvianApp.shadowLeft.render(x + w, y + h, -256, AvianApp.shadow.getHeight(), alpha * 255f);
			AvianApp.shadow.render(AvianApp.shadow.getWidth() + x, y + h, w - 512, AvianApp.shadow.getHeight(), alpha * 255f);

		} else {
			AvianApp.shadowLeft.render(x, y + h, w / 2, AvianApp.shadow.getHeight(), alpha * 255f);
			AvianApp.shadowLeft.render(x + w, y + h, -w / 2, AvianApp.shadow.getHeight(), alpha * 255f);
		}

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

	public void set(AvianRectangle r) {
		this.x = r.getX();
		this.w = r.getW();
		this.y = r.getY();
		this.h = r.getH();
	}
	
	public void set(float x, float y, float w, float h) {
		this.x = x;
		this.w = w;
		this.y = y;
		this.h = h;
	}

}
