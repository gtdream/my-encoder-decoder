package unicode;

import java.net.URL;

public class Encoder {

	public static void main (String[] args) {
		// Utilities.printSystemInfo();
		if (args.length < 1) {
			System.out.println("not enough arguments");
			return;
		}

		URL filePath = Encoder.class.getResource("utf-8.txt");

		if (null == filePath) {
			System.out.println("not exist " + "\"" + args[0] + "\"");
			System.exit(0);
		}

		UTF8 utf8 = new UTF8();

		String translatedText = utf8.encode(filePath.getPath());
		// System.out.print(translatedText);
	}

}