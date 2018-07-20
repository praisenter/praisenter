package org.praisenter.data.configuration;

import org.praisenter.data.Copyable;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;

public interface ReadonlyDisplay extends Copyable {
	public int getId();
	public DisplayRole getRole();
	public String getName();
	public int getX();
	public int getY();
	public int getWidth();
	public int getHeight();
	
	public ReadOnlyIntegerProperty idProperty();
	public ReadOnlyObjectProperty<DisplayRole> roleProperty();
	public ReadOnlyStringProperty nameProperty();
	public ReadOnlyIntegerProperty xProperty();
	public ReadOnlyIntegerProperty yProperty();
	public ReadOnlyIntegerProperty widthProperty();
	public ReadOnlyIntegerProperty heightProperty();
}
