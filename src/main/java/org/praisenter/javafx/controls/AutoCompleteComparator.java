package org.praisenter.javafx.controls;

public interface AutoCompleteComparator<T> {
	boolean matches(String typedText, T objectToCompare);
}
