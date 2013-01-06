package org.praisenter.media.ui;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.xml.DOMConfigurator;
import org.praisenter.media.MediaLibrary;
import org.praisenter.utilities.LookAndFeelUtilities;

public class MediaUITest {
	public static void main(String[] args) {
		DOMConfigurator.configure("config/log4j.xml");
		
		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
	        if (LookAndFeelUtilities.NIMBUS.equalsIgnoreCase(info.getName())) {
	            try {
					UIManager.setLookAndFeel(info.getClassName());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (UnsupportedLookAndFeelException e) {
					e.printStackTrace();
				}
	        }
	    }
		
		// fix the nimbus disabled tooltip coloring
    	UIManager.put("ToolTip[Disabled].backgroundPainter", UIManager.get("ToolTip[Enabled].backgroundPainter"));
		
		MediaLibrary.loadMediaLibrary();
		
		MediaLibraryDialog.show(null);
	}
}
