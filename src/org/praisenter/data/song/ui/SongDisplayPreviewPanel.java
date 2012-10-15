package org.praisenter.data.song.ui;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.util.ArrayList;
import java.util.List;

import org.praisenter.data.song.Song;
import org.praisenter.data.song.SongPart;
import org.praisenter.display.DisplayFactory;
import org.praisenter.display.SongDisplay;
import org.praisenter.display.ui.InlineDisplayPreviewPanel;
import org.praisenter.settings.GeneralSettings;
import org.praisenter.settings.SongSettings;

/**
 * Represents a panel that shows a preview of song displays.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SongDisplayPreviewPanel extends InlineDisplayPreviewPanel<SongDisplay> {
	/** The version id */
	private static final long serialVersionUID = -6376569581892016128L;
	
	/** The list of song part names */
	private List<String> names;
	
	/**
	 * Default constructor.
	 */
	public SongDisplayPreviewPanel() {
		super(10, 5);
		this.names = new ArrayList<String>();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.display.ui.MultipleDisplayPreviewPanel#getDisplayName(org.praisenter.display.Display, int)
	 */
	@Override
	protected String getDisplayName(SongDisplay display, int index) {
		return this.names.get(index);
	}
	
	/**
	 * Sets the currently displayed song.
	 * @param song the song
	 */
	public void setSong(Song song) {
		this.displays.clear();
		this.names.clear();
		
		GeneralSettings gSettings = GeneralSettings.getInstance();
		SongSettings sSettings = SongSettings.getInstance();
		
		Dimension displaySize = gSettings.getPrimaryDisplaySize();
		GraphicsDevice device = gSettings.getPrimaryOrDefaultDisplay();
		
		for (SongPart part : song.getParts()) {
			SongDisplay display = DisplayFactory.getDisplay(sSettings, displaySize);
			display.getTextComponent().setText(part.getText());
			display.getTextComponent().setTextFont(display.getTextComponent().getTextFont().deriveFont((float)part.getFontSize()));
			display.prepare(device.getDefaultConfiguration());
			//this.addDisplay(display, part.getName());
			this.displays.add(display);
			this.names.add(part.getName());
		}
	}
}
