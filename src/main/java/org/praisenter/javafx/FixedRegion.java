package org.praisenter.javafx;

import javafx.geometry.Rectangle2D;
import javafx.scene.layout.Region;

public final class FixedRegion extends Region {
	public double getWidth();
	public double getHeight();
	public double getX();
	public double getY();
	
	public void setWidth(double width);
	public void setHeight(double height);
	public void setX(double x);
	public void setY(double y);
	
	public void setSize(double width, double height);
	public void setBounds(Rectangle2D bounds);
}
