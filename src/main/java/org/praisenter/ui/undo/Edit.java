package org.praisenter.ui.undo;

interface Edit {
	public String getName();
	public void undo();
	public void redo();
	public boolean isMergeSupported(Edit edit);
	public Edit merge(Edit edit);
	
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
		public boolean isMergeSupported(Edit command) {
			if (command == this) {
				return true;
			}
			return false;
		}
		@Override
		public Edit merge(Edit command) {
			return this;
		}
	};
}
