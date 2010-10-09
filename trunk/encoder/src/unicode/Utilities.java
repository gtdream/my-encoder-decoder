package unicode;

import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

public class Utilities {
	public static void printSystemInfo () {
		Properties p = System.getProperties();
		Set<Object> keys = p.keySet();
		Iterator<Object> itr = keys.iterator();
		while (itr.hasNext()) {
			String key = (String) itr.next();
			System.out.printf("%s=%s\n", key, System.getProperty(key));
		}
	}

	public static void printBinary (int value) {
		for (int i = 0; i < Integer.SIZE; i+=Byte.SIZE) {
			for (int j = 0; j < Byte.SIZE; j++) {
				System.out.print((value >> ((Integer.SIZE-1) - (j+i))) & 1);
			}
			if (Integer.SIZE-Byte.SIZE != i)
				System.out.print(' ');
		}
		System.out.println();
	}
	
	public static void printBinary (char value) {
		for (int i = 0; i < Character.SIZE; i+=Byte.SIZE) {
			for (int j = 0; j < Byte.SIZE; j++) {
				System.out.print((value >> ((Character.SIZE-1) - (j+i))) & 1);
			}
			if (Character.SIZE-Byte.SIZE != i)
				System.out.print(' ');
		}
		System.out.println();
	}
	
	public static void printBinary (byte value) {
		for (int i = 0; i < Byte.SIZE; i+=Byte.SIZE) {
			for (int j = 0; j < Byte.SIZE; j++) {
				System.out.print((value >> ((Byte.SIZE-1) - (j+i))) & 1);
			}
			if (Byte.SIZE-Byte.SIZE != i)
				System.out.print(' ');
		}
		System.out.println();
	}

}
