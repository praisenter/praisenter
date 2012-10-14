package org.praisenter.data.song.ui;

import java.util.ArrayList;
import java.util.List;

import org.praisenter.display.SongDisplay;
import org.praisenter.display.ui.InlineDisplayPreviewPanel;
import org.praisenter.display.ui.TabularDisplayPreviewPanel;

/**
 * Represents a panel that shows a preview of bible displays
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
//public class SongDisplayPreviewPanel extends TabularDisplayPreviewPanel<SongDisplay> {
public class SongDisplayPreviewPanel extends InlineDisplayPreviewPanel<SongDisplay> {
	/** The version id */
	private static final long serialVersionUID = -6376569581892016128L;
	
	private List<String> names;
	
	/**
	 * Default constructor.
	 */
	public SongDisplayPreviewPanel() {
		super(20, 5);
		this.names = new ArrayList<String>();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.display.ui.MultipleDisplayPreviewPanel#getDisplayName(org.praisenter.display.Display, int)
	 */
	@Override
	protected String getDisplayName(SongDisplay display, int index) {
		return this.names.get(index);
	}
	
	public void addDisplay(SongDisplay display, String name) {
		this.displays.add(display);
		this.names.add(name);
	}
	
	public void addDisplays(List<SongDisplay> displays, List<String> names) {
		this.displays = displays;
		this.names = names;
	}
	
	public void removeDisplays() {
//		this.imageCache.clear();
		this.displays.clear();
		this.names.clear();
	}
}
