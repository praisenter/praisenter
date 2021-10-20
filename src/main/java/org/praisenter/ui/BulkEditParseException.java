package org.praisenter.ui;

import java.io.IOException;

public final class BulkEditParseException extends IOException {
	private static final long serialVersionUID = -4456427509948656330L;

	public BulkEditParseException() {
		super();
	}
	
	public BulkEditParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public BulkEditParseException(String message) {
		super(message);
	}

	public BulkEditParseException(Throwable cause) {
		super(cause);
	}
}
