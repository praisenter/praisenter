package org.praisenter.data.bible;

import org.praisenter.data.Copyable;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.collections.ObservableList;

public interface ReadOnlyChapter extends Copyable {
	public int getNumber();
	
	public ReadOnlyIntegerProperty numberProperty();
	
	public ObservableList<? extends ReadOnlyVerse> getVersesUnmodifiable();
	
	public Verse getVerse(int verse);
}
