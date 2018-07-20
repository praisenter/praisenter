package org.praisenter.data.slide.text;

import org.praisenter.data.Copyable;
import org.praisenter.data.Identifiable;
import org.praisenter.data.TextType;
import org.praisenter.data.TextVariant;
import org.praisenter.data.slide.ReadonlySlideComponent;
import org.praisenter.data.slide.ReadonlySlideRegion;

import javafx.beans.property.ReadOnlyObjectProperty;

public interface ReadonlyTextPlaceholderComponent extends ReaonlyTextComponent, ReadonlySlideComponent, ReadonlySlideRegion, Copyable, Identifiable {
	public TextType getPlaceholderType();
	public TextVariant getPlaceholderVariant();
	
	public ReadOnlyObjectProperty<TextType> placeholderTypeProperty();
	public ReadOnlyObjectProperty<TextVariant> placeholderVariantProperty();
}
