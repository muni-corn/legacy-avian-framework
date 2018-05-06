
package musicaflight.avianutils;

import musicaflight.avianutils.AvianEase.Ease;

public class AvianScrollbar {

	private float contentHeight;
	boolean restrict;
	private float finalOffset;
	private float offset;
	static boolean scrollAmount;
	private boolean smoothScrolling = true;
	static AvianEase e = new AvianEase(1f, Ease.LINEAR);
	static AvianRectangle r = new AvianRectangle();

	public AvianScrollbar() {
		AvianApp.addMouseListener(new AvianMouse() {

			@Override
			public void press(int button, float x, float y) {
				// TODO Auto-generated method stub

			}

			@Override
			public void release(int button, float x, float y) {
				// TODO Auto-generated method stub

			}

			@Override
			public void move(float x, float y) {
				// TODO Auto-generated method stub

			}

			@Override
			public void scroll(int count) {
				if (restrict)
					return;

			}

		});
	}

	public void restrictScrolling(boolean res) {
		restrict = res;
	}

	public void setContentHeight(float height) {
		contentHeight = height;
	}

	public void useSmoothScrolling(boolean smooth) {
		smoothScrolling = smooth;
	}

	public float getOffset() {
		return smoothScrolling ? finalOffset : offset;
	}

	public void render() {
		if (contentHeight > AvianApp.getHeight()) {
			float availableHeight = AvianApp.getHeight() - 8;
			float scrubberHeight = (AvianApp.getHeight() / contentHeight) * availableHeight;
			if (scrubberHeight < 4)
				scrubberHeight = 4;
			float percentage = getOffset() / (AvianApp.getHeight() - contentHeight);
			r.set(AvianApp.getWidth() - 6, 4 + percentage * (availableHeight - scrubberHeight), 2, scrubberHeight);
			r.render((float) (150d * (1d - e.result())) / 255f);
		}
	}

}
