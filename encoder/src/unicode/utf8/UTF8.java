package unicode.utf8;

import java.io.BufferedInputStream;
import java.io.IOException;

import unicode.Encoder;
import unicode.EncodingType;
import unicode.Utilities;

public class UTF8 extends Encoder{

	private final static int BUFFER_SIZE = 8192;
	
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
		StringBuilder utf8Text = new StringBuilder(BUFFER_SIZE);

		// BufferedInputStream의 내부 buffer 와 같은 크기로 맞춤
		byte[] buffer = new byte[BUFFER_SIZE];
		
		int beforeShortageBytes = 0;
		int incompletionData = 0;
		
		while (true) {
			
			int numberReadedBytes = this.bInputStream.read(buffer, 0, BUFFER_SIZE);
			if (-1 == numberReadedBytes) {
				break;
			}
			
			// 가장 마지막에 있는 identifier 의 index 를 찾음
			int lastIdentifierIndex = getLastIdentifierIndex(buffer, numberReadedBytes);
			
			// 마지막 identifier 뒤에 있는 byte 개수를 구함
			// index 로 개수를 계산하기위해 buffer 에 읽힌 개수에서 1을 뺌
			int numberBackBytes = (numberReadedBytes-1) - lastIdentifierIndex;
			
			// identifier 와 연결되야 할 byte 중 몇 byte 가 부족한지 계산
			int numberShortageBytes = getNumberShortageBytes(buffer[lastIdentifierIndex], numberBackBytes);
			
			// identifier 가 ASCII 문자일경우 포함, 2byte이상 문자일 경우 제외
			// ASCII일 경우 index 가 identifier 를 포함하고
			// 아닐 경우 identifier 앞에서 멈춰야 한다
			int numberValidBytes = ((buffer[lastIdentifierIndex] & OneByte.MASK) == OneByte.IDENTIFIER) ? lastIdentifierIndex+1 : lastIdentifierIndex;
			
			// 유효범위까지 변환
			// 만약 이전 buffer data 에서 잘린 문자가 있으면 넣어서 맨앞에 붙여준다
			// 잘린 문자 data 와 부족했던 byte 개수 
			String part = translateUTF8(buffer, numberValidBytes, incompletionData, beforeShortageBytes);
			
			beforeShortageBytes = numberShortageBytes;
			
			// 앞에서 읽은 내용중 미완성 문자만 byte 배열에 저장
			// buffer 의 마지막 index 와 마지막에 발견한 identifier 의 index 가 같지 않으면
			// 완성되지 못한 문자가 있으므로 따로 저장한다
			// 다음에 오는 buffer 에서 부족한 부분을 채우기 위해서 
			if (numberReadedBytes-1 != lastIdentifierIndex) {

				// 마지막 문자가 미완성일 경우 다음에 읽은 buffer 와 연결하기 위하여 사용
				incompletionData = getIncompletionCharacterData(buffer, lastIdentifierIndex, numberBackBytes);
				
			}
			
			// 바로 출력 하거나
//			System.out.print(part);
			
			// 모아서 한번에 출력하거나
			utf8Text.append(part);
			
			System.out.println(part);
			System.out.println("====================================================================================================================================================");
		}

		return utf8Text.toString();
	}

	private int getNumberShortageBytes(byte identifier, int numberBackBytes) {
		
		int count = 0;
		
		if ((identifier & TwoByte.MASK) == TwoByte.IDENTIFIER) {
			
			count = TwoByte.NUMBER_PART - numberBackBytes;
			
		} else if ((identifier & ThreeByte.MASK) == ThreeByte.IDENTIFIER) {
			
			count = ThreeByte.NUMBER_PART - numberBackBytes;
			
		} else if ((identifier & FourByte.MASK) == FourByte.IDENTIFIER) {
			
			count = FourByte.NUMBER_PART - numberBackBytes;
			
		}
		
		return count;
	}
	
	private int getIncompletionCharacterData(byte[] buffer, int identifierIndex, int numberBackBytes) {

		int incompletionData = 0;
		int identifier = buffer[identifierIndex];
		
		if ((identifier & TwoByte.MASK) == TwoByte.IDENTIFIER) {
			
			incompletionData = (identifier  &  0x1f) << 11;
			
		} else if ((identifier & ThreeByte.MASK) == ThreeByte.IDENTIFIER) {
			
			incompletionData = (identifier  &  0x0f) << 12;
			
		} else if ((identifier & FourByte.MASK) == FourByte.IDENTIFIER) {
			
			incompletionData = (identifier  &  0x07) << 18;
			
		}
		
		System.out.println("aaaaaaaaaaaaa");
		Utilities.printBinary(incompletionData);
		System.out.println("aaaaaaaaaaaaa");
		
		for (int i = 1; i < numberBackBytes+1; i++) {
			
			incompletionData |= (buffer[identifierIndex+i] & 0x3f) << (6 * i);
			
		}
		
		return incompletionData;
	}
	
	private int getLastIdentifierIndex (byte[] buffer, int numberReadedBytes) {
		
		int index = numberReadedBytes - 1;
		
		while (true) {
			
			byte currentByte = buffer[index];
			
			if ((currentByte & OneByte.MASK) == OneByte.IDENTIFIER) {
				
				break;
				
			} else if ((currentByte & TwoByte.MASK) == TwoByte.IDENTIFIER) {
				
				// 마지막에 발견한 2byte identifier 뒤에 1byte가 있으면
				// 마지막에 identifier 가 있는 것으로 처리 
				int remainder = (numberReadedBytes - 1) - index ;
				if (remainder == 1) {
					index = numberReadedBytes - 1;
				}
				
				break;
				
			} else if ((currentByte & ThreeByte.MASK) == ThreeByte.IDENTIFIER) {
				
				// 마지막에 발견한 3byte identifier 뒤에 2byte가 있으면
				// 마지막에 identifier 가 있는 것으로 처리
				int remainder = (numberReadedBytes - 1) - index ;
				if (remainder == 2) {
					index = numberReadedBytes - 1;
				}
				
				break;
				
			} else if ((currentByte & FourByte.MASK) == FourByte.IDENTIFIER) {
				
				// 마지막에 발견한 3byte identifier 뒤에 3byte가 있으면
				// 마지막에 identifier 가 있는 것으로 처리
				int remainder = (numberReadedBytes - 1) - index ;
				if (remainder == 3) {
					index = numberReadedBytes - 1;
				}
				
				break;
				
			} 
			
			index--;
			
		}
		
		return index;
	}

	private String translateUTF8 (byte[] buffer, int length, int incompletionData, int numberShortageBytes) {
		
		StringBuilder text = new StringBuilder(BUFFER_SIZE);
		int count = 0;
		int character = 0;

//		System.out.println(numberShortageBytes);
//		Utilities.printBinary(incompletionData);
		if (numberShortageBytes > 0) {
			
			character = incompletionData;
			
			for (int i = 0; i < numberShortageBytes; i++) {
			
				byte part = buffer[count++];
				character |= (part & 0x3f) << (6 * ((numberShortageBytes-1) - i));
				
			}
			
			text.appendCodePoint(character);
		}
		
		
		while (count < length) {
			
			character = 0;
			byte currentByte = buffer[count];
			
			if ((currentByte & OneByte.MASK) == OneByte.IDENTIFIER) {
				
				// 첫 bit 가 0이면 ascii 문자
				character = currentByte;
				
			} else if ((currentByte & FourByte.MASK) == FourByte.IDENTIFIER) {
				
				//맨앞 byte 개수를 알리는 5bit제거후 남은 3bit를 뒤에서 21번째 bit부터 덮어씌움
				// 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
				// 00011122 22223333 33444444
				character  = (buffer[count++]  &  0x07) << 18;
				character |= (buffer[count++]  &  0x3f) << 12;
				character |= (buffer[count++]  &  0x3f) << 6;
				character |= (buffer[count]    &  0x3f);
				
			} else if ((currentByte & ThreeByte.MASK) == ThreeByte.IDENTIFIER) {
				
				//맨앞 byte 개수를 알리는 4bit제거후 남은 4bit를 뒤에서 16번 bit에 덮어씌움
				// 1110XXXX 10XXXXXX 10XXXXXX
				// 11112222 22333333
				character  = (buffer[count++]  &  0x0f) << 12;
				character |= (buffer[count++]  &  0x3f) << 6;
				character |= (buffer[count]    &  0x3f);
				
			} else if ((currentByte & TwoByte.MASK) == TwoByte.IDENTIFIER) {
				
				//맨앞 byte 개수를 알리는 3bit제거후 남은 5bit를 뒤에서 11번째 bit부터 덮어씌움
				// 110XXXXX 10XXXXXX
				// 00000111 11222222
				character  = (buffer[count++]  &  0x1f) << 11;
				character |= (buffer[count]    &  0x3f);
				
			}
			
			// char 의 범위로는 BMP범위 문자만 사용 가능하므로
			// 더 큰 범위를 갖는 integer 를 사용해 주소를 지정하여 넣어줌
			text.appendCodePoint(character);
			count++;
		}
		
		return text.toString();
	}
	
}