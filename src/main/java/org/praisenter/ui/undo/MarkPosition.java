package org.praisenter.ui.undo;

final class MarkPosition implements Edit {
	@Override
	public String toString() {
		return this.getName();
	}
	@Override
	public String getName() {
		return "MARK_POSITION";
	}
	@Override
	public void redo() {}
	@Override
	public void undo() {}
	@Override
	public boolean isMergeSupported(Edit previous) {
		if (previous == this) {
			return true;
		}
		return false;
	}
	@Override
	public Edit merge(Edit previous) {
		return this;
	}
}
