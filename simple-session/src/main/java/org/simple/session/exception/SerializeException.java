package org.simple.session.exception;

/**
 * @author clx 2018/4/3.
 */
public class SerializeException extends RuntimeException {
	public SerializeException() {
	}

	public SerializeException(String s) {
		super(s);
	}

	public SerializeException(String s, Throwable throwable) {
		super(s, throwable);
	}

	public SerializeException(Throwable throwable) {
		super(throwable);
	}
}
