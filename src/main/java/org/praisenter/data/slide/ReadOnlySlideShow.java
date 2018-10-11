package org.praisenter.data.slide;

import java.time.Instant;
import java.util.UUID;

import org.praisenter.data.Copyable;
import org.praisenter.data.Identifiable;
import org.praisenter.data.Persistable;
import org.praisenter.data.search.Indexable;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.collections.ObservableList;

public interface ReadOnlySlideShow extends Indexable, Persistable, Copyable, Identifiable {
	public boolean isLoopEnabled();
	
	public ReadOnlyStringProperty formatProperty();
	public ReadOnlyStringProperty versionProperty();
	public ReadOnlyObjectProperty<UUID> idProperty();
	public ReadOnlyStringProperty nameProperty();
	public ReadOnlyObjectProperty<Instant> createdDateProperty();
	public ReadOnlyObjectProperty<Instant> modifiedDateProperty();
	public ReadOnlyBooleanProperty loopEnabledProperty();
	
	public ObservableList<SlideAssignment> getSlidesUnmodifiable();
}
