package org.praisenter.ui.slide;

import java.util.function.BiConsumer;

import org.praisenter.ui.undo.Edit;

import javafx.beans.property.ObjectProperty;

final class SlideTargetSizeEdit implements Edit {
	private final ObjectProperty<Integer> width;
	private final ObjectProperty<Integer> height;
	
	private final int oldWidth;
	private final int oldHeight;
	private final int newWidth;
	private final int newHeight;
	
	private final BiConsumer<Integer, Integer> onUndoRedo;
	
	public SlideTargetSizeEdit(
			ObjectProperty<Integer> width,
			ObjectProperty<Integer> height,
			int oldWidth,
			int oldHeight,
			int newWidth,
			int newHeight,
			BiConsumer<Integer, Integer> onUndoRedo) {
		this.width = width;
		this.height = height;
		this.newWidth = newWidth;
		this.newHeight = newHeight;
		this.oldWidth = oldWidth;
		this.oldHeight = oldHeight;
		this.onUndoRedo = onUndoRedo;
	}
	
	@Override
	public String getName() {
		return "resolution";
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("size[")
		.append(this.oldWidth).append("x").append(this.oldHeight).append(" => ")
		.append(this.newWidth).append("x").append(this.newHeight).append("]");
		return sb.toString();
	}

	@Override
	public void undo() {
		this.width.set(this.oldWidth);
		this.height.set(this.oldHeight);
		this.onUndoRedo.accept(this.oldWidth, this.oldHeight);
	}

	@Override
	public void redo() {
		this.width.set(this.newWidth);
		this.height.set(this.newHeight);
		this.onUndoRedo.accept(this.newWidth, this.newHeight);
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
