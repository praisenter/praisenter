package org.praisenter.media.ui;

import java.awt.Component;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.SwingConstants;

import org.praisenter.media.Thumbnail;

public class ThumbnailListCellRenderer extends DefaultListCellRenderer {
	private FileSystem fileSystem = FileSystems.getDefault();
	
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (value instanceof Thumbnail) {
			Thumbnail t = (Thumbnail)value;
			this.setIcon(new ImageIcon(t.getImage()));
			this.setHorizontalTextPosition(SwingConstants.CENTER);
			this.setVerticalTextPosition(SwingConstants.BOTTOM);
			this.setText(this.getFileName(t.getFileName()));
			this.setHorizontalAlignment(CENTER);
		}
		return this;
	}
	
	private String getFileName(String filePath) {
		Path path = fileSystem.getPath(filePath);
		return path.getFileName().toString();
	}
}
