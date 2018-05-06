
package musicaflight.avianutils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL15.*;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

public class AvianImage {

	int texture;

	private static FloatBuffer vertexData = BufferUtils.createFloatBuffer(12),
			textureData, colorData = BufferUtils.createFloatBuffer(16);

	private float x, y, width, height;
	private float cropWidth, cropHeight;
	private boolean cropped;

	private static int vboVertexHandle, vboTextureHandle, vboColorHandle;

	private BufferedImage original, image;

	private int radius = 0;

	private static final int bytesPerPixel = 4;

	private boolean glLoaded = false;

	private ByteBuffer buffer;

	//	private boolean initialized;

	private String filepath;

	//	private AvianColor dominantColor;

	private File file;

	public AvianImage(String filepath) {
		try {
			this.filepath = filepath;
			file = new File(filepath);
			BufferedInputStream stream;

			if (file.isAbsolute())
				original = ImageIO.read(stream = new BufferedInputStream(new FileInputStream(filepath)));
			else
				original = ImageIO.read(stream = new BufferedInputStream(AvianImage.class.getResourceAsStream(filepath)));

			stream.close();

			image = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = image.createGraphics();
			g2.drawImage(original, 0, 0, null);
			g2.dispose();
			width = original.getWidth();
			height = original.getHeight();
			loadBuffer(original);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public AvianImage(BufferedImage img) {
		original = img;

		image = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		g2.drawImage(original, 0, 0, null);
		g2.dispose();
		g2 = null;
		width = img.getWidth();
		height = img.getHeight();
		loadBuffer(img);
	}

	private void loadBuffer(BufferedImage img) {
		image = img;

		int[] pixels = new int[img.getWidth() * img.getHeight()];

		img.getRGB(0, 0, img.getWidth(), img.getHeight(), pixels, 0, img.getWidth());

		buffer = BufferUtils.createByteBuffer(img.getWidth() * img.getHeight() * bytesPerPixel); // 4 for RGBA, 3 for RGB

		for (int yy = 0; yy < img.getHeight(); yy++) {
			for (int xx = 0; xx < img.getWidth(); xx++) {
				int pixel = pixels[(yy * img.getWidth()) + xx];
				buffer.put((byte) ((pixel >> 16) & 0xFF));
				buffer.put((byte) ((pixel >> 8) & 0xFF));
				buffer.put((byte) (pixel & 0xFF));
				buffer.put((byte) ((pixel >> 24) & 0xFF));
			}
		}

		buffer.flip();
		pixels = null;
		img = null;
	}

	public float radius() {
		return radius;
	}

	public void loadGL() {
		if (destroyed)
			return;
		radius = 0;
		glLoaded = false;
		loadTex(original);
	}

	private void loadTex(BufferedImage img) {
		if (glLoaded || destroyed)
			return;

		if (vboColorHandle == 0)
			vboColorHandle = glGenBuffers();
		if (vboVertexHandle == 0)
			vboVertexHandle = glGenBuffers();
		if (vboTextureHandle == 0)
			vboTextureHandle = glGenBuffers();

		texture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texture);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, img.getWidth(), img.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		glBindTexture(GL_TEXTURE_2D, 0);

		glLoaded = true;
		img = null;
		buffer.clear();
		buffer = null;
	}

	public void setSharpTexture(boolean sharp) {
		if (destroyed)
			return;
		if (!glLoaded) {
			loadGL();
		}
		glBindTexture(GL_TEXTURE_2D, texture);

		if (sharp) {
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		} else {
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		}
		glBindTexture(GL_TEXTURE_2D, 0);

	}

	/** Special thanks to Mario Klingemann at quasimondo.com */
	public void gaussianBlur(int rad) {

		if (destroyed)
			return;

		radius = rad;

		if (rad < 1) {
			loadBuffer(original);
			radius = 0;
			return;
		}

		int w = original.getWidth();
		int h = original.getHeight();
		int imgArea = w * h;
		int[] pix = new int[imgArea];

		BufferedImage imgnew = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = imgnew.createGraphics();
		g2.drawImage(original, 0, 0, null);
		g2.dispose();
		g2 = null;

		imgnew.getRGB(0, 0, w, h, pix, 0, w);

		int widthMinusOne = w - 1;
		int heightMinusOne = h - 1;
		int div = rad + rad + 1;

		int[] r = new int[imgArea];
		int[] g = new int[imgArea];
		int[] b = new int[imgArea];
		int[] a = new int[imgArea];
		int rsum, gsum, bsum, asum, xx, yy, i, p, yp, yi, yw;
		int vmin[] = new int[Math.max(w, h)];

		int divsum = (div + 1) >> 1;
		divsum *= divsum;
		int[] dv = new int[256 * divsum];
		for (i = 0; i < (256 * divsum); i++) {
			dv[i] = (i / divsum);
		}

		yw = yi = 0;

		int[][] stack = new int[div][4];
		int stackpointer;
		int stackstart;
		int[] sir;
		int rbs;
		int r1 = rad + 1;
		int routsum, goutsum, boutsum, aoutsum;
		int rinsum, ginsum, binsum, ainsum;

		for (yy = 0; yy < h; yy++) {
			rinsum = ginsum = binsum = ainsum = routsum = goutsum = boutsum = aoutsum = rsum = gsum = bsum = asum = 0;
			for (i = -rad; i <= rad; i++) {
				p = pix[yi + Math.min(widthMinusOne, Math.max(i, 0))];
				sir = stack[i + rad];
				sir[0] = (p >> 16) & 0xFF;
				sir[1] = (p >> 8) & 0xFF;
				sir[2] = p & 0xFF;
				sir[3] = (p >> 24) & 0xFF;
				rbs = r1 - Math.abs(i);
				rsum += sir[0] * rbs;
				gsum += sir[1] * rbs;
				bsum += sir[2] * rbs;
				asum += sir[3] * rbs;
				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
					ainsum += sir[3];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
					aoutsum += sir[3];
				}
			}
			stackpointer = rad;

			for (xx = 0; xx < w; xx++) {

				r[yi] = dv[rsum];
				g[yi] = dv[gsum];
				b[yi] = dv[bsum];
				a[yi] = dv[asum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;
				asum -= aoutsum;

				stackstart = (stackpointer - rad) + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];
				aoutsum -= sir[3];

				if (yy == 0) {
					vmin[xx] = Math.min(xx + rad + 1, widthMinusOne);
				}
				p = pix[yw + vmin[xx]];

				sir[0] = (p >> 16) & 0xFF;
				sir[1] = (p >> 8) & 0xFF;
				sir[2] = p & 0xFF;
				sir[3] = (p >> 24) & 0xFF;

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];
				ainsum += sir[3];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;
				asum += ainsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[(stackpointer) % div];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];
				aoutsum += sir[3];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];
				ainsum -= sir[3];

				yi++;
			}
			yw += w;
		}
		for (xx = 0; xx < w; xx++) {
			rinsum = ginsum = binsum = ainsum = routsum = goutsum = boutsum = aoutsum = rsum = gsum = bsum = asum = 0;
			yp = -rad * w;
			for (i = -rad; i <= rad; i++) {
				yi = Math.max(0, yp) + xx;

				sir = stack[i + rad];

				sir[0] = r[yi];
				sir[1] = g[yi];
				sir[2] = b[yi];
				sir[3] = a[yi];

				rbs = r1 - Math.abs(i);

				rsum += r[yi] * rbs;
				gsum += g[yi] * rbs;
				bsum += b[yi] * rbs;
				asum += a[yi] * rbs;

				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
					ainsum += sir[3];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
					aoutsum += sir[3];
				}

				if (i < heightMinusOne) {
					yp += w;
				}
			}
			yi = xx;
			stackpointer = rad;
			for (yy = 0; yy < h; yy++) {
				pix[yi] = (dv[asum] << 24) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;
				asum -= aoutsum;

				stackstart = (stackpointer - rad) + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];
				aoutsum -= sir[3];

				if (xx == 0) {
					vmin[yy] = Math.min(yy + r1, heightMinusOne) * w;
				}
				p = xx + vmin[yy];

				sir[0] = r[p];
				sir[1] = g[p];
				sir[2] = b[p];
				sir[3] = a[p];

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];
				ainsum += sir[3];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;
				asum += ainsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[stackpointer];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];
				aoutsum += sir[3];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];
				ainsum -= sir[3];

				yi += w;
			}
		}

		imgnew.setRGB(0, 0, w, h, pix, 0, w);

		loadBuffer(imgnew);

		imgnew = null;
		r = null;
		g = null;
		b = null;
		a = null;
		vmin = null;
		dv = null;
		stack = null;
		sir = null;
		pix = null;
	}

	/*public void boxBlur(int rad) {
	
		if (destroyed)
			return;
	
		this.radius = rad;
	
		if (rad < 1) {
			loadBuffer(original);
			this.radius = 0;
			return;
		}
	
		int w = original.getWidth();
		int h = original.getHeight();
		int imgArea = w * h;
		int[] pix = new int[imgArea];
	
		BufferedImage imgnew = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = imgnew.createGraphics();
		g2.drawImage(original, 0, 0, null);
		g2.dispose();
		g2 = null;
	
		imgnew.getRGB(0, 0, w, h, pix, 0, w);
	
		int[] r = new int[imgArea];
		int[] g = new int[imgArea];
		int[] b = new int[imgArea];
		int[] a = new int[imgArea];
	
		for (int i = 0; i < pix.length; i++) {
			int p = pix[i];
			r[i] = ((p >> 24) & 0xFF);
			g[i] = ((p >> 16) & 0xFF);
			b[i] = ((p >> 8) & 0xFF);
			a[i] = p & 0xFF;
		}
	
		imgnew.setRGB(0, 0, w, h, pix, 0, w);
	
		loadBuffer(imgnew);
	
		imgnew = null;
		r = null;
		g = null;
		b = null;
		a = null;
		pix = null;
	}*/

	public float getX() {
		if (destroyed)
			return 0;
		return x;
	}

	public float getCenterX() {
		if (destroyed)
			return 0;
		return x + (original.getWidth() / 2f);
	}

	public float getY() {
		if (destroyed)
			return 0;
		return y;
	}

	public float getCenterY() {
		if (destroyed)
			return 0;
		return y + (original.getHeight() / 2f);
	}

	/** @return The raw image's width. */

	public float getWidth() {
		if (destroyed)
			return 0;
		return original.getWidth();
	}

	/** @return The raw image's height. */

	public float getHeight() {
		if (destroyed)
			return 0;
		return original.getHeight();
	}

	public float getCroppedWidth() {
		if (!cropped)
			return getWidth();
		return cropWidth;
	}

	/** @return The raw image's height. */

	public float getCroppedHeight() {
		if (!cropped)
			return getHeight();
		return cropHeight;
	}

	public float getImageAspectRatio() {
		if (destroyed)
			return 0;
		return (float) original.getWidth() / (float) original.getHeight();
	}

	public void render(float xx, float yy) {
		if (destroyed)
			return;

		x = xx;
		y = yy;
		width = image.getWidth();
		height = image.getHeight();
		if (cropped) {
			width *= cropWidth / getWidth();
			height *= cropHeight / getHeight();
		}
		vertexData();
		colorDataAllWhite();

		render();
	}

	public void render(float xx, float yy, float alpha) {
		if (destroyed)
			return;

		x = xx;
		y = yy;
		width = image.getWidth();
		height = image.getHeight();
		if (cropped) {
			width *= cropWidth / getWidth();
			height *= cropHeight / getHeight();
		}
		vertexData();
		colorDataWithAlpha(alpha);

		render();
	}

	public void render(float xx, float yy, float[] c) {
		if (destroyed)
			return;

		x = xx;
		y = yy;
		width = getWidth();
		height = getHeight();
		if (cropped) {
			width *= cropWidth / getWidth();
			height *= cropHeight / getHeight();
		}
		vertexData();
		colorDataWithCustomColor(c);

		render();
	}

	public void render(float xx, float yy, float w, float h) {
		if (destroyed)
			return;

		x = xx;
		y = yy;
		width = w;
		height = h;
		vertexData();
		colorDataAllWhite();

		render();
	}

	public void render(float xx, float yy, float w, float h, float alpha) {
		if (destroyed)
			return;

		x = xx;
		y = yy;
		width = w;
		height = h;
		vertexData();
		colorDataWithAlpha(alpha);

		render();
	}

	public void render(float xx, float yy, float w, float h, float[] c) {
		if (destroyed)
			return;

		x = xx;
		y = yy;
		width = w;
		height = h;
		vertexData();
		colorDataWithCustomColor(c);

		render();
	}

	private void render() {
		if (destroyed)
			return;

		if (!glLoaded) {
			// System.err.println("GL methods have not been called for this AvianImage. Loading methods now.");
			// StackTraceElement[] stes = new Throwable().getStackTrace();
			// for (StackTraceElement s : stes) {
			// System.err.println("\tat " + s);
			// }
			loadGL();
		}
		glEnable(GL_TEXTURE_2D);

		glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
		glBufferData(GL_ARRAY_BUFFER, vertexData, GL_STATIC_DRAW);
		glVertexPointer(3, GL_FLOAT, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		glBindBuffer(GL_ARRAY_BUFFER, vboColorHandle);
		glBufferData(GL_ARRAY_BUFFER, colorData, GL_STATIC_DRAW);
		glColorPointer(4, GL_FLOAT, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		textureData = BufferUtils.createFloatBuffer(8);
		textureData.put(textureFloats);
		textureData.flip();

		glBindBuffer(GL_ARRAY_BUFFER, vboTextureHandle);
		glBufferData(GL_ARRAY_BUFFER, textureData, GL_STATIC_DRAW);
		glTexCoordPointer(2, GL_FLOAT, 0, 0);

		glBindBuffer(GL_ARRAY_BUFFER, 0);

		if (glGetBoolean(GL_VERTEX_ARRAY)==GL_FALSE)
			glEnableClientState(GL_VERTEX_ARRAY);
		if (glGetBoolean(GL_COLOR_ARRAY)==GL_FALSE)
			glEnableClientState(GL_COLOR_ARRAY);
		if (glGetBoolean(GL_TEXTURE_COORD_ARRAY)==GL_FALSE)
			glEnableClientState(GL_TEXTURE_COORD_ARRAY);

		glBindTexture(GL_TEXTURE_2D, texture);
		glDrawArrays(GL_QUADS, 0, 4);

		glDisable(GL_TEXTURE_2D);
	}

	float[] textureFloats = new float[] { 0, 0, 1, 0, 1, 1, 0, 1 };

	public int getTextureID() {
		if (!glLoaded) {
			loadGL();
		}
		if (destroyed)
			return 0;
		return texture;
	}

	public void uncrop() {
		if (destroyed)
			return;
		textureFloats[0] = 0;
		textureFloats[1] = 0;
		textureFloats[2] = 1;
		textureFloats[3] = 0;
		textureFloats[4] = 1;
		textureFloats[5] = 1;
		textureFloats[6] = 0;
		textureFloats[7] = 1;
		cropped = false;
	}

	public void crop(float xx, float yy, float w, float h) {
		if (destroyed)
			return;

		cropWidth = w;
		cropHeight = h;
		cropped = true;

		textureFloats[0] = xx / getWidth();
		textureFloats[1] = yy / getHeight();
		textureFloats[2] = (xx + w) / getWidth();
		textureFloats[3] = yy / getHeight();
		textureFloats[4] = (xx + w) / getWidth();
		textureFloats[5] = (yy + h) / getHeight();
		textureFloats[6] = xx / getWidth();
		textureFloats[7] = (yy + h) / getHeight();

	}

	private boolean destroyed = false;

	public AvianImage destroy() {
		if (!destroyed) {
			glDeleteBuffers(vboColorHandle);
			glDeleteBuffers(vboVertexHandle);
			glDeleteBuffers(vboTextureHandle);
			glDeleteTextures(texture);

			glLoaded = false;
			if (buffer != null) {
				buffer.clear();
				buffer = null;
			}
			//			if (vertexData != null) {
			//				vertexData.clear();
			//				vertexData = null;
			//			}
			//			if (colorData != null) {
			//				colorData.clear();
			//				colorData = null;
			//			}
			//			if (textureData != null) {
			//				textureData.clear();
			//				textureData = null;
			//			}
			textureFloats = null;
			destroyed = true;
		}

		return null;
	}

	private void colorDataAllWhite() {
		colorData.clear();
		for (int i = 0; i < 16; i++)
			colorData.put(1f);
		colorData.flip();
	}

	private void colorDataWithAlpha(float alpha) {
		colorData.clear();
		for (int i = 0; i < 4; i++) {
			colorData.put(1f);
			colorData.put(1f);
			colorData.put(1f);
			colorData.put(alpha);
		}
		colorData.flip();
	}

	private void colorDataWithCustomColor(float... color) {
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

		colorData.clear();
		for (int i = 0; i < 4; i++) {
			colorData.put(r).put(g).put(b).put(a);
		}
		colorData.flip();
	}

	private void vertexData() {
		vertexData.clear();
		vertexData.put(x).put(y).put(0);
		vertexData.put(x + width).put(y).put(0);
		vertexData.put(x + width).put(y + height).put(0);
		vertexData.put(x).put(y + height).put(0);
		vertexData.flip();
	}

	public File getFile() {
		return file;
	}

	public String getImageFilepath() {
		return filepath;
	}

	public BufferedImage getBufferedImage() {
		if (radius > 0)
			return image.getSubimage(0, 0, image.getWidth(), image.getHeight());
		return original;
	}

	//	/** @return The dominant color of this AvianImage. */
	//
	//	public AvianColor getColor() {
	//		if (dominantColor == null)
	//			dominantColor = getColor(getBufferedImage(), 3);
	//		return dominantColor;
	//	}
	//
	//	private AvianColor getColor(BufferedImage img, int quality) {
	//		try {
	//			int h = img.getHeight();
	//			int w = img.getWidth();
	//
	//			Map<Integer, Integer> m = new HashMap<Integer, Integer>();
	//
	//			for (int i = 0; i < w; i += quality) {
	//				for (int j = 0; j < h; j += quality) {
	//					int rgb = img.getRGB(i, j);
	//
	//					int[] rgbArr = getPixelRGB(rgb);
	//					if (!gray(rgbArr)) {
	//						Integer counter = m.get(rgb);
	//						if (counter == null)
	//							counter = 0;
	//						counter++;
	//						m.put(rgb, counter);
	//					}
	//				}
	//				if (((i + quality) >= w) && (m.size() <= 0)) {
	//					return new AvianColor(255 / 2, 255 / 2, 255 / 2);
	//				}
	//			}
	//			AvianColor dominant = getDominantColor(m);
	//			float avg = (dominant.getR() + dominant.getG() + dominant.getB()) / 3f;
	//			if ((avg < 100) && (avg != 0)) {
	//				float multiplier = 100f / avg;
	//				dominant.setR(dominant.getR() * multiplier);
	//				dominant.setG(dominant.getG() * multiplier);
	//				dominant.setB(dominant.getB() * multiplier);
	//			}
	//			return dominant;
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//		}
	//		return null;
	//	}
	//
	//	@SuppressWarnings("unchecked")
	//	private AvianColor getDominantColor(Map<Integer, Integer> map) {
	//		List<Object> list = new LinkedList<Object>(map.entrySet());
	//		Collections.sort(list, new Comparator<Object>() {
	//			@Override
	//			public int compare(Object o1, Object o2) {
	//
	//				Entry<Integer, Integer> e1 = (Entry<Integer, Integer>) o1;
	//				Entry<Integer, Integer> e2 = (Entry<Integer, Integer>) o2;
	//
	//				return ((Comparable<Integer>) (e1).getValue()).compareTo(e2.getValue());
	//			}
	//		});
	//		int[] dominant = getPixelRGB(((Entry<Integer, Integer>) (list.get(list.size() - 1))).getKey());
	//		return new AvianColor(dominant[0], dominant[1], dominant[2]);
	//	}

	//	private int[] getPixelRGB(int pixel) {
	//		int red = (pixel >> 16) & 0xff;
	//		int green = (pixel >> 8) & 0xff;
	//		int blue = (pixel) & 0xff;
	//		return new int[] { red, green, blue };
	//	}

	//	private boolean gray(int[] rgbArr) {
	//		int rgDiff = rgbArr[0] - rgbArr[1];
	//		int rbDiff = rgbArr[0] - rgbArr[2];
	//		int tolerance = 10;
	//		if ((rgDiff > tolerance) || (rgDiff < -tolerance)) {
	//			if ((rbDiff > tolerance) || (rbDiff < -tolerance)) {
	//				return false;
	//			}
	//		}
	//		return true;
	//	}

	public static void enableClientStates() {
		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_TEXTURE_COORD_ARRAY);
		glEnableClientState(GL_COLOR_ARRAY);
	}

	public static void disableClientStates() {
		glDisableClientState(GL_VERTEX_ARRAY);
		glDisableClientState(GL_TEXTURE_COORD_ARRAY);
		glDisableClientState(GL_COLOR_ARRAY);
	}

}