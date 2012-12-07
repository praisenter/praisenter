package org.praisenter.media.ui;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;

import org.praisenter.preferences.Preferences;
import org.praisenter.slide.BibleSlideTemplate;
import org.praisenter.slide.ui.editor.PaintEditorPanel;
import org.praisenter.slide.ui.editor.SlideEditorPanel;
import org.praisenter.slide.ui.editor.StrokeEditorPanel;
import org.praisenter.utilities.LookAndFeelUtilities;

public class TestSlideEditor {
	public static void main(String[] args) {
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
//		PaintEditorPanel panel = new PaintEditorPanel(null);
//		StrokeEditorPanel panel = new StrokeEditorPanel(null);
		Dimension size = Preferences.getInstance().getPrimaryOrDefaultDeviceResolution();
		SlideEditorPanel panel = new SlideEditorPanel(BibleSlideTemplate.getDefaultTemplate(size.width, size.height), size);
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
	}
}
