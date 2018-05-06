
package musicaflight.avianutils;

import static org.lwjgl.opengl.GL11.*;

import musicaflight.avianutils.AvianEase.Ease;

public class AvianInfoPanel {

	static boolean open = false;
	private static float infoPanelScale;

	private static AvianEase anim = new AvianEase(1.1f, 1f, .5f, Ease.QUINTIC);

	private static String[] strings = new String[] { "This app has no info.",
			"Use AvianApp.setInfo() to add info here." };
	private static String title = "ABOUT";

	static void setTitle(String t) {
		title = t;
	}

	static void setInfo(String... s) {
		strings = s;
	}

	static void logic() {
		if (open) {
			infoPanelScale = (float) anim.forward();
		} else {
			infoPanelScale = (float) anim.rewind();
		}
	}

	static AvianRectangle aboutSlab;

	static void render() {
		if (anim.getPhase() == 0f)
			return;

		AvianFont.setAlignment(AvianFont.ALIGN_TOP, AvianFont.ALIGN_CENTER);

		if (aboutSlab == null) {
			aboutSlab = new AvianRectangle(0, 0, AvianApp.getWidth(), AvianApp.getHeight());
		}
		aboutSlab.set(0, 0, AvianApp.getWidth(), AvianApp.getHeight());

		if (infoPanelScale < 1.5f) {
			glPushMatrix();
			glTranslatef((AvianApp.getWidth() - (AvianApp.getWidth() * (infoPanelScale))) / 2f, (AvianApp.getHeight() - (AvianApp.getHeight() * (infoPanelScale))) / 2f, 0);
			glScalef(infoPanelScale, infoPanelScale, 1);
			float alpha = (float) (220f * anim.normalizedResult());
			if (alpha < 0)
				alpha = 0;

			float textAlpha = (float) (anim.normalizedResult());

			aboutSlab.render(0, alpha / 255f);
			AvianApp.Vegur().drawString(110, AvianApp.getAppName() + " " + AvianApp.getVersion(), textAlpha);
			AvianApp.Vegur().drawString(160, title, textAlpha);

			for (int i = 0; i < strings.length; i++) {
				AvianApp.Vegur().drawString((i * 25) + 210, strings[i], textAlpha);
			}
			glPopMatrix();
		}

	}
}
