package org.praisenter.slide.ui.editor;

import java.awt.Component;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.text.MessageFormat;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.praisenter.resources.Messages;
import org.praisenter.slide.Resolution;
import org.praisenter.utilities.WindowUtilities;

/**
 * List cell renderer for a list of {@link Resolution} objects.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class ResolutionListCellRenderer extends DefaultListCellRenderer {
	/** The version id */
	private static final long serialVersionUID = 7195442835688788991L;
	
	/* (non-Javadoc)
	 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
	 */
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
		GraphicsDevice[] devices = WindowUtilities.getScreenDevices();
		if (value instanceof Resolution) {
			Resolution r = (Resolution)value;
			for (GraphicsDevice device : devices) {
				DisplayMode mode = device.getDisplayMode();
				Resolution o = new Resolution(mode.getWidth(), mode.getHeight());
				if (r.equals(o)) {
					this.setText(MessageFormat.format(Messages.getString("resolution.format.native"), r.getWidth(), r.getHeight()));
				}
			}
		}
		
		return this;
	}
}
