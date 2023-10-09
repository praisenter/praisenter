package org.praisenter.ui.themes;

import atlantafx.base.theme.CupertinoDark;
import atlantafx.base.theme.CupertinoLight;
import atlantafx.base.theme.Dracula;
import atlantafx.base.theme.NordDark;
import atlantafx.base.theme.NordLight;
import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;

public final class Themes {
	public static final atlantafx.base.theme.Theme[] THEMES = new atlantafx.base.theme.Theme[] { 
		new PrimerLight(), 
		new PrimerDark(), 
		new NordLight(), 
		new NordDark(), 
		new CupertinoLight(), 
		new CupertinoDark(), 
		new Dracula() 
	};
	
	public static final atlantafx.base.theme.Theme getTheme(String userAgentStylesheet) {
		for (var theme : THEMES) {
			if (theme.getUserAgentStylesheet().equalsIgnoreCase(userAgentStylesheet)) {
				return theme;
			}
		}
		return null;
	}
}
