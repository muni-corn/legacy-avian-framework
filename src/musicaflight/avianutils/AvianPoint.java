
package musicaflight.avianutils;

public class AvianPoint {

	float x, y, z;

	public AvianPoint() {
		x = 0;
		y = 0;
		z = 0;
	}

	public AvianPoint(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public AvianPoint(float x, float y, float z) {
		this(x, y);
		this.z = z;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

}
