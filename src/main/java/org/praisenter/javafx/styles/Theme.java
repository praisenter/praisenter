package org.praisenter.javafx.styles;

public enum Theme {
	DEFAULT("Default", Theme.class.getResource("/org/praisenter/javafx/styles/default.css").toExternalForm()),
	DARK("Dark", Theme.class.getResource("/org/praisenter/javafx/styles/dark.css").toExternalForm());
	
	private final String name;
	private final String css;
	
	private Theme(String name, String css) {
		this.name = name;
		this.css = css;
	}

	public String getName() {
		return this.name;
	}

	public String getCss() {
		return this.css;
	}
}
