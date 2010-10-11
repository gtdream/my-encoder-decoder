package unicode.utf16;

import java.io.BufferedInputStream;
import java.io.IOException;

import unicode.TransformationType;
import unicode.Transformer;

public class UTF16LE extends Transformer{
	
	public UTF16LE (BufferedInputStream bInputStream) {
		super(TransformationType.UTF16LE);
		this.bInputStream = bInputStream;
	}
	
	@Override
	public String encode() throws IOException {
		
		StringBuilder utf8Text = new StringBuilder();
		
		
		
		return utf8Text.toString();
		
	}

	@Override
	public String decode() throws IOException {
		StringBuilder utf16Text = new StringBuilder();
		
		while (true) {
			
			byte[] buffer = new byte[8192];
			
			int numberReadedBytes = this.bInputStream.read(buffer);
			
			if (-1 == numberReadedBytes) {
				break;
			}
			
			String part = translateUTF16LE(buffer, numberReadedBytes);
			
			System.out.print(part);
			
		}
		
		return utf16Text.toString();
	}
	
	private String translateUTF16LE(byte[] buffer, int numberReadedBytes) {
		StringBuilder text = new StringBuilder();
		
		int count = 0;
		
		while (count < numberReadedBytes) {
			
			int character = buffer[count++] & 0x000000ff;
			character |= (buffer[count++] << 8) & 0x0000ff00;
			
//			System.out.print((char)character);
			text.append((char)character);
			
		}
		
		return text.toString();
	}
}
