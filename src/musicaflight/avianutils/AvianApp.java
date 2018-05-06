
package musicaflight.avianutils;

import static musicaflight.avianutils.AvianInput.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.openal.*;

import musicaflight.avianutils.AvianEase.Direction;
import musicaflight.avianutils.AvianEase.Ease;

public abstract class AvianApp {

	static ArrayList<NotiBar> deck = new ArrayList<NotiBar>();
	//	private static ArrayList<AvoWindow> windowStack = new ArrayList<AvoWindow>();

	private static AvianLoadIcon loadIcon;

	private static boolean ignoreUnprocessed = true;
	private static boolean loading = true;
	private static AvianCamera cam;

	private static int FPS, UPS;

	private static float flashAlpha = 0;

	@SuppressWarnings("unused")
	private static AvianImage screenBlur;
	static AvianImage shadow, shadowLeft, shadowSide, shadowBottom, shadowTip;

	static AvianResourceLoader rl = new AvianResourceLoader();

	private static AvianImage splash;
	static AvianFont Vegur;

	static AvianFont Vegur_Bold;

	private static AvianFont Normal;

	static Thread resourceThread = new Thread(rl);

	private static enum DebuzzleType {
		APP,
		AVO,
		//		BOTH,
		HIDDEN;
	}

	private static DebuzzleType debuzzleType = DebuzzleType.HIDDEN;
	private static ArrayList<DebuzzleStat> leftStats = new ArrayList<DebuzzleStat>();
	private static ArrayList<DebuzzleStat> rightStats = new ArrayList<DebuzzleStat>();
	private static float debuzzleY = -100;
	private static float debuzzleH;
	private static float appDebH = 0;
	private static boolean crosshair = false;

	private static String APP_NAME = "App";
	private static String VERSION = "0.0.0";

	private static boolean showSplashScreen;
	private boolean startFullscreen = false;
	/** The width of the display. */
	static int WIDTH = 1000;
	/** The height of the display. */
	static int HEIGHT = 600;
	/** The x position of the display. */
	static int X;
	/** The y position of the display. */
	static int Y;

	private String splashFilepath;

	private static boolean resizable = true;
	private static boolean undecorated;

	private float keyHold;
	private long keyPrev;
	private long keyNow;

	private static long start;
	private static long uptime;

	static float mx, my;
	static boolean iconified = false;

	static GLFWWindowIconifyCallback windowIconifyCallback;
	static GLFWFramebufferSizeCallback framebufferSizeCallback;
	static GLFWWindowPosCallback windowPosCallback;

	int framebufferID;
	int colorTextureID;
	int depthRenderBufferID;

	public void start() {

		if (started) {
			throw new IllegalStateException("This app has already been started");
		}

//		GLFWErrorCallback.createPrint(System.err).set();

		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		//		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, resizable ? GL_TRUE : GL_FALSE);
		glfwWindowHint(GLFW_DECORATED, !undecorated ? GL_TRUE : GL_FALSE);
		glfwWindowHint(GLFW_SAMPLES, 8);

		// Create the window
		window = glfwCreateWindow(WIDTH, HEIGHT, "", NULL, NULL);
		if (window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		// Center our window
		glfwSetWindowPos(window, (vidmode.width() - WIDTH) / 2, (vidmode.height() - HEIGHT) / 2);

		AvianInput.create();
		loadCallbacks();

		glfwMakeContextCurrent(window);
		org.lwjgl.opengl.GL.createCapabilities();
		glClearColor(0, 0, 0, 0);

		// Vsync
		glfwSwapInterval(1);

		ByteBuffer deviceSpecifier = ByteBuffer.allocate(1);
		ALC10.alcMakeContextCurrent(ALC10.alcCreateContext(alcDeviceHandle = ALC10.alcOpenDevice(deviceSpecifier), (int[]) null));
		AL.createCapabilities(ALC.createCapabilities(alcDeviceHandle));
		
		rl.loadAll();

		cam = new AvianCamera((float) WIDTH / (float) HEIGHT);

		r = new AvianRectangle();
		l = new AvianLine();
		s = new AvianShadow();

		if (showSplashScreen) {
			splash = new AvianImage(splashFilepath);
		}
		shadow = new AvianImage("/res/shadowBottom.png");
		shadowSide = new AvianImage("/res/shadowSide.png");
		shadowLeft = new AvianImage("/res/shadowLCorner.png");
		shadowBottom = new AvianImage("/res/shadowSideBCorner.png");
		shadowTip = new AvianImage("/res/shadowBLTip.png");

		Vegur = new AvianFont("/res/VEGUR-LIGHT.OTF", 20);
		Vegur_Bold = new AvianFont("/res/VEGUR-BOLD.OTF", 20);
		Font f = new Font(Font.MONOSPACED, Font.PLAIN, 13);
		Normal = new AvianFont(f, f.getSize());

		loadIcon = new AvianLoadIcon();

		GL();

		// check if GL_EXT_framebuffer_object can be use on this system
		//		if (!context.getCapabilities().GL_EXT_framebuffer_object) {
		//			System.out.println("FBO not supported!!!");
		//			System.exit(0);
		//		} else {

		System.out.println("FBO is supported!!!");

		// init our fbo

		framebufferID = glGenFramebuffersEXT(); // create a new framebuffer
		colorTextureID = glGenTextures(); // and a new texture used as a color buffer
		depthRenderBufferID = glGenRenderbuffersEXT(); // And finally a new depthbuffer

		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, framebufferID); // switch to the new framebuffer

		// initialize color texture
		glBindTexture(GL_TEXTURE_2D, colorTextureID); // Bind the colorbuffer texture
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR); // make it linear filtered
		//TODO 
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, WIDTH, HEIGHT, 0, GL_RGBA, GL_INT, (java.nio.ByteBuffer) null); // Create the texture data
		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, colorTextureID, 0); // attach it to the framebuffer

		// initialize depth renderbuffer
		//			glBindRenderbufferEXT(GL_RENDERBUFFER_EXT, depthRenderBufferID); // bind the depth renderbuffer
		//			glRenderbufferStorageEXT(GL_RENDERBUFFER_EXT, GL14.GL_DEPTH_COMPONENT24, 512, 512); // get the data space for it
		//			glFramebufferRenderbufferEXT(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT_EXT, GL_RENDERBUFFER_EXT, depthRenderBufferID); // bind it to the renderbuffer

		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0); // Swithch back to normal framebuffer rendering

		//		}

		cam = new AvianCamera(((float) WIDTH / (float) HEIGHT), 0f, 0f, 0f, 0f, 0f, 0f, 1f, 10000f);
		cam.setFieldOfView(70);
		cam.applyAvianOrthoMatrix();

		runApp();
	}

	private void runApp() {
		long lastTime = System.nanoTime();
		float unprocessed = 0;
		float nsPerTick = 1_000_000_000f / 100;

		int frames = 0;
		int ticks = 0;
		long lastTimer = start = System.currentTimeMillis();

		while (!glfwWindowShouldClose(window)) {
			long now = System.nanoTime();
			unprocessed += (now - lastTime) / nsPerTick;
			lastTime = now;
			boolean shouldRender = false;

			glfwPollEvents();

			if (!loading) {
				leftStats.clear();
				rightStats.clear();
				if (AvianInput.isKeyAction(GLFW_PRESS)) {
					keyHold = 0;
					keyPrev = keyNow = System.currentTimeMillis();
				}
			}

			if (ignoreUnprocessed || exception != null) {
				unprocessed = 1;
			}
			while (unprocessed > 0) {

				if (ignoreUnprocessed || exception != null) {
					unprocessed = 0;
				} else {
					unprocessed -= 1;
				}

				if (!loading && exception == null) {
					AvianUtils.startNanoWatch();
					logic();
					AvianUtils.stopNanoWatch();
					logicTime = AvianUtils.getNanoDifference();
					ignoreUnprocessed = false;
				} else {
					glfwSetWindowTitle(window, APP_NAME + " " + VERSION);
				}

				l();
				ticks++;

				shouldRender = true;
			}
			if (shouldRender) {

				frames++;
				glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
				if (!loading) {
					if (glfwGetWindowAttrib(window, GLFW_VISIBLE) == 0 && !loading) {
						glfwShowWindow(window);
					}
					glfwSetWindowTitle(window, customTitle());
					AvianUtils.startNanoWatch();
					AvianTextField.ibeam = false;
					render();
					AvianUtils.stopNanoWatch();
					renderTime = AvianUtils.getNanoDifference();
				}
				r();
				glfwSwapBuffers(window);
				shouldRender = false;
			}

			if (((System.currentTimeMillis()) - lastTimer) > (1000)) {
				lastTimer += 1000;
				FPS = frames;
				UPS = ticks;
				frames = 0;
				ticks = 0;
				lT = logicTime;
				rT = renderTime;
			}

			uptime = System.currentTimeMillis() - start;

		}
		closeSafely();
	}

	private static void loadCallbacks() {
		if (windowIconifyCallback != null) {
			windowIconifyCallback.close();
		}
		glfwSetWindowIconifyCallback(window, windowIconifyCallback = new GLFWWindowIconifyCallback() {

			@Override
			public void invoke(long w, boolean i) {
				if (i)
					AvianApp.iconified = true;
				else
					AvianApp.iconified = false;
			}

		});
		if (framebufferSizeCallback != null) {
			framebufferSizeCallback.close();
		}
		glfwSetFramebufferSizeCallback(window, framebufferSizeCallback = new GLFWFramebufferSizeCallback() {

			@Override
			public void invoke(long w, int width, int height) {
				WIDTH = width;
				HEIGHT = height;
			}
		});
		if (windowPosCallback != null) {
			windowPosCallback.close();
		}
		glfwSetWindowPosCallback(window, windowPosCallback = new GLFWWindowPosCallback() {

			@Override
			public void invoke(long w, int x, int y) {
				X = x;
				Y = y;
			}
		});
	}

	private static void GL() {
		glEnable(GL_TEXTURE_2D);
		glShadeModel(GL_SMOOTH);
		glDisable(GL_LIGHTING);

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}

	void k() {

	}

	private static float finishSeconds, finishAlpha = 255f;
	private static float scaleSin;
	private static boolean constructAndGL = true;

	private void l() {

		long now = System.currentTimeMillis();
		deltaLogicTime = (now - lastLogicTime) / 1000f;
		lastLogicTime = now;

		if (crash == null) {

			keyNow = System.currentTimeMillis();

			keyHold += ((keyNow - keyPrev) / 1000f);
			if (keyHold >= .660f) {}
			keyPrev = keyNow;

			flashAlpha -= (1f / 25f);

			if (debuzzleType != DebuzzleType.HIDDEN) {
				debAnim.forward();
			} else {
				debAnim.rewind();
			}

			switch (debuzzleType) {
				case AVO:
					debHeightAnim.forward();
					break;
				case APP:
					break;
				case HIDDEN:
					debHeightAnim.forward();
					break;
				default:
					break;
			}

			appDebH = Math.max(leftStats.size() + 1, rightStats.size()) * 25 + 10;

			debuzzleH = (float) (appDebH - (debHeightAnim.normalizedResult() * (appDebH - 135f + 25f)));

			debAnim.setStart(-debuzzleH);

			if (scaleSin < 90)
				scaleSin += 6.4f;
			if (scaleSin > 90)
				scaleSin = 90;

			if (rl.isFinished()) {
				if (constructAndGL) {
					setupGL();
					construct();
					constructAndGL = false;
				}
				finishSeconds += 0.01;
			}
			if (finishSeconds >= 1.5f) {
				finishAlpha = AvianMath.glide(finishAlpha, 0f, 10f);
			}
			if (rl.isFinished()) {
				if (startFullscreen)
					setFullscreen(true);
				loading = false;
			} else if (loading) {
				loadIcon.logic();
				if (rl.isFinished()) {
					//					arcStart = AvoMath.glide(arcStart, 720f, 15f);
					//					arcEnd = AvoMath.glide(arcEnd, 720f, 15f);
				} else {
					//					arcStart = AvoMath.glide(arcStart, 0, 10f);
					//					arcEnd = AvoMath.glide(arcEnd, (rl.progress / 100f) * 360, 10f);
				}
			}

			int i = 0;
			Iterator<NotiBar> iter = deck.iterator();
			while (iter.hasNext()) {
				NotiBar n = iter.next();
				n.logic(i);
				if (n.e.getPhase() <= 0 && !n.shown) {
					iter.remove();
				}
				i++;
			}

			AvianInfoPanel.logic();
		} else {
			delay += .01f;
			if (delay > .5f)
				crashEase.forward();
		}

	}

	private static AvianRectangle r;

	private static long logicTime, renderTime;

	private static long lT, rT;

	private static long lastRenderTime = System.currentTimeMillis();
	private static float deltaRenderTime;
	private static long lastLogicTime = System.currentTimeMillis();
	private static float deltaLogicTime;

	private static DecimalFormat df = new DecimalFormat("00");

	//	private static void getBlur(int x, int y, int width, int height) {
	//		glReadBuffer(GL_BACK);
	//		int bpp = 4;
	//		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);
	//		glReadPixels(x, AvoAppCore.getHeight() - y - height, width, height, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
	//
	//		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	//
	//		for (int i = 0; i < width; i++) {
	//			for (int j = 0; j < height; j++) {
	//				int k = (i + (width * j)) * bpp;
	//				int r = buffer.get(k) & 0xFF;
	//				int g = buffer.get(k + 1) & 0xFF;
	//				int b = buffer.get(k + 2) & 0xFF;
	//				image.setRGB(i, height - (j + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
	//			}
	//		}
	//
	//		blur = new AvoImage(image);
	//		blur.blur(10, true);
	//	}

	static AvianLine l;
	static AvianShadow s;

	private static AvianEase debAnim = new AvianEase(0, 0, .5f, Ease.QUINTIC);
	private static AvianEase debHeightAnim = new AvianEase(0, 1, 0.25f, Ease.QUADRATIC, Direction.OUT);

	static DecimalFormat percentage = new DecimalFormat("0.0");

	private static void r() {
		if (cam == null || iconified)
			return;

		glViewport(0, 0, WIDTH, HEIGHT);
		glLineWidth(1f);

		if (crash == null) {
			long now = System.currentTimeMillis();
			deltaRenderTime = (now - lastRenderTime) / 1000f;
			lastRenderTime = now;

			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

			cam.setAspectRatio((float) WIDTH / (float) HEIGHT);

			cam.applyAvianOrthoMatrix();

			float scale = (AvianMath.sin(scaleSin) / 5f) + .8f;

			if (loading) {
				if (showSplashScreen) {
					splash.render((WIDTH / 2) - ((WIDTH * scale) / 2), (HEIGHT / 2) - ((HEIGHT * scale) / 2), WIDTH * scale, HEIGHT * scale, AvianMath.sin(scaleSin) * 255f);
				}

				loadIcon.render(WIDTH / 2, HEIGHT / 2);
			}

			AvianInfoPanel.render();

			debuzzleY = debAnim.result();

			if (debuzzleY > -debuzzleH) {

				float alpha = 1f - (debuzzleY / -debuzzleH);

				r.set(0, debuzzleY, WIDTH, debuzzleH);
				r.render(0, 150f / 255f);
				s.set(r.getX(), r.getY(), r.getW(), r.getH());
				s.render();

				//				long maxMemory = Runtime.getRuntime().maxMemory();
				long allocatedMemory = Runtime.getRuntime().totalMemory();
				long freeMemory = Runtime.getRuntime().freeMemory();

				alpha = (float) (alpha * (debHeightAnim.normalizedResult() - .5f) * 2);

				AvianFont.setVerticalAlignment(AvianFont.ALIGN_BOTTOM);
				AvianFont.setHorizontalAlignment(AvianFont.ALIGN_LEFT);

				int toMB = 1000 * 1000;
				int sec = (int) (uptime / 1000) % 60;
				int m = (int) (uptime / (60 * 1000)) % 60;
				int h = (int) (uptime / (60 * 60 * 1000));

				Vegur.drawString(10, (debuzzleY + 25) + (debuzzleH - 110), "Debuzzle", alpha);
				Vegur.drawString(10, (debuzzleY + 50) + (debuzzleH - 110), "Logic: " + lT + " nanoseconds", alpha);
				Vegur.drawString(10, (debuzzleY + 75) + (debuzzleH - 110), "Rendering: " + rT + " nanoseconds", alpha);
				Vegur.drawString(10, (debuzzleY + 100) + (debuzzleH - 110), "Memory: " + ((allocatedMemory - freeMemory) / toMB) + " MB used of " + (allocatedMemory / toMB) + " MB allocated", alpha);

				Vegur.drawString(10, (debuzzleY + 25) + (debuzzleH - appDebH), APP_NAME + " " + VERSION, 1f - alpha);
				for (int i = 0; i < leftStats.size(); i++) {
					DebuzzleStat d = leftStats.get(i);
					Vegur.drawString(10, (debuzzleY + 50) + i * 25 + (debuzzleH - appDebH), d.getName() + " " + String.valueOf(d.getValue()) + (d.getSuffix().equals("") ? "" : " " + d.getSuffix()), 1f - alpha);
				}

				AvianFont.setHorizontalAlignment(AvianFont.ALIGN_RIGHT);
				int threads = Thread.activeCount();
				Vegur.drawString(WIDTH - 10, (debuzzleY + 25) + (debuzzleH - 110), UPS + " updates per second", alpha);
				Vegur.drawString(WIDTH - 10, (debuzzleY + 50) + (debuzzleH - 110), FPS + " frames per second", alpha);
				Vegur.drawString(WIDTH - 10, (debuzzleY + 75) + (debuzzleH - 110), threads + " " + (threads != 1 ? "threads" : "thread") + " running", alpha);
				Vegur.drawString(WIDTH - 10, (debuzzleY + 100) + (debuzzleH - 110), "Uptime: " + h + ":" + df.format(m) + ":" + df.format(sec), alpha);

				for (int i = 0; i < rightStats.size(); i++) {
					DebuzzleStat d = rightStats.get(i);
					Vegur.drawString(WIDTH - 10, (debuzzleY + 25) + i * 25 + (debuzzleH - appDebH), d.getName() + " " + String.valueOf(d.getValue()) + (d.getSuffix().equals("") ? "" : " " + d.getSuffix()), 1f - alpha);
				}

			}

			for (int i = 0; i < deck.size(); i++) {
				deck.get(i).render(i);
			}

			r.set(0, 0, WIDTH, HEIGHT);
			r.render(flashAlpha);
		} else {

			float ad = Normal.getHeight();

			double y = (HEIGHT - (crashEase.result() * ((stackTrace.length + 4) * (ad)) - 30));

			if (WIDTH <= crash.getWidth() && HEIGHT <= crash.getHeight()) {
				if (crashEase.result() < 1f) {
					crash.render(0, 0);
				}
				crashGray.render(0, 0, crashEase.result() * 255f);

			} else {
				if (crashEase.result() < 1f) {
					crash.render(0, 0, WIDTH, HEIGHT);
				}
				crashGray.render(0, 0, WIDTH, HEIGHT, crashEase.result() * 255f);
			}

			r.set(0, 0, WIDTH, HEIGHT);
			r.render(0, ((float) (150d * crashEase.result())));
			r.set(WIDTH / 2 - maxWidth / 2 - 10, (float) y, maxWidth + 20, (float) (HEIGHT - y));
			r.render(0, 150 / 255f);

			//			l.set(WIDTH / 2 - maxWidth / 2 - 10, y, WIDTH / 2 - maxWidth / 2 - 10, HEIGHT);
			//			l.render(AvoColor.black());
			//			l.set(WIDTH / 2 - maxWidth / 2 + 10 + maxWidth, y, WIDTH / 2 - maxWidth / 2 + 10 + maxWidth, HEIGHT);
			//			l.render(AvoColor.black());
			//			l.set(WIDTH / 2 - maxWidth / 2 - 10, y, WIDTH / 2 - maxWidth / 2 + 10 + maxWidth, y);
			//			l.render(AvoColor.black());

			AvianFont.setAlignment(AvianFont.ALIGN_LEFT, AvianFont.ALIGN_BOTTOM);
			Vegur.drawString(WIDTH / 2 - maxWidth / 2, (float) (y - 10), APP_NAME + " has crashed");

			Normal.drawString(WIDTH / 2 - maxWidth / 2, (float) (y + 5), header);
			for (int i = 0; i < stackTrace.length; i++) {
				Normal.drawString(WIDTH / 2 - maxWidth / 2, (float) (y + i * (ad) + 5 + ad), stackTrace[i]);
			}
		}
		if (crosshair) {
			r.set(mx, 0, 1, HEIGHT);
			r.render();
			r.set(0, my, WIDTH, 1);
			r.render();
			AvianFont.setAlignment(AvianFont.ALIGN_LEFT, AvianFont.ALIGN_BOTTOM);
			Vegur.drawString(mx + 5, my - 5, "(" + (int) mx + ", " + (int) my + ")");
		}

	}

	/** @return A time, in seconds, from the last render call to the current render call. */

	public static float getDeltaRenderTime() {
		return deltaRenderTime;
	}

	/** @return A time, in seconds, from the last logic call to the current logic call. */

	public static float getDeltaLogicTime() {
		return deltaLogicTime;
	}

	private static boolean fullscreen;
	private static int xx, yy, ww, hh;

	public static void setFullscreen(boolean fullscreen) {
		AvianApp.fullscreen = fullscreen;

		long oldWindow = window;

		if (fullscreen) {
			ww = WIDTH;
			hh = HEIGHT;
			xx = X;
			yy = Y;
		} else {
			//			glfwHideWindow(oldWindow);
		}

		glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, resizable ? GL_TRUE : GL_FALSE);
		glfwWindowHint(GLFW_DECORATED, !undecorated ? GL_TRUE : GL_FALSE);

		long monitor = glfwGetPrimaryMonitor();

		vidmode = glfwGetVideoMode(monitor);

		glfwWindowHint(GLFW_RED_BITS, vidmode.redBits());
		glfwWindowHint(GLFW_GREEN_BITS, vidmode.greenBits());
		glfwWindowHint(GLFW_BLUE_BITS, vidmode.blueBits());
		glfwWindowHint(GLFW_REFRESH_RATE, vidmode.refreshRate());

		window = glfwCreateWindow(vidmode.width(), vidmode.height(), "", fullscreen ? monitor : NULL, glfwGetCurrentContext());

		if (window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		if (!fullscreen)
			glfwSetWindowPos(window, (vidmode.width() - WIDTH) / 2, (vidmode.height() - HEIGHT) / 2);

		AvianInput.create();
		loadCallbacks();
		IntBuffer xBuf = IntBuffer.allocate(4);
		xBuf.order();
		IntBuffer yBuf = IntBuffer.allocate(4);
		yBuf.order();
		glfwGetFramebufferSize(window, xBuf, yBuf);
		if (fullscreen) {
			framebufferSizeCallback.invoke(window, xBuf.get(0), yBuf.get(0));
		} else {
			glfwSetWindowSize(window, ww, hh);
		}

		glfwMakeContextCurrent(window);
		GL();

		glfwSwapInterval(1);

		if (!fullscreen) {
			glfwSetWindowPos(window, xx, yy);
		}
		glfwDestroyWindow(oldWindow);
		glfwShowWindow(window);

	}

	public static void close() {
		glfwSetWindowShouldClose(window, true);
	}

	private static long alcDeviceHandle;

	private static void closeSafely() {
		for (int i = 0; i < tasks.size(); i++) {
			tasks.get(i).run();
		}
		glfwSetWindowTitle(window, "Closing...");
		glfwDestroyWindow(window);

		ALC10.alcCloseDevice(alcDeviceHandle);

		System.exit(0);
	}

	public abstract void construct();

	public abstract void setupGL();

	public abstract void logic();

	public abstract void render();

	public abstract String customTitle();

	private static ArrayList<ShutdownTask> tasks = new ArrayList<ShutdownTask>();

	public static void addShutdownTask(ShutdownTask task) {
		tasks.add(task);
	}

	public static abstract class ShutdownTask {
		public abstract void run();
	}

	public static abstract class IsolatedTask implements Runnable {
		String name;

		public IsolatedTask() {
			name = "IsolatedTask";
		}

		public IsolatedTask(String threadName) {
			this.name = threadName;
		}

		public abstract void task();

		public void run() {
			task();
			isoTasks--;
		}
	}

	static int isoTasks = 0;

	public static void startIsolatedTask(IsolatedTask t) {
		isoTasks++;
		new Thread(t, t.name).start();
	}

	public static boolean hasIsolatedTasksRunning() {
		return isoTasks > 0;
	}

	public static AvianCamera getCam() {
		return cam;
	}

	/** Take a screenshot and save it to the user's Avo folder. */

	private static DecimalFormat dec = new DecimalFormat("0000");
	private static String format = "PNG";

	public static void takeScreenshot() {

		AvianAudioBank.INIT_SCREENSHOT.play();

		long init = System.currentTimeMillis();

		glReadBuffer(GL_FRONT);
		int width = WIDTH;
		int height = HEIGHT;
		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);
		glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

		File file;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int i = (x + (width * y)) * 4;
				int red = buffer.get(i) & 0xFF;
				int g = buffer.get(i + 1) & 0xFF;
				int b = buffer.get(i + 2) & 0xFF;
				image.setRGB(x, height - (y + 1), (0xFF << 24) | (red << 16) | (g << 8) | b);
			}
		}

		int screenshotNumber = 0;

		String filepath = "C:/Avo/" + System.getProperty("user.name") + "/screenshots/" + dec.format(screenshotNumber) + "." + format.toLowerCase();

		file = new File(filepath);
		while (file.exists() && file.isFile()) {
			screenshotNumber++;
			filepath = "C:/Avo/" + System.getProperty("user.name") + "/screenshots/" + dec.format(screenshotNumber) + "." + format.toLowerCase();
			file = new File(filepath);
		}
		file.mkdirs();

		AvianUtils.stopNanoWatch();

		long end = System.currentTimeMillis();

		if (end - init > 0) {
			try {
				Thread.sleep(1000 - (end - init));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		flashAlpha = 1;

		AvianAudioBank.SNAP.play();

		try {
			ImageIO.write(image, format, file);
		} catch (IOException e) {
			deck.add(new NotiBar("Could not save screenshot", e.getMessage(), 0, true));
		}
		deck.add(new NotiBar("Screenshot saved successfully", filepath, 0, false));

		buffer = null;
		file = null;
		image = null;
	}

	//	@Deprecated
	//	public void setIcons(String path16, String path32) {
	//	}

	public void setSplash(String path) {
		if (path == null) {
			showSplashScreen = false;
			return;
		}
		splashFilepath = path;
		showSplashScreen = true;
	}

	public static int getWidth() {
		return WIDTH;
	}

	public static int getHeight() {
		return HEIGHT;
	}

	public static int getX() {
		return X;
	}

	public static int getY() {
		return Y;
	}

	public void setSize(int width, int height) {
		if (window != NULL)
			glfwSetWindowSize(window, width, height);
		WIDTH = width;
		HEIGHT = height;
	}

	public void setPosition(int x, int y) {
		if (window != NULL)
			glfwSetWindowPos(window, x, y);
		X = x;
		Y = y;
	}

	public void setUndecorated(boolean undec) {
		undecorated = undec;
	}

	public void setResizable(boolean resize) {
		resizable = resize;
	}

	boolean started = false;

	public void startWithFullscreen() {
		startFullscreen = true;
	}

	public void setAppNameAndVersion(String name, String version) {
		APP_NAME = name;
		VERSION = version;
	}

	public static void newNotification(String title, String message, int volume, boolean error) {
		deck.add(new NotiBar(title, message, volume, error));
	}

	public static void newNotification(String title, String message, AvianSound sound, boolean error) {
		deck.add(new NotiBar(title, message, sound, error));
	}

	//	public static void addWindow(String title, String message, AvianImage icon, int type, WindowTask task) {
	//		windowStack.add(new AvoWindow(title, message, icon, type, task));
	//	}
	//
	//	public static void addWindow(String title, String message, AvianImage icon, int type, AvianWindowButton... buttons) {
	//		windowStack.add(new AvoWindow(title, message, icon, type, buttons));
	//	}
	//
	//	public static void addWindow(String title, String message, AvianImage icon, AvianSound sound, AvianWindowButton... buttons) {
	//		windowStack.add(new AvoWindow(title, message, icon, sound, buttons));
	//	}

	public static void setInfo(String... lines) {
		AvianInfoPanel.setInfo(lines);
	}

	public static void setInfoSubject(String subject) {
		AvianInfoPanel.setTitle(subject.toUpperCase());
	}

	public static void setStat(String name, Object value, boolean onRightSide) {
		name = name.trim();
		for (DebuzzleStat d : leftStats) {
			if (name.equals(d.getName())) {
				d.setValue(value);
				return;
			}
		}
		for (DebuzzleStat d : rightStats) {
			if (name.equals(d.getName())) {
				d.setValue(value);
				return;
			}
		}
		if (onRightSide) {
			rightStats.add(new DebuzzleStat(name, value));
		} else {
			leftStats.add(new DebuzzleStat(name, value));
		}
	}

	public static void setStat(String name, Object value, String suffix, boolean onRightSide) {
		name = name.trim();
		for (DebuzzleStat d : leftStats) {
			if (name.equals(d.getName())) {
				d.setValue(value);
				d.setSuffix(suffix);
				return;
			}
		}
		for (DebuzzleStat d : rightStats) {
			if (name.equals(d.getName())) {
				d.setValue(value);
				d.setSuffix(suffix);
				return;
			}
		}
		if (onRightSide) {
			rightStats.add(new DebuzzleStat(name, value, suffix));
		} else {
			leftStats.add(new DebuzzleStat(name, value, suffix));
		}
	}

	public static AvianFont Vegur() {
		return Vegur;
	}

	public static AvianFont VegurBold() {
		return Vegur_Bold;
	}

	private static ArrayList<AudioBank> audio = new ArrayList<AudioBank>();
	private static ArrayList<ImageBank> images = new ArrayList<ImageBank>();
	private static ArrayList<FontBank> fonts = new ArrayList<FontBank>();

	public void addAudioBank(AudioBank bank) {
		audio.add(bank);
	}

	public void addImageBank(ImageBank bank) {
		images.add(bank);
	}

	public void addFontBank(FontBank bank) {
		fonts.add(bank);
	}

	public static ArrayList<AudioBank> getAudioBanks() {
		return audio;
	}

	public static ArrayList<ImageBank> getImageBanks() {
		return images;
	}

	public static ArrayList<FontBank> getFontBanks() {
		return fonts;
	}

	public static void setSFXVolume(float volume) {
		for (int i = 0; i < audio.size(); i++) {
			audio.get(i).setSFXVolume(volume);
		}
	}

	public static void setMusicVolume(float volume) {
		for (int i = 0; i < audio.size(); i++) {
			audio.get(i).setMusicVolume(volume);
		}
	}

	public static void stopAllSound() {
		for (int i = 0; i < audio.size(); i++) {
			audio.get(i).stopAll();
		}
	}

	private static ArrayList<AvianKeyboard> keyboards = new ArrayList<AvianKeyboard>();
	private static ArrayList<AvianMouse> mice = new ArrayList<AvianMouse>();

	public static void addKeyListener(AvianKeyboard ak) {
		keyboards.add(ak);
	}

	static void keyPressed(int k) {
		if (activeBox != null)
			return;
		//		newNotification("Key pressed", "" + (key), 0, false);
		for (int i = 0; i < keyboards.size(); i++) {
			keyboards.get(i).press(k);
		}
		if (k == KEY_F11 && resizable) {
			setFullscreen(!fullscreen);
		} else if (k == (KEY_HOME) || k == (KEY_ESCAPE)) {
			close();
		} else if (crash != null) {
			return;
		} else if (k == (KEY_F2)) {
			takeScreenshot();
		} else if (k == (KEY_F3)) {
			switch (debuzzleType) {
				case AVO:
					debuzzleType = DebuzzleType.HIDDEN;
					break;
				case APP:
					debuzzleType = DebuzzleType.AVO;
					break;
				//					case BOTH:
				//						break;
				case HIDDEN:
					if (leftStats.size() > 0 || rightStats.size() > 0) {
						debuzzleType = DebuzzleType.APP;
						debAnim.set(0);
						debHeightAnim.set(0);
					} else {
						debuzzleType = DebuzzleType.AVO;
						debHeightAnim.set(1);
					}
					break;
			}
		} else if (k == (KEY_F1)) {
			if (AvianInfoPanel.open) {
				AvianInfoPanel.open = false;
				AvianAudioBank.CLOSE_INFO.play();
			} else {
				AvianInfoPanel.open = true;
				AvianAudioBank.OPEN_INFO.play();
			}
		} else if (k == KEY_F4) {
			crosshair = !crosshair;
		}
	}

	static void keyTyped(char text) {
		if (activeBox != null)
			return;
		for (int i = 0; i < keyboards.size(); i++) {
			keyboards.get(i).type(text);
		}
	}

	static void keyRepeated(int k) {
		if (activeBox != null)
			return;
		//		newNotification("Key repeated", "" + (key), 0, false);
		for (int i = 0; i < keyboards.size(); i++) {
			keyboards.get(i).repeat(k);
		}
	}

	static void keyReleased(int k) {
		if (activeBox != null)
			return;
		//		newNotification("Key released", "" + (key), 0, false);
		for (int i = 0; i < keyboards.size(); i++) {
			keyboards.get(i).release(k);
		}

	}

	public static void addMouseListener(AvianMouse am) {
		mice.add(am);
	}

	static void mousePressed(int b) {
		if (activeBox != null)
			return;

		//		newNotification("Mouse pressed", button + ", " + mx + ", " + my, 0, false);

		for (int i = 0; i < mice.size(); i++) {
			mice.get(i).press(b, mx, my);
		}
	}

	static void mouseReleased(int b) {
		if (activeBox != null) {
			if (b != 1 && mx > activeBox.x && mx < activeBox.x + activeBox.w) {
				activeBox.index = (int) ((my - activeBox.y - activeBox.yOffset + activeBox.getHeight() / 2) / (activeBox.font == null ? AvianApp.Vegur.getHeight() + 10 : activeBox.font.getHeight() + 10));
			}
			activeBox.focus = false;
			activeBox = null;
			return;
		}

		//		newNotification("Mouse released", button + ", " + mx + ", " + my, 0, false);

		for (int i = 0; i < mice.size(); i++) {
			mice.get(i).release(b, mx, my);
		}
	}

	static void mouseMoved(float x, float y) {
		mx = x;
		my = y;
		if (activeBox != null)
			return;
		for (int i = 0; i < mice.size(); i++) {
			mice.get(i).move(x, y);
		}
	}

	static void mouseScrolled(int yoffset) {
		if (activeBox != null)
			return;

		//		newNotification("Mouse scrolled", "" + count, 0, false);

		for (int i = 0; i < mice.size(); i++) {
			mice.get(i).scroll(yoffset);
		}
	}

	public static String getVersion() {
		return VERSION;
	}

	public static String getAppName() {
		return APP_NAME;
	}

	/** @return A directory reserved specially for this app in the user's Avo folder. An example of what this might look like is <code>C:/Avo/username/appname/</code>. */

	public static String getAppDirectoryPath() {
		return "C:\\Avo\\" + System.getProperty("user.name") + "\\" + APP_NAME + "\\";
	}

	private static long window;

	public static long getWindow() {
		return window;
	}

	private static Exception exception;
	private static AvianImage crash, crashGray;
	private static AvianEase crashEase = new AvianEase(.5f, Ease.EXPONENTIAL);
	private static float delay;
	private static String[] stackTrace;
	private static String header;
	private static float maxWidth;

	//	public static void catchException(Exception e) {
	//		try {
	//			if (e == null || exception != null)
	//				return;
	//			exception = e;
	//
	//			glReadBuffer(GL_FRONT);
	//			ByteBuffer buffer = BufferUtils.createByteBuffer(WIDTH * HEIGHT * 4);
	//			glReadPixels(0, 0, WIDTH, HEIGHT, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
	//
	//			BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	//			BufferedImage grayImg = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	//
	//			for (int x = 0; x < WIDTH; x++) {
	//				for (int y = 0; y < HEIGHT; y++) {
	//					int i = (x + (WIDTH * y)) * 4;
	//					int red = buffer.get(i) & 0xFF;
	//					int g = buffer.get(i + 1) & 0xFF;
	//					int b = buffer.get(i + 2) & 0xFF;
	//
	//					image.setRGB(x, HEIGHT - (y + 1), (0xFF << 24) | (red << 16) | (g << 8) | b);
	//
	//					float[] hsv = Color.RGBtoHSB(red, g, b, null);
	//					hsv[1] /= 2f;
	//					hsv[2] /= 2f;
	//					int gray = Color.HSBtoRGB(hsv[0], hsv[1], hsv[2]);
	//
	//					//				int grayLevel = (r + g + b) / 3;
	//					//				int gray = (grayLevel << 16) + (grayLevel << 8) + grayLevel;
	//					grayImg.setRGB(x, HEIGHT - (y + 1), gray);
	//				}
	//			}
	//
	//			crash = new AvianImage(image);
	//			crashGray = new AvianImage(grayImg);
	//			crash.setSharpTexture(false);
	//			crashGray.setSharpTexture(false);
	//
	//			header = e.toString();
	//			StackTraceElement[] ste = e.getStackTrace();
	//			stackTrace = new String[ste.length];
	//			maxWidth = Normal.getWidth(header);
	//			for (int i = 0; i < ste.length; i++) {
	//				stackTrace[i] = ste[i].toString();
	//				maxWidth = Math.max(maxWidth, Normal.getWidth(stackTrace[i]));
	//			}
	//		} catch (Exception exc) {
	//			exc.printStackTrace();
	//		}
	//	}

	private static GLFWVidMode vidmode;

	private static class NotiBar {

		String title, message;
		boolean shown = true;

		int time;
		//		float errorSin;

		boolean error;

		float y = -35;
		float alpha;
		float line;

		AvianEase e = new AvianEase(-70, 0, .5f, Ease.QUINTIC);

		public NotiBar(String title, String message, int volume, boolean error) {
			this.error = error;
			this.message = message;
			this.title = title;
			switch (volume) {
				case NOTIFICATION_IMPORTANT:
					if (error) {
						AvianAudioBank.NOTI_ERROR_IMPORTANT.play();
					} else {
						AvianAudioBank.NOTI_IMPORTANT.play();
					}
					break;
				case NOTIFICATION_SUBTLE:
					if (error) {
						AvianAudioBank.NOTI_ERROR_SUBTLE.play();
					} else {
						AvianAudioBank.NOTI_SUBTLE.play();
					}
					break;
			}
		}

		public NotiBar(String title, String message, AvianSound sound, boolean error) {
			this.error = error;
			this.message = message;
			this.title = title;
			if (sound != null)
				sound.play();
		}

		public void logic(int num) {
			time++;
			if (time < 500) {
				shown = true;
			} else {
				shown = false;
			}
			if (shown) {
				y = (float) e.forward();
			} else {
				y = (float) e.rewind();
			}
			alpha = (float) e.normalizedResult();
			line += .0075f;
			if (line > 1f)
				line = 1f;
		}

		@SuppressWarnings("unused")
		public float getY() {
			return y;
		}

		static AvianRectangle bar = new AvianRectangle(0, 0, AvianApp.getWidth(), 60);

		public void render(int num) {
			AvianFont.setAlignment(AvianFont.ALIGN_LEFT, AvianFont.ALIGN_CENTER);
			float maxW = 30 + Math.max(Vegur.getWidth(title), Math.max(Vegur.getWidth(message), Math.max(470, AvianApp.getWidth() / 2 - 30)));
			bar.set(AvianApp.getWidth() / 2 - maxW / 2, y, maxW, 70);

			if (num == deck.size() - 1) {
				s.set(bar.getX(), bar.getY(), bar.getW(), bar.getH());
				s.render();
			}

			bar.render(0, alpha * 150 / 255f);

			AvianColor c = AvianColor.get(error ? 50 : 0, 0, 0, alpha * 255f);

			//			l.set(bar.getX(), bar.getY(), bar.getX(), bar.getY() + 70);
			//			l.render(c);
			//
			//			l.set(bar.getX() + bar.getW(), bar.getY(), bar.getX() + bar.getW(), bar.getY() + 70);
			//			l.render(c);
			//
			//			l.set(bar.getX(), bar.getY() + 70, bar.getX() + bar.getW(), bar.getY() + 70);
			//			l.render(c);

			c = AvianColor.get(255, !error ? 255 : 0, !error ? 255 : 0, alpha * 255f);

			float lineY = line * ((bar.getW()) + (bar.getH()));

			float vLineY1 = lineY - bar.getW() / 2f;
			float vLineY2 = lineY;

			if (vLineY1 < 0) {
				vLineY1 = 0;
			} else if (vLineY1 > bar.getH()) {
				vLineY1 = bar.getH();
			}
			if (vLineY2 < 0) {
				vLineY2 = 0;
			} else if (vLineY2 > bar.getH()) {
				vLineY2 = bar.getH();
			}

			vLineY1 += bar.getY();
			vLineY2 += bar.getY();

			l.set(bar.getX(), vLineY1, bar.getX(), vLineY2);
			l.render(c.getR() / 255f, c.getG() / 255f, c.getB() / 255f, c.getA() / 255f);
			l.set(bar.getX() + bar.getW(), vLineY1, bar.getX() + bar.getW(), vLineY2);
			l.render(c.getR() / 255f, c.getG() / 255f, c.getB() / 255f, c.getA() / 255f);

			if (lineY > bar.getH()) {

				float hLine1 = lineY - bar.getH() - bar.getW() / 2;
				float hLine2 = lineY - bar.getH();

				if (hLine1 < 0) {
					hLine1 = 0;
				} else if (hLine1 > bar.getW() / 2f) {
					hLine1 = bar.getW() / 2f;
				}

				if (hLine2 < 0) {
					hLine2 = 0;
				} else if (hLine2 > bar.getW() / 2f) {
					hLine2 = bar.getW() / 2f;
				}

				l.set(AvianApp.getWidth() / 2 - bar.getW() / 2 + hLine1, bar.getY() + 70, AvianApp.getWidth() / 2 - bar.getW() / 2 + hLine2, bar.getY() + 70);
				l.render(c.getR() / 255f, c.getG() / 255f, c.getB() / 255f, c.getA() / 255f);
				l.set(AvianApp.getWidth() / 2 + bar.getW() / 2 - hLine1, bar.getY() + 70, AvianApp.getWidth() / 2 + bar.getW() / 2 - hLine2, bar.getY() + 70);
				l.render(c.getR() / 255f, c.getG() / 255f, c.getB() / 255f, c.getA() / 255f);

			}

			Vegur_Bold.drawString(AvianApp.getWidth() / 2 - maxW / 2 + 15, y + 70f / 3f, title, alpha);
			Vegur.drawString(AvianApp.getWidth() / 2 - maxW / 2 + 15, y + 140f / 3f, message, alpha);
		}
	}

	public static final int NOTIFICATION_SILENCED = 0;
	public static final int NOTIFICATION_SUBTLE = 1;
	public static final int NOTIFICATION_IMPORTANT = 2;

	public static final int WINDOW_SILENT = 0;
	public static final int WINDOW_GENERAL = 1;

	//	private static class AvianWindow {
	//
	//		//		private boolean hasTask;
	//
	//		private AvoWindow(String title, String message, AvianImage icon, int type, WindowTask task) {
	//			if (type == WINDOW_GENERAL)
	//				AvianAudioBank.window.play();
	//			new Thread(task, "AvoWindowTask").start();
	//			//			hasTask = true;
	//
	//		}
	//
	//		private AvoWindow(String title, String message, AvianImage icon, int type, AvianWindowButton... buttons) {
	//			if (type == WINDOW_GENERAL)
	//				AvianAudioBank.window.play();
	//		}
	//
	//		private AvoWindow(String title, String message, AvianImage icon, AvianSound sound, AvianWindowButton... buttons) {
	//			if (sound != null)
	//				sound.play();
	//		}
	//
	//	}
	//
	//	public static abstract class WindowTask implements Runnable {
	//
	//		public WindowTask() {
	//
	//		}
	//
	//	}
	//
	//	public static class AvianWindowButton {
	//
	//		String name;
	//		Method m;
	//		Object obj;
	//		Object[] parameters;
	//
	//		public AvianWindowButton(String name, Method methodToExecute, Object objectToInvokeOn, Object... parameters) {
	//			this.name = name;
	//			m = methodToExecute;
	//			obj = objectToInvokeOn;
	//			this.parameters = parameters;
	//		}
	//
	//	}

	private static class DebuzzleStat {

		private String n;
		private String suf = "";
		private Object v;

		DebuzzleStat(String name, Object value) {
			n = name;
			v = value;
		}

		DebuzzleStat(String name, Object value, String suffix) {
			n = name;
			v = value;
			suf = suffix;
		}

		void setValue(Object value) {
			this.v = value;
		}

		void setSuffix(String suf) {
			this.suf = suf;
		}

		String getName() {
			return n;
		}

		Object getValue() {
			return v;
		}

		String getSuffix() {
			return suf;
		}
	}

	private static AvianComboBox activeBox;

	public static void setActiveBox(AvianComboBox box) {
		activeBox = box;
	}

}
