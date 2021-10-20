package org.praisenter.data.slide.text;

import java.time.LocalDateTime;

import org.praisenter.data.Copyable;
import org.praisenter.data.Identifiable;
import org.praisenter.data.slide.ReadOnlySlideComponent;
import org.praisenter.data.slide.ReadOnlySlideRegion;

import javafx.beans.property.ReadOnlyObjectProperty;

public interface ReadOnlyTimedTextComponent extends ReadOnlyTextComponent, ReadOnlySlideComponent, ReadOnlySlideRegion, Copyable, Identifiable {
	public LocalDateTime getNow();
	
	public ReadOnlyObjectProperty<LocalDateTime> nowProperty();
}
