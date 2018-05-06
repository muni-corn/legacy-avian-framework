
package musicaflight.avianutils;

import static org.lwjgl.opengl.GL11.*;

public class AvianLoadIcon {

	int width;

	float sin;
	float alpha1, alpha2, alpha3, alpha4;
	AvianRectangle r1 = new AvianRectangle(0, 0, 10, 10);
	AvianRectangle r2 = new AvianRectangle(15, 0, 10, 10);
	AvianRectangle r3 = new AvianRectangle(15, 15, 10, 10);
	AvianRectangle r4 = new AvianRectangle(0, 15, 10, 10);
	final float halfAlpha = (255f / 2f);

	public void logic() {
		sin += 5;
	}

	AvianColor c = new AvianColor(255, 255, 255);

	public void render(float x, float y) {
		glPushMatrix();

		glTranslatef(x, y, 0);
		glRotatef(45, 0, 0, 1);

		alpha1 = halfAlpha + (AvianMath.sin(sin + 270) * halfAlpha);
		alpha2 = halfAlpha + (AvianMath.sin(sin + 180) * halfAlpha);
		alpha3 = halfAlpha + (AvianMath.sin(sin + 90) * halfAlpha);
		alpha4 = halfAlpha + (AvianMath.sin(sin) * halfAlpha);

		r1.render(alpha1 / 255f);
		r2.render(alpha2 / 255f);
		r3.render(alpha3 / 255f);
		r4.render(alpha4 / 255f);

		glPopMatrix();
	}

}
