package unicode;

import java.io.BufferedInputStream;
import java.io.IOException;


public abstract class Transformer {
	
	protected int bufferSize = 8192;
	protected BufferedInputStream bInputStream;
	private final TransformationType encodingType;
	
	public Transformer (TransformationType encodingType) {
		this.encodingType = encodingType;
	}
	
	public TransformationType getTransformationType() {
		return this.encodingType;
	}
	
	public abstract String encode() throws IOException;
	
	public abstract String decode() throws IOException;
	
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
