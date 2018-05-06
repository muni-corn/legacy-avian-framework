
package musicaflight.avianutils;

public class AvianEase {

	private Ease ease = Ease.EXPONENTIAL;
	private Direction direction = Direction.OUT;
	private double phase, start, end = 1d, duration = 1d,
			amplitude = Double.NaN, period = Double.NaN;

	public static enum Ease {
		LINEAR,
		SINUSOIDAL,
		QUADRATIC,
		CUBIC,
		QUARTIC,
		QUINTIC,
		EXPONENTIAL,
		CIRCULAR,
		BOUNCE,
		BACK,
		ELASTIC;
	}

	public static enum Direction {
		IN,
		OUT,
		IN_AND_OUT;
	}

	public AvianEase(Ease ease) {
		this.ease = ease;
	}

	public AvianEase(Ease ease, Direction direction) {
		this(ease);
		this.direction = direction;
	}

	public AvianEase(double duration, Ease ease) {
		this(ease);
		this.duration = duration;
	}

	public AvianEase(double duration, Ease ease, Direction direction) {
		this(ease, direction);
		this.duration = duration;
	}

	public AvianEase(double start, double end, Ease ease) {
		this(ease);
		this.start = start;
		this.end = end;
	}

	public AvianEase(double start, double end, Ease ease, Direction direction) {
		this(start, end, ease);
		this.direction = direction;
	}

	public AvianEase(double start, double end, double duration, Ease ease) {
		this(start, end, ease);
		this.duration = duration;
	}

	public AvianEase(double start, double end, double duration, Ease ease, Direction direction) {
		this(start, end, duration, ease);
		this.direction = direction;
	}

	public AvianEase setBack(double amp) {
		this.amplitude = amp;
		return this;
	}

	public AvianEase setAmplitudeAndPeriod(double amp, double period) {
		this.amplitude = amp;
		this.period = period;
		return this;
	}

	public double forward() {
		phase += (AvianApp.getDeltaLogicTime() / duration);
		if (phase > 1d)
			phase = 1;
		return result();
	}

	public double rewind() {
		phase -= (AvianApp.getDeltaLogicTime() / duration);
		if (phase < 0d)
			phase = 0;
		return result();

	}

	public double forward(double p) {
		this.phase += p;
		if (this.phase < 0d)
			this.phase = 0;
		if (this.phase > 1d)
			this.phase = 1;
		return result();

	}

	public double rewind(double p) {
		this.phase -= p;
		if (this.phase < 0d)
			this.phase = 0;
		if (this.phase > 1d)
			this.phase = 1;
		return result();

	}

	public float result() {
		return resultAt(phase);
	}

	/** @return The result within a range drom 0 - 1. */

	public double normalizedResult() {
		return (result() - start) / (end - start);
	}

	public float resultAt(double p) {
		switch (ease) {
			case LINEAR:
				return (float) linear(p * duration, start, end - start, duration);
			case SINUSOIDAL:
				return (float) sinusoifal(p * duration, start, end - start, duration);
			case QUADRATIC:
				return (float) quadratic(p * duration, start, end - start, duration);
			case CUBIC:
				return (float) cubic(p * duration, start, end - start, duration);
			case QUARTIC:
				return (float) quartic(p * duration, start, end - start, duration);
			case QUINTIC:
				return (float) quintic(p * duration, start, end - start, duration);
			case EXPONENTIAL:
				return (float) exponential(p * duration, start, end - start, duration);
			case CIRCULAR:
				return (float) circular(p * duration, start, end - start, duration);
			case BOUNCE:
				return (float) bounce(p * duration, start, end - start, duration);
			case BACK:
				return (float) back(p * duration, start, end - start, duration);
			case ELASTIC:
				return (float) elastic(p * duration, start, end - start, duration);
			default:
				throw new IllegalArgumentException("Specified Ease type does not exist or has not been given functionality: " + ease);
		}
	}

	//time = current time
	//start = start value
	//(end - start) = change in value
	//duration = duration

	public void set(double phase) {
		if (phase > 1d)
			phase = 1d;
		else if (phase < 0d)
			phase = 0d;
		this.phase = phase;
	}

	public void setStart(double start) {
		this.start = start;
	}

	public void setEnd(double end) {
		this.end = end;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

	public double getPhase() {
		return phase;
	}

	public static double linear(double t, double b, double c, double d) {
		return c * t / d + b;
	}

	private double sinusoifal(double t, double b, double c, double d) {
		switch (direction) {
			case IN:
				return sinEaseIn(t, b, c, d);
			case IN_AND_OUT:
				return sinEaseInOut(t, b, c, d);
			case OUT:
				return sinEaseOut(t, b, c, d);
			default:
				break;

		}
		return Double.NaN;
	}

	public static double sinEaseIn(double t, double b, double c, double d) {
		return -c * Math.cos(t / d * (Math.PI / 2d)) + c + b;
	}

	public static double sinEaseOut(double t, double b, double c, double d) {
		return c * Math.sin(t / d * (Math.PI / 2d)) + b;
	}

	public static double sinEaseInOut(double t, double b, double c, double d) {
		return -c / 2 * (Math.cos(Math.PI * t / d) - 1d) + b;
	}

	private double quadratic(double t, double b, double c, double d) {
		switch (direction) {
			case IN:
				return quadEaseIn(t, b, c, d);
			case IN_AND_OUT:
				return quadEaseInOut(t, b, c, d);
			case OUT:
				return quadEaseOut(t, b, c, d);
			default:
				break;
		}
		return Double.NaN;
	}

	public static double quadEaseIn(double t, double b, double c, double d) {
		return c * (t /= d) * t + b;
	}

	public static double quadEaseOut(double t, double b, double c, double d) {
		return -c * (t /= d) * (t - 2d) + b;
	}

	public static double quadEaseInOut(double t, double b, double c, double d) {
		if ((t /= d / 2d) < 1)
			return c / 2d * t * t + b;
		return -c / 2d * ((--t) * (t - 2d) - 1d) + b;
	}

	private double cubic(double t, double b, double c, double d) {
		switch (direction) {
			case IN:
				return cubEaseIn(t, b, c, d);
			case IN_AND_OUT:
				return cubEaseInOut(t, b, c, d);
			case OUT:
				return cubEaseOut(t, b, c, d);
			default:
				break;
		}
		return Double.NaN;
	}

	public static double cubEaseIn(double t, double b, double c, double d) {
		return c * (t /= d) * t * t + b;
	}

	public static double cubEaseOut(double t, double b, double c, double d) {
		return c * ((t = t / d - 1d) * t * t + 1d) + b;
	}

	public static double cubEaseInOut(double t, double b, double c, double d) {
		if ((t /= d / 2d) < 1)
			return c / 2d * t * t * t + b;
		return c / 2d * ((t -= 2d) * t * t + 2d) + b;
	}

	private double quartic(double t, double b, double c, double d) {
		switch (direction) {
			case IN:
				return quartEaseIn(t, b, c, d);
			case IN_AND_OUT:
				return quartEaseInOut(t, b, c, d);
			case OUT:
				return quartEaseOut(t, b, c, d);
			default:
				break;
		}
		return Double.NaN;
	}

	public static double quartEaseIn(double t, double b, double c, double d) {
		return c * (t /= d) * t * t * t + b;
	}

	public static double quartEaseOut(double t, double b, double c, double d) {
		return -c * ((t = t / d - 1d) * t * t * t - 1d) + b;
	}

	public static double quartEaseInOut(double t, double b, double c, double d) {
		if ((t /= d / 2d) < 1d)
			return c / 2d * t * t * t * t + b;
		return -c / 2d * ((t -= 2d) * t * t * t - 2d) + b;
	}

	private double quintic(double t, double b, double c, double d) {
		switch (direction) {
			case IN:
				return quintEaseIn(t, b, c, d);
			case IN_AND_OUT:
				return quintEaseInOut(t, b, c, d);
			case OUT:
				return quintEaseOut(t, b, c, d);
			default:
				break;
		}
		return Double.NaN;
	}

	public static double quintEaseIn(double t, double b, double c, double d) {
		return c * (t /= d) * t * t * t * t + b;
	}

	public static double quintEaseOut(double t, double b, double c, double d) {
		return c * ((t = t / d - 1d) * t * t * t * t + 1d) + b;
	}

	public static double quintEaseInOut(double t, double b, double c, double d) {
		if ((t /= d / 2d) < 1d)
			return c / 2d * t * t * t * t * t + b;
		return c / 2d * ((t -= 2d) * t * t * t * t + 2d) + b;
	}

	private double exponential(double t, double b, double c, double d) {
		switch (direction) {
			case IN:
				return expoEaseIn(t, b, c, d);
			case IN_AND_OUT:
				return expoEaseInOut(t, b, c, d);
			case OUT:
				return expoEaseOut(t, b, c, d);
			default:
				break;
		}
		return Double.NaN;
	}

	public static double expoEaseIn(double t, double b, double c, double d) {
		return (t == 0) ? b : c * Math.pow(2d, 10d * (t / d - 1d)) + b;
	}

	public static double expoEaseOut(double t, double b, double c, double d) {
		return (t == d) ? b + c : c * (-Math.pow(2.0, -10.0 * t / d) + 1.0) + b;
	}

	public static double expoEaseInOut(double t, double b, double c, double d) {
		if (t == 0)
			return b;
		if (t == d)
			return b + c;
		if ((t /= d / 2d) < 1d)
			return c / 2d * Math.pow(2d, 10d * (t - 1d)) + b;
		return c / 2d * (-Math.pow(2d, -10d * --t) + 2d) + b;
	}

	private double circular(double t, double b, double c, double d) {
		switch (direction) {
			case IN:
				return circEaseIn(t, b, c, d);
			case IN_AND_OUT:
				return circEaseInOut(t, b, c, d);
			case OUT:
				return circEaseOut(t, b, c, d);
			default:
				break;
		}
		return Double.NaN;
	}

	public static double circEaseIn(double t, double b, double c, double d) {
		return -c * (Math.sqrt(1d - (t /= d) * t) - 1d) + b;
	}

	public static double circEaseOut(double t, double b, double c, double d) {
		return c * Math.sqrt(1d - (t = t / d - 1d) * t) + b;
	}

	public static double circEaseInOut(double t, double b, double c, double d) {
		if ((t /= d / 2d) <= 1d)
			return -c / 2d * (Math.sqrt(1d - t * t) - 1d) + b;
		return c / 2d * (Math.sqrt(1d - (t -= 2d) * t) + 1d) + b;
	}

	private double bounce(double t, double b, double c, double d) {
		switch (direction) {
			case IN:
				return bounceEaseIn(t, b, c, d);
			case IN_AND_OUT:
				return bounceEaseInOut(t, b, c, d);
			case OUT:
				return bounceEaseOut(t, b, c, d);
			default:
				break;
		}
		return Double.NaN;
	}

	public static double bounceEaseIn(double t, double b, double c, double d) {
		return c - bounceEaseOut(d - t, 0d, c, d) + b;
	}

	public static double bounceEaseOut(double t, double b, double c, double d) {
		if ((t /= d) < (1d / 2.75d)) {
			return c * (7.5625d * t * t) + b;
		} else if (t < (2d / 2.75d)) {
			return c * (7.5625d * (t -= (1.5d / 2.75d)) * t + .75d) + b;
		} else if (t < (2.5d / 2.75d)) {
			return c * (7.5625d * (t -= (2.25d / 2.75d)) * t + .9375d) + b;
		} else {
			return c * (7.5625d * (t -= (2.625d / 2.75d)) * t + .984375d) + b;
		}
	}

	public static double bounceEaseInOut(double t, double b, double c, double d) {
		if (t < d / 2d)
			return bounceEaseIn(t * 2d, 0, c, d) * .5d + b;
		return bounceEaseOut(t * 2d - d, 0, c, d) * .5d + c * .5d + b;
	}

	private double back(double t, double b, double c, double d) {
		if (Double.isNaN(amplitude)) {
			switch (direction) {
				case IN:
					return backEaseIn(t, b, c, d);
				case IN_AND_OUT:
					return backEaseInOut(t, b, c, d);
				case OUT:
					return backEaseOut(t, b, c, d);
				default:
					break;
			}
		} else {
			switch (direction) {
				case IN:
					return backEaseIn(t, b, c, d, amplitude);
				case IN_AND_OUT:
					return backEaseInOut(t, b, c, d, amplitude);
				case OUT:
					return backEaseOut(t, b, c, d, amplitude);
				default:
					break;
			}
		}
		return Double.NaN;
	}

	public static double backEaseIn(double t, double b, double c, double d) {
		double a = 1.70158d;
		return c * (t /= d) * t * ((a + 1d) * t - a) + b;
	}

	public static double backEaseIn(double t, double b, double c, double d, double a) {
		return c * (t /= d) * t * ((a + 1d) * t - a) + b;
	}

	public static double backEaseOut(double t, double b, double c, double d) {
		double a = 1.70158d;
		return c * ((t = t / d - 1d) * t * ((a + 1d) * t + a) + 1d) + b;
	}

	public static double backEaseOut(double t, double b, double c, double d, double a) {
		return c * ((t = t / d - 1d) * t * ((a + 1d) * t + a) + 1d) + b;
	}

	public static double backEaseInOut(double t, double b, double c, double d) {
		double a = 1.70158d;
		if ((t /= d / 2d) < 1d)
			return c / 2d * (t * t * (((a *= (1.525d)) + 1d) * t - a)) + b;
		return c / 2d * ((t -= 2d) * t * (((a *= (1.525d)) + 1d) * t + a) + 2d) + b;
	}

	public static double backEaseInOut(double t, double b, double c, double d, double a) {
		if ((t /= d / 2d) < 1d)
			return c / 2d * (t * t * (((a *= (1.525d)) + 1d) * t - a)) + b;
		return c / 2d * ((t -= 2d) * t * (((a *= (1.525d)) + 1d) * t + a) + 2d) + b;
	}

	private double elastic(double t, double b, double c, double d) {
		if (Double.isNaN(amplitude) || Double.isNaN(period)) {
			switch (direction) {
				case IN:
					return elasticEaseIn(t, b, c, d);
				case IN_AND_OUT:
					return elasticEaseInOut(t, b, c, d);
				case OUT:
					return elasticEaseOut(t, b, c, d);
				default:
					break;
			}
		} else {
			switch (direction) {
				case IN:
					return elasticEaseIn(t, b, c, d, amplitude, period);
				case IN_AND_OUT:
					return elasticEaseInOut(t, b, c, d, amplitude, period);
				case OUT:
					return elasticEaseOut(t, b, c, d, amplitude, period);
				default:
					break;
			}
		}
		return Double.NaN;
	}

	public static double elasticEaseIn(double t, double b, double c, double d) {
		if (t == 0)
			return b;
		if ((t /= d) == 1d)
			return b + c;
		double p = d * .3d;
		double a = c;
		double s = p / 4d;
		return -(a * Math.pow(2d, 10d * (t -= 1d)) * Math.sin((t * d - s) * (2d * Math.PI) / p)) + b;
	}

	public static double elasticEaseIn(double t, double b, double c, double d, double a, double p) {
		double s;
		if (t == 0d)
			return b;
		if ((t /= d) == 1d)
			return b + c;
		if (a < Math.abs(c)) {
			a = c;
			s = p / 4d;
		} else {
			s = p / (2d * Math.PI) * Math.asin(c / a);
		}
		return -(a * Math.pow(2d, 10d * (t -= 1d)) * Math.sin((t * d - s) * (2d * Math.PI) / p)) + b;
	}

	public static double elasticEaseOut(double t, double b, double c, double d) {
		if (t == 0)
			return b;
		if ((t /= d) == 1d)
			return b + c;
		double p = d * .3d;
		double a = c;
		double s = p / 4d;
		return (a * Math.pow(2d, -10d * t) * Math.sin((t * d - s) * (2d * Math.PI) / p) + c + b);
	}

	public static double elasticEaseOut(double t, double b, double c, double d, double a, double p) {
		double s;
		if (t == 0)
			return b;
		if ((t /= d) == 1d)
			return b + c;
		if (a < Math.abs(c)) {
			a = c;
			s = p / 4d;
		} else {
			s = p / (2d * Math.PI) * Math.asin(c / a);
		}
		return (a * Math.pow(2d, -10d * t) * Math.sin((t * d - s) * (2d * Math.PI) / p) + c + b);
	}

	public static double elasticEaseInOut(double t, double b, double c, double d) {
		if (t == 0)
			return b;
		if ((t /= d / 2d) == 2d)
			return b + c;
		double p = d * (.3d * 1.5d);
		double a = c;
		double s = p / 4d;
		if (t < 1)
			return -.5d * (a * Math.pow(2d, 10d * (t -= 1d)) * Math.sin((t * d - s) * (2d * Math.PI) / p)) + b;
		return a * Math.pow(2d, -10d * (t -= 1d)) * Math.sin((t * d - s) * (2d * Math.PI) / p) * .5d + c + b;
	}

	public static double elasticEaseInOut(double t, double b, double c, double d, double a, double p) {
		double s;
		if (t == 0)
			return b;
		if ((t /= d / 2d) == 2)
			return b + c;
		if (a < Math.abs(c)) {
			a = c;
			s = p / 4d;
		} else {
			s = p / (2d * Math.PI) * Math.asin(c / a);
		}
		if (t < 1d)
			return -.5d * (a * Math.pow(2d, 10d * (t -= 1d)) * Math.sin((t * d - s) * (2d * Math.PI) / p)) + b;
		return a * Math.pow(2d, -10d * (t -= 1d)) * Math.sin((t * d - s) * (2d * Math.PI) / p) * .5d + c + b;
	}

	public void setEaseType(Ease type) {
		this.ease = type;
	}

	public void setDirection(Direction dir) {
		this.direction = dir;
	}

}
