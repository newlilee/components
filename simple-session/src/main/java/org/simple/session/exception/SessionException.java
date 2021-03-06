package org.simple.session.exception;

/**
 * @author clx 2018/4/3.
 */
public class SessionException extends RuntimeException {
	public SessionException() {
	}

	public SessionException(String message) {
		super(message);
	}

	public SessionException(String message, Throwable cause) {
		super(message, cause);
	}

	public SessionException(Throwable cause) {
		super(cause);
	}
}
