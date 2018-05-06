
package musicaflight.avianutils;

public interface AudioBank {

	/** Assign and load all AdoxSounds to be loaded in a class that <code>implements AudioBank</code>. */
	public void initAudio();

	/** Set the volume of every AvianSound considered to be music. This can be accomplished by calling <code>setVolume()</code> on each AvianSound. */
	public void setMusicVolume(float volume);

	/** Set the volume of every AvianSound considered to be a sound effect. This can be accomplished by calling <code>setVolume()</code> on each AvianSound. */
	public void setSFXVolume(float volume);

	/** Stop all music and/or sound effects. This can be accomplished by calling <code>stop()</code> on each AvianSound. */
	public void stopAll();

}
