package org.praisenter.ui.undo;

import java.util.List;

final class CompositeEdit implements Edit {
	private final String name;
	private final List<Edit> edits;
	
	public CompositeEdit(String name, List<Edit> edits) {
		this.name = name;
		this.edits = edits;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public void redo() {
		for (Edit edit : this.edits) {
			edit.redo();
		}
	}
	
	@Override
	public void undo() {
		for (int i = this.edits.size() - 1; i >= 0; i--) {
			this.edits.get(i).undo();
		}
	}
	
	@Override
	public boolean isMergeSupported(Edit edit) {
		return false;
	}
	
	@Override
	public Edit merge(Edit edit) {
		return null;
	}
}
