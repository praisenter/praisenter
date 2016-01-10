package org.praisenter.media;

public class MediaFormatException extends Exception {
	private static final long serialVersionUID = 6330495060583102082L;

	public MediaFormatException() {
		super();
	}

	public MediaFormatException(String message, Throwable cause) {
		super(message, cause);
	}

	public MediaFormatException(String message) {
		super(message);
	}

	public MediaFormatException(Throwable cause) {
		super(cause);
	}
}