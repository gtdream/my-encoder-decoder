package unicode.utf8;

import java.io.BufferedInputStream;
import java.io.IOException;

import unicode.Transformer;
import unicode.TransformationType;

public class UTF8 extends Transformer{

	private final static int BUFFER_SIZE = 8192;
	
	public UTF8 (BufferedInputStream bInputStream, TransformationType encodingType) {
		
		super(encodingType);
		this.bInputStream = bInputStream;
		
	}
	
	public UTF8 (BufferedInputStream bInputStream) {
		
		super(TransformationType.UTF8);
		this.bInputStream = bInputStream;
		
	}
	 

	@Override
	public String decode () throws IOException {
		StringBuilder utf8Text = new StringBuilder(BUFFER_SIZE);

		// BufferedInputStream의 내부 buffer 와 같은 크기로 맞춤
		byte[] buffer = new byte[BUFFER_SIZE];
		
		int beforeShortageBytes = 0;
		int incompletionData = 0;
		
//		BufferedInputStream bis = new BufferedInputStream(System.in);
		
		while (true) {
			
			int numberReadedBytes = this.bInputStream.read(buffer, 0, BUFFER_SIZE);
			
			if (-1 == numberReadedBytes) {
				break;
			}
			
			// 가장 마지막에 있는 identifier 의 index 를 찾음
			int lastIdentifierIndex = getLastIdentifierIndex(buffer, numberReadedBytes);
			
			// 마지막 identifier 뒤에 있는 byte 개수를 구함
			// index 로 개수를 계산하기위해 buffer 에 읽힌 총개수에서 1을 뺌
			int numberRearBytes = (numberReadedBytes-1) - lastIdentifierIndex;
			
			// identifier 가 ASCII 문자일경우 포함, 2byte이상 문자일 경우 제외
			// ASCII일 경우 index 가 identifier 를 포함하고
			// 아닐 경우 identifier 앞에서 멈춰야 한다
			// 우선 identifier 가 1byte 문자로 가정한다
			int validIndex = lastIdentifierIndex + 1;
			int numberShortageBytes = 0;
			
			// identifier 가 1byte 문자가 아니라면
			if ((buffer[lastIdentifierIndex] & OneByte.MASK) != OneByte.IDENTIFIER) {
				
				// 읽어온 buffer data 에서
				// 마지막 identifier 와 연결되야 할 byte 중 몇 byte 가 부족한지 계산
				numberShortageBytes = getNumberShortageBytes(buffer[lastIdentifierIndex], numberRearBytes);
				
				// identifier 의 바로 앞에서 끝나야 하므로 
				validIndex = lastIdentifierIndex;
				
			}
			
			// 만약 이전 buffer data 에서 미완성 문자가 있었으면 
			// 이번에 읽은 buffer data 로 완성할 수 있도록
			// 미완성 문자와 부족했던 byte 개수를 넣음
			// 읽은 data, 변환에 사용할 byte 수, 이전 buffer 의 미완성 문자 data , 부족했던 byte 개수 
			String part = translateUTF8(buffer, validIndex, incompletionData, beforeShortageBytes);
			
			// 다음에 읽은 buffer data 를 처리할 때 이번 buffer data 에서
			// 몇 byte 부족했는 지 알려주기 위해 저장
			beforeShortageBytes = numberShortageBytes;
			
			// 앞에서 읽은 내용중 미완성 문자만 byte 배열에 저장
			// buffer 의 마지막 index 와 마지막에 발견한 identifier 의 index 가 같지 않으면
			// 완성되지 못한 문자가 있으므로 따로 저장한다
			// 다음에 오는 buffer 에서 부족한 부분을 채우기 위해서 
			if (numberReadedBytes-1 != lastIdentifierIndex) {

				// 마지막 문자가 미완성일 경우 다음에 읽은 buffer 와 연결하기 위하여 사용
				incompletionData = getIncompletionCharacterData(buffer, lastIdentifierIndex, numberRearBytes);
				
			}
			
			// 바로 출력
			System.out.print(part);
			
			// 한번에 출력하기위해 모음
//			utf8Text.append(part);
			
//			bis.read();
			
		}

//		bis.close();
		
		return utf8Text.toString();
	}

	/*
	 * buffer data 에서 마지막 문자가 2byte 이상으로 구성되는 문자라면
	 * 마지막 문자를 구성하기 위해 필요한 byte 가
	 * 몇 byte 부족한지 계산
	 * 마지막 문자의 identifier 로 몇 byte 구성 문자인지 검사
	 * 문자 구성 byte 개수에서
	 * 마지막 문자의 identifier index 다음부터 남아있는 byte 개수를 빼면
	 * 몇 byte 가 더 필요한지 알 수 있음
	 */
	private int getNumberShortageBytes(byte identifier, int numberBackBytes) {
		
		int count = 0;
		
		if ((identifier & TwoBytes.MASK) == TwoBytes.IDENTIFIER) {
			
			count = TwoBytes.NUMBER_PART - numberBackBytes;
			
		} else if ((identifier & ThreeBytes.MASK) == ThreeBytes.IDENTIFIER) {
			
			count = ThreeBytes.NUMBER_PART - numberBackBytes;
			
		} else if ((identifier & FourBytes.MASK) == FourBytes.IDENTIFIER) {
			
			count = FourBytes.NUMBER_PART - numberBackBytes;
			
		}
		
		return count;
	}
	
	/*
	 * 미완성 문자를 다음에 읽은 buffer 를 이용해 완성해야 하므로
	 * 현재 있는 data 로 앞부분을 만들어서 return
	 * 뒷 부분은 다음에 읽은 buffer 에서 채움
	 */
	private int getIncompletionCharacterData(byte[] buffer, int identifierIndex, int numberBackBytes) {

		int incompletionData = 0;
		int numberParts = 0;
		byte identifier = buffer[identifierIndex];
		
		// identifier 를 검사해 몇 byte 로 구성된 문자인지 알아냄
		// identifier 제외 몇 byte 필요한지 numberParts 에 저장
		if ((identifier & TwoBytes.MASK) == TwoBytes.IDENTIFIER) {
			
			incompletionData = (identifier  &  0x1f) << 11;
			numberParts = TwoBytes.NUMBER_PART;
			
		} else if ((identifier & ThreeBytes.MASK) == ThreeBytes.IDENTIFIER) {
			
			incompletionData = (identifier  &  0x0f) << 12;
			numberParts =ThreeBytes.NUMBER_PART;
			
		} else if ((identifier & FourBytes.MASK) == FourBytes.IDENTIFIER) {
			
			incompletionData = (identifier  &  0x07) << 18;
			numberParts = FourBytes.NUMBER_PART;
			
		}
		
		// 이번에 읽은 buffer 에서 미완성 문자 identifier 뒤에 있는
		//  byte 개수 만큼 이어 붙임
		for (int i = 0; i < numberBackBytes; i++) {
			
			incompletionData |= (buffer[identifierIndex+(i+1)] & 0x3f) << (6 * (numberParts - (i+1)));

		}
		
		return incompletionData;
	}
	
	/*
	 * buffer data 에서 가장 마지막 identifier index 를 찾아냄
	 * identifier 뒤에 필요한 byte 의 개수가 정확하게 있다면
	 * 가장 마지막 index 를 return
	 */
	private int getLastIdentifierIndex (byte[] buffer, int numberReadedBytes) {
		
		int index = numberReadedBytes - 1;
		
		while (true) {
			
			byte currentByte = buffer[index];
			
			if ((currentByte & OneByte.MASK) == OneByte.IDENTIFIER) {
				
				break;
				
			} else if ((currentByte & TwoBytes.MASK) == TwoBytes.IDENTIFIER) {
				
				// 마지막에 발견한 2byte identifier 뒤에 1byte가 있으면
				// 마지막에 identifier 가 있는 것으로 처리 
				int remainder = (numberReadedBytes - 1) - index ;
				if (remainder == 1) {
					index = numberReadedBytes - 1;
				}
				
				break;
				
			} else if ((currentByte & ThreeBytes.MASK) == ThreeBytes.IDENTIFIER) {
				
				// 마지막에 발견한 3byte identifier 뒤에 2byte가 있으면
				// 마지막에 identifier 가 있는 것으로 처리
				int remainder = (numberReadedBytes - 1) - index ;
				if (remainder == 2) {
					index = numberReadedBytes - 1;
				}
				
				break;
				
			} else if ((currentByte & FourBytes.MASK) == FourBytes.IDENTIFIER) {
				
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

	/*
	 * utf8 encoding 방식인 buffer data 를 UNICODE index 로 변환
	 * 먼저 이전에 읽은 내용 중에 미완성 문자가 있으면
	 * 이번에 읽은 내용에서 사용한 만큼 index 를 증가시키고
	 * 증가시킨 index 부터 buffer 내용을 처리함
	 * 처리한 byte 수를 count 저장
	 * count 가 buffer 의 마지막 index 보다 커지면 종료
	 */
	private String translateUTF8 (byte[] buffer, int endIndex, int incompletionData, int numberShortageBytes) {
		
		StringBuilder text = new StringBuilder(BUFFER_SIZE);
		int count = 0;
		int character = 0;

		// 이전 buffer 에서 부족했던 data 가 있으면
		if (numberShortageBytes > 0) {

			// 이전 buffer 의 미완성 문자
			character = incompletionData;
			
			// 이번 buffer data 에서 미완성 문자에 필요한 나머지 data 를 이어붙임
			for (int i = 0; i < numberShortageBytes; i++) {
			
				// 이번 buffer 에서 꺼낸만큼 제외하기 위해 count 증가시킴
				character |= (buffer[count++] & 0x3f) << (6 * ((numberShortageBytes-1) - i));
				
			}
			
			text.appendCodePoint(character);
			
		}
		
		
		while (count < endIndex) {
			
			character = 0;
			byte currentByte = buffer[count];
			
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
				character  = (buffer[count++]  &  0x07) << 18;
				
				// 00xxxxxx 변환
				// 2영역 data
				character |= (buffer[count++]  &  0x3f) << 12;
				
				// 3영역 data
				character |= (buffer[count++]  &  0x3f) << 6;
				
				// 4영역 data
				character |= (buffer[count]    &  0x3f);
				
			} else if ((currentByte & ThreeBytes.MASK) == ThreeBytes.IDENTIFIER) {
				
				// byte 개수를 알리는 4bit제거후 남은 4bit를 뒤에서 16번째 bit 부터 덮어씌움
				// 1110XXXX 10XXXXXX 10XXXXXX
				// 11112222 22333333
				
				// 0000xxxx 변환
				// 1영역 data 
				character  = (buffer[count++]  &  0x0f) << 12;
				
				// 00xxxxxx 변환
				// 2영역 data
				character |= (buffer[count++]  &  0x3f) << 6;
				
				// 3영역 data 
				character |= (buffer[count]    &  0x3f);
				
			} else if ((currentByte & TwoBytes.MASK) == TwoBytes.IDENTIFIER) {
				
				// byte 개수를 알리는 3bit제거후 남은 5bit를 뒤에서 11번째 bit 부터 덮어씌움
				// 110XXXXX 10XXXXXX
				// 00000111 11222222
				
				// 000xxxxx 변환
				// 1영역 data
				character  = (buffer[count++]  &  0x1f) << 6;
				
				// 00xxxxxx 변환
				// 2영역 data
				character |= (buffer[count]    &  0x3f);
				
			}
			
			// char 의 범위로는 BMP범위 문자까지만 표현가능
			// SMP, SIP 를 사용하기 위해서 integer 로 index 를 직접 넣어줌
			text.appendCodePoint(character);
			count++;
			
		}
		
		return text.toString();
	}
	
}