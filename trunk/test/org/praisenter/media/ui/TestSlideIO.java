package org.praisenter.media.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.xml.DOMConfigurator;
import org.praisenter.media.MediaException;
import org.praisenter.media.NoMediaLoaderException;
import org.praisenter.slide.SlideLibraryException;
import org.praisenter.slide.ui.SlideLibraryPanel;
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
		
//		BibleSlide bSlide = new BibleSlide("Default Template", 1280, 1024);
//		// set the background
////		ImageMediaComponent background = bSlide.createImageBackgroundComponent((ImageMedia)MediaLibrary.getMedia("media\\images\\bg3.png"));
//		VideoMediaComponent background = bSlide.createVideoBackgroundComponent((AbstractVideoMedia)MediaLibrary.getMedia("media\\videos\\033_JumpBack.avi"));
////		VideoMediaComponent background = bSlide.createVideoBackgroundComponent((AbstractVideoMedia)MediaLibrary.getMedia("media\\videos\\033_JumpBack_1280.avi"));
////		VideoMediaComponent background = bSlide.createVideoBackgroundComponent((AbstractVideoMedia)MediaLibrary.getMedia("media\\videos\\trailer_1080p.mov"));
//		background.setScaleType(ScaleType.NONUNIFORM);
//		background.setBackgroundPaint(Color.BLUE);
//		background.setBackgroundPaintVisible(false);
//		background.setAudioMuted(true);
//		background.setLoopEnabled(true);
//		
//		bSlide.setBackground(background);
//		
//		TextComponent lc = bSlide.getScriptureLocationComponent();
//		lc.setText("Genesis 1:1");
//		lc.setTextFont(FontManager.getDefaultFont().deriveFont(65.0f));
//		lc.setTextPaint(Color.WHITE);
//		
//		TextComponent tc = bSlide.getScriptureTextComponent();
//		tc.setText("In the beginning, God created the heaven and the earth:");
//		tc.setTextFont(FontManager.getDefaultFont().deriveFont(65.0f));
//		tc.setTextPaint(new LinearGradientPaint(0.0f, 0.0f, tc.getWidth(), tc.getHeight(), new float[] { 0.5f, 1.0f }, new Color[] { Color.WHITE, Color.YELLOW }, CycleMethod.NO_CYCLE));
//		tc.setBorderVisible(true);
//		tc.setBorderStroke(new BasicStroke(5.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 5.0f));
//		tc.setBorderPaint(new RadialGradientPaint(new Point2D.Float(tc.getWidth() / 2, tc.getHeight() / 2), 200, new float[] { 0.5f, 1.0f }, new Color[] { Color.PINK, Color.ORANGE }, CycleMethod.NO_CYCLE));
////		tc.setBackgroundPaint(Color.GRAY);
////		tc.setBackgroundPaintVisible(true);
//		
//		VideoMediaComponent vc = new VideoMediaComponent((AbstractVideoMedia)MediaLibrary.getMedia("media\\videos\\033_JumpBack.avi"), 640, 500 /*480*/);
//		vc.setBorderPaint(Color.BLACK);
//		vc.setBorderStroke(new BasicStroke(5.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 5.0f));
//		vc.setBorderVisible(true);
//		vc.setX(100);
//		vc.setY(200);
//		vc.setAudioMuted(true);
//		vc.setLoopEnabled(false);
//		vc.setScaleType(ScaleType.UNIFORM);
//		bSlide.addComponent(vc);
//		
//		ImageMediaComponent ic = new ImageMediaComponent((ImageMedia)MediaLibrary.getMedia("media\\images\\bg3.png"), 400, 400);
//		ic.setScaleQuality(ScaleQuality.NEAREST_NEIGHBOR);
//		ic.setScaleType(ScaleType.UNIFORM);
//		ic.setBackgroundPaint(new LinearGradientPaint(0.0f, 0.0f, tc.getWidth(), tc.getHeight(), new float[] { 0.5f, 1.0f }, new Color[] { Color.RED, Color.YELLOW }, CycleMethod.NO_CYCLE));
//		ic.setBackgroundPaintVisible(true);
//		ic.setX(600);
//		ic.setY(100);
//		bSlide.addComponent(ic);
//		
//		BufferedImage image = new BufferedImage(1280, 1024, BufferedImage.TYPE_INT_ARGB);
//		Graphics2D g = image.createGraphics();
//		bSlide.renderPreview(g);
//		g.dispose();
////		ImageIO.write(image, "png", new File("C:\\Users\\uswibit\\Desktop\\test.png"));
//		
//		try {
//			SlideLibrary.saveTemplate("default", bSlide.createTemplate());
//		} catch (SlideCopyException e1) {
//			e1.printStackTrace();
//		}
//		
//		Slide slide = new Slide("Test Slide", 1280, 1024);
//		slide.setBackground(background.copy());
//		
//		slide.addComponent(lc.copy());
//		slide.addComponent(tc.copy());
//		
//		SlideLibrary.saveSlide("test", slide);
//		try {
//			SlideLibrary.saveTemplate("test_template", (SlideTemplate)slide.createTemplate());
//		} catch (SlideCopyException e1) {
//			e1.printStackTrace();
//		}
		
//		
//		bSlide.getScriptureTextComponent().setBackgroundPaint(null);
//		bSlide.getScriptureTextComponent().setBackgroundPaintVisible(false);
//		bSlide.getScriptureTextComponent().setTextPaint(Color.WHITE);
//		bSlide.getScriptureTextComponent().setBorderPaint(null);
//		bSlide.getScriptureTextComponent().setBorderStroke(null);
//		bSlide.getScriptureTextComponent().setBorderVisible(false);
		
//		BibleSlideTemplate template = SlideLibrary.getTemplate("templates\\bible\\default.xml", BibleSlideTemplate.class);
//		try {
//			BibleSlide testbSlide = template.createSlide();
//			SlideWindows.getPrimarySlideWindow().send(testbSlide, new TransitionAnimator(Transitions.IN[12], 600));
//		} catch (SlideCopyException e) {
//			e.printStackTrace();
//		}
		
//		BibleSlideTemplate bt = new BibleSlideTemplate("Test Template", 1280, 1024);
//		
//		ImageMediaComponent bg = bt.createImageBackgroundComponent((ImageMedia)MediaLibrary.getMedia("media\\images\\bg3.png"));
//		bg.setBackgroundPaint(Color.BLUE);
//		bg.setBackgroundPaintVisible(true);
//		bg.setBorderPaint(null);
//		bg.setBorderStroke(null);
//		bg.setBorderVisible(false);
//		bg.setScaleQuality(ScaleQuality.BICUBIC);
//		bg.setScaleType(ScaleType.NONUNIFORM);
//		bt.setBackground(bg);
//		
//		TextComponent sl = bt.getScriptureLocationComponent();
//		sl.setText("Lorem Ipsum");
//		sl.setTextFont(FontManager.getDefaultFont().deriveFont(80.0f));
//		sl.setTextFontScaleType(FontScaleType.REDUCE_SIZE_ONLY);
//		sl.setTextPadding(30);
//		sl.setTextPaint(Color.WHITE);
//		sl.setTextWrapped(false);
//		sl.setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);
//		sl.setVerticalTextAlignment(VerticalTextAlignment.CENTER);
//		
//		TextComponent st = bt.getScriptureTextComponent();
//		st.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque bibendum volutpat libero nec varius. Vestibulum pharetra tincidunt ligula vitae facilisis. Vivamus eget rutrum massa. Quisque et lorem augue, ac dapibus lacus. Duis accumsan purus in nibh tempor convallis. Ut et massa ac lorem volutpat lobortis. Cras magna libero, lobortis ut fringilla vitae, posuere eget sem.");
//		st.setTextFont(FontManager.getDefaultFont().deriveFont(60.0f));
//		st.setTextFontScaleType(FontScaleType.REDUCE_SIZE_ONLY);
//		st.setTextPadding(30);
//		st.setTextPaint(Color.WHITE);
//		st.setTextWrapped(false);
//		st.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
//		st.setVerticalTextAlignment(VerticalTextAlignment.TOP);
//		
//		SlideLibrary.saveTemplate("default2", bt);
		
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
