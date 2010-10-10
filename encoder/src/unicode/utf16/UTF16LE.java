package unicode.utf16;

import java.io.BufferedInputStream;
import java.io.IOException;

import unicode.Encoder;
import unicode.EncodingType;

public class UTF16LE extends Encoder{
	
	public UTF16LE (BufferedInputStream bInputStream) {
		super(EncodingType.UTF16LE);
		this.bInputStream = bInputStream;
	}

	@Override
	public String encode() throws IOException {
		StringBuilder utf16Text = new StringBuilder();
		return utf16Text.toString();
	}
}
