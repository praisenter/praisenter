/*
 * Copyright (c) 2011-2013 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.praisenter.application.song.ui;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.praisenter.application.resources.Messages;
import org.praisenter.common.utilities.StringUtilities;
import org.praisenter.data.song.Song;
import org.praisenter.data.song.SongPart;
import org.praisenter.data.song.SongPartType;

/**
 * Panel used to quick send common song parts.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SongQuickSendPanel extends JPanel {
	/** The version id */
	private static final long serialVersionUID = 275321877547497400L;

	/** The mapping of hot keys */
	private static final Map<SongPartKey, String> HOTKEYS = getHotKeys();
	
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
			JButton button = new JButton(SongHelper.getPartName(SongPartType.VERSE, i + 1));
			button.setActionCommand("quickSend=VERSE|" + (i + 1));
			this.buttons.put(key, button);
		}
		
		// add the chorus buttons
		for (int i = 0; i < n; i++) {
			SongPartKey key = new SongPartKey(SongPartType.CHORUS, i + 1);
			JButton button = new JButton(SongHelper.getPartName(SongPartType.CHORUS, i + 1));
			button.setActionCommand("quickSend=CHORUS|" + (i + 1));
			this.buttons.put(key, button);
		}
		
		// add the other buttons
		// bridge 1
		{
			SongPartKey key = new SongPartKey(SongPartType.BRIDGE, 1);
			JButton button = new JButton(SongHelper.getPartName(SongPartType.BRIDGE, 1));
			button.setActionCommand("quickSend=BRIDGE|1");
			this.buttons.put(key, button);
		}
		// tag 1
		{
			SongPartKey key = new SongPartKey(SongPartType.TAG, 1);
			JButton button = new JButton(SongHelper.getPartName(SongPartType.TAG, 1));
			button.setActionCommand("quickSend=TAG|1");
			this.buttons.put(key, button);
		}
		// vamp 1
		{
			SongPartKey key = new SongPartKey(SongPartType.VAMP, 1);
			JButton button = new JButton(SongHelper.getPartName(SongPartType.VAMP, 1));
			button.setActionCommand("quickSend=VAMP|1");
			this.buttons.put(key, button);
		}
		// end 1
		{
			SongPartKey key = new SongPartKey(SongPartType.END, 1);
			JButton button = new JButton(SongHelper.getPartName(SongPartType.END, 1));
			button.setActionCommand("quickSend=END|1");
			this.buttons.put(key, button);
		}
		// other 1
		{
			SongPartKey key = new SongPartKey(SongPartType.OTHER, 1);
			JButton button = new JButton(SongHelper.getPartName(SongPartType.OTHER, 1));
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
				button.setToolTipText(StringUtilities.addLineBreaksAtInterval("<html><b>" + Messages.getString("panel.song.quickSend") + " (" + HOTKEYS.get(key) + ")</b>\n" + part.getText() + "</html>", 50, true));
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
	
	/**
	 * Returns a mapping of song part keys to function names.
	 * @return Map&lt;{@link SongPartType}, String&gt;
	 */
	private static final Map<SongPartKey, String> getHotKeys() {
		Map<SongPartKey, String> keys = new HashMap<SongPartKey, String>();
		
		keys.put(new SongPartKey(SongPartType.VERSE, 1), "F1");
		keys.put(new SongPartKey(SongPartType.VERSE, 2), "F2");
		keys.put(new SongPartKey(SongPartType.VERSE, 3), "F3");
		keys.put(new SongPartKey(SongPartType.VERSE, 4), "F4");
		keys.put(new SongPartKey(SongPartType.VERSE, 5), "F5");
		
		keys.put(new SongPartKey(SongPartType.CHORUS, 1), "F6");
		keys.put(new SongPartKey(SongPartType.CHORUS, 2), "F7");
		keys.put(new SongPartKey(SongPartType.CHORUS, 3), "F8");
		keys.put(new SongPartKey(SongPartType.CHORUS, 4), "F9");
		keys.put(new SongPartKey(SongPartType.CHORUS, 5), "F10");
		
		keys.put(new SongPartKey(SongPartType.BRIDGE, 1), "F11");
		keys.put(new SongPartKey(SongPartType.TAG, 1), "F12");
		keys.put(new SongPartKey(SongPartType.VAMP, 1), "Shift + F1");
		keys.put(new SongPartKey(SongPartType.END, 1), "Shift + F2");
		keys.put(new SongPartKey(SongPartType.OTHER, 1), "Shift + F3");
		
		return keys;
	}
	
	/**
	 * Returns a listing of actions that call the given listener.
	 * @param listener the listener to be called
	 * @return {@link SongQuickSendAction}[]
	 */
	public static final SongQuickSendAction[] getQuickSendActions(ActionListener listener) {
		return new SongQuickSendAction[] {
			// verse
			new SongQuickSendAction(listener, KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "quickSend=VERSE|1"),
			new SongQuickSendAction(listener, KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "quickSend=VERSE|2"),
			new SongQuickSendAction(listener, KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), "quickSend=VERSE|3"),
			new SongQuickSendAction(listener, KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), "quickSend=VERSE|4"),
			new SongQuickSendAction(listener, KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "quickSend=VERSE|5"),
			// chorus
			new SongQuickSendAction(listener, KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0), "quickSend=CHORUS|1"),
			new SongQuickSendAction(listener, KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0), "quickSend=CHORUS|2"),
			new SongQuickSendAction(listener, KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0), "quickSend=CHORUS|3"),
			new SongQuickSendAction(listener, KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), "quickSend=CHORUS|4"),
			new SongQuickSendAction(listener, KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0), "quickSend=CHORUS|5"),
			// others
			new SongQuickSendAction(listener, KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0), "quickSend=BRIDGE|1"),
			new SongQuickSendAction(listener, KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0), "quickSend=TAG|1"),
			new SongQuickSendAction(listener, KeyStroke.getKeyStroke(KeyEvent.VK_F1, InputEvent.SHIFT_MASK), "quickSend=VAMP|1"),
			new SongQuickSendAction(listener, KeyStroke.getKeyStroke(KeyEvent.VK_F2, InputEvent.SHIFT_MASK), "quickSend=END|1"),
			new SongQuickSendAction(listener, KeyStroke.getKeyStroke(KeyEvent.VK_F3, InputEvent.SHIFT_MASK), "quickSend=OTHER|1"),
		};
	}
}
