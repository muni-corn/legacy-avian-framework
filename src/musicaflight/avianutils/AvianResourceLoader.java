
package musicaflight.avianutils;

import java.text.DecimalFormat;

public class AvianResourceLoader implements Runnable {

	public int totalResourceCount, resourcesLoaded;
	public boolean imagesLoaded, fontsLoaded, audioLoaded;
	public boolean mustLoadImgGL = true, mustLoadFontGL = true;
//	float progress = 0;

	DecimalFormat format = new DecimalFormat("0.##");

	public static enum CurrentlyLoading {
		NOTHING(
				"One moment..."),
		FONTS(
				"Loading..."),
		IMAGES(
				"Loading..."),
		AUDIO(
				"Loading..."),
		FINISHED(
				"Done.");

		public String description;

		CurrentlyLoading(String description) {
			this.description = description;
		}
	}

	public static CurrentlyLoading current = CurrentlyLoading.NOTHING;

	public AvianResourceLoader() {
	}

	public void loadAll() {

		int images = 0, audio = 0, fonts = 0;

		for (int i = 0; i < AvianApp.getImageBanks().size(); i++)
			images += AvianApp.getImageBanks().get(i).getClass().getFields().length;

		for (int i = 0; i < AvianApp.getAudioBanks().size(); i++)
			audio += AvianApp.getAudioBanks().get(i).getClass().getFields().length;

		for (int i = 0; i < AvianApp.getFontBanks().size(); i++)
			fonts += AvianApp.getFontBanks().get(i).getClass().getFields().length;

		totalResourceCount = images + audio + fonts + AvianAudioBank.class.getFields().length;

		loadFonts();
		loadImages();
		loadAudio();

		current = CurrentlyLoading.FINISHED;

		AvianApp.resourceThread = null;
	}

	public void loadImages() {
		current = CurrentlyLoading.IMAGES;

		for (int i = 0; i < AvianApp.getImageBanks().size(); i++)
			AvianApp.getImageBanks().get(i).initImages();

		imagesLoaded = true;
	}

	public void loadAudio() {
		current = CurrentlyLoading.AUDIO;

		for (int i = 0; i < AvianApp.getAudioBanks().size(); i++)
			AvianApp.getAudioBanks().get(i).initAudio();

		audioLoaded = true;
	}

	public void loadFonts() {
		current = CurrentlyLoading.FONTS;

		for (int i = 0; i < AvianApp.getFontBanks().size(); i++)
			AvianApp.getFontBanks().get(i).initFonts();

		fontsLoaded = true;
	}

//	public void resourceLoaded() {
//		resourcesLoaded++;
//		progress = ((float) resourcesLoaded / (float) totalResourceCount) * 100f;
//	}

	public boolean isFinished() {
		return current == CurrentlyLoading.FINISHED;
	}

	@Override
	public void run() {
		loadAll();
	}

}
