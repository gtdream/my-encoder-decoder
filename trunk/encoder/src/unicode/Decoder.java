package unicode;

import java.util.ArrayList;

public abstract class Decoder {
	
	public abstract ArrayList<Integer> decode (byte[] source, int readedBytes);
	
}
