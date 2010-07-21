package ua.cn.yet.waiter.service;

public class GeneralServiceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GeneralServiceException() {
		super();
	}

	public GeneralServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public GeneralServiceException(String message) {
		super(message);
	}

	public GeneralServiceException(Throwable cause) {
		super(cause);
	}
}
