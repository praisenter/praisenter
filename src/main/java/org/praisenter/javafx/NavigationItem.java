package org.praisenter.javafx;

public class NavigationItem<T> {
	private final String name;
	private final T data;
	
	public NavigationItem(String name, T data) {
		this.name = name;
		this.data = data;
	}
	
	@Override
	public String toString() {
		return this.name;
	}

	public String getName() {
		return name;
	}

	public T getData() {
		return data;
	}
	
}
