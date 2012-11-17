package org.praisenter.media.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
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
import org.praisenter.media.ScaleQuality;
import org.praisenter.media.ScaleType;
import org.praisenter.slide.BibleSlide;
import org.praisenter.slide.Slide;
import org.praisenter.slide.SlideComponentCopyException;
import org.praisenter.slide.SlideCopyException;
import org.praisenter.slide.SlideLibrary;
import org.praisenter.slide.SlideLibraryException;
import org.praisenter.slide.SlideTemplate;
import org.praisenter.slide.media.ImageMediaComponent;
import org.praisenter.slide.media.VideoMediaComponent;
import org.praisenter.slide.text.TextComponent;
import org.praisenter.slide.transitions.TransitionAnimator;
import org.praisenter.slide.transitions.Transitions;
import org.praisenter.slide.ui.SlideLibraryPanel;
import org.praisenter.slide.ui.display.SlideWindows;
import org.praisenter.utilities.FontManager;
import org.praisenter.utilities.LookAndFeelUtilities;

public class TestSlideIO {
	public static void main(String[] args) throws NoMediaLoaderException, MediaException, SlideLibraryException, IOException, SlideComponentCopyException {
		
		DOMConfigurator.configure("config/log4j.xml");
		
		BibleSlide bSlide = new BibleSlide("Default Template", 1280, 1024);
		// set the background
//		ImageMediaComponent background = bSlide.createImageBackgroundComponent((ImageMedia)MediaLibrary.getMedia("media\\images\\bg3.png"));
		VideoMediaComponent background = bSlide.createVideoBackgroundComponent((AbstractVideoMedia)MediaLibrary.getMedia("media\\videos\\033_JumpBack.avi"));
//		VideoMediaComponent background = bSlide.createVideoBackgroundComponent((AbstractVideoMedia)MediaLibrary.getMedia("media\\videos\\033_JumpBack_1280.avi"));
//		VideoMediaComponent background = bSlide.createVideoBackgroundComponent((AbstractVideoMedia)MediaLibrary.getMedia("media\\videos\\trailer_1080p.mov"));
		background.setScaleType(ScaleType.NONUNIFORM);
		background.setBackgroundPaint(Color.BLUE);
		background.setBackgroundPaintVisible(false);
		background.setAudioMuted(true);
		background.setLoopEnabled(true);
		
		bSlide.setBackground(background);
		
		TextComponent lc = bSlide.getScriptureLocationComponent();
		lc.setText("Genesis 1:1");
		lc.setTextFont(FontManager.getDefaultFont().deriveFont(65.0f));
		lc.setTextPaint(Color.WHITE);
		
		TextComponent tc = bSlide.getScriptureTextComponent();
		tc.setText("In the beginning, God created the heaven and the earth:");
		tc.setTextFont(FontManager.getDefaultFont().deriveFont(65.0f));
		tc.setTextPaint(new LinearGradientPaint(0.0f, 0.0f, tc.getWidth(), tc.getHeight(), new float[] { 0.5f, 1.0f }, new Color[] { Color.WHITE, Color.YELLOW }, CycleMethod.NO_CYCLE));
		tc.setBorderVisible(true);
		tc.setBorderStroke(new BasicStroke(5.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 5.0f));
		tc.setBorderPaint(new RadialGradientPaint(new Point2D.Float(tc.getWidth() / 2, tc.getHeight() / 2), 200, new float[] { 0.5f, 1.0f }, new Color[] { Color.PINK, Color.ORANGE }, CycleMethod.NO_CYCLE));
//		tc.setBackgroundPaint(Color.GRAY);
//		tc.setBackgroundPaintVisible(true);
		
		VideoMediaComponent vc = new VideoMediaComponent((AbstractVideoMedia)MediaLibrary.getMedia("media\\videos\\033_JumpBack.avi"), 640, 500 /*480*/);
		vc.setBorderPaint(Color.BLACK);
		vc.setBorderStroke(new BasicStroke(5.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 5.0f));
		vc.setBorderVisible(true);
		vc.setX(100);
		vc.setY(200);
		vc.setAudioMuted(true);
		vc.setLoopEnabled(false);
		vc.setScaleType(ScaleType.UNIFORM);
		bSlide.addComponent(vc);
		
		ImageMediaComponent ic = new ImageMediaComponent((ImageMedia)MediaLibrary.getMedia("media\\images\\bg3.png"), 400, 400);
		ic.setScaleQuality(ScaleQuality.NEAREST_NEIGHBOR);
		ic.setScaleType(ScaleType.UNIFORM);
		ic.setBackgroundPaint(new LinearGradientPaint(0.0f, 0.0f, tc.getWidth(), tc.getHeight(), new float[] { 0.5f, 1.0f }, new Color[] { Color.RED, Color.YELLOW }, CycleMethod.NO_CYCLE));
		ic.setBackgroundPaintVisible(true);
		ic.setX(600);
		ic.setY(100);
		bSlide.addComponent(ic);
		
		BufferedImage image = new BufferedImage(1280, 1024, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		bSlide.renderPreview(g);
		g.dispose();
//		ImageIO.write(image, "png", new File("C:\\Users\\uswibit\\Desktop\\test.png"));
		
		try {
			SlideLibrary.saveTemplate("default", bSlide.createTemplate());
		} catch (SlideCopyException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Slide slide = new Slide("Test Slide", 1280, 1024);
		slide.setBackground(background.copy());
		
		slide.addComponent(lc.copy());
		slide.addComponent(tc.copy());
		
		SlideLibrary.saveSlide("test", slide);
		try {
			SlideLibrary.saveTemplate("test_template", (SlideTemplate)slide.createTemplate());
		} catch (SlideCopyException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
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
//		
//		bSlide.getScriptureTextComponent().setBackgroundPaint(null);
//		bSlide.getScriptureTextComponent().setBackgroundPaintVisible(false);
//		bSlide.getScriptureTextComponent().setTextPaint(Color.WHITE);
//		bSlide.getScriptureTextComponent().setBorderPaint(null);
//		bSlide.getScriptureTextComponent().setBorderStroke(null);
//		bSlide.getScriptureTextComponent().setBorderVisible(false);
		
//		SlideWindows.getPrimarySlideWindow().send(bSlide, new TransitionAnimator(Transitions.IN[10], 600));
		
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
