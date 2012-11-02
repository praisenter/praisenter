package org.praisenter.media.ui;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;

public class MediaUITest {
	public static void main(String[] args) {
		new TestFrame();
	}
	
	private static class TestFrame extends JFrame {
		public TestFrame() {
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			Container container = this.getContentPane();
			container.setLayout(new BorderLayout());
			
			MediaLibraryPanel pnlMedia = new MediaLibraryPanel();
			container.add(pnlMedia, BorderLayout.CENTER);
			
			this.pack();
			this.setVisible(true);
		}
	}
}
