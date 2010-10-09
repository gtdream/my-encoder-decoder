package unicode;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import unicode.type.EncodingType;

class UTF8 {
	private final static int BUFFER_SIZE = 8196;

	private final static int USING_1BYTE = 0;
	private final static int USING_2BYTE = 0xC0;
	private final static int USING_3BYTE = 0xE0;
	private final static int USING_4BYTE = 0xF0;
	
	private final static int LEVEL1 = 0;
	private final static int LEVEL2 = 0xC080;
	private final static int LEVEL3 = 0xE08080;
	private final static int LEVEL4 = 0xF8808080;

	private final static int BOM_UTF8_MASK 		= 0xEFBBBF;
	private final static int BOM_UTF16LE_MASK	= 0xFFFE;
	private final static int BOM_UTF16BE_MASK 	= 0xFEFF;
	private final static int BOM_UTF32LE_MASK	= 0x0000FEFF;
	private final static int BOM_UTF32BE_MASK 	= 0xFFFE0000;
	
	public UTF8 () {
		
	}

	public String encode (String filePath) {
		File file = new File(filePath);
		FileInputStream fileStream = null;
		BufferedInputStream b = null;
		
		StringBuilder utf8Text = new StringBuilder();
		
		try {
			fileStream = new FileInputStream(file);

			// default buffer size 8192byte
			b = new BufferedInputStream(fileStream);

			// BufferedInputStream의 내부 buffer와 같은 크기로 맞춤
			byte[] buffer = new byte[BUFFER_SIZE];
			byte[] bom = new byte[4];
			
			//bom를 읽고 이동되는 pointer를 처음으로 돌리기 위해 0지정
			b.mark(0);
			
			//file에서 4byte만 읽어들임
			//1~4 byte data밖에 내용이 없어도 뒷부분은 0이기 때문에 상관없음
			b.read(bom);
			
			//encoding type 지정
			EncodingType encodingType = getEncoding(bom);
			
			//stream pointer 0으로 이동
			b.reset();
			while (true) {
				int numberReadedByte = b.read(buffer, 0, BUFFER_SIZE);
				if (-1 == numberReadedByte) {
					break;
				}
				
				int countNotCompoundByte = 0;
				int lastIdentifierIndex = numberReadedByte - 1;
				while (true) {
					byte currentByte = buffer[lastIdentifierIndex];
					if (isDiscriminationByte(currentByte)) {
						countNotCompoundByte =numberReadedByte - lastIdentifierIndex;
						break;
					}
					lastIdentifierIndex--;
				}
				translateUTF8(buffer, lastIdentifierIndex);
				// builder.append(new String(buffer, 0, numberReadedByte));
			}

		} catch (IOException ie) {
			ie.printStackTrace();
		} finally {
			try {
				if (null != b) {
					b.close();
				}
			} catch (IOException ie) {
				ie.printStackTrace();
			}
		}

		return new String();
	}

	private boolean isDiscriminationByte (byte currentByte) {
		if ((currentByte & USING_4BYTE) == USING_4BYTE) {
			return true;
		} else if ((currentByte & USING_3BYTE) == USING_3BYTE) {
			return true;
		} else if ((currentByte & USING_2BYTE) == USING_2BYTE) {
			return true;
		} else {
			return true;
		}
	}
	
	private EncodingType getEncoding (byte[] buffer) {
		EncodingType encodingType = null;
		int byteOrderMark = 0;

//		for (int i = 0; i < 4; i++) {
//			byteOrderMark  |= (buffer[i] << (Integer.SIZE - (Byte.SIZE * (i+1)))) & (0xff000000 >>> (Byte.SIZE * i));
//		}
//		System.out.printf("%x\n",byteOrderMark);
		
		byteOrderMark  = buffer[0] << 24 & 0xff000000;
		byteOrderMark |= buffer[1] << 16 & 0x00ff0000;
		byteOrderMark |= buffer[2] << 8  & 0x0000ff00;
		byteOrderMark |= buffer[3]       & 0x000000ff;
		
		if (byteOrderMark == BOM_UTF32LE_MASK) {
			encodingType = EncodingType.UTF32LE;
		}else if (byteOrderMark == BOM_UTF32BE_MASK) {
			encodingType = EncodingType.UTF32BE;
		} else if ((byteOrderMark>>8 & 0xffffff) == BOM_UTF8_MASK) {
			encodingType = EncodingType.UTF8BOM;
		} else if ((byteOrderMark>>16 & 0xffff) == BOM_UTF16LE_MASK) {
			encodingType = EncodingType.UTF16LE;
		} else if ((byteOrderMark>>16 & 0xffff) == BOM_UTF16BE_MASK) {
			encodingType = EncodingType.UTF16BE;
		} else {
			encodingType = EncodingType.UTF8;
		}
		
		System.out.printf("This file is opened on %s\n", encodingType);
		
		return encodingType;
	}

	int remainingData;
	int numberLackByte;
	private void translateUTF8 (byte[] buffer, int length) {
		int count = 0;
		while (count < length) {
			byte currentByte = buffer[count];
			
			if ((currentByte & USING_4BYTE) == USING_4BYTE) {
				count+=3;
			} else if ((currentByte & USING_3BYTE) == USING_3BYTE) {
				int character = 0;
				character  = (buffer[count++]   &  0x0f) << 12;
				character |= (buffer[count++]   &  0x3f) << 6;
				character |= (buffer[count]     &  0x3f);
				System.out.print((char)(character));
			} else if ((currentByte & USING_2BYTE) == USING_2BYTE) {
				int character = 0;
				character  = (buffer[count++]   &  0x1f) << 11;
				character |= (buffer[count]     &  0x3f);
				System.out.print((char)(character));
			} else if ((currentByte & USING_1BYTE) == USING_1BYTE) {
				System.out.print((char)currentByte);
			}
			count++;
		}
		System.out.println("===========================================================");
	}
}