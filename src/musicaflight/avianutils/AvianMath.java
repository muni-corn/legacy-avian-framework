
package musicaflight.avianutils;

import java.util.Random;

public class AvianMath {

	static Random rand = new Random();

	public static float specialrtSin(float arg, float root) {
		return (float) Math.pow(sin(arg), 1f / root);
	}

	/** @return A sine output using degrees */

	public static float sin(float arg) {
		return (float) Math.sin(Math.toRadians(arg));
	}

	/** @return A cosine output using degrees */

	public static float cos(float arg) {
		return (float) Math.cos(Math.toRadians(arg));
	}

	/** @return A tangent output using degrees */

	public static float tan(float arg) {
		return (float) Math.tan(Math.toRadians(arg));
	}
	
	/** @return An inverse sine output using degrees */
	
	public static float arcsin(float arg) {
		return (float) Math.toDegrees(Math.asin(arg));
	}
	
	/** @return An inverse cosine output using degrees */
	
	public static float arccos(float arg) {
		return (float) Math.toDegrees(Math.acos(arg));
	}
	
	/** @return An inverse tangent output using degrees */
	
	public static float arctan(float arg) {
		return (float) Math.toDegrees(Math.atan(arg));
	}

	public static float glide(float current, float target, float smoothness) {
		if (smoothness == 0 || current == Float.NaN)
			return target;
		float delta = target - current;
		float next = current + (delta / smoothness);
		return next;
	}

	public static double randomDouble() {
		return rand.nextDouble();
	}

	public static float randomFloat() {
		return rand.nextFloat();
	}

	/** @return A random int from 0 to the specified <code>max</code> value minus 1. */

	public static int randomInt(int max) {
		if (max <= 0) {
			return 0;
		}
		return rand.nextInt(max);
	}

}
