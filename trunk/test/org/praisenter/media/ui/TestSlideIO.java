package org.praisenter.media.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.xml.DOMConfigurator;
import org.praisenter.media.AbstractVideoMedia;
import org.praisenter.media.ImageMedia;
import org.praisenter.media.MediaException;
import org.praisenter.media.MediaLibrary;
import org.praisenter.media.NoMediaLoaderException;
import org.praisenter.media.ScaleType;
import org.praisenter.preferences.Preferences;
import org.praisenter.slide.BibleSlide;
import org.praisenter.slide.BibleSlideTemplate;
import org.praisenter.slide.NotificationSlideTemplate;
import org.praisenter.slide.Slide;
import org.praisenter.slide.SlideLibrary;
import org.praisenter.slide.SlideLibraryException;
import org.praisenter.slide.SlideTemplate;
import org.praisenter.slide.graphics.CapType;
import org.praisenter.slide.graphics.ColorFill;
import org.praisenter.slide.graphics.DashPattern;
import org.praisenter.slide.graphics.JoinType;
import org.praisenter.slide.graphics.LineStyle;
import org.praisenter.slide.graphics.LinearGradientDirection;
import org.praisenter.slide.graphics.LinearGradientFill;
import org.praisenter.slide.graphics.RadialGradientDirection;
import org.praisenter.slide.graphics.RadialGradientFill;
import org.praisenter.slide.graphics.Stop;
import org.praisenter.slide.media.ImageMediaComponent;
import org.praisenter.slide.media.VideoMediaComponent;
import org.praisenter.slide.text.FontScaleType;
import org.praisenter.slide.text.HorizontalTextAlignment;
import org.praisenter.slide.text.TextComponent;
import org.praisenter.slide.text.VerticalTextAlignment;
import org.praisenter.slide.ui.SlideLibraryPanel;
import org.praisenter.utilities.ColorUtilities;
import org.praisenter.utilities.FontManager;
import org.praisenter.utilities.LookAndFeelUtilities;

public class TestSlideIO {
	public static void main(String[] args) throws NoMediaLoaderException, MediaException, SlideLibraryException, IOException {
		
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
		
		MediaLibrary.loadMediaLibrary();
		
		BibleSlide bSlide = new BibleSlide("Default Template", 1280, 1024);
		// set the background
//		ImageMediaComponent background = bSlide.createImageBackgroundComponent((ImageMedia)MediaLibrary.getMedia("media\\images\\bg3.png"));
		VideoMediaComponent background = bSlide.createVideoBackgroundComponent((AbstractVideoMedia)MediaLibrary.getMedia("media\\videos\\033_JumpBack.avi"));
//		VideoMediaComponent background = bSlide.createVideoBackgroundComponent((AbstractVideoMedia)MediaLibrary.getMedia("media\\videos\\033_JumpBack_1280.avi"));
//		VideoMediaComponent background = bSlide.createVideoBackgroundComponent((AbstractVideoMedia)MediaLibrary.getMedia("media\\videos\\trailer_1080p.mov"));
		background.setScaleType(ScaleType.NONUNIFORM);
		background.setBackgroundFill(new ColorFill(Color.BLUE));
		background.setBackgroundVisible(false);
		background.setAudioMuted(true);
		background.setLoopEnabled(true);
		
		bSlide.setBackground(background);
		
		TextComponent lc = bSlide.getScriptureLocationComponent();
		lc.setText("Genesis 1:1");
		lc.setTextFont(FontManager.getDefaultFont().deriveFont(65.0f));
		lc.setTextFill(new ColorFill(Color.WHITE));
		
		TextComponent tc = bSlide.getScriptureTextComponent();
		tc.setText("In the beginning, God created the heaven and the earth:");
		tc.setTextFont(FontManager.getDefaultFont().deriveFont(65.0f));
		tc.setTextFill(new LinearGradientFill(
				LinearGradientDirection.TOP, 
				new Stop(0.0f, Color.YELLOW), 
				new Stop(0.5f, ColorUtilities.getColorAtMidpoint(Color.YELLOW, Color.WHITE)), 
				new Stop(1.0f, Color.WHITE)));
		tc.setBorderVisible(true);
		tc.setBorderStyle(new LineStyle(5.0f, CapType.ROUND, JoinType.ROUND, DashPattern.SOLID));
		tc.setBorderFill(new RadialGradientFill(
				RadialGradientDirection.CENTER,
				new Stop(0.0f, Color.PINK), 
				new Stop(0.5f, ColorUtilities.getColorAtMidpoint(Color.PINK, Color.ORANGE)), 
				new Stop(1.0f, Color.ORANGE)));
		
		VideoMediaComponent vc = new VideoMediaComponent("Random Video", (AbstractVideoMedia)MediaLibrary.getMedia("media\\videos\\033_JumpBack.avi"), 640, 500 /*480*/);
		vc.setBorderFill(new ColorFill(Color.BLACK));
		vc.setBorderStyle(new LineStyle(5.0f, CapType.ROUND, JoinType.ROUND, DashPattern.SOLID));
		vc.setBorderVisible(true);
		vc.setX(100);
		vc.setY(200);
		vc.setAudioMuted(true);
		vc.setLoopEnabled(false);
		vc.setScaleType(ScaleType.UNIFORM);
		bSlide.addComponent(vc);
		
		ImageMediaComponent ic = new ImageMediaComponent("Random Image", (ImageMedia)MediaLibrary.getMedia("media\\images\\bg3.png"), 400, 400);
		ic.setScaleType(ScaleType.UNIFORM);
		ic.setBackgroundFill(new LinearGradientFill(
				LinearGradientDirection.TOP_LEFT, 
				new Stop(0.0f, Color.RED), 
				new Stop(0.5f, ColorUtilities.getColorAtMidpoint(Color.RED, Color.YELLOW)), 
				new Stop(1.0f, Color.YELLOW)));
		ic.setBackgroundVisible(true);
		ic.setX(600);
		ic.setY(100);
		bSlide.addComponent(ic);
		
		SlideLibrary.saveTemplate("default", bSlide.createTemplate());
		
		Slide slide = new Slide("Test Slide", 1280, 1024);
		slide.setBackground(background.copy());
		
		slide.addComponent(lc.copy());
		slide.addComponent(tc.copy());
		
		SlideLibrary.saveSlide("test", slide);
		SlideLibrary.saveTemplate("test_template", (SlideTemplate)slide.createTemplate());
		
		bSlide.getScriptureTextComponent().setBackgroundFill(null);
		bSlide.getScriptureTextComponent().setBackgroundVisible(false);
		bSlide.getScriptureTextComponent().setTextFill(new ColorFill(Color.WHITE));
		bSlide.getScriptureTextComponent().setBorderFill(null);
		bSlide.getScriptureTextComponent().setBorderStyle(null);
		bSlide.getScriptureTextComponent().setBorderVisible(false);
		
		BibleSlideTemplate bt = new BibleSlideTemplate("Test Template", 1280, 1024);
		
		ImageMediaComponent bg = bt.createImageBackgroundComponent((ImageMedia)MediaLibrary.getMedia("media\\images\\bg3.png"));
		bg.setBackgroundFill(new ColorFill(Color.BLUE));
		bg.setBackgroundVisible(true);
		bg.setBorderFill(null);
		bg.setBorderStyle(null);
		bg.setBorderVisible(false);
		bg.setScaleType(ScaleType.NONUNIFORM);
		bt.setBackground(bg);
		
		TextComponent sl = bt.getScriptureLocationComponent();
		sl.setText("Lorem Ipsum");
		sl.setTextFont(FontManager.getDefaultFont().deriveFont(80.0f));
		sl.setTextFontScaleType(FontScaleType.REDUCE_SIZE_ONLY);
		sl.setTextPadding(30);
		sl.setTextFill(new ColorFill(Color.WHITE));
		sl.setTextWrapped(false);
		sl.setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);
		sl.setVerticalTextAlignment(VerticalTextAlignment.CENTER);
		
		TextComponent st = bt.getScriptureTextComponent();
		st.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque bibendum volutpat libero nec varius. Vestibulum pharetra tincidunt ligula vitae facilisis. Vivamus eget rutrum massa. Quisque et lorem augue, ac dapibus lacus. Duis accumsan purus in nibh tempor convallis. Ut et massa ac lorem volutpat lobortis. Cras magna libero, lobortis ut fringilla vitae, posuere eget sem.");
		st.setTextFont(FontManager.getDefaultFont().deriveFont(60.0f));
		st.setTextFontScaleType(FontScaleType.REDUCE_SIZE_ONLY);
		st.setTextPadding(30);
		st.setTextFill(new ColorFill(Color.WHITE));
		st.setTextWrapped(true);
		st.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
		st.setVerticalTextAlignment(VerticalTextAlignment.TOP);
		
		SlideLibrary.saveTemplate("default2", bt);
		
		Dimension size = Preferences.getInstance().getPrimaryOrDefaultDeviceResolution();
		NotificationSlideTemplate ntemplate = NotificationSlideTemplate.getDefaultTemplate(size.width, size.height);
		
		SlideLibrary.saveTemplate("default", ntemplate);
		
		SlideLibrary.loadSlideLibrary();
		
		new TestFrame();
	}
	
	private static class TestFrame extends JFrame {
		public TestFrame() {
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			Container container = this.getContentPane();
			container.setLayout(new BorderLayout());
			
			SlideLibraryPanel pnlMedia = new SlideLibraryPanel();
			container.add(pnlMedia, BorderLayout.CENTER);
			
			
			
			this.pack();
			this.setVisible(true);
		}
	}
}
