package org.praisenter.media.ui;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.log4j.xml.DOMConfigurator;
import org.praisenter.preferences.BiblePreferences;
import org.praisenter.preferences.Preferences;
import org.praisenter.preferences.PreferencesException;
import org.praisenter.preferences.ui.BiblePreferencesPanel;
import org.praisenter.preferences.ui.GeneralPreferencesPanel;
import org.praisenter.preferences.ui.PreferencesDialog;
import org.praisenter.utilities.LookAndFeelUtilities;

public class TestConfigIO {
	public static void main(String[] args) throws PreferencesException {
		
		DOMConfigurator.configure("config/log4j.xml");
		
		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
	        if (LookAndFeelUtilities.NIMBUS.equalsIgnoreCase(info.getName())) {
	            try {
					UIManager.setLookAndFeel(info.getClassName());
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedLookAndFeelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	    }
		
		Preferences.getInstance();
		
		PreferencesDialog.show(null);
	}
}
