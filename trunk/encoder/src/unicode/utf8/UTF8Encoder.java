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
		
		int countByte = 0;
		
		// source 의 전체크기는 곧 UNICODE character 의 개수
		int length = source.size();
		
		// BOM 있는 UTF-8 encoding 일 경우 BOM 생성 
		if (byteOrderMark != 0) {
			
			dest[countByte++] = (byte) ((byteOrderMark>>16)	& 0xff);
			dest[countByte++] = (byte) ((byteOrderMark>>8)	& 0xff);
			dest[countByte++] = (byte) (byteOrderMark		& 0xff);
			
		}
		
		for (int i = 0; i < length; i++) {
			
			// UNICODE character code
			int character = source.get(i);
			
			// ASCII 범위 8bit사용
			// 0x0 ~ 0x7f까지
			// 1byte 사용
			if (character <= OneByte.CHARACTER_SCOPE) {
				
				dest[countByte++] = (byte) character;
				
			// ASCII 범위를 넘으면 2byte 사용
			// 0x80 ~ 0x7ff까지 
			} else if (character <= TwoBytes.CHARACTER_SCOPE){
				
				// 110xxxxx
				dest[countByte++] = (byte) (((character>>6) & 0xff) + TwoBytes.HEADER);
				
				// 10xxxxxx
				dest[countByte++] = (byte) ((character & 0x3f) + OneByte.BODY);
				
			// 2byte 표현 범위를 넘으면 3byte 사용
			// 0x800 ~ 0xffff까지
			} else if (character <= ThreeBytes.CHARACTER_SCOPE){
				
				// 1110xxxx
				dest[countByte++] = (byte) (((character>>12) & 0xff) + ThreeBytes.HEADER);
				
				// 10xxxxxx
				dest[countByte++] = (byte) (((character>>6) & 0x3f) + OneByte.BODY);
				
				// 10xxxxxx
				dest[countByte++] = (byte) ((character & 0x3f) + OneByte.BODY);
				
			// 3byte 표현 범위를 넘으면 4byte 사용
			// 0x10000 ~ 0x10ffff까지
			} else if (character <= FourBytes.CHARACTER_SCOPE) {
				
				// 11110xxx
				dest[countByte++] = (byte) (((character>>18) & 0xff) + FourBytes.HEADER);
				
				// 10xxxxxx
				dest[countByte++] = (byte) (((character>>12) & 0x3f) + OneByte.BODY);
				
				// 10xxxxxx
				dest[countByte++] = (byte) (((character>>6) & 0x3f) + OneByte.BODY);
				
				// 10xxxxxx
				dest[countByte++] = (byte) ((character & 0x3f) + OneByte.BODY);
				
			}
			
		}
		
		return countByte;
		
	}
	
}
