package unicode.utf8;

import java.util.ArrayList;

import unicode.ByteOrderMark;
import unicode.Decoder;

public class UTF8Decoder extends Decoder{
	
	@Override
	public ArrayList<Integer> decode (byte[] source, int readedBytes) {
		
		ArrayList<Integer> unicode = new ArrayList<Integer>();
		int count = 0;
		int character = 0;
		
		// source 가 3byte 이상이라면 BOM 검사
		if (source.length > 2) {
			
			int bom = 0;
			
			bom  = (source[0]<<16)	& 0x00ff0000; // 3번째 byte masking
			bom |= (source[1]<<8)	& 0x0000ff00; // 2번재 byte masking
			bom |= (source[2])		& 0x000000ff; // 1번째 byte masking
			
			// 최선두 3byte 가 UTF-8 bom 이라면
			// 유효한 data 부터 decoding 되도록
			// 시작 index 증가
			if (bom == ByteOrderMark.UTF8) {
				count = 3;
			}
		}
		
		while (count < readedBytes) {

			character = 0;
			byte currentByte = source[count];

			if ((currentByte & OneByte.MASK) == OneByte.IDENTIFIER) {

				// 그대로 덮어 씌움
				// 0xxxxxxx
				character = currentByte;
				
			} else if ((currentByte & FourBytes.MASK) == FourBytes.IDENTIFIER) {

				// byte 개수를 알리는 앞부분 5bit제거후 남은 3bit를 뒤에서 21번째 bit 부터 덮어씌움
				// 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
				// 00011122 22223333 33444444

				// 00000xxx 변환
				// 1영역 data
				character = (source[count++] & 0x07) << 18;

				// 00xxxxxx 변환
				// 2영역 data
				character |= (source[count++] & 0x3f) << 12;

				// 3영역 data
				character |= (source[count++] & 0x3f) << 6;

				// 4영역 data
				character |= (source[count] & 0x3f);
				

			} else if ((currentByte & ThreeBytes.MASK) == ThreeBytes.IDENTIFIER) {

				// byte 개수를 알리는 4bit제거후 남은 4bit를 뒤에서 16번째 bit 부터 덮어씌움
				// 1110XXXX 10XXXXXX 10XXXXXX
				// 11112222 22333333

				// 0000xxxx 변환
				// 1영역 data
				character = (source[count++] & 0x0f) << 12;

				// 00xxxxxx 변환
				// 2영역 data
				character |= (source[count++] & 0x3f) << 6;

				// 3영역 data
				character |= (source[count] & 0x3f);
				
			} else if ((currentByte & TwoBytes.MASK) == TwoBytes.IDENTIFIER) {

				// byte 개수를 알리는 3bit제거후 남은 5bit를 뒤에서 11번째 bit 부터 덮어씌움
				// 110XXXXX 10XXXXXX
				// 00000111 11222222

				// 000xxxxx 변환
				// 1영역 data
				character = (source[count++] & 0x1f) << 6;

				// 00xxxxxx 변환
				// 2영역 data
				character |= (source[count] & 0x3f);

			}

			// char 의 범위로는 BMP범위 문자까지만 표현가능
			// SMP, SIP 를 사용하기 위해서 integer 로 index 를 직접 넣어줌
			unicode.add(character);
			count++;
			
		}
		
		return unicode;
		
	}
	
}
