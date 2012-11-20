package org.praisenter.media.ui;

import javax.swing.JFrame;

import org.apache.log4j.xml.DOMConfigurator;
import org.praisenter.preferences.BiblePreferences;
import org.praisenter.preferences.Preferences;
import org.praisenter.preferences.PreferencesException;
import org.praisenter.preferences.ui.BiblePreferencesPanel;
import org.praisenter.preferences.ui.GeneralPreferencesPanel;
import org.praisenter.preferences.ui.PreferencesDialog;

public class TestConfigIO {
	public static void main(String[] args) throws PreferencesException {
		
		DOMConfigurator.configure("config/log4j.xml");
		
		Preferences.getInstance();
		
		PreferencesDialog.show(null);
	}
}
