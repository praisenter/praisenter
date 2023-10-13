package org.praisenter.ui.themes;

import atlantafx.base.theme.CupertinoDark;
import atlantafx.base.theme.CupertinoLight;
import atlantafx.base.theme.Dracula;
import atlantafx.base.theme.NordDark;
import atlantafx.base.theme.NordLight;
import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import javafx.css.PseudoClass;

public final class Theming {
	public static final AtlantaFXTheme[] THEMES = new AtlantaFXTheme[] { 
		new AtlantaFXTheme(new PrimerLight(), ThemeType.LIGHT), 
		new AtlantaFXTheme(new PrimerDark(), ThemeType.DARK), 
		new AtlantaFXTheme(new NordLight(), ThemeType.LIGHT), 
		new AtlantaFXTheme(new NordDark(), ThemeType.DARK), 
		new AtlantaFXTheme(new CupertinoLight(), ThemeType.LIGHT), 
		new AtlantaFXTheme(new CupertinoDark(), ThemeType.DARK), 
		new AtlantaFXTheme(new Dracula(), ThemeType.OTHER) 
	};
	
	public static final Accent[] ACCENTS = new Accent[] {
		new Accent(PseudoClass.getPseudoClass("p-color-accent-default"), ThemeType.LIGHT),
		new Accent(PseudoClass.getPseudoClass("p-color-accent-purple"), ThemeType.LIGHT),
		new Accent(PseudoClass.getPseudoClass("p-color-accent-pink"), ThemeType.LIGHT),
		new Accent(PseudoClass.getPseudoClass("p-color-accent-coral"), ThemeType.LIGHT),
		new Accent(PseudoClass.getPseudoClass("p-color-accent-green"), ThemeType.LIGHT),
		new Accent(PseudoClass.getPseudoClass("p-color-accent-gold"), ThemeType.LIGHT),
		
		new Accent(PseudoClass.getPseudoClass("p-color-accent-default-dark"), ThemeType.DARK),
		new Accent(PseudoClass.getPseudoClass("p-color-accent-purple-dark"), ThemeType.DARK),
		new Accent(PseudoClass.getPseudoClass("p-color-accent-pink-dark"), ThemeType.DARK),
		new Accent(PseudoClass.getPseudoClass("p-color-accent-coral-dark"), ThemeType.DARK),
		new Accent(PseudoClass.getPseudoClass("p-color-accent-green-dark"), ThemeType.DARK),
		new Accent(PseudoClass.getPseudoClass("p-color-accent-gold-dark"), ThemeType.DARK)
	};
	
	public static final AtlantaFXTheme getTheme(String userAgentStylesheet) {
		for (var theme : THEMES) {
			if (theme.getTheme().getUserAgentStylesheet().equalsIgnoreCase(userAgentStylesheet)) {
				return theme;
			}
		}
		return null;
	}
	
	public static final Accent getAccent(String accentClassName) {
		for (var accent : ACCENTS) {
			if (accent.getPseudoClass().getPseudoClassName().equalsIgnoreCase(accentClassName)) {
				return accent;
			}
		}
		return null;
	}
}
