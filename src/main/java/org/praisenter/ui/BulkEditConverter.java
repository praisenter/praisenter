package org.praisenter.ui;

public interface BulkEditConverter<T> {
	public String getSample();
	public String toString(T obj);
	public T fromString(String data) throws BulkEditParseException;
}
