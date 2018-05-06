
package musicaflight.avianutils;

import static musicaflight.avianutils.AvianInput.*;
import static org.lwjgl.opengl.GL11.*;

public final class AvianCamera {

	private float x = 0;
	private float y = 0;
	private float z = 0;
	private float pitch = 0;
	private float yaw = 0;
	private float roll = 0;
	private float fov = 90;
	private float aspectRatio = 1;
	private float zNear;
	private float zFar;

	public AvianCamera() {
		zNear = 0.03f;
		zFar = 100;
	}

	public AvianCamera(float aspectRatio) {
		this.aspectRatio = aspectRatio;
		zNear = 0.03f;
		zFar = 100;
	}

	public AvianCamera(float aspectRatio, float zNear, float zFar) {
		this.aspectRatio = aspectRatio;
		this.zNear = zNear;
		this.zFar = zFar;
	}

	public AvianCamera(float aspectRatio, float x, float y, float z) {
		this(aspectRatio);
		this.x = x;
		this.y = y;
		this.z = z;
		zNear = 0.03f;
		zFar = 300;
	}

	public AvianCamera(float aspectRatio, float x, float y, float z, float pitch, float yaw, float roll) {
		this(aspectRatio, x, y, z);
		this.pitch = pitch;
		this.yaw = yaw;
		this.roll = roll;
	}

	public AvianCamera(float aspectRatio, float x, float y, float z, float pitch, float yaw, float roll, float zNear, float zFar) {
		//		if (aspectRatio <= 0) {
		//			throw new IllegalArgumentException("aspectRatio " + aspectRatio + " was 0 or was smaller than 0");
		//		}
		//		if (zNear <= 0) {
		//			throw new IllegalArgumentException("zNear " + zNear + " was 0 or was smaller than 0");
		//		}
		//		if (zFar <= zNear) {
		//			throw new IllegalArgumentException("zFar " + zFar + " was smaller or the same as zNear " + zNear);
		//		}
		this.aspectRatio = aspectRatio;
		this.x = x;
		this.y = y;
		this.z = z;
		this.pitch = pitch;
		this.yaw = yaw;
		this.roll = roll;
		this.zNear = zNear;
		this.zFar = zFar;
	}

	float lastX, lastY;

	public void mouse() {
		final float MAX_LOOK_UP = 90;
		final float MAX_LOOK_DOWN = -90;
		float dx = AvianInput.getMouseX() - lastX;
		float dy = AvianInput.getMouseY() - lastY;
		lastX = AvianInput.getMouseX();
		lastY = AvianInput.getMouseY();
		float mouseDX = dx * 0.16f;
		float mouseDY = dy * 0.16f;
		if ((yaw + mouseDX) >= 360) {
			yaw = (yaw + mouseDX) - 360;
		} else if ((yaw + mouseDX) < 0) {
			yaw = (360 - yaw) + mouseDX;
		} else {
			yaw += mouseDX;
		}
		if (((pitch - mouseDY) >= MAX_LOOK_DOWN) && ((pitch - mouseDY) <= MAX_LOOK_UP)) {
			pitch -= mouseDY;
		} else if ((pitch - mouseDY) < MAX_LOOK_DOWN) {
			pitch = MAX_LOOK_DOWN;
		} else if ((pitch - mouseDY) > MAX_LOOK_UP) {
			pitch = MAX_LOOK_UP;
		}

	}

	long time = System.currentTimeMillis();

	public void keyboard(float speed) {

		boolean keyUp = isKeyDown(KEY_UP) || isKeyDown(KEY_W);
		boolean keyDown = isKeyDown(KEY_DOWN) || isKeyDown(KEY_S);
		boolean keyLeft = isKeyDown(KEY_LEFT) || isKeyDown(KEY_A);
		boolean keyRight = isKeyDown(KEY_RIGHT) || isKeyDown(KEY_D);
		boolean flyUp = isKeyDown(KEY_SPACE);
		boolean flyDown = isKeyDown(KEY_LEFT_SHIFT);

		speed = ((System.currentTimeMillis() - time) * speed) / 1000f;

		if (keyUp && keyRight && !keyLeft && !keyDown) {
			move(speed, 0, -speed);
		} else if (keyUp && keyLeft && !keyRight && !keyDown) {
			move(-speed, 0, -speed);
		} else if (keyUp && !keyLeft && !keyRight && !keyDown) {
			move(0, 0, -speed);
		} else if (keyDown && keyLeft && !keyRight && !keyUp) {
			move(-speed, 0, speed);
		} else if (keyDown && keyRight && !keyLeft && !keyUp) {
			move(speed, 0, speed);
		} else if (keyDown && !keyUp && !keyLeft && !keyRight) {
			move(0, 0, speed);
		} else if (keyLeft && !keyRight && !keyUp && !keyDown) {
			move(-speed, 0, 0);
		} else if (keyRight && !keyLeft && !keyUp && !keyDown) {
			move(speed, 0, 0);
		}
		if (flyUp && !flyDown) {
			y += speed;
		} else if (flyDown && !flyUp) {
			y -= speed;
		}
	}

	public void move(float dx, float dy, float dz) {
		z += (dx * AvianMath.cos(yaw - 90f)) + (dz * AvianMath.cos(yaw));
		x -= (dx * AvianMath.sin(yaw - 90f)) + (dz * AvianMath.sin(yaw));
		y += dy * AvianMath.sin(pitch - 90f);
	}

	public void setPosition(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void applyOrthographicMatrix() {
		glDisable(GL_DEPTH_TEST);
		glDisable(GL_CULL_FACE);
		glPushAttrib(GL_TRANSFORM_BIT);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(-aspectRatio, aspectRatio, -1, 1, 0, zFar);
		glPopAttrib();
	}

	public void applyAvianOrthoMatrix() {
		glPushAttrib(GL_TRANSFORM_BIT);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();

		glOrtho(0, AvianApp.getWidth(), AvianApp.getHeight(), 0, -1000, 1000);
		glPopAttrib();
		glLoadIdentity();

		glDisable(GL_DEPTH_TEST);
		glDisable(GL_CULL_FACE);
		glDisable(GL_LIGHTING);
	}

	//	public void applyOptimalStates() {
	//		if (GLContext.getCapabilities().GL_ARB_depth_clamp) {
	//			glEnable(GL_DEPTH_CLAMP);
	//		}
	//	}

	public void applyPerspectiveMatrix() {
		glPushAttrib(GL_TRANSFORM_BIT);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		//		float aspect = AdoxAppCore.getWidth() / AdoxAppCore.getHeight();
		//		float near = 1; // near should be chosen as far into the scene as possible
		//		float far = 100;
		//		float fov = 1; // 1 gives you a 90° field of view. It's tan(fov_angle)/2.
		//		glFrustum(-aspect * near * fov, aspect * near * fov, -fov, fov, near, far);
		glFrustum(-500, 500, -300, 300, 1, 100000);
		glPopAttrib();
		glMatrixMode(GL_MODELVIEW);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
	}

	public void applyTranslations() {
		glLoadIdentity();
		glPushAttrib(GL_TRANSFORM_BIT);
		glMatrixMode(GL_MODELVIEW);
		glRotatef(-pitch, 1, 0, 0);
		glRotatef(yaw, 0, 1, 0);
		glRotatef(roll, 0, 0, 1);
		glTranslatef(-x, -y, -z);
		glPopAttrib();
	}

	public void setRotation(float pitch, float yaw, float roll) {
		this.pitch = pitch;
		this.yaw = yaw;
		this.roll = roll;
	}

	public float x() {
		return x;
	}

	public float y() {
		return y;
	}

	public float z() {
		return z;
	}

	public float pitch() {
		return pitch;
	}

	public float yaw() {
		return yaw;
	}

	public float roll() {
		return roll;
	}

	public float fieldOfView() {
		return fov;
	}

	public void setFieldOfView(float fov) {
		this.fov = fov;
	}

	public void setAspectRatio(float aspectRatio) {
		this.aspectRatio = aspectRatio;
	}

	public float aspectRatio() {
		return aspectRatio;
	}

	public float nearClippingPane() {
		return zNear;
	}

	public float farClippingPane() {
		return zFar;
	}

}
