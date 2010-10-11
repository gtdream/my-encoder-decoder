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

		URL srcPath = TransformerMain.class.getResource("utf-8bom_hanja.txt");
		

		if (null == srcPath) {
			
			System.out.println("not exist " + "\"" + args[0] + "\"");
			System.exit(0);
			
		}
		
		Transformer transformer = null;
		
		try {
			
			transformer = TransformerFactory.getTransformer(new File(srcPath.getPath()));
			System.out.println(transformer.getTransformationType());
			String translatedText = transformer.decode();
//			System.out.print(translatedText);
			
		} catch (EncoderNotSupportedException ense) {
			
			ense.printStackTrace();
			
		} catch (IOException ie) {
			
			ie.printStackTrace();
			
		} finally {
			
			transformer.close();
			
		}
	}

}