package utf8;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

public class Encoder {

	public static void main (String[] args) {
		
		if (args.length < 1) {
			System.out.println("not enough arguments");
			return;
		}

		URL filePath = Encoder.class.getResource("utf-16le.txt");

		if (null == filePath) {
			System.out.println("not exist " + "\"" + args[0] + "\"");
			System.exit(0);
		}

		UTF8.encode(filePath.getPath());
	}

}

class UTF8 {
	final static int LEVEL1 = 0;
	final static int LEVEL2 = 0xC080;
	final static int LEVEL3 = 0xE08080;
	final static int LEVEL4 = 0xF8808080;
	final static int BOM_UTF8 = 0xEFBBBF;
	final static int BOM_UTF16LE = 0xFEFF;
	final static int BOM_UTF16BE = 0xFFFE;
	final static int BOM_UTF32LE = 0x0000FEFF;
	final static int BOM_UTF32BE = 0xFFFE0000;

	public static String encode (String filePath) {
		FileInputStream fileStream = null;
		try {
			fileStream = new FileInputStream(filePath);
			// windows 파일저장 최소단위 4KB x 2
			byte[] buffer = new byte[8192];
			int offset = 0;
			int numberReadedByte = 0;
			while (0 == numberReadedByte) {
				int numberCanReadSize = fileStream.available();
				numberReadedByte = fileStream.read(buffer, offset, numberCanReadSize);
				System.out.println(new String(buffer, 0, numberReadedByte, "utf-16le"));
			}
			System.out.printf("%x %x %x\n", buffer[0], buffer[1], buffer[2]);
			for (int j = 0; j < 3; j++) {
				for (int i = Byte.SIZE; i > 0; i--) {
					System.out.print((buffer[j] >> i) & 0x1);
				}
				System.out.print(' ');
			}
			
			char c = '중';
			int bitArray = 0;
			for (int i = 0; i < Character.SIZE; i++) {
				System.out.print(c >> (Character.SIZE - (i+1)) & 0x1);
				bitArray |= c >> (Character.SIZE - (i+1)) & 0x1;
			}
			System.out.println("\n" + c);
			//1100 1001 0001 0001
		} catch (IOException ie) {
			ie.printStackTrace();
		} finally {
			try {
				if (null != fileStream)
					fileStream.close();
			} catch (IOException ie) {
				ie.printStackTrace();
			}
		}

		return null;
	}
}