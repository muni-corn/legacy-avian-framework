
package musicaflight.avianutils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.*;

import org.lwjgl.BufferUtils;

public class AvianFont {

	public static final int ALIGN_DEFAULT = 0;
	public static final int ALIGN_LEFT = 1;
	public static final int ALIGN_RIGHT = 2;
	public static final int ALIGN_TOP = 2;
	public static final int ALIGN_BOTTOM = 1;
	public static final int ALIGN_CENTER = 3;
	public static final int BOLD = Font.BOLD;
	public static final int ITALIC = Font.ITALIC;

	private Font font;
	private static BufferedImage atlas;

	static String uprightAlphabet = "abcdefhiklmnorstuvwxz";

	private ByteBuffer buffer;
	private boolean glLoaded;
	private static FloatBuffer colorData, vertexData, textureData;
	private int atlasWidth, atlasHeight;

	public AvianFont(String filepath, float size) {
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, new BufferedInputStream(AvianFont.class.getResourceAsStream(filepath)));
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}

		font = font.deriveFont(size);

		loadFont();
	}

	public AvianFont(String filepath, float size, int style) {
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, new BufferedInputStream(AvianFont.class.getResourceAsStream(filepath)));
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}

		font = font.deriveFont(style, size);

		loadFont();
	}

	public AvianFont(Font font, float size) {
		this.font = font.deriveFont(size);
		loadFont();
	}

	public AvianFont(Font font, float size, int style) {
		this.font = font.deriveFont(style, size);
		loadFont();
	}

	public Font getFont() {
		return font;
	}

	private Map<Character, Glyph> glyphs = new HashMap<Character, Glyph>();

	private void loadFont() {
		BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setFont(font);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

		fm = g.getFontMetrics();
		height = fm.getHeight();
		g.dispose();

		int imageWidth = 0;
		int imageHeight = 0;
		int max = glGetInteger(GL_MAX_TEXTURE_SIZE);
		int row = 0;
		int atlasX = 0;
		for (int i = 32; i < 256; i++) {
			if (i == 127) {
				continue;
			}
			char c = (char) i;
			BufferedImage ch = createCharImage(c);

			if (atlasX + ch.getWidth() < max) {
				atlasX += ch.getWidth();
			} else {
				row++;
				atlasX = 0;
			}
			imageWidth = Math.max(atlasX, imageWidth);

		}

		imageHeight = fm.getHeight() * (row + 1);

		atlas = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
		g = atlas.createGraphics();

		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		atlasX = row = 0;

		for (int i = 32; i < 256; i++) {
			if (i == 127) {
				continue;
			}
			char c = (char) i;
			BufferedImage chImg = createCharImage(c);

			float charWidth = chImg.getWidth();
			float charHeight = chImg.getHeight();

			if (atlasX + charWidth >= max) {
				atlasX = 0;
				row++;
			}
			atlasX += charWidth;
			Glyph ch = new Glyph(charWidth, charHeight, atlasX - charWidth, fm.getHeight() * (row + 1) - charHeight);

			g.drawImage(chImg, (int) (atlasX - charWidth), (int) ch.y, null);
			glyphs.put(c, ch);
			chImg = null;
		}

		g.dispose();

		int[] pixels = new int[atlas.getWidth() * atlas.getHeight()];

		atlas.getRGB(0, 0, atlas.getWidth(), atlas.getHeight(), pixels, 0, atlas.getWidth());

		buffer = BufferUtils.createByteBuffer(atlas.getWidth() * atlas.getHeight() * 4); // 4 for RGBA, 3 for RGB

		for (int y = 0; y < atlas.getHeight(); y++) {
			for (int x = 0; x < atlas.getWidth(); x++) {
				int pixel = pixels[(y * atlas.getWidth()) + x];
				buffer.put((byte) ((pixel >> 16) & 0xFF));
				buffer.put((byte) ((pixel >> 8) & 0xFF));
				buffer.put((byte) (pixel & 0xFF));
				buffer.put((byte) ((pixel >> 24) & 0xFF));
			}
		}

		buffer.flip();

		pixels = null;
		g = null;
		atlasWidth = atlas.getWidth();
		atlasHeight = atlas.getHeight();
		atlas = null;
		glLoaded = false;
	}

	public float getAscent() {
		return fm.getAscent();
	}

	public float getDescent() {
		return fm.getDescent();
	}

	FontMetrics fm;

	private BufferedImage createCharImage(char c) {
		int charWidth = fm.charWidth(c);
		int charHeight = fm.getHeight();

		BufferedImage image = new BufferedImage(charWidth <= 0 ? 1 : charWidth, charHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setFont(font);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g.setColor(Color.WHITE);
		g.drawString(String.valueOf(c), (float) 0, (float) fm.getAscent());
		g.dispose();
		return image;
	}

	private int texture;
	private static int vboColorHandle, vboTextureHandle, vboVertexHandle;

	public void loadGL() {
		if (glLoaded)
			return;

		if (vboColorHandle == 0)
			vboColorHandle = glGenBuffers();
		if (vboVertexHandle == 0)
			vboVertexHandle = glGenBuffers();
		if (vboTextureHandle == 0)
			vboTextureHandle = glGenBuffers();

		texture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texture);

		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, atlasWidth, atlasHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

		glGenerateMipmap(GL_TEXTURE_2D);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

		glBindTexture(GL_TEXTURE_2D, 0);

		glLoaded = true;
		buffer.clear();
		buffer = null;
	}

	/** @return a uniform height based on the uppercase alphabet of the AvianFont. */

	private float height;

	public float getHeight() {
		return height;
	}

	public float getWidth(String string) {

		AvianUtils.startNanoWatch();
		if (string == null)
			return 0;
		float width = 0;

		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			Glyph g = glyphs.get(c);
			if (g == null)
				continue;
			width += g.width;
		}

		return width;
	}

	private static int horizontalAlign = ALIGN_LEFT,
			verticalAlign = ALIGN_CENTER;

	public static void setHorizontalAlignment(int align) {
		horizontalAlign = align;
	}

	public static void setVerticalAlignment(int align) {
		verticalAlign = align;
	}

	public static void setAlignment(int hAlign, int vAlign) {
		horizontalAlign = hAlign;
		verticalAlign = vAlign;
	}

	public void drawString(float y, String string, float... color) {
		horizontalAlign = ALIGN_CENTER;
		drawString(AvianApp.getWidth() / 2f, y, string, color);
	}

	private ArrayList<String> lines = new ArrayList<String>();

	public String wrap(String text, int width) {
		if (width > 0) {
			lines.clear();
			int cutoff = 0;
			for (int i = 0; i < text.length(); i++) {
				if (text.charAt(i) == '\n') {
					lines.add(text.substring(cutoff, i + 1));
					cutoff = i + 1;
					if (i == text.length() - 1)
						lines.add("");
				} else if (getWidth(text.substring(cutoff, i + 1)) > width) {
					String foo = text.substring(cutoff, i);
					if (foo.lastIndexOf(" ") == -1) {
						foo = text.substring(cutoff, i - 1);
					} else {
						foo = text.substring(cutoff, cutoff + foo.lastIndexOf(" ") + 1);
					}
					lines.add(foo);
					cutoff += foo.length();
					i = cutoff - 1;
				} else if (i == text.length() - 1) {
					lines.add(text.substring(cutoff));
				}
			}
		}
		String s = "";
		for (int i = 0; i < lines.size(); i++) {
			s = s + lines.get(i).trim() + (i == lines.size() - 1 ? "" : "\n");
		}
		return s;
	}

	@Deprecated
	public void drawString(String s, float x, float y, AvianColor c, int h, int v) {
		setAlignment(h, v);
		drawString(x, y, s, c.getR() / 255f, c.getG() / 255f, c.getB() / 255f, c.getA() / 255f);
	}

	public void drawString(float x, float y, String string, float... color) {
		if (string == null)
			return;

		switch (verticalAlign) {
			case ALIGN_BOTTOM:
				y -= getAscent();
				break;
			case ALIGN_CENTER:
				y -= getAscent() / 2f;
				break;
		}
		switch (horizontalAlign) {
			case ALIGN_RIGHT:
				x -= getWidth(string);
				break;
			case ALIGN_CENTER:
				x -= getWidth(string) / 2f;
				break;
		}

		if (!glLoaded)
			loadGL();

		String actualCharacters = string.replaceAll("\r\n|\r|\n", "");

		float r, g, b, a;
		if (color.length == 0) {
			r = g = b = a = 1;
		} else if (color.length == 1) {
			r = g = b = 1;
			a = color[0];
		} else if (color.length == 2) {
			r = g = b = color[0];
			a = color[1];
		} else if (color.length == 3) {
			r = color[0];
			g = color[1];
			b = color[2];
			a = 1;
		} else {
			r = color[0];
			g = color[1];
			b = color[2];
			a = color[3];
		}

		colorData = BufferUtils.createFloatBuffer(actualCharacters.length() * 4 * 4);
		for (int i = 0; i < actualCharacters.length() * 4; i++) {
			colorData.put(r).put(g).put(b).put(a);
		}
		colorData.flip();

		float drawX = x;
		float drawY = y;

		glEnable(GL_TEXTURE_2D);

		glBindBuffer(GL_ARRAY_BUFFER, vboColorHandle);
		glBufferData(GL_ARRAY_BUFFER, colorData, GL_STATIC_DRAW);
		glColorPointer(4, GL_FLOAT, 0, 0);

		glBindBuffer(GL_ARRAY_BUFFER, 0);

		vertexData = BufferUtils.createFloatBuffer(actualCharacters.length() * 4 * 2);
		textureData = BufferUtils.createFloatBuffer(actualCharacters.length() * 4 * 2);

		char[] characters = string.toCharArray();

		for (int i = 0; i < string.length(); i++) {
			String ch = String.valueOf(characters[i]);

			if (ch.equals("\r") || ch.equals("\n")) {
				drawY += getHeight();
				drawX = x;
				continue;
			}
			Glyph gly = glyphs.get(ch.charAt(0));

			float x1 = (int) drawX;
			float y1 = (int) drawY;
			float x2 = (int) (drawX + gly.width);
			float y2 = (int) (drawY + gly.height);

			vertexData.put(x1).put(y1);
			vertexData.put(x1).put(y2);
			vertexData.put(x2).put(y2);
			vertexData.put(x2).put(y1);

			/* Calculate Texture coordinates */
			float s1 = gly.x / atlasWidth;
			float t1 = gly.y / atlasHeight;
			float s2 = (gly.x + gly.width) / atlasWidth;
			float t2 = (gly.y + gly.height) / atlasHeight;

			textureData.put(s1).put(t1);
			textureData.put(s1).put(t2);
			textureData.put(s2).put(t2);
			textureData.put(s2).put(t1);

			drawX += gly.width;
		}
		vertexData.flip();

		glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
		glBufferData(GL_ARRAY_BUFFER, vertexData, GL_STATIC_DRAW);
		glVertexPointer(2, GL_FLOAT, 0, 0);

		glBindBuffer(GL_ARRAY_BUFFER, 0);

		textureData.flip();

		glBindBuffer(GL_ARRAY_BUFFER, vboTextureHandle);
		glBufferData(GL_ARRAY_BUFFER, textureData, GL_STATIC_DRAW);
		glTexCoordPointer(2, GL_FLOAT, 0, 0);

		glBindBuffer(GL_ARRAY_BUFFER, 0);

		if (glGetBoolean(GL_VERTEX_ARRAY) == GL_FALSE)
			glEnableClientState(GL_VERTEX_ARRAY);
		if (glGetBoolean(GL_COLOR_ARRAY) == GL_FALSE)
			glEnableClientState(GL_COLOR_ARRAY);
		if (glGetBoolean(GL_TEXTURE_COORD_ARRAY) == GL_FALSE)
			glEnableClientState(GL_TEXTURE_COORD_ARRAY);

		glBindTexture(GL_TEXTURE_2D, texture);
		glDrawArrays(GL_QUADS, 0, 4 * actualCharacters.length());
		glBindTexture(GL_TEXTURE_2D, 0);

		glDisable(GL_TEXTURE_2D);
		glDeleteBuffers(vboTextureHandle);
		glDeleteBuffers(vboColorHandle);
		glDeleteBuffers(vboVertexHandle);
		vertexData.clear();
		colorData.clear();
		textureData.clear();

	}

	private class Glyph {
		final float width;
		final float height;
		final float x;
		final float y;

		public Glyph(float charWidth, float charHeight, float x, float y) {
			this.width = charWidth;
			this.height = charHeight;
			this.x = x;
			this.y = y;
		}
	}

	@Deprecated
	public void drawString(String s, float x, float y, AvianColor c, int h) {
		AvianFont.setAlignment(h, ALIGN_BOTTOM);
		drawString(x, y, s, c.getR() / 255f, c.getG() / 255f, c.getB() / 255f, c.getA() / 255f);

	}

	@Deprecated
	public void drawString(String s, float y, AvianColor c, int v) {
		AvianFont.setAlignment(ALIGN_BOTTOM, v);
		drawString(y, s, c.getR() / 255f, c.getG() / 255f, c.getB() / 255f, c.getA() / 255f);

	}
}