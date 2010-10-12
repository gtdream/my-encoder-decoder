package unicode;

import java.util.ArrayList;


public abstract class Encoder {
	
	public abstract int encode (ArrayList<Integer> source, byte[] dest);
	
}
