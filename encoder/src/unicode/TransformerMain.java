package unicode;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import unicode.exception.EncoderNotSupportedException;


public class TransformerMain {

	public static void main (String[] args) {
		
//		if (args.length < 1) {
//			
//			System.out.println("not enough arguments");
//			
//			return;
//			
//		}

		URL filePath = TransformerMain.class.getResource("utf-16le.txt");

		if (null == filePath) {
			
			System.out.println("not exist " + "\"" + args[0] + "\"");
			System.exit(0);
			
		}
		
		Transformer encoder = null;
		
		try {
			
			encoder = TransformerFactory.getEncodingMethod(new File(filePath.getPath()));
			System.out.println(encoder.getEncodingType());
			String translatedText = encoder.decode();
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