package unicode.util;

import java.io.File;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

public class Utilities {
	
	public static File generateFilePathAndName (String destFileName) {
		
		int numberPadding = 1;
		File destFile = new File(destFileName);
		String finalFileName = destFile.getPath();
		
		// 목표 file 과 같은 이름의 file 의 존재 여부를 확인한다
		// 같은 이름이 있을 때마다 'x' 를 늘려가며 재확인한다
		while (true) {
			
			if (!destFile.exists()) {
				
				break;
				
			} else {

				StringBuilder extraName = new StringBuilder();
				finalFileName = destFileName.substring(0, destFileName.lastIndexOf('.')) + "_";
				extraName.append(finalFileName);
				
				for (int i = 0; i < numberPadding; i++) {
					extraName.append('x');
				}
				
				extraName.append(".txt");
				
				destFile = new File(extraName.toString());

				finalFileName = extraName.toString();
				
				numberPadding++;
				
			}
			
		}
		
		return new File(finalFileName);
		
	}
	
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
	}
	
	public static void printBinary (char value) {
		for (int i = 0; i < Character.SIZE; i+=Byte.SIZE) {
			for (int j = 0; j < Byte.SIZE; j++) {
				System.out.print((value >> ((Character.SIZE-1) - (j+i))) & 1);
			}
			if (Character.SIZE-Byte.SIZE != i)
				System.out.print(' ');
		}
	}
	
	public static void printBinary (byte value) {
		for (int i = 0; i < Byte.SIZE; i+=Byte.SIZE) {
			for (int j = 0; j < Byte.SIZE; j++) {
				System.out.print((value >> ((Byte.SIZE-1) - (j+i))) & 1);
			}
			if (Byte.SIZE-Byte.SIZE != i)
				System.out.print(' ');
		}
	}
	
}
