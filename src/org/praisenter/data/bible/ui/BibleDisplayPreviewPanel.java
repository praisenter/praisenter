package org.praisenter.data.bible.ui;

import org.praisenter.display.BibleDisplay;
import org.praisenter.display.ui.InlineDisplayPreviewPanel;
import org.praisenter.resources.Messages;

/**
 * Represents a panel that shows a preview of bible displays
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class BibleDisplayPreviewPanel extends InlineDisplayPreviewPanel<BibleDisplay> {
	/** The version id */
	private static final long serialVersionUID = -6376569581892016128L;
	
	/** The previous, current, and next verse display names */
	private static final String[] DISPLAY_NAMES = new String[] {
		Messages.getString("panel.bible.preview.previous"),
		Messages.getString("panel.bible.preview.current"),
		Messages.getString("panel.bible.preview.next")
	};
	
	/**
	 * Default constructor.
	 */
	public BibleDisplayPreviewPanel() {
		super(20, 5, DISPLAY_NAMES);
		
		this.displays.add(null);
		this.displays.add(null);
		this.displays.add(null);
	}
	
	/**
	 * Sets the previous verse display.
	 * @param display the display
	 */
	public void setPreviousVerseDisplay(BibleDisplay display) {
		this.displays.set(0, display);
	}
	
	/**
	 * Sets the current verse display.
	 * @param display the display
	 */
	public void setCurrentVerseDisplay(BibleDisplay display) {
		this.displays.set(1, display);
	}
	
	/**
	 * Sets the next verse display.
	 * @param display the display
	 */
	public void setNextVerseDisplay(BibleDisplay display) {
		this.displays.set(2, display);
	}
	
	/**
	 * Returns the previous verse display.
	 * @return {@link BibleDisplay}
	 */
	public BibleDisplay getPreviousVerseDisplay() {
		return this.displays.get(0);
	}
	
	/**
	 * Returns the current verse display.
	 * @return {@link BibleDisplay}
	 */
	public BibleDisplay getCurrentVerseDisplay() {
		return this.displays.get(1);	
	}
	
	/**
	 * Returns the next verse display.
	 * @return {@link BibleDisplay}
	 */
	public BibleDisplay getNextVerseDisplay() {
		return this.displays.get(2);
	}
}
