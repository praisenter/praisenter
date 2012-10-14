package org.praisenter.data.song.ui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;

import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.praisenter.data.song.Song;
import org.praisenter.data.song.SongPart;
import org.praisenter.data.song.SongPartType;
import org.praisenter.ui.SelectTextFocusListener;

/**
 * Sub panel for viewing/editing song parts.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SongPartPanel extends JPanel implements ItemListener, PropertyChangeListener, DocumentListener {
	/** The version id */
	private static final long serialVersionUID = -6670959052726030508L;
	
	// data
	
	private Song song;
	private SongPart part;
	
	// controls
	
	/** The combo box of part types */
	private JComboBox<SongPartType> cmbPartTypes;
	
	/** The part index */
	private JFormattedTextField txtPartIndex;
	
	/** The part text */
	private JTextArea txtPartText;
	
	// temp
	
	private boolean notificationsDisabled;
	
	/**
	 * Full constructor.
	 * @param song the song the part is on
	 * @param part the song part to edit; can be null
	 */
	public SongPartPanel(Song song, SongPart part) {
		this.song = song;
		this.part = part;
		this.notificationsDisabled = true;
		
		// FIXME for now we only allowing these modifications; later we may need to add/remove some fields from the SongPart class
		SongPartType type = SongPartType.CHORUS;
		int index = 1;
		String text = "";
		boolean edit = false;
		
		if (part != null) {
			type = part.getType();
			index = part.getIndex();
			text = part.getText();
			edit = true;
		}
		
		this.cmbPartTypes = new JComboBox<SongPartType>(SongPartType.values());
		this.cmbPartTypes.setRenderer(new SongPartTypeCellRenderer());
		this.cmbPartTypes.setSelectedItem(type);
		this.cmbPartTypes.setEnabled(edit);
		this.cmbPartTypes.addItemListener(this);
		
		this.txtPartIndex = new JFormattedTextField(NumberFormat.getIntegerInstance());
		this.txtPartIndex.addFocusListener(new SelectTextFocusListener(this.txtPartIndex));
		this.txtPartIndex.setValue(index);
		this.txtPartIndex.setColumns(3);
		this.txtPartIndex.setEnabled(edit);
		this.txtPartIndex.addPropertyChangeListener("value", this);
		
		this.txtPartText = new JTextArea(text);
		this.txtPartText.setRows(6);
		this.txtPartText.setLineWrap(true);
		this.txtPartText.setWrapStyleWord(true);
		this.txtPartText.setEnabled(edit);
		this.txtPartText.getDocument().addDocumentListener(this);
		JScrollPane pneText = new JScrollPane(this.txtPartText);
		
		this.notificationsDisabled = false;
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
						.addComponent(this.cmbPartTypes)
						.addComponent(this.txtPartIndex))
				.addComponent(pneText, 100, 200, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(this.cmbPartTypes, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.txtPartIndex, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addComponent(pneText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == this.cmbPartTypes) {
			SongPartType type = (SongPartType)this.cmbPartTypes.getSelectedItem();
			if (this.part != null) {
				this.part.setType(type);
				this.notifySongListeners();
			}
		}
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == this.txtPartIndex) {
			Object o = evt.getNewValue();
			if (o instanceof Number) {
				Number n = (Number)o;
				if (this.part != null) {
					this.part.setIndex(n.intValue());
					this.notifySongListeners();
				}
			}
		}
	}
	
	@Override
	public void changedUpdate(DocumentEvent e) {
		this.updateSongPartText(this.txtPartText.getText());
	}
	
	@Override
	public void insertUpdate(DocumentEvent e) {
		this.updateSongPartText(this.txtPartText.getText());
	}
	
	@Override
	public void removeUpdate(DocumentEvent e) {
		this.updateSongPartText(this.txtPartText.getText());
	}
	
	private void updateSongPartText(String text) {
		if (this.part != null) {
			this.part.setText(text);
			this.notifySongListeners();
		}
	}
	
	public SongPart getSongPart() {
		return this.part;
	}
	
	/**
	 * Sets the current {@link SongPart} to edit.
	 * @param song the song the part is on
	 * @param part the song part; can be null
	 */
	public void setSongPart(Song song, SongPart part) {
		this.song = song;
		this.part = part;
		
		SongPartType type = SongPartType.CHORUS;
		int index = 1;
		String text = "";
		boolean edit = false;
		
		if (part != null) {
			type = part.getType();
			index = part.getIndex();
			text = part.getText();
			edit = true;
		}
		
		this.notificationsDisabled = true;
		
		this.cmbPartTypes.setSelectedItem(type);
		this.txtPartIndex.setValue(index);
		this.txtPartText.setText(text);
		this.txtPartText.setCaretPosition(0);
		
		this.cmbPartTypes.setEnabled(edit);
		this.txtPartIndex.setEnabled(edit);
		this.txtPartText.setEnabled(edit);
		
		this.notificationsDisabled = false;
	}
	
	/**
	 * Called when any song part data changes.
	 * <p>
	 * Note: this does not indicate that the song was saved.
	 */
	private void notifySongListeners() {
		if (!this.notificationsDisabled) {
			SongListener[] listeners = this.getListeners(SongListener.class);
			for (SongListener listener : listeners) {
				listener.songChanged(this.song);
			}
		}
	}
	
	public void addSongListener(SongListener listener) {
		this.listenerList.add(SongListener.class, listener);
	}
}
