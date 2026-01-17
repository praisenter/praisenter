package org.praisenter.ui;

public class WorkspaceInitializationException extends Exception {
	private static final long serialVersionUID = 8326476275257403277L;

	public WorkspaceInitializationException() {
		super();
	}
	
	public WorkspaceInitializationException(String message, Throwable cause) {
		super(message, cause);
	}

	public WorkspaceInitializationException(String message) {
		super(message);
	}

	public WorkspaceInitializationException(Throwable cause) {
		super(cause);
	}
}