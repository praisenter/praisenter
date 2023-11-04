package org.praisenter.ui.themes;

import atlantafx.base.theme.Theme;

public final class AtlantaFXTheme {
	private final Theme theme;
	private final ThemeType type;
	
	public AtlantaFXTheme(Theme theme, ThemeType type) {
		this.theme = theme;
		this.type = type;
	}

	public Theme getTheme() {
		return this.theme;
	}

	public ThemeType getType() {
		return this.type;
	}
}
