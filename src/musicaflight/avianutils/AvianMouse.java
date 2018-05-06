
package musicaflight.avianutils;

public interface AvianMouse {

	public void press(int button, float x, float y);

	public void release(int button, float x, float y);

	public void move(float x, float y);

	public void scroll(int count);

}
