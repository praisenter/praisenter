package org.praisenter.media.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.xml.DOMConfigurator;
import org.praisenter.media.MediaException;
import org.praisenter.media.MediaLibrary;
import org.praisenter.media.VideoMediaPlayerListener;
import org.praisenter.media.XugglerPlayableMedia;
import org.praisenter.media.XugglerVideoMedia;
import org.praisenter.media.player.XugglerMediaPlayer;
import org.praisenter.utilities.LookAndFeelUtilities;

public class VideoPlayerTest {
	public static void main(String[] args) throws Exception {
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
		
		DOMConfigurator.configure("config/log4j.xml");
		
//		AudioFormat f = new AudioFormat(48000, 16, 6, true, false);
//		DataLine.Info finfo = new DataLine.Info(SourceDataLine.class, f);
//		Mixer.Info[] mi = AudioSystem.getMixerInfo();
//        for (Mixer.Info info : mi) {
//        	System.out.println();
//            System.out.println("info: " + info);
//            Mixer m = AudioSystem.getMixer(info);
//            Line.Info[] t2 = m.getTargetLineInfo();
//            System.out.println("mixer " + m);
//            Line.Info[] sl = m.getSourceLineInfo();
//            for (Line.Info info2 : sl) {
//                System.out.println("    info: " + info2);
//                Line line = AudioSystem.getLine(info2);
//                if (line instanceof SourceDataLine) {
//                    SourceDataLine source = (SourceDataLine) line;
//
//                    DataLine.Info i = (DataLine.Info) source.getLineInfo();
//                    for (AudioFormat format : i.getFormats()) {
//                        System.out.println("    format: " + format);
//                    }
//                }
//            }
//            for (Line.Info infot : t2) {
//            	System.out.println("    info: " + infot);
//            	Line line = AudioSystem.getLine(infot);
//                if (line instanceof TargetDataLine) {
//                	TargetDataLine source = (TargetDataLine) line;
//
//                    DataLine.Info i = (DataLine.Info) source.getLineInfo();
//                    for (AudioFormat format : i.getFormats()) {
//                        System.out.println("    format: " + format);
//                    }
//                }
//            }
//        }
		
		new TestFrame();
	}
	
	private static class TestFrame extends JFrame implements VideoMediaPlayerListener {
		XugglerVideoMedia media = null;
		XugglerMediaPlayer player = null;
		VideoImagePanel pnlImage;
		BufferedImage image;
		boolean imageQueued = false;
		public TestFrame() {
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			Container container = this.getContentPane();
			container.setLayout(new BorderLayout());
			
			pnlImage = new VideoImagePanel();
			
			container.add(pnlImage, BorderLayout.CENTER);
			
			this.pack();
			this.setVisible(true);
			
			try {
				media = (XugglerVideoMedia)MediaLibrary.getMedia("media\\videos\\trailer_1080p.mov");
//				media = (XugglerVideoMedia)MediaLibrary.getMedia("media\\videos\\trailer_1080p.ogg");
//				media = (XugglerVideoMedia)MediaLibrary.getMedia("media\\videos\\033_JumpBack.avi");
				player = (XugglerMediaPlayer)MediaLibrary.getMediaPlayerFactory(XugglerPlayableMedia.class).createMediaPlayer();
				player.addMediaPlayerListener(this);
				player.setMedia(media);
				// enable looping
				player.getConfiguration().setLoopEnabled(true);
				player.play();
//				player = new XugglerMediaPlayer();
//				player.addMediaPlayerListener(this);
//				player.setMedia(media);
//				player.setLooped(true);
//				player.play();
			} catch (MediaException e) {
				e.printStackTrace();
			}
			
//			try {
//				Thread.sleep(5000);
//				player.pause();
//				Thread.sleep(2000);
//				player.resume();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		}
		
		@Override
		public void onVideoImage(BufferedImage image) {
//			this.image = image;

//				SwingUtilities.invokeLater(new Runnable() {
//					@Override
//					public void run() {
//						
//						imageQueued = false;
//					}
//				});
				pnlImage.setImage(image);
			
		}
	}
	
	private static class VideoImagePanel extends JPanel {
		BufferedImage image;
		public VideoImagePanel() {
			this.setPreferredSize(new Dimension(400, 400));
		}
		
		public void setImage(BufferedImage image) {
//			if (!repaintIssued) {
//				repaintIssued = true;
				this.image = image;
				this.repaint();
//			}
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			if (this.image != null) {
				final double iw = this.image.getWidth();
				final double ih = this.image.getHeight();
				
				final double ts = 400;
				final double sw = ts / iw;
				final double sh = ts / ih;
				final double s = sw < sh ? sw : sh;
				
				final int w = (int)Math.ceil(s * (double)iw);
				final int h = (int)Math.ceil(s * (double)ih);
				
				g.drawImage(this.image, 0, 0, w, h, null);
			}
		}
	}
}
