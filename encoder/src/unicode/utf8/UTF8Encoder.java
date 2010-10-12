package unicode.utf8;

import java.util.ArrayList;

import unicode.Encoder;

public class UTF8Encoder extends Encoder{
	
	private final int byteOrderMark;
	
	public UTF8Encoder(int byteOrderMark) {
		
		this.byteOrderMark = byteOrderMark;
		
	}
	
	public UTF8Encoder () {
		
		this.byteOrderMark = 0;
		
	}
	
	@Override
	public int encode (ArrayList<Integer> source, byte[] dest) {
		
		int usedCharacter = 0;
		
		int length = source.size();
		
		if (byteOrderMark != 0) {
			dest[usedCharacter++] = (byte) ((byteOrderMark>>16)	& 0xff);
			dest[usedCharacter++] = (byte) ((byteOrderMark>>8)	& 0xff);
			dest[usedCharacter++] = (byte) (byteOrderMark		& 0xff);
		}
		
		for (int i = 0; i < length; i++) {
			
			int character = source.get(i);
			
			// ASCII 범위
			if (0x0080 > character) {
				
				dest[usedCharacter++] = (byte) character;
				
			// ASCII 범위를 넘으면
			} else if (0x7ff >= character){
				
				// 110xxxxx
				dest[usedCharacter++] = (byte) (((character>>6) & 0xff) + TwoBytes.HEADER);
				
				// 10xxxxxx
				dest[usedCharacter++] = (byte) ((character & 0x3f) + OneByte.BODY);
				
			// 2byte 표현 범위를 넘으면
			} else if (0xffff >= character){
				
				// 1110xxxx
				dest[usedCharacter++] = (byte) (((character>>12) & 0xff) + ThreeBytes.HEADER);
				
				// 10xxxxxx
				dest[usedCharacter++] = (byte) (((character>>6) & 0x3f) + OneByte.BODY);
				
				// 10xxxxxx
				dest[usedCharacter++] = (byte) ((character & 0x3f) + OneByte.BODY);
				
			// 3byte 표현 범위를 넘으면
			} else if (0x10ffff >= character) {
				
				// 11110xxx
				dest[usedCharacter++] = (byte) (((character>>18) & 0xff) + FourBytes.HEADER);
				
				// 10xxxxxx
				dest[usedCharacter++] = (byte) (((character>>12) & 0x3f) + OneByte.BODY);
				
				// 10xxxxxx
				dest[usedCharacter++] = (byte) (((character>>6) & 0x3f) + OneByte.BODY);
				
				// 10xxxxxx
				dest[usedCharacter++] = (byte) ((character & 0x3f) + OneByte.BODY);
				
			}
			
		}
		
		return usedCharacter;
		
	}
	
}
