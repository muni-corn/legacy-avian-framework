
package musicaflight.avianutils;

import java.io.*;
import java.net.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class AvianFileDownloader implements Runnable {

	URL url;
	File file;

	private boolean finished = false;
	private boolean saving = false;
	private long onlineFileSize;
	private boolean doRetrieveLines = true;

	float progress;

	@Override
	public void run() {
		e = null;
		finished = false;
		try {
			retrieveAll();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		finished = true;
	}

	public AvianFileDownloader(String url) {
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e1) {
			e = e1;
		}
	}

	public AvianFileDownloader(String outputFilepath, String url) {
		this(url);
		file = new File(outputFilepath);
	}

	private Exception e = null;

	public Exception getException() {
		return e;
	}

	public AvianFileDownloader(String outputFilepath, String url, boolean getLinesWhenThreadStarts) {
		this(outputFilepath, url);
		doRetrieveLines = getLinesWhenThreadStarts;
	}

	public boolean isFinished() {
		return finished;
	}

	public void retrieveAll() throws IOException {
		if (file != null) {
			if (file.exists())
				file.delete();
			if (file.getParentFile() != null)
				file.getParentFile().mkdirs();
		}
		loadOnlineFileSize();
		if (doRetrieveLines)
			getFileLines();
		if (file != null) {
			downloadFile();
		}
	}

	private long loadOnlineFileSize() throws IOException {
		HttpURLConnection conn = null;
		conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("HEAD");
		conn.getInputStream();
		onlineFileSize = conn.getContentLength();
		conn.disconnect();
		return onlineFileSize;
	}

	public long getOnlineFileSize() {
		return onlineFileSize;
	}

	public float getProgress() {
		if (saving) {
			progress = ((getBytesDownloaded() / onlineFileSize));
			return progress;
		}
		return 0;
	}

	public long getBytesDownloaded() {
		return (saving || finished) ? file.length() : 0;
	}

	public long getBytesRemaining() {
		return onlineFileSize - getBytesDownloaded();
	}

	public long getRemainingMilliseconds() {
		return (long) (getBytesRemaining() / getDownloadSpeed());
	}

	public long getAverageRemainingMilliseconds() {
		return (long) (getBytesRemaining() / getAverageDownloadSpeed());
	}

	public String getRemainingTimeAsWords() {
		if (saving) {
			long millis = getRemainingMilliseconds();

			String t;

			if (millis >= (60 * 60 * 1000)) {
				t = d.format(millis / (60.0 * 60.0 * 1000.0));
				return t + (t.equals("1") ? " hour" : " hours");
			} else if (millis >= (60 * 1000)) {
				t = d.format(millis / (60.0 * 1000.0));
				return t + (t.equals("1") ? " minute" : " minutes");
			} else {
				t = d2.format(millis / (1000.0));
				return t + (t.equals("1") ? " second" : " seconds");
			}
		}
		return "???";
	}

	static DecimalFormat d = new DecimalFormat("0.#");
	static DecimalFormat d2 = new DecimalFormat("0");

	public String getAverageRemainingTimeAsWords() {
		if (saving) {
			long millis = getAverageRemainingMilliseconds();

			String t;

			if (millis >= (60 * 60 * 1000)) {
				t = d.format(millis / (60.0 * 60.0 * 1000.0));
				return t + (t.equals("1") ? " hour" : " hours");
			} else if (millis >= (60 * 1000)) {
				t = d.format(millis / (60.0 * 1000.0));
				return t + (t.equals("1") ? " minute" : " minutes");
			} else {
				t = d2.format(millis / (1000.0));
				return t + (t.equals("1") ? " second" : " seconds");
			}
		}
		return "???";
	}

	double downloadSpeed;
	double bytesDownloadedSinceLastSecond;

	public double getAverageDownloadSpeed() {
		return getBytesDownloaded() / (System.currentTimeMillis() - timeAtStart);
	}

	public double getDownloadSpeed() {
		if ((System.currentTimeMillis() - time) >= 1000) {
			time += 1000;
			long fileSize = getBytesDownloaded();
			downloadSpeed = (fileSize - bytesDownloadedSinceLastSecond) / 1000;
			bytesDownloadedSinceLastSecond = fileSize;
		}
		return downloadSpeed;
	}

	long timeAtStart;
	long time;

	public long getDownloadBeginTime() {
		return timeAtStart;
	}

	public void downloadFile() {
		saving = true;
		try {
			ReadableByteChannel rbc = Channels.newChannel(url.openStream());
			if (file.exists())
				file.delete();
			if (file.getParentFile() != null)
				file.getParentFile().mkdirs();
			file.createNewFile();
			timeAtStart = time = System.currentTimeMillis();
			FileOutputStream fileOut = new FileOutputStream(file);
			fileOut.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fileOut.close();
			rbc.close();
		} catch (IOException e1) {
			e = e1;
		}
		saving = false;
		finished = true;
	}

	String[] fileLines;

	public String[] getFileLines() {
		if (fileLines == null) {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

				String line;

				ArrayList<String> lines = new ArrayList<String>();

				while ((line = reader.readLine()) != null)
					lines.add(line);

				reader.close();

				return fileLines = lines.toArray(new String[lines.size()]);

			} catch (IOException e1) {
				e = e1;
			}
		}
		return fileLines;

	}

	public void initialize() {
		finished = false;
		fileLines = null;
		timeAtStart = time = 0;
		downloadSpeed = bytesDownloadedSinceLastSecond = onlineFileSize = 0;
		progress = 0;
		saving = false;
	}

	public boolean isSaving() {
		return saving;
	}

}
