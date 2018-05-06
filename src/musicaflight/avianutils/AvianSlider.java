
package musicaflight.avianutils;

public class AvianSlider {

	private static AvianRectangle r = new AvianRectangle();
	private static AvianCircle c = new AvianCircle();

	float x;
	float y;
	float w;
	double value;
	private double shownValue;
	boolean highlight;
	boolean dragging;

	public AvianSlider() {
		AvianApp.addMouseListener(new AvianMouse() {

			@Override
			public void press(int button, float mx, float yy) {
				if (restrict)
					return;

				if (highlight && button == 0) {
					value = (mx - x) / w;
					dragging = true;
				}
				if (value < 0)
					value = 0;
				else if (value > 1)
					value = 1;

			}

			@Override
			public void release(int button, float xx, float yy) {
				dragging = false;
			}

			@Override
			public void move(float mx, float my) {
				if (restrict)
					return;
				highlight = mx > x - 10 && mx < x + w + 10 && my > y - 10 && my < y + 10;
				if (dragging) {
					value = (mx - x) / w;
				}
				if (value < 0)
					value = 0;
				else if (value > 1)
					value = 1;
			}

			@Override
			public void scroll(int count) {
				// TODO Auto-generated method stub

			}

		});
	}

	public AvianSlider(double init) {
		this();
		value = init;
	}

	public void set(float x, float y, float w) {
		this.x = x;
		this.y = y;
		this.w = w;
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

	public double getValue() {
		return value;
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

	public void setValue(double value) {
		this.value = value;
	}

	public boolean isDragging() {
		return dragging;
	}

	public void render() {
		render(255f);
	}

	boolean restrict;

	public void restrictFocus(boolean res) {
		this.restrict = res;
	}

	float mult;

	public void logic() {
		if (dragging) {
			mult = AvianMath.glide(mult, 1.5f, 10f);
		} else if (highlight) {
			mult = AvianMath.glide(mult, 1.25f, 10f);
		} else {
			mult = AvianMath.glide(mult, 1f, 10f);
		}
		shownValue = AvianMath.glide((float) shownValue, (float) value, 5f);
	}

	public void render(float alpha) {
		alpha *= mult;
		r.set(x, y - 1, (float) (w * shownValue), 2);
		r.render(alpha * 200f / 255f);
		r.set(x + r.getW(), y - 1, (float) (w * (1d - shownValue)), 2);
		r.render(alpha * 50 / 255f);
		c.set((float) (x + shownValue * w), y, 10 * mult);
		c.render(true, alpha);
	}
}
