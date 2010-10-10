package unicode.utf8;

import java.io.BufferedInputStream;
import java.io.IOException;

import unicode.Encoder;
import unicode.EncodingType;
import unicode.Utilities;

public class UTF8 extends Encoder{
	private final static int BUFFER_SIZE = 8192;
	
	private final static int LEVEL1 = 0;
	private final static int LEVEL2 = 0xC080;
	private final static int LEVEL3 = 0xE08080;
	private final static int LEVEL4 = 0xF8808080;
	
	public UTF8 (BufferedInputStream bInputStream, EncodingType encodingType) {
		super(encodingType);
		this.bInputStream = bInputStream;
	}
	
	public UTF8 (BufferedInputStream bInputStream) {
		super(EncodingType.UTF8);
		this.bInputStream = bInputStream;
	}
	 

	@Override
	public String encode () throws IOException {
		StringBuilder utf8Text = new StringBuilder(8192);

		// BufferedInputStream의 내부 buffer와 같은 크기로 맞춤
		byte[] buffer = new byte[BUFFER_SIZE];
		
		// 마지막 문자가 미완성일 경우 다음에 읽은 buffer와 연결하기 위하여 사용
		byte[] incompletionCharacterData = null;
		
		while (true) {
			
			int numberReadedBytes = this.bInputStream.read(buffer, 0, BUFFER_SIZE);
			if (-1 == numberReadedBytes) {
				break;
			}
			
			// buffer끝에서 부터 identifier의 index를 찾음
			int lastIdentifierIndex = getLastIdentifierIndex(buffer, numberReadedBytes);
			
			// identifier와 연결되야 할 byte중 몇 byte가 부족한지 계산
			int numberShortageBytes = getNumberShortageBytes(buffer[lastIdentifierIndex], numberReadedBytes - lastIdentifierIndex - 1);
			System.out.println("부족" + numberShortageBytes);
			System.out.println("읽음" + numberReadedBytes);
			System.out.printf("마지막번호 %d %x\n", lastIdentifierIndex, buffer[lastIdentifierIndex]);
			
			// 가장 마지막 identifier index + 1을 해야 buffer에서 identifier 바로 앞에서 루프 종료
			// buffer, 유효 byte개수, 미완성 마지막문자, 부족한 byte개수
			String part = translateUTF8(buffer, lastIdentifierIndex, incompletionCharacterData, numberShortageBytes);
//			String part = translateUTF8(buffer, numberReadedBytes, null, numberShortageBytes);
			utf8Text.append(part);
			System.out.println(part);
			System.out.println("====================================================================================================================================================");
			
			// 앞에서 읽은 내용중 미완성 문자만 byte배열에 저장
			if (numberReadedBytes-1 != lastIdentifierIndex) {
				incompletionCharacterData = getIncompletionCharacterData(buffer, numberReadedBytes, lastIdentifierIndex);
			}
		}

		return utf8Text.toString();
	}

	private int getNumberShortageBytes(byte identifier, int numberNextBytes) {
		int count = 0;
		
		if ((identifier & TwoByte.MASK) == TwoByte.IDENTIFIER) {
			count = TwoByte.NUMBER_PART - numberNextBytes;
		} else if ((identifier & ThreeByte.MASK) == ThreeByte.IDENTIFIER) {
			count = ThreeByte.NUMBER_PART - numberNextBytes;
		} else if ((identifier & FourByte.MASK) == FourByte.IDENTIFIER) {
			count = FourByte.NUMBER_PART - numberNextBytes;
		}
		
		System.out.printf("\nidentifier %x %d\n", identifier, count);
		
		return count;
	}
	
	private byte[] getIncompletionCharacterData(byte[] buffer, int numberReadedBytes, int identifierIndex) {

		int numberSubPart = numberReadedBytes - identifierIndex - 1;
		
		byte[] incompletionCharacterData = new byte[numberSubPart];
		
		for (int i = 0; i < numberSubPart; i++) {
			incompletionCharacterData[i] = buffer[identifierIndex+i+1];
			System.out.printf("뒷부분\n%x\n", incompletionCharacterData[i]);
		}
		
		return incompletionCharacterData;
	}
	
	private int getLastIdentifierIndex (byte[] buffer, int numberReadedBytes) {
		
		int index = numberReadedBytes - 1;
		
		while (true) {
			
			byte currentByte = buffer[index];
			
			if ((currentByte & OneByte.MASK) == OneByte.IDENTIFIER) {
				break;
			} else if ((currentByte & TwoByte.MASK) == TwoByte.IDENTIFIER) {
				break;
			} else if ((currentByte & FourByte.MASK) == FourByte.IDENTIFIER) {
				break;
			} else if ((currentByte & ThreeByte.MASK) == ThreeByte.IDENTIFIER) {
				break;
			}
			index--;
		}
		
		Utilities.printBinary(buffer[index]);
		System.out.println(index);
		return index;
	}

	private String translateUTF8 (byte[] buffer, int length, byte[] incompletionCharacterData, int numberShortageBytes) {
		StringBuilder text = new StringBuilder(8192);
		int count = 0;
		int character = 0;
		
		// identifier가 맨 앞이므로 이번에 읽어온 buffer에서 
		// shortage는 무조건 0 ~ 3 
		
		if (incompletionCharacterData != null) {
			character = (incompletionCharacterData[0] & 0xe0) << 12;
			System.out.printf("%x", character);
			for (int i = 1; i < incompletionCharacterData.length; i++) {
				character |= (incompletionCharacterData[i] & 0x3f) << (6 * (incompletionCharacterData.length));
			}
			
			for (int i = 0; i < numberShortageBytes; i++) {
				
				byte part = (byte) (buffer[count++] & 0x3f);
				character |= part << (6 * (numberShortageBytes - i));
				
			}
			System.out.println("찌꺼기" + (char)character);
			text.append((char)character);
		}
		
		while (count < length) {
			
			character = 0;
			byte currentByte = buffer[count];
			
			if ((currentByte & OneByte.MASK) == OneByte.IDENTIFIER) {
				// 첫bit가 0이면 ascii 문자
				character = currentByte;
			} else if ((currentByte & FourByte.MASK) == FourByte.IDENTIFIER) {
				//맨앞 byte개수를 알리는 5bit제거후 남은 3bit를 뒤에서 21번째 bit부터 덮어씌움
				// 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
				// 00011122 22223333 33444444
				character  = (buffer[count++]  &  0x07) << 18;
				character |= (buffer[count++]  &  0x3f) << 12;
				character |= (buffer[count++]  &  0x3f) << 6;
				character |= (buffer[count]    &  0x3f);
//				System.out.printf("\n\n%x\n\n", character);
			} else if ((currentByte & ThreeByte.MASK) == ThreeByte.IDENTIFIER) {
				//맨앞 byte개수를 알리는 4bit제거후 남은 4bit를 뒤에서 16번 bit에 덮어씌움
				// 1110XXXX 10XXXXXX 10XXXXXX
				// 11112222 22333333
				character  = (buffer[count++]  &  0x0f) << 12;
				character |= (buffer[count++]  &  0x3f) << 6;
				character |= (buffer[count]    &  0x3f);
			} else if ((currentByte & TwoByte.MASK) == TwoByte.IDENTIFIER) {
				//맨앞 byte개수를 알리는 3bit제거후 남은 5bit를 뒤에서 11번째 bit부터 덮어씌움
				// 110XXXXX 10XXXXXX
				// 00000111 11222222
				character  = (buffer[count++]  &  0x1f) << 11;
				character |= (buffer[count]    &  0x3f);
			}
			
			// char의 범위로는 BMP범위 문자만 사용 가능하므로
			// 더 큰 범위를 갖는 int를 사용해 주소를 지정하여 넣어줌
			text.appendCodePoint(character);
			count++;
		}
		
		return text.toString();
	}
	
}