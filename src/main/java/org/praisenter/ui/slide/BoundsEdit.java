package org.praisenter.ui.slide;

import org.praisenter.ui.undo.Edit;

import javafx.beans.property.DoubleProperty;

final class BoundsEdit implements Edit {
	private final DoubleProperty x;
	private final DoubleProperty y;
	private final DoubleProperty width;
	private final DoubleProperty height;
	
	private final double oldX;
	private final double oldY;
	private final double oldWidth;
	private final double oldHeight;
	
	private final double newX;
	private final double newY;
	private final double newWidth;
	private final double newHeight;
	
	public BoundsEdit(
			DoubleProperty x, 
			DoubleProperty y, 
			DoubleProperty width, 
			DoubleProperty height, 
			double oldX, 
			double oldY,
			double oldWidth,
			double oldHeight,
			double newX,
			double newY,
			double newWidth,
			double newHeight) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.oldX = oldX;
		this.oldY = oldY;
		this.oldWidth = oldWidth;
		this.oldHeight = oldHeight;
		this.newX = newX;
		this.newY = newY;
		this.newWidth = newWidth;
		this.newHeight = newHeight;
	}
	
	@Override
	public String getName() {
		return "bounds";
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(")
		.append(this.oldX).append(", ")
		.append(this.oldY).append(") ")
		.append(this.oldWidth).append("x")
		.append(this.oldHeight).append(") => (")
		.append(this.newX).append(", ")
		.append(this.newY).append(") ")
		.append(this.newWidth).append("x")
		.append(this.newHeight);
		return sb.toString();
	}

	@Override
	public void undo() {
		this.x.set(this.oldX);
		this.y.set(this.oldY);
		this.width.set(this.oldWidth);
		this.height.set(this.oldHeight);
	}

	@Override
	public void redo() {
		this.x.set(this.newX);
		this.y.set(this.newY);
		this.width.set(this.newWidth);
		this.height.set(this.newHeight);
	}

	@Override
	public boolean isMergeSupported(Edit previous) {
		return false;
	}

	@Override
	public Edit merge(Edit previous) {
		return null;
	}
}
