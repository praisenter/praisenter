package org.praisenter.data.song.ui;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.praisenter.data.song.Song;
import org.praisenter.data.song.SongPart;
import org.praisenter.display.DisplayFactory;
import org.praisenter.display.SongDisplay;
import org.praisenter.display.ui.InlineDisplayPreviewPanel;
import org.praisenter.preferences.GeneralSettings;
import org.praisenter.preferences.SongPreferences;

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
	
	/** A mapping of the song parts to their respective displays */
	private Map<SongPartKey, SongDisplay> map;
	
	/**
	 * Default constructor.
	 */
	public SongDisplayPreviewPanel() {
		super(10, 5);
		this.names = new ArrayList<String>();
		this.map = new HashMap<SongPartKey, SongDisplay>();
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
		
		if (song != null) {
			GeneralSettings gSettings = GeneralSettings.getInstance();
			SongPreferences sSettings = SongPreferences.getInstance();
			
			Dimension displaySize = gSettings.getPrimaryDisplaySize();
			GraphicsDevice device = gSettings.getPrimaryOrDefaultDisplay();
			
			for (SongPart part : song.getParts()) {
				SongDisplay display = DisplayFactory.getDisplay(sSettings, displaySize);
				display.getTextComponent().setText(part.getText());
				display.getTextComponent().setTextFont(display.getTextComponent().getTextFont().deriveFont((float)part.getFontSize()));
				display.prepare(device.getDefaultConfiguration());
				this.displays.add(display);
				this.map.put(new SongPartKey(part.getType(), part.getIndex()), display);
				this.names.add(part.getName());
			}
		}
	}
	
	/**
	 * Returns the display for the given song part.
	 * <p>
	 * Returns null if the display does not exist.
	 * @param part the song part
	 * @return {@link SongDisplay}
	 */
	public SongDisplay getDisplay(SongPart part) {
		SongPartKey key = new SongPartKey(part.getType(), part.getIndex());
		return this.getDisplay(key);
	}
	
	/**
	 * Returns the display for the given song part key.
	 * <p>
	 * Returns null if the display does not exist.
	 * @param key the key
	 * @return {@link SongDisplay}
	 */
	public SongDisplay getDisplay(SongPartKey key) {
		return this.map.get(key);
	}
}
