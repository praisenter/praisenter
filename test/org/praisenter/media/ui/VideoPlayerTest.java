package org.praisenter.media.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import org.praisenter.media.MediaException;
import org.praisenter.media.MediaLibrary;
import org.praisenter.media.NoMediaLoaderException;
import org.praisenter.media.PlayableMediaListener;
import org.praisenter.media.VideoMedia;
import org.praisenter.utilities.LookAndFeelUtilities;

public class VideoPlayerTest {
	public static void main(String[] args) {
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
		
		new TestFrame();
	}
	
	private static class TestFrame extends JFrame implements PlayableMediaListener {
		VideoMedia media = null;
		VideoImagePanel pnlImage;
		public TestFrame() {
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			Container container = this.getContentPane();
			container.setLayout(new BorderLayout());
			
			pnlImage = new VideoImagePanel();
			
			try {
				media = (VideoMedia)MediaLibrary.getMedia("media\\videos\\big_buck_bunny.ogv");
				media.addMediaListener(this);
				media.play();
			} catch (MediaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			container.add(pnlImage, BorderLayout.CENTER);
			
			this.pack();
			this.setVisible(true);
		}
		
		@Override
		public void paused() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void updated() {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					pnlImage.setImage(media.getCurrentFrame());
				}
			});
		}
		
		@Override
		public void seeked() {
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
			this.image = image;
			this.repaint();
		}
		
		@Override
		protected void paintComponent(Graphics g) {
//			super.paintComponent(g);
			g.drawImage(this.image, 0, 0, null);
		}
	}
}
