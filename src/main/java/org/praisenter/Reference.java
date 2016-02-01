package org.praisenter;

public final class Reference<T> {
	private T data;
	
	public T get() {
		return this.data;
	}
	
	public void set(T data) {
		this.data = data;
	}
}
