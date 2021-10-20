package org.praisenter.ui.bind;

public interface ObjectConverter<T, E> {
	public E convertFrom(T t);
	public T convertTo(E e);
}
