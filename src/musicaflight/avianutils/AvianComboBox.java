
package musicaflight.avianutils;

public class AvianComboBox {

	private String[] options;
	int index;
	float yOffset;
	AvianFont font;
	float x, y, w;
	static AvianRectangle r = new AvianRectangle();
	boolean focus;
	float trans;

	public AvianComboBox(String... options) {
		this.options = options;
		AvianApp.addMouseListener(new AvianMouse() {

			@Override
			public void press(int button, float mx, float my) {
				// TODO Auto-generated method stub

			}

			@Override
			public void release(int button, float mx, float my) {
				if (restrict || button != 0)
					return;
				if (mx > x && mx < x + w && my > y - getHeight() / 2 && my < y + getHeight() / 2) {
					AvianApp.setActiveBox(AvianComboBox.this);
					focus = true;
				}
			}

			@Override
			public void move(float mx, float my) {
				// TODO Auto-generated method stub

			}

			@Override
			public void scroll(int count) {
				// TODO Auto-generated method stub

			}

		});
	}

	public void logic() {
		if (focus) {
			trans = AvianMath.glide(trans, 1, 7);
		} else {
			trans = AvianMath.glide(trans, 0, 7);
		}
		yOffset = AvianMath.glide(yOffset, -index * (font == null ? AvianApp.Vegur.getHeight() + 10 : font.getHeight() + 10), 7);

	}

	public void set(float x, float y, float w) {
		this.x = x;
		this.y = y;
		this.w = w;
	}

	public float getHeight() {
		if (font == null)
			return AvianApp.Vegur.getHeight() + 10;
		return font.getHeight() + 10;
	}

	public void render(float alpha) {
		AvianFont.setAlignment(AvianFont.ALIGN_LEFT, AvianFont.ALIGN_CENTER);

		r.set(x - 5, (y - getHeight() / 2f) * 1, w, (getHeight())/* * (1d - trans) + AvianApp.getHeight() * trans*/);
		r.render(0, alpha * 100 / 255);
		if (trans > 0) {
			r.set(0, 0, AvianApp.getWidth(), AvianApp.getHeight());
			r.render(0, trans * 200 / 255f);
			for (int i = 0; i < options.length; i++) {
				float selection = y + index * (font == null ? AvianApp.Vegur.getHeight() + 10 : font.getHeight() + 10) + yOffset;
				float shouldBe = y + i * (font == null ? AvianApp.Vegur.getHeight() + 10 : font.getHeight() + 10) + yOffset;
				AvianApp.Vegur.drawString(x, shouldBe * trans + selection * (1f - trans), options[i], alpha * trans);
			}
		}
		if (index < 0)
			index = 0;
		else if (index >= options.length)
			index = options.length - 1;
		if (font == null) {
			AvianApp.Vegur.drawString(x, y + yOffset + index * (font == null ? AvianApp.Vegur.getHeight() + 10 : font.getHeight() + 10), options[index], alpha);
		} else {
			AvianFont.setAlignment(AvianFont.ALIGN_LEFT, AvianFont.ALIGN_CENTER);
			font.drawString(x, y + yOffset + index * (font == null ? AvianApp.Vegur.getHeight() + 10 : font.getHeight() + 10), options[index], alpha);
		}

	}

	public void setSelection(int i) {
		index = i;
	}

	public void setFont(AvianFont f) {
		font = f;
	}

	boolean restrict;

	public void restrictFocus(boolean b) {
		restrict = b;
	}

	public String getSelectionName() {
		return options[index];
	}

	public int getSelectionIndex() {
		return index;
	}

	public void setSelectionByName(String v) {
		for (int i = 0; i < options.length; i++) {
			if (v.equals(options[i])) {
				index = i;
				return;
			}
		}
		index = 0;
	}

}
