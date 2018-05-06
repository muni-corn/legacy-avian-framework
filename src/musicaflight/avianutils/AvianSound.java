
package musicaflight.avianutils;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.AL11.AL_SEC_OFFSET;

import java.io.*;

import org.lwjgl.openal.AL11;
import org.lwjgl.util.WaveData;

public class AvianSound {

	public int source, buffer;

	boolean loops;

	String filepath;

	boolean initialized = false;

	File file;

	public AvianSound() {
		initialized = false;

		//		if (AvianResourceLoader.current == CurrentlyLoading.AUDIO)
		//			AdoxAppCore.rl.resourceLoaded();
	}

	public AvianSound(String filepath) {
		this(filepath, false);
	}

	public AvianSound(String filepath, boolean loops) {
		this.filepath = filepath;
		this.loops = loops;

		WaveData data = null;

		boolean absolute = (file = new File(filepath)).isAbsolute();
		BufferedInputStream stream = null;
		try {
			stream = new BufferedInputStream(absolute ? new FileInputStream(filepath) : AvianSound.class.getResourceAsStream(filepath));
			data = WaveData.create(stream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stream != null)
					stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (data != null) {
			int buf = alGenBuffers();
			alBufferData(buf, data.format, data.data, data.samplerate);
			data.dispose();
			int src = alGenSources();
			alSourcei(src, AL_BUFFER, buf);
			if (loops)
				alSourcei(src, AL_LOOPING, AL_TRUE);

			this.source = src;
			this.buffer = buf;

			initialized = true;

			play();
			stop();
		} else {
			throw new NullPointerException("The file at \"" + filepath + "\" is not a WAV file.");
		}
		data = null;
	}

	float volume, pitch;

	public boolean isInitialized() {
		return initialized;
	}

	public void setVolume(float volume) {
		this.volume = volume;
		alSourcef(source, AL_GAIN, this.volume);
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
		alSourcef(source, AL_PITCH, this.pitch);
	}

	public float getPitch() {
		return pitch;
	}

	public float getVolume() {
		return volume;
	}

	public void play() {
		if (initialized)
			alSourcePlay(source);

	}

	public void playWithRandomVolume() {
		if (initialized) {
			alSourcef(source, AL_GAIN, AvianMath.randomFloat());
			alSourcePlay(source);
		}
	}

	public void playWithRandomPitch() {
		if (initialized) {
			alSourcef(source, AL_PITCH, AvianMath.randomFloat() + .5f);
			alSourcePlay(source);
		}
	}

	public void playWithRandomPitch(float lower, float upper) {
		if (initialized) {
			alSourcef(source, AL_PITCH, lower + (AvianMath.randomFloat() * (upper - lower)));
			alSourcePlay(source);
		}
	}

	public void pause() {
		if (initialized)
			alSourcePause(source);
	}

	public void stop() {
		if (initialized)
			alSourceStop(source);
	}

	public float getVelocity() {
		return alGetSourcef(source, AL_VELOCITY);
	}

	public boolean isPlaying() {
		return initialized && (alGetSourcei(source, AL_SOURCE_STATE) == AL_PLAYING);
	}

	public boolean isPaused() {
		return initialized && (alGetSourcei(source, AL_SOURCE_STATE) == AL_PAUSED);

	}

	public boolean isStopped() {
		return initialized && (alGetSourcei(source, AL_SOURCE_STATE) == AL_STOPPED);
	}

	public float getPosition() {
		if (initialized)
			return alGetSourcef(source, AL_SEC_OFFSET);

		return 0;
	}

	public void setPosition(float seconds) {
		if (initialized) {
			if (isStopped()) {
				play();
				pause();
			}
			if ((seconds > getDuration()) || (seconds < 0f)) {
				alSourcef(source, AL_SEC_OFFSET, 0f);
				return;
			}
			alSourcef(source, AL_SEC_OFFSET, seconds);
		}
	}

	public float getDuration() {
		if (initialized) {
			int sizeInBytes = AL11.alGetBufferi(buffer, AL_SIZE);
			int channels = AL11.alGetBufferi(buffer, AL_CHANNELS);
			int bits = AL11.alGetBufferi(buffer, AL_BITS);

			float lengthInSamples = (sizeInBytes * 8f) / ((float) channels * (float) bits);

			int frequency = AL11.alGetBufferi(buffer, AL_FREQUENCY);

			return lengthInSamples / frequency;
		}
		return 0;
	}

	public File getFile() {
		if (initialized)
			try {
				return file.getCanonicalFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		return null;
	}

	public void setLooping(boolean loops) {
		this.loops = loops;
		alSourcei(source, AL_LOOPING, loops ? AL_TRUE : AL_FALSE);
	}

	public AvianSound destroy() {
		alDeleteSources(source);
		alDeleteBuffers(buffer);
		return null;
	}

	public String getName() {
		try {
			return file.getCanonicalPath().substring(filepath.lastIndexOf(File.separator) + 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

}
