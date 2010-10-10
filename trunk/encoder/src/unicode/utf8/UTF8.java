package unicode.utf8;

import java.io.BufferedInputStream;
import java.io.IOException;

import unicode.Encoder;
import unicode.EncodingType;

public class UTF8 extends Encoder{
	private final static int BUFFER_SIZE = 8196;
	
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
		StringBuilder utf8Text = new StringBuilder();

		// BufferedInputStream의 내부 buffer와 같은 크기로 맞춤
		byte[] buffer = new byte[BUFFER_SIZE];
		
		// 마지막 문자가 미완성일 경우 다음에 읽은 buffer와 연결하기 위하여 사용
		int incompletionCharacter = 0;
		
		while (true) {
			
			int numberReadedBytes = this.bInputStream.read(buffer, 0, BUFFER_SIZE);
			
			if (-1 == numberReadedBytes) {
				break;
			}
			
			int lastIdentifierIndex = getLastIdentifierIndex(buffer, numberReadedBytes);
			int numberShortageBytes = numberReadedBytes - lastIdentifierIndex - 1;
			int numberValidBytes = lastIdentifierIndex-numberShortageBytes;
			String part = translateUTF8(buffer, numberValidBytes, incompletionCharacter, numberShortageBytes);
			utf8Text.append(part);
			incompletionCharacter = getIncompletionCharacter(buffer, numberReadedBytes, lastIdentifierIndex);
		}

		return utf8Text.toString();
	}

	private int getIncompletionCharacter(byte[] buffer, int numberReadedBytes, int identifierIndex) {

		int numberSubPart = numberReadedBytes - identifierIndex;
		
		int character = 0;
		
		for (int i = 0; i < numberSubPart; i++) {
			character |= buffer[identifierIndex+i] << (24 - (i*8));
		}
		
		return character;
	}
	
	private int getLastIdentifierIndex (byte[] buffer, int numberReadedBytes) {
		
		int index = numberReadedBytes - 1;
		
		while (true) {
			
			byte currentByte = buffer[index];
			
			if ((currentByte & OneByte.MASK) == OneByte.IDENTIFIER) {
				System.out.println("1");
				System.out.printf("%x\n", currentByte & OneByte.MASK);
				break;
			} else if ((currentByte & FourByte.MASK) == FourByte.IDENTIFIER) {
				System.out.println("4");
				System.out.printf("%x\n", currentByte);
				break;
			} else if ((currentByte & ThreeByte.MASK) == ThreeByte.IDENTIFIER) {
				System.out.println("3");
				System.out.printf("%x\n", currentByte);
				break;
			} else if ((currentByte & TwoByte.MASK) == TwoByte.IDENTIFIER) {
				System.out.println("2");
				System.out.printf("%x\n", currentByte);
				break;
			}
			
			index--;
		}
		
		return index;
	}

	private String translateUTF8 (byte[] buffer, int length, int incompletionCharacter, int previousBytes) {
		StringBuilder text = new StringBuilder(8192);
		int count = 0;
		int character = 0;
		
//		for (int i = 0; i < previousBytes; i++) {
//			
//			character |= buffer[count++];
//			
//			
//		}
		while (count < length) {
			
			character = 0;
			byte currentByte = buffer[count];
			
			if ((currentByte & OneByte.MASK) == OneByte.IDENTIFIER) {
				character = currentByte;
//				System.out.print((char)currentByte);
			} else if ((currentByte & FourByte.MASK) == FourByte.IDENTIFIER) {
				character |= (buffer[count++]   &  0x0f) << 13;
				character |= (buffer[count++]   &  0x3f) << 6;
				character |= (buffer[count++]   &  0x3f) << 6;
				character |= (buffer[count]     &  0x3f);
//				System.out.print((char)(character));
			} else if ((currentByte & ThreeByte.MASK) == ThreeByte.IDENTIFIER) {
				character |= (buffer[count++]   &  0x0f) << 12;
				character |= (buffer[count++]   &  0x3f) << 6;
				character |= (buffer[count]     &  0x3f);
//				System.out.print((char)(character));
			} else if ((currentByte & TwoByte.MASK) == TwoByte.IDENTIFIER) {
				character |= (buffer[count++]   &  0x1f) << 11;
				character |= (buffer[count]     &  0x3f);
//				System.out.print((char)(character));
			}
			text.append((char)character);
			count++;
		}
		
		return text.toString();
	}
	
}