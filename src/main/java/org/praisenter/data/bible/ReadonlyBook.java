package org.praisenter.data.bible;

import org.praisenter.data.Copyable;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.collections.ObservableList;

public interface ReadonlyBook extends Copyable {
	public int getNumber();
	public String getName();
	
	public ReadOnlyIntegerProperty numberProperty();
	public ReadOnlyStringProperty nameProperty();
	
	public ObservableList<? extends ReadonlyChapter> getChaptersUnmodifiable();
	
	public int getMaxChapterNumber();
	public Chapter getChapter(int chapter);
	public Chapter getLastChapter();
}