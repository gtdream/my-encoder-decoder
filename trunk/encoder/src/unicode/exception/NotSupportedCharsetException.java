package unicode.exception;

public class NotSupportedCharsetException extends Exception {

	public NotSupportedCharsetException () {
		System.out.println("encoder not supported");
	}
	
	public NotSupportedCharsetException (String message) {
		System.out.println(message + " encoder not supported");
	}
	
	private static final long serialVersionUID = 1L;

}
