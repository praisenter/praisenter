package org.praisenter.data.song.ui;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.praisenter.data.song.Song;
import org.praisenter.data.song.SongPart;
import org.praisenter.data.song.SongPartType;
import org.praisenter.resources.Messages;
import org.praisenter.utilities.StringUtilities;

// TODO add F1...F12 hot key support to buttons
/**
 * Panel used to quick send common song parts.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SongQuickSendPanel extends JPanel {
	/** The version id */
	private static final long serialVersionUID = 275321877547497400L;

	/** The listener for all the controls on this panel */
	protected ActionListener listener;
	
	/** The map of quick send buttons */
	protected SortedMap<SongPartKey, JButton> buttons;
	
	/**
	 * Full constructor.
	 * @param listener the listener for the send events
	 */
	public SongQuickSendPanel(ActionListener listener) {
		this.listener = listener;
		this.buttons = new TreeMap<SongPartKey, JButton>();
		
		// initially all buttons should be disabled
		
		// add the verse buttons
		final int n = 5;
		for (int i = 0; i < n; i++) {
			SongPartKey key = new SongPartKey(SongPartType.VERSE, i + 1);
			JButton button = new JButton(MessageFormat.format(Messages.getString("song.part.name.pattern"), SongPartType.VERSE.getName(), i + 1));
			button.setActionCommand("quickSend=VERSE|" + (i + 1));
			this.buttons.put(key, button);
		}
		
		// add the chorus buttons
		for (int i = 0; i < n; i++) {
			SongPartKey key = new SongPartKey(SongPartType.CHORUS, i + 1);
			JButton button = new JButton(MessageFormat.format(Messages.getString("song.part.name.pattern"), SongPartType.CHORUS.getName(), i + 1));
			button.setActionCommand("quickSend=CHORUS|" + (i + 1));
			this.buttons.put(key, button);
		}
		
		// add the other buttons
		// bridge 1
		{
			SongPartKey key = new SongPartKey(SongPartType.BRIDGE, 1);
			JButton button = new JButton(MessageFormat.format(Messages.getString("song.part.name.pattern"), SongPartType.BRIDGE.getName(), 1));
			button.setActionCommand("quickSend=BRIDGE|1");
			this.buttons.put(key, button);
		}
		// tag 1
		{
			SongPartKey key = new SongPartKey(SongPartType.TAG, 1);
			JButton button = new JButton(MessageFormat.format(Messages.getString("song.part.name.pattern"), SongPartType.TAG.getName(), 1));
			button.setActionCommand("quickSend=TAG|1");
			this.buttons.put(key, button);
		}
		// vamp 1
		{
			SongPartKey key = new SongPartKey(SongPartType.VAMP, 1);
			JButton button = new JButton(MessageFormat.format(Messages.getString("song.part.name.pattern"), SongPartType.VAMP.getName(), 1));
			button.setActionCommand("quickSend=VAMP|1");
			this.buttons.put(key, button);
		}
		// end 1
		{
			SongPartKey key = new SongPartKey(SongPartType.END, 1);
			JButton button = new JButton(MessageFormat.format(Messages.getString("song.part.name.pattern"), SongPartType.END.getName(), 1));
			button.setActionCommand("quickSend=END|1");
			this.buttons.put(key, button);
		}
		// other 1
		{
			SongPartKey key = new SongPartKey(SongPartType.OTHER, 1);
			JButton button = new JButton(MessageFormat.format(Messages.getString("song.part.name.pattern"), SongPartType.OTHER.getName(), 1));
			button.setActionCommand("quickSend=OTHER|1");
			this.buttons.put(key, button);
		}
		
		// this will make 3 rows of 5 columns each
		this.setLayout(new GridLayout(3, n, 0, 0));
		
		for (JButton button : this.buttons.values()) {
			// start by all being disabled
			button.setEnabled(false);
			// add the action listener
			button.addActionListener(this.listener);
			// add the button to the layout
			this.add(button);
		}
	}
	
	/**
	 * Updates the state of the buttons given the new song.
	 * @param song the song
	 */
	public void setButtonsEnabled(Song song) {
		// disable all buttons
		this.setButtonsEnabled(false);
		// clear the tooltip texts
		for (JButton button : this.buttons.values()) {
			button.setToolTipText(null);
		}
		// only enable the buttons that are available for the song
		for (SongPart part : song.getParts()) {
			SongPartKey key = new SongPartKey(part.getType(), part.getIndex());
			JButton button = this.buttons.get(key);
			// we only show 5 verses, 5 choruses, and 1 bridge, tag, end, and other
			// so its possible that the button doesnt exist for the part
			if (button != null) {
				button.setEnabled(true);
				button.setToolTipText(StringUtilities.addLineBreaksAtInterval("<html><b>" + Messages.getString("panel.songs.quickSend") + "</b>\n" + part.getText() + "</html>", 100, true));
			}
		}
	}
	
	/**
	 * Sets all the buttons to enabled or disabled. 
	 * @param flag true if all the buttons should be enabled.
	 */
	public void setButtonsEnabled(boolean flag) {
		// also disable all the buttons
		for (JButton button : this.buttons.values()) {
			button.setEnabled(flag);
		}
	}
}
