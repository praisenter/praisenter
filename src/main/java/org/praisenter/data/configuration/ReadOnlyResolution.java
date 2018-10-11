package org.praisenter.data.configuration;

import org.praisenter.data.Copyable;

import javafx.beans.property.ReadOnlyIntegerProperty;

public interface ReadOnlyResolution extends Copyable {
	public int getWidth();
	public int getHeight();

	public ReadOnlyIntegerProperty widthProperty();
	public ReadOnlyIntegerProperty heightProperty();
}
