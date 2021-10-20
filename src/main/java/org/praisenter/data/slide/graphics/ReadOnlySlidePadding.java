package org.praisenter.data.slide.graphics;

import org.praisenter.data.Copyable;

import javafx.beans.property.ReadOnlyDoubleProperty;

public interface ReadOnlySlidePadding extends Copyable {
	public double getTop();
	public double getRight();
	public double getBottom();
	public double getLeft();
	
	public ReadOnlyDoubleProperty topProperty();
	public ReadOnlyDoubleProperty rightProperty();
	public ReadOnlyDoubleProperty bottomProperty();
	public ReadOnlyDoubleProperty leftProperty();
}
