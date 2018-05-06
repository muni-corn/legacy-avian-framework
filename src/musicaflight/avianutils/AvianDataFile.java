
package musicaflight.avianutils;

import java.io.*;
import java.util.*;

public class AvianDataFile {

	File file;
	BufferedWriter w;
	BufferedReader r;
	String lines[];

	HashMap<String, String> elements = new HashMap<String, String>();

	public AvianDataFile(String filepath) {
		try {
			file = new File(filepath);
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			loadElements();
		} catch (IOException e) {}
	}

	private void loadElements() {
		try {
			r = new BufferedReader(new FileReader(file));

			String line;

			while ((line = r.readLine()) != null) {
				int sub = Integer.parseInt(line.split(" ")[0]);
				String noInt = line.substring(String.valueOf(sub).length() + 1);
				String value = noInt.substring(sub).replace("<!n!>", "\n");
				String key = noInt.substring(0, sub);
				setElement(key, value);
			}

		} catch (IOException e) {
			return;
		}
	}

	public void setElement(String name, String value) {
		elements.put(name.trim(), value);
	}

	public void setElement(String name, int value) {
		setElement(name, String.valueOf(value));
	}

	public void setElement(String name, long value) {
		setElement(name, String.valueOf(value));
	}

	public void setElement(String name, double value) {
		setElement(name, String.valueOf(value));
	}

	public void setElement(String name, float value) {
		setElement(name, String.valueOf(value));
	}

	public void setElement(String name, boolean value) {
		setElement(name, String.valueOf(value));
	}

	public void flushElements() {
		try {
			w = new BufferedWriter(new FileWriter(file));

			Iterator<String> iter = elements.keySet().iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				w.write(key.length() + " " + key + elements.get(key).replace("\n", "<!n!>"));
				w.newLine();
			}
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		loadElements();
	}

	public String retrieveElement(String element) {
		return elements.get(element.trim());
	}

	public int retrieveElementInt(String element) {
		String s = retrieveElement(element);
		if (s != null && !s.equals("")) {
			return (int) Double.parseDouble(s);
		}
		return 0;
	}

	public long retrieveElementLong(String element) {
		String s = retrieveElement(element);
		if (s != null && !s.equals("")) {
			return Long.parseLong(s);
		}
		return 0;
	}

	public double retrieveElementDouble(String element) {
		String s = retrieveElement(element);
		if (s != null && !s.equals("")) {
			return Double.parseDouble(s);
		}
		return 0;
	}

	public float retrieveElementFloat(String element) {
		String s = retrieveElement(element);
		if (s != null && !s.equals("")) {
			return Float.parseFloat(s);
		}
		return 0;
	}

	public boolean retrieveElementBoolean(String element) {
		String s = retrieveElement(element);
		if ((s != null) && !s.equals("")) {
			return Boolean.parseBoolean(s);
		}
		return false;
	}

	public String[] keys() {
		Set<String> k = elements.keySet();
		return k.toArray(new String[k.size()]);
	}

	public String[] values() {
		Collection<String> v = elements.values();
		return v.toArray(new String[v.size()]);
	}

	public void clear() {
		elements.clear();
	}

}
