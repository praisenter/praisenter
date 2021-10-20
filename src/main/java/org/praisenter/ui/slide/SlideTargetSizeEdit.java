package org.praisenter.ui.slide;

import java.util.function.BiConsumer;

import org.praisenter.ui.undo.Edit;

final class SlideTargetSizeEdit implements Edit {
	private final double oldWidth;
	private final double oldHeight;
	private final double newWidth;
	private final double newHeight;
	
	private final BiConsumer<Double, Double> onUndoRedo;
	
	public SlideTargetSizeEdit(
			double oldWidth,
			double oldHeight,
			double newWidth,
			double newHeight,
			BiConsumer<Double, Double> onUndoRedo) {
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
		this.onUndoRedo.accept(this.oldWidth, this.oldHeight);
	}

	@Override
	public void redo() {
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
