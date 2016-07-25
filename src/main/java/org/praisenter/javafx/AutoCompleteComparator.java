package org.praisenter.javafx;

public interface AutoCompleteComparator<T> {
	boolean matches(String typedText, T objectToCompare);
}
