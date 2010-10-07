package utf8;

public class Encoder {

	public static void main (String[] args) {
		if (args.length < 1) {
			System.out.println("not enough arguments");
			return;
		}
		
		StringBuffer originalText = new StringBuffer();
		for (int i = 0; i < args.length-1; i++) {
			originalText.append(args[i] + " ");
		}
		originalText.append(args[args.length-1]);
		
	}

}

class UTF8 {
	final static int LEVEL1 = 0;
	final static int LEVEL2 = 0xC080;
	final static int LEVEL3 = 0xE08080;
	final static int LEVEL4 = 0xF8808080;
	final static int BOM_UTF8 = 0xEFBBBF;
	final static int BOM_UTF16LE = 0xFEFF;
	final static int BOM_UTF16BE = 0xFFFE;
	final static int BOM_UTF32LE = 0x0000FEFF;
	final static int BOM_UTF32BE = 0xFFFE0000;
	
	public static String encode(String originalText) {
		StringBuffer utf8Text = new StringBuffer();
		byte[] originalBytes = originalText.getBytes();
		
		
		return utf8Text.toString();
	}
}