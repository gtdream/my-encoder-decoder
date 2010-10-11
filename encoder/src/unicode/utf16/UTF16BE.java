package unicode.utf16;

import java.io.BufferedInputStream;
import java.io.IOException;

import unicode.TransformationType;
import unicode.Transformer;

public class UTF16BE extends Transformer{
	
	public UTF16BE (BufferedInputStream bInputStream) {
		super(TransformationType.UTF16BE);
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
		return utf16Text.toString();
	}
}
