package unicode.exception;

public class EncoderNotSupportedException extends Exception {

	public EncoderNotSupportedException () {
		System.out.println("encoder not supported");
	}
	
	public EncoderNotSupportedException (String message) {
		System.out.println(message + " encoder not supported");
	}
	
	private static final long serialVersionUID = 1L;

}
