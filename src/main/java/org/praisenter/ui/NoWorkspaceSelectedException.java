package org.praisenter.ui;

import java.io.IOException;

public class NoWorkspaceSelectedException extends IOException {
	private static final long serialVersionUID = 2669849621119221827L;

	public NoWorkspaceSelectedException() {
		super();
	}
	
	public NoWorkspaceSelectedException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoWorkspaceSelectedException(String message) {
		super(message);
	}

	public NoWorkspaceSelectedException(Throwable cause) {
		super(cause);
	}
}