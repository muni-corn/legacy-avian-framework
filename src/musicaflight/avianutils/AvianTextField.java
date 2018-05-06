
package musicaflight.avianutils;

import static musicaflight.avianutils.AvianInput.*;

import java.util.ArrayList;

public class AvianTextField {

	StringBuilder sb = new StringBuilder();

	int index, start, end;

	AvianFont f = AvianApp.Vegur;

	protected static boolean ibeam;

	public AvianTextField(String init) {
		this();
		sb = new StringBuilder(init);
		index = sb.length();
	}

	private ArrayList<String> lines = new ArrayList<String>();

	public AvianTextField() {
		AvianApp.addMouseListener(new AvianMouse() {

			@Override
			public void press(int b, float mx, float my) {
				if (restrictFocus) {
					focus = false;
					return;
				}

				if (mx > x - 5 && mx < x + width + 5 && my > y - 5 && my < y + getHeight() + 5) {
					focus = true;
				} else {
					focus = false;
				}

				index = positionCaret(b, mx, my);

			}

			@Override
			public void release(int b, float xx, float yy) {

			}

			@Override
			public void move(float mx, float my) {
				if (AvianInput.isMouseButtonDown(0)) {
					index = positionCaret(0, mx, my);
				}
			}

			@Override
			public void scroll(int count) {

			}

		});
		AvianApp.addKeyListener(new AvianKeyboard() {

			@Override
			public void type(char text) {
				if (!focus)
					return;

				if (index < 0)
					index = 0;
				if (index > sb.length())
					index = sb.length();
				sb.insert(index, text);
				index++;
				caretCos = 0;
			}

			@Override
			public void repeat(int k) {
				if (!focus)
					return;

				handleKey(k);
			}

			@Override
			public void release(int k) {

			}

			@Override
			public void press(int k) {
				if (!focus)
					return;
				handleKey(k);
			}
		});
	}

	int positionCaret(int b, float mx, float my) {
		caretCos = 0;
		if (b == 0 && focus) {
			int caretY = (int) ((my - y) / f.getHeight());

			if (caretY < 0) {
				return 0;
			} else if (caretY >= lines.size()) {
				return sb.length();
			}
			String line = lines.get(caretY);
			if (line.length() == 0) {
				return sb.length();
			}
			int caretX = 0;
			int characters = 0;
			for (int i = 0; i < line.length() + (caretY == lines.size() - 1 ? 1 : 0); i++) {
				caretX = i;
				if (f.getWidth(line.substring(0, i)) + (i < line.length() ? f.getWidth(String.valueOf(line.charAt(i))) / 2 : 0) > mx - x)
					break;
			}
			if (caretX < 0)
				caretX = 0;
			for (int i = 0; i < caretY; i++) {
				characters += lines.get(i).length();
			}
			return characters + caretX;
		}
		return sb.length();
	}

	void handleKey(int k) {
		caretCos = 0;

		if (k == KEY_RIGHT) {
			index++;
		} else if (k == KEY_LEFT) {
			index--;
		} else if (k == KEY_UP) {
			//			TODO caretY--;
		} else if (k == KEY_DOWN) {
			//			TODO caretY++;
		} else if (k == KEY_BACKSPACE && (sb.length() > 0) && index > 0) {
			sb.deleteCharAt(index - 1);
			index--;
		} else if (k == KEY_ENTER) {
			sb.insert(index, "\n");
			caretCos = 0;
			index++;
		} else
			return;
		if (index < 0)
			index = 0;
		if (index > sb.length())
			index = sb.length();

	}

	String emptyMessage = "";

	public void setEmptyMessage(String empty) {
		emptyMessage = empty;
	}

	public void setFont(AvianFont f) {
		this.f = f;
	}

	public void setString(String string) {
		sb = new StringBuilder(string);
		if (index > sb.length())
			index = sb.length();
	}

	public String toString() {
		if (sb == null)
			return null;
		return sb.toString();
	}

	public StringBuilder getStringBuilder() {
		return sb;
	}

	public void set(float x, float y, float w) {
		this.x = x;
		this.y = y;
		width = w;
		wrapLines();
	}

	boolean restrictHeight = false;
	float x, y, width, height;
	boolean restrictFocus = false;
	boolean focus;
	float alpha = 0;

	public void restrictHeight(float h) {
		this.height = h;
	}

	public float getHeight() {
		int lineCount = lines.size() <= 0 ? 1 : lines.size();
		return restrictHeight ? height : lineCount * f.getHeight();
	}

	public void restrictFocus(boolean restrict) {
		if (restrict)
			focus = false;
		this.restrictFocus = restrict;
	}

	public void setFocus(boolean focus) {
		this.focus = focus;
	}

	public void logic() {
		if (restrictFocus) {
			alpha = AvianMath.glide(alpha, 0f, 10f);
		} else if (focus) {
			alpha = AvianMath.glide(alpha, 1f, 10f);
			caretCos += 90f / .5f / 100f;
			caretAlpha = 255 * Math.abs(AvianMath.cos(caretCos));
		} else {
			alpha = AvianMath.glide(alpha, .5f, 10f);
			caretCos = 90;
			caretAlpha = AvianMath.glide(caretAlpha, 0f, 10f);
		}
	}

	static AvianRectangle r = new AvianRectangle();
	float caretCos;
	float caretAlpha;
	float textR = 1, textG = 1, textB = 1;
	float boxR = 0, boxG = 0, boxB = 0;

	public void setTextColor(float... color) {
		if (color.length == 0) {
			textR = textG = textB;
		} else if (color.length == 1) {
			textR = textG = textB = 1;
		} else if (color.length == 2) {
			textR = textG = textB = color[0];
		} else {
			textR = color[0];
			textG = color[1];
			textB = color[2];
		}
	}

	public void setBoxColor(float... color) {
		if (color.length == 0) {
			boxR = boxG = boxB = 1;
		} else if (color.length == 1) {
			boxR = boxG = boxB = 1;
		} else if (color.length == 2) {
			boxR = boxG = boxB = color[0];
		} else {
			boxR = color[0];
			boxG = color[1];
			boxB = color[2];
		}
	}

	public void renderContentOnly(float a) {
		if (width < 0)
			return;
		wrapLines();
		if (sb.length() == 0 || lines.size() == 0)
			return;
		for (int i = 0; i < lines.size(); i++) {
			f.drawString(x, y + i * f.getHeight(), lines.get(i).replace("\n", ""), textR, textG, textB, a);
		}
	}

	public void render(float a) {
		if (width < 0)
			return;
		try {
			wrapLines();

			float boxAlpha = 35f * alpha * a / 255f;

			r.set(x - 5, y - 5, width + 10, getHeight() + 10);
			r.render(boxR, boxG, boxB, boxAlpha);

			AvianFont.setAlignment(AvianFont.ALIGN_LEFT, AvianFont.ALIGN_TOP);
			
			if (!restrictFocus) {
				int mx = (int) AvianInput.getMouseX();
				int my = (int) AvianInput.getMouseY();
				boolean hover = mx > x - 5 && mx < x + width + 5 && my > y - 5 && my < y + getHeight() + 5;
				if (hover) {
					ibeam = true;
				}
			}

			if (!restrictFocus && sb.length() == 0) {
				f.drawString(x, y, emptyMessage, a * .25f);
			} else {
				for (int i = 0; i < lines.size(); i++) {
					f.drawString(x, y + i * f.getHeight(), lines.get(i).replace("\n", ""), a);
				}
			}

			if (!restrictFocus && focus) {
				getCaretPos();
				int caretX = caretPos[0];
				int caretY = caretPos[1];

				if (lines.size() > 0) {
					r.set(x + f.getWidth(lines.get(caretY).substring(0, caretX)), y + caretY * f.getHeight(), 2, f.getAscent() + f.getDescent() / 2f);
				} else {
					r.set(x, y, 2, f.getAscent() + f.getDescent() / 2f);
				}
				r.render(caretAlpha / 255f);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	int[] caretPos = new int[2];

	private int[] getCaretPos() {
		int characters = 0, caretX = 0, caretY = 0;
		if (index == sb.toString().length() && sb.length() > 0) {
			if (lines.size() > 0) {
				caretY = lines.size() - 1;
				caretX = lines.get(lines.size() - 1).length();
			} else {
				caretY = 0;
				caretX = 0;
			}
		} else {
			for (int i = 0; i < lines.size(); i++) {
				characters += lines.get(i).length();
				if (characters > index) {
					caretY = i;
					characters -= lines.get(i).length();
					caretX = index - characters;
					break;
				}
			}
		}
		caretPos[0] = caretX;
		caretPos[1] = caretY;
		return caretPos;
	}

	public AvianFont getFont() {
		return f;
	}

	private void wrapLines() {
		lines.clear();
		if (width < 0 || sb.length() == 0) {
			lines.add("");
			return;
		}

		String text = sb.toString();
		int cutoff = 0;
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == '\n') {
				lines.add(text.substring(cutoff, i + 1));
				cutoff = i + 1;
				if (i == text.length() - 1)
					lines.add("");
			} else if (f.getWidth(text.substring(cutoff, i + 1)) > width) {
				String foo = text.substring(cutoff, i);
				if (foo.lastIndexOf(" ") == -1) {
					foo = text.substring(cutoff, i - 1);
				} else {
					foo = text.substring(cutoff, cutoff + foo.lastIndexOf(" ") + 1);
				}
				lines.add(foo);
				cutoff += foo.length();
				//TODO Subract one?
				i = cutoff;
			} else if (i == text.length() - 1) {
				lines.add(text.substring(cutoff));
			}
		}

	}
}
