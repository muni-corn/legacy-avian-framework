
package musicaflight.avianutils;

public class AvianColor {

	private float r, g, b, a;

	private boolean lock;

	private static AvianColor c = new AvianColor(0, 0, 0, 0);

	public AvianColor(float r, float g, float b) {
		this.r = r > 255f ? 255f : (r < 0f ? 0f : r);
		this.g = g > 255f ? 255f : (g < 0f ? 0f : g);
		this.b = b > 255f ? 255f : (b < 0f ? 0f : b);
		a = 255f;
	}

	public AvianColor(float r, float g, float b, float a) {
		this(r, g, b);
		this.a = a > 255f ? 255f : (a < 0f ? 0f : a);
	}

	public float getR() {
		return r;
	}

	public float getG() {
		return g;
	}

	public float getB() {
		return b;
	}

	public float getA() {
		return a;
	}

	public AvianColor setR(float r) {
		if (!lock)
			this.r = r > 255f ? 255f : (r < 0f ? 0f : r);
		else
			printError();
		return this;
	}

	public AvianColor setG(float g) {
		if (!lock)
			this.g = g > 255f ? 255f : (g < 0f ? 0f : g);
		else
			printError();
		return this;
	}

	public AvianColor setB(float b) {
		if (!lock)
			this.b = b > 255f ? 255f : (b < 0f ? 0f : b);
		else
			printError();
		return this;
	}

	public AvianColor setA(float a) {
		if (!lock)
			this.a = a > 255f ? 255f : (a < 0f ? 0f : a);
		else
			printError();

		return this;
	}

	public AvianColor setRGBA(float r, float g, float b, float a) {
		if (!lock) {
			this.r = r > 255f ? 255f : (r < 0f ? 0f : r);
			this.g = g > 255f ? 255f : (g < 0f ? 0f : g);
			this.b = b > 255f ? 255f : (b < 0f ? 0f : b);
			this.a = a > 255f ? 255f : (a < 0f ? 0f : a);
		} else
			printError();
		return this;
	}

	public AvianColor setRGB(float r, float g, float b) {
		if (!lock) {
			this.r = r > 255f ? 255f : (r < 0f ? 0f : r);
			this.g = g > 255f ? 255f : (g < 0f ? 0f : g);
			this.b = b > 255f ? 255f : (b < 0f ? 0f : b);
		} else
			printError();
		return this;
	}

	private void printError() {
		System.err.println("!!WARNING!! -- This set method you called is on an AvianColor that is locked and can not be altered!");
		StackTraceElement e = Thread.currentThread().getStackTrace()[3];
		System.err.println("caused at " + e);
	}

	public static AvianColor get(float r, float g, float b, float a) {
		return c.setRGBA(r, g, b, a);
	}

	public static AvianColor get(float r, float g, float b) {
		return c.setRGBA(r, g, b, 255);
	}

	public static AvianColor black() {
		return black.setA(255);
	}

	public static AvianColor black(float a) {
		return black.setA(a);
	}

	public static AvianColor white() {
		return white.setA(255);
	}

	public static AvianColor white(float a) {
		return white.setA(a);
	}

	public static AvianColor red() {
		return red.setA(255);
	}

	public static AvianColor red(float a) {
		return red.setA(a);
	}

	public static AvianColor green() {
		return green.setA(255);
	}

	public static AvianColor green(float a) {
		return green.setA(a);
	}

	public static AvianColor blue() {
		return blue.setA(255);
	}

	public static AvianColor blue(float a) {
		return blue.setA(a);
	}

	private static AvianColor black = new AvianColor(0, 0, 0),
			white = new AvianColor(255, 255, 255),
			red = new AvianColor(255, 0, 0), green = new AvianColor(0, 255, 0),
			blue = new AvianColor(0, 0, 255);

}
