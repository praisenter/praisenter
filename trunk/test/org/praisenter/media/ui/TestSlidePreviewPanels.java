package org.praisenter.media.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.xml.DOMConfigurator;
import org.praisenter.preferences.PreferencesException;
import org.praisenter.slide.BibleSlide;
import org.praisenter.slide.BibleSlideTemplate;
import org.praisenter.slide.SlideLibrary;
import org.praisenter.slide.SlideLibraryException;
import org.praisenter.slide.ui.preview.InlineSlidePreviewPanel;
import org.praisenter.utilities.LookAndFeelUtilities;

public class TestSlidePreviewPanels {
	public static void main(String[] args) throws PreferencesException {
		
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
		
		new TestFrame();
	}
	
	private static class TestFrame extends JFrame {
		public TestFrame() {
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			Container container = this.getContentPane();
			container.setLayout(new BorderLayout());
			
			try {
				BibleSlideTemplate template = SlideLibrary.getTemplate("templates\\bible\\default2.xml", BibleSlideTemplate.class);
				
				BibleSlide slide1 = template.createSlide();
				BibleSlide slide2 = template.createSlide();
				BibleSlide slide3 = template.createSlide();
				
				slide1.getScriptureLocationComponent().setText("Genesis 1:1");
				slide1.getScriptureTextComponent().setText("In the beginning, God created the heaven and the earth");
				
				slide2.getScriptureLocationComponent().setText("Genesis 1:2");
				slide2.getScriptureTextComponent().setText("test 2");
				
				slide3.getScriptureLocationComponent().setText("Genesis 1:3");
				slide3.getScriptureTextComponent().setText("More test text");
				
				InlineSlidePreviewPanel panel = new InlineSlidePreviewPanel(10, 0);
				panel.addSlide(slide1);
				panel.addSlide(slide2);
				panel.addSlide(slide3);
				panel.setMinimumSize(250);
				panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
//				ScrollableInlineSlidePreviewPanel scroller = new ScrollableInlineSlidePreviewPanel(panel);
//				container.add(scroller, BorderLayout.CENTER);
				container.add(panel, BorderLayout.CENTER);
			} catch (SlideLibraryException e) {
				e.printStackTrace();
			}
			
			this.setMinimumSize(new Dimension(600, 200));
			
			this.pack();
			this.setVisible(true);
		}
	}
}
