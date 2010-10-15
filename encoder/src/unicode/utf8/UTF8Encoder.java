package unicode.utf8;

import java.util.ArrayList;

import unicode.ByteOrderMark;
import unicode.Encoder;

public class UTF8Encoder extends Encoder{
	
	private final int byteOrderMark;
	private final CharacterGenerator generator;
	
	public UTF8Encoder(int byteOrderMark) {
		
		this.byteOrderMark = byteOrderMark;
		generator = new CharacterGenerator();
		
	}
	
	@Override
	public int encode (ArrayList<Integer> source, byte[] dest) {
		
		int countByte = 0;
		
		// source 의 전체크기는 곧 UNICODE character 의 개수
		int length = source.size();
		
		// BOM 있는 UTF-8 encoding 일 경우 BOM 생성 
		if (byteOrderMark != ByteOrderMark.EMPTY) {
			
			dest[countByte++] = (byte) ((byteOrderMark>>16)	& 0xff);
			dest[countByte++] = (byte) ((byteOrderMark>>8)	& 0xff);
			dest[countByte++] = (byte) (byteOrderMark		& 0xff);
			
		}
		
		for (int i = 0; i < length; i++) {
			
			// UNICODE character code
			int character = source.get(i);
			
			generator.generate(character, dest);
			
		}
		
		return countByte;
		
	}
	
}
