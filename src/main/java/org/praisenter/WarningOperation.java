package org.praisenter;

public final class WarningOperation<T> {
	private final T data;
	private final String message;
	
	public WarningOperation(T data, String message) {
		this.data = data;
		this.message = message;
	}
	
	public T getData() {
		return data;
	}
	
	public String getMessage() {
		return message;
	}
	
}
