package unicode;

import java.io.BufferedInputStream;
import java.io.IOException;


public abstract class Encoder {
	
	protected BufferedInputStream bInputStream;
	private final EncodingType encodingType;
	
	public abstract String encode() throws IOException;
	
	public Encoder (EncodingType encodingType) {
		this.encodingType = encodingType;
	}
	
	public EncodingType getEncodingType() {
		return this.encodingType;
	}
	
	public void close() {
		if (bInputStream != null) {
			try {
				this.bInputStream.close();
			} catch (IOException ie) {
				ie.printStackTrace();
			}
		}
	}
}
