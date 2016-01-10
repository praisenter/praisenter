package org.praisenter.media;

public class TranscodeException extends Exception {
	private static final long serialVersionUID = 6330495060583102082L;

	public TranscodeException() {
		super();
	}

	public TranscodeException(String message, Throwable cause) {
		super(message, cause);
	}

	public TranscodeException(String message) {
		super(message);
	}

	public TranscodeException(Throwable cause) {
		super(cause);
	}
}
