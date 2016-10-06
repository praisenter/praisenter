package org.praisenter.javafx.bible;

import org.praisenter.bible.Bible;

final class BibleTreeData extends TreeData {
	final Bible bible;
	
	public BibleTreeData(Bible bible) {
		this.bible = bible;
		this.label.set(bible.getName());
	}
	
	@Override
	public void update() {
		this.label.set(bible.getName());
	}
}
