package org.praisenter.data.slide;

import org.praisenter.data.Copyable;
import org.praisenter.data.Identifiable;
import org.praisenter.data.Persistable;
import org.praisenter.data.search.Indexable;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.collections.ObservableList;

public interface ReadOnlySlideShow extends Indexable, Persistable, Copyable, Identifiable {
	public boolean isLoopEnabled();
	
	public ReadOnlyBooleanProperty loopEnabledProperty();
	
	public ObservableList<SlideAssignment> getSlidesUnmodifiable();
}
