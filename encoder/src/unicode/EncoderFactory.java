package unicode;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import unicode.exception.EncoderNotSupportedException;
import unicode.utf16.UTF16BE;
import unicode.utf16.UTF16LE;
import unicode.utf8.UTF8;

public class EncoderFactory {
	
	public final static int UTF8_MASK 	= 0xFFFFFF00;
	public final static int UTF16_MASK 	= 0xFFFF0000;
	public final static int UTF32_MASK 	= 0xFFFFFFFF;
	
	public final static int UTF8 		= 0xEFBBBF00;
	public final static int UTF16LE 	= 0x0000FFFE;
	public final static int UTF16BE		= 0x0000FEFF;
	public final static int UTF32LE 	= 0xFFFE0000;
	public final static int UTF32BE 	= 0x0000FEFF;
	
	public static Encoder getEncodingMethod(File file) throws IOException, EncoderNotSupportedException {

		BufferedInputStream bInputStream = null;
		bInputStream = new BufferedInputStream(new FileInputStream(file));

		int byteOrderMark = 0;
		byte[] bom = new byte[4];

		bInputStream.mark(0);
		
		for (int i = 0; i < 4; i++) {
			bom[i] = (byte) bInputStream.read();		
		}
		 		    
		bInputStream.reset();
		
		// for (int i = 0; i < 4; i++) {
		// byteOrderMark |= (buffer[i] << (Integer.SIZE - (Byte.SIZE * (i+1))))
		// & (0xff000000 >>> (Byte.SIZE * i));
		// }
		
		byteOrderMark  = bom[0] << 24 & 0xff000000;
		byteOrderMark |= bom[1] << 16 & 0x00ff0000;
		byteOrderMark |= bom[2] << 8  & 0x0000ff00;
		byteOrderMark |= bom[3]       & 0x000000ff;
		
//		 System.out.printf("%x\n",byteOrderMark);
		
		if ((byteOrderMark & UTF32_MASK) == UTF32LE) {
			
			throw new EncoderNotSupportedException(EncodingType.UTF32LE.toString());
			
		} else if ((byteOrderMark & UTF32_MASK) == UTF32BE) {
			
			throw new EncoderNotSupportedException(EncodingType.UTF32LE.toString());
			
		} else if ((byteOrderMark & UTF8_MASK) == UTF8) {
			
			return new UTF8(bInputStream, EncodingType.UTF8BOM);
			
		} else if ((byteOrderMark & UTF16_MASK)>>>16 == UTF16LE) {
			
			return new UTF16LE(bInputStream);
			
		} else if ((byteOrderMark & UTF16_MASK)>>>16 == UTF16BE) {
			
			return new UTF16BE(bInputStream);
			
		} else {
			
			return new UTF8(bInputStream);
			
		}
		
	}

}
