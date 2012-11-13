package org.praisenter.media.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.activation.DataSource;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import org.praisenter.media.MediaException;
import org.praisenter.media.MediaLibrary;
import org.praisenter.media.MediaPlayer;
import org.praisenter.media.MediaPlayerListener;
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
	
	private static class TestFrame extends JFrame implements MediaPlayerListener {
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
				player = (XugglerMediaPlayer)MediaLibrary.getMediaPlayer(XugglerPlayableMedia.class);
				player.addMediaPlayerListener(this);
				player.setMedia(media);
				player.setLooped(true);
				player.play();
//				player = new XugglerMediaPlayer();
//				player.addMediaPlayerListener(this);
//				player.setMedia(media);
//				player.setLooped(true);
//				player.play();
			} catch (MediaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				Thread.sleep(5000);
				player.pause();
				Thread.sleep(2000);
				player.resume();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@Override
		public void paused() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onVideoPicture(BufferedImage image) {
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
		
		@Override
		public void seeked() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void resumed() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void started() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void stopped() {
			// TODO Auto-generated method stub
			
		}
	}
	
	private static class VideoImagePanel extends JPanel {
		BufferedImage image;
		public VideoImagePanel() {
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
//			super.paintComponent(g);
			g.drawImage(this.image, 0, 0, 300, 300, null);
		}
	}
}
