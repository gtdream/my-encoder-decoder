package base64;

import java.util.HashMap;

public class Base64 {
	
	private static final char[] BASE64_ENCODE_TABLE = {
		'A','B','C','D','E','F','G','H','I','J',
		'K','L','M','N','O','P','Q','R','S','T',
		'U','V','W','X','Y','Z',
		'a','b','c','d','e','f','g','h','i','j',
		'k','l','m','n','o','p','q','r','s','t',
		'u','v','w','x','y','z',
		'0','1','2','3','4','5','6','7','8','9',
		'+','/'};
	
	private static final HashMap<Character, Integer> BASE64_DECODE_TABLE = new HashMap<Character, Integer>();
	
	static {
		for (int i = 0; i < BASE64_ENCODE_TABLE.length; i++) {
			BASE64_DECODE_TABLE.put(BASE64_ENCODE_TABLE[i], new Integer(i));
		}
	}
	
	public static String encode(String original) {
		StringBuffer base64Text = new StringBuffer();
		byte[] originalBytes = original.getBytes();		
		int remainBits = originalBytes.length * Byte.SIZE;
		int originalFetchCount = 0;
		
		System.out.println(original + "'s bit length:" + remainBits);
		
		while (remainBits > 0) {
			int twoFourBitBuffer = 0;
			int numberPullData = 0;
			if (remainBits < 24) {
				numberPullData = remainBits / 8;
			} else {
				numberPullData = 3;
			}
			
			for (int i = 0; i < numberPullData; i++) {
				System.out.print((char)originalBytes[originalFetchCount]);
				twoFourBitBuffer |= originalBytes[originalFetchCount++] << ((3-i) * 8);
			}
			System.out.print("->");
			
			numberPullData++;
			
			for (int i = 0; i < numberPullData; i++) {
				int c = (twoFourBitBuffer >> (26-(6*i)) & 0x3F);
				System.out.print(BASE64_ENCODE_TABLE[c]);
				base64Text.append(BASE64_ENCODE_TABLE[c]);
			}
			for (int i = 0; i < 4-numberPullData; i++) {
				base64Text.append('=');
			}
			System.out.println("\nbase64 text:" + base64Text.toString());
			remainBits -= numberPullData * 6;
		}
		
		return base64Text.toString();
	}
	
	public static String decode(String base64Text) {
		StringBuffer originalText = new StringBuffer();
		byte[] base64Bytes = base64Text.getBytes();
		int countFetchByte = 0;
		while (countFetchByte < base64Bytes.length) {
			int twoFourBitBuffer = 0;
			int characterFetchCount = 0;
			for (int i = 0; i < 4; i++) {
				int oneByte = base64Bytes[countFetchByte++];
				if ('=' != oneByte) {
					char c = (char)oneByte;
					int encodeTableIndex = BASE64_DECODE_TABLE.get(c);
					twoFourBitBuffer |= (encodeTableIndex & 0x3F) << 26-(i*6);
					System.out.print(BASE64_ENCODE_TABLE[encodeTableIndex]);
					characterFetchCount++;
				} else {
					break;
				}
			}
			System.out.print("->");
			characterFetchCount--;
			for (int  i = 0; i < characterFetchCount; i++) {
				char c = (char) ((twoFourBitBuffer >> 24 - (i*8)) & 0xFF);
				System.out.print(c);
				originalText.append(c);
			}
			System.out.println("\noriginal text:"+originalText.toString());
		}
		return originalText.toString();
	}
}
