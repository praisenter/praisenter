package org.praisenter.data.slide.text;

import java.text.SimpleDateFormat;

import org.praisenter.data.Copyable;
import org.praisenter.data.Identifiable;
import org.praisenter.data.slide.ReadOnlySlideComponent;
import org.praisenter.data.slide.ReadOnlySlideRegion;

import javafx.beans.property.ReadOnlyObjectProperty;

public interface ReadOnlyDateTimeComponent extends ReadOnlyTextComponent, ReadOnlyTimedTextComponent, ReadOnlySlideComponent, ReadOnlySlideRegion, Copyable, Identifiable {
	public SimpleDateFormat getDateTimeFormat();
	
	public ReadOnlyObjectProperty<SimpleDateFormat> dateTimeFormatProperty();
}
