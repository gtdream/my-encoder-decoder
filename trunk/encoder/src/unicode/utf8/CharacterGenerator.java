package unicode.utf8;

public class CharacterGenerator {
	public void generate (int source, byte[] dest){
		
		int countByte = 0;
		
		// ASCII 범위 8bit사용
		// 0x0 ~ 0x7f까지
		// 1byte 사용
		if (source <= OneByte.CHARACTER_SCOPE) {
			
			dest[countByte++] = (byte) source;
			
		// ASCII 범위를 넘으면 2byte 사용
		// 0x80 ~ 0x7ff까지 
		} else if (source <= TwoBytes.CHARACTER_SCOPE){
			
			// 110xxxxx
			dest[countByte++] = (byte) (((source>>6) & 0xff) + TwoBytes.HEADER);
			
			// 10xxxxxx
			dest[countByte++] = (byte) ((source & 0x3f) + OneByte.BODY);
			
		// 2byte 표현 범위를 넘으면 3byte 사용
		// 0x800 ~ 0xffff까지
		} else if (source <= ThreeBytes.CHARACTER_SCOPE){
			
			// 1110xxxx
			dest[countByte++] = (byte) (((source>>12) & 0xff) + ThreeBytes.HEADER);
			
			// 10xxxxxx
			dest[countByte++] = (byte) (((source>>6) & 0x3f) + OneByte.BODY);
			
			// 10xxxxxx
			dest[countByte++] = (byte) ((source & 0x3f) + OneByte.BODY);
			
		// 3byte 표현 범위를 넘으면 4byte 사용
		// 0x10000 ~ 0x10ffff까지
		} else if (source <= FourBytes.CHARACTER_SCOPE) {
			
			// 11110xxx
			dest[countByte++] = (byte) (((source>>18) & 0xff) + FourBytes.HEADER);
			
			// 10xxxxxx
			dest[countByte++] = (byte) (((source>>12) & 0x3f) + OneByte.BODY);
			
			// 10xxxxxx
			dest[countByte++] = (byte) (((source>>6) & 0x3f) + OneByte.BODY);
			
			// 10xxxxxx
			dest[countByte++] = (byte) ((source & 0x3f) + OneByte.BODY);
			
		}
	}
}
