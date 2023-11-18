package org.praisenter.ui.pages;

public interface Page {
	public static final int PRESENT = 0;
	public static final int LIBRARY = 1;
	public static final int EDITOR = 2;
	public static final int SETTINGS = 3;
	public static final int TASKLIST = 4;
	
	public void setDefaultFocus();
}
