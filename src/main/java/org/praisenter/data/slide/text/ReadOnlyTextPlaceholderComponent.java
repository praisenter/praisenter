package org.praisenter.data.slide.text;

import org.praisenter.data.Copyable;
import org.praisenter.data.Identifiable;
import org.praisenter.data.TextType;
import org.praisenter.data.TextVariant;
import org.praisenter.data.slide.ReadOnlySlideComponent;
import org.praisenter.data.slide.ReadOnlySlideRegion;

import javafx.beans.property.ReadOnlyObjectProperty;

public interface ReadOnlyTextPlaceholderComponent extends ReadOnlyTextComponent, ReadOnlySlideComponent, ReadOnlySlideRegion, Copyable, Identifiable {
	public TextType getPlaceholderType();
	public TextVariant getPlaceholderVariant();
	
	public ReadOnlyObjectProperty<TextType> placeholderTypeProperty();
	public ReadOnlyObjectProperty<TextVariant> placeholderVariantProperty();
	
	public ReadOnlyTextComponent toTextComponent();
}
