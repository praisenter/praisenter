package org.praisenter.ui.themes;

import javafx.css.PseudoClass;

public final class Accent {
	private final PseudoClass pseudoClass;
	private final ThemeType themeType;
	
	public Accent(PseudoClass pseudoClass, ThemeType themeType) {
		this.pseudoClass = pseudoClass;
		this.themeType = themeType;
	}

	public PseudoClass getPseudoClass() {
		return this.pseudoClass;
	}

	public ThemeType getThemeType() {
		return this.themeType;
	}
}
