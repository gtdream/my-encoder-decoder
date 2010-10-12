package unicode.utf16;

import java.util.ArrayList;

import unicode.ByteOrderMark;
import unicode.Encoder;

public class UTF16Encoder extends Encoder{
	
	private final int endian;
	
	private static char HI_SURRO = 0xD800;
	private static char LOW_SURRO = 0xDC00;
	
	public UTF16Encoder (int endian) {
		this.endian = endian;
	}
	
	public int getEndian () {
		
		return endian;
	}
	
	@Override
	public int encode (ArrayList<Integer> source, byte[] dest) {

		int length = source.size();

		int count = 0;
		
		dest[count++] = (byte) (endian>>8);
		dest[count++] = (byte) endian;
		
		if (endian == ByteOrderMark.UTF16BE) {
		
			for (int i = 0; i < length; i++) {
				
				int character = source.get(i);
								
				if ((character >> 16) == 0) {
					
					dest[count++] = (byte)(character>>8);
					dest[count++] = (byte)character;
				
				} else {
					
					
					byte[] fourByte = new byte[4];
					
					// unicode form
					// 00000000 000ZZZZZ xxxxxxxyy yyyyyyyy
					
					// hi surrogate
					// 110110zz zzxxxxxx
					
					// zzzz = ZZZZZ - 1

					char hi = (char) ((character>>10) & 0x3f);	// xxxxxx 6bit 채움
					hi += ((((character >> 16) & 0x1f)-1) << 6);	// zzzz 4bit 채움
					hi += HI_SURRO; // surrogate bit 추가
					fourByte[0] = (byte) (hi>>8);	// 앞쪽 110110zz
					fourByte[1] = (byte) hi;	// 뒷쪽 zzxxxxxx
					
					// low surrogate
					// 110111yy yyyyyyyy
					char low = (char) (character & 0x03ff); // y부분 10bit, 110111yy yyyyyyyy 
					low += LOW_SURRO; // surrogate bit 추가
					fourByte[2] = (byte) (low>>8); // 앞쪽 110111yy
					fourByte[3] = (byte) low; // 뒷쪽 yyyyyyyy
					
					// 4byte utf-16 문자 data 를 1byte 씩   
					for (byte b : fourByte) {
						dest[count++] = b;
					}
					
				}
				
			}
			
		} else {
			
				for (int i = 0; i < length; i++) {
				
				int character = source.get(i);
				
				if ((character >> 16) == 0) {
					
					dest[count++] = (byte) character;
					dest[count++] = (byte) (character>>8);
				
				} else {
					
					byte[] fourByte = new byte[4];

					char hi = (char) ((character>>10) & 0x3f);
					hi += ((((character >> 16) & 0x1f)-1) << 6);
					hi += HI_SURRO; 
					fourByte[1] = (byte) (hi>>8);
					fourByte[0] = (byte) hi;
					
					char low = (char) (character & 0x03ff);
					low += LOW_SURRO;
					fourByte[3] = (byte) (low>>8);
					fourByte[2] = (byte) low;
					
					for (byte b : fourByte) {
						dest[count++] = b;
					}
					
				}
				
			}
			
		}
		
		return count;
	}
	
}
