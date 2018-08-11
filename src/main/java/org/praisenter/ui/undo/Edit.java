package org.praisenter.ui.undo;

interface Edit {
	public String getName();
	public void undo();
	public void redo();
	public boolean isMergeSupported(Edit previous);
	public Edit merge(Edit previous);
	
	public static final Edit MARK = new Edit() {
		@Override
		public String toString() {
			return this.getName();
		}
		@Override
		public String getName() {
			return "MARK";
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
	};
}
