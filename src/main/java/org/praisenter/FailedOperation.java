package org.praisenter;

public final class FailedOperation<T> {
	private final T data;
	private final Exception exception;
	
	public FailedOperation(T data, Exception exception) {
		this.data = data;
		this.exception = exception;
	}
	
	public T getData() {
		return data;
	}
	
	public Exception getException() {
		return exception;
	}
	
}
