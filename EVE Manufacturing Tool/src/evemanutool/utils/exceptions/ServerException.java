package evemanutool.utils.exceptions;

@SuppressWarnings("serial")
public abstract class ServerException extends Exception {
	
	/*
	 * Superclass for different ServerExceptions.
	 * Subclasses are meant to provide more useful information to the user.
	 */

	private String userMessage;
	
	/*
	 * Used if message is using the decode-implementation. 
	 */
	public ServerException(Exception e) {
		super(e.getCause());
		userMessage = decodeException(e);
	}

	/*
	 * Used if message can be given on creation. 
	 */
	public ServerException(String userMessage) {
		super(userMessage);
		this.userMessage = userMessage;
	}
	
	/*
	 * Receives an exception an tries to convert it to a relevant message to the user.
	 * Subclasses should implement a constructor for a specific Exception for cleaner code.
	 */
	protected abstract String decodeException(Exception e);

	public String getUserErrorMessage() {
		return userMessage;
	}

}