package unicode.utf16;

import java.io.BufferedInputStream;
import java.io.IOException;

import unicode.Transformer;
import unicode.TransformationType;

public class UTF16BE extends Transformer{
	
	public UTF16BE (BufferedInputStream bInputStream) {
		super(TransformationType.UTF16BE);
		this.bInputStream = bInputStream;
	}

	@Override
	public String decode() throws IOException {
		StringBuilder utf16Text = new StringBuilder();
		return utf16Text.toString();
	}
}
