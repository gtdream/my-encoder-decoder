package unicode;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import unicode.exception.EncoderNotSupportedException;


public class EncoderMain {

	public static void main (String[] args) {
		// Utilities.printSystemInfo();
		if (args.length < 1) {
			System.out.println("not enough arguments");
			return;
		}

		URL filePath = EncoderMain.class.getResource("utf-8.txt");

		if (null == filePath) {
			System.out.println("not exist " + "\"" + args[0] + "\"");
			System.exit(0);
		}

		Encoder encoder = null;
		try {
			encoder = EncoderFactory.getEncodingMethod(new File(filePath.getPath()));
			System.out.println(encoder.getEncodingType());
			String translatedText = encoder.encode();
			System.out.print(translatedText);
		} catch (EncoderNotSupportedException ense) {
			ense.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		} finally {
			encoder.close();
		}
	}

}