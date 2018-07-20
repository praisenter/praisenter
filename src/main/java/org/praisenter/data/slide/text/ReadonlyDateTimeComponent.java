package org.praisenter.data.slide.text;

import java.text.SimpleDateFormat;

import org.praisenter.data.Copyable;
import org.praisenter.data.Identifiable;
import org.praisenter.data.slide.ReadonlySlideComponent;
import org.praisenter.data.slide.ReadonlySlideRegion;

import javafx.beans.property.ReadOnlyObjectProperty;

public interface ReadonlyDateTimeComponent extends ReaonlyTextComponent, ReadonlySlideComponent, ReadonlySlideRegion, Copyable, Identifiable {
	public SimpleDateFormat getDateTimeFormat();
	
	public ReadOnlyObjectProperty<SimpleDateFormat> dateTimeFormatProperty();
}
