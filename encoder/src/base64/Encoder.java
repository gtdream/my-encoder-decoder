package base64;



public class Encoder {
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("not enough arguments");
			return;
		}
		
		StringBuffer originalText = new StringBuffer();
		for (int i = 0; i < args.length-1; i++) {
			originalText.append(args[i] + " ");
		}
		originalText.append(args[args.length-1]);
		
		String encodedBase64 = Base64.encode(originalText.toString());
		System.out.println("encode = " + encodedBase64);
		String decodedBase64 = Base64.decode(encodedBase64);
		System.out.println("decode = " + decodedBase64);
	}
}