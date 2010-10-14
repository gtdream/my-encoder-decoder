package unicode.exception;

public class NotSupportedEncdoingException extends Exception {

	public NotSupportedEncdoingException () {
		System.out.println("encoder not supported");
	}
	
	public NotSupportedEncdoingException (String message) {
		System.out.println(message + " encoder not supported");
	}
	
	private static final long serialVersionUID = 1L;

}
