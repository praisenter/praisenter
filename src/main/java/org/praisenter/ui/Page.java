package org.praisenter.ui;

public enum Page {
	PRESENT(0),
	LIBRARY(1),
	EDITOR(2),
	SETTINGS(3),
	TASKLIST(4);
	
	private final int index;
	
	private Page(int index) {
		this.index = index;
	}
	
	public int getPageIndex() {
		return this.index;
	}
	
	public static Page getPageForIndex(int index) {
		for (Page page : Page.values()) {
			if (page.index == index) {
				return page;
			}
		}
		return null;
	}
}
