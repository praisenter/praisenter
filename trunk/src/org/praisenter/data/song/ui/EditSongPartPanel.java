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
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.praisenter.data.song.SongPart;
import org.praisenter.data.song.SongPartType;
import org.praisenter.ui.SelectTextFocusListener;

/**
 * Sub panel for viewing/editing song parts.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class EditSongPartPanel extends JPanel implements ItemListener, PropertyChangeListener, DocumentListener, ChangeListener {
	/** The version id */
	private static final long serialVersionUID = -6670959052726030508L;
	
	// data
	
	/** The song part being edited */
	private SongPart part;
	
	// controls
	
	/** The combo box of part types */
	private JComboBox<SongPartType> cmbPartTypes;
	
	/** The part index */
	private JFormattedTextField txtPartIndex;
	
	/** The font size spinner */
	private JSpinner spnFontSize;
	
	/** The part text */
	private JTextArea txtPartText;
	
	// temp
	
	/** True if notifications should be sent */
	private boolean notificationsDisabled;
	
	/**
	 * Full constructor.
	 * @param part the song part to edit; can be null
	 */
	public EditSongPartPanel(SongPart part) {
		this.part = part;
		this.notificationsDisabled = true;
		
		SongPartType type = SongPartType.CHORUS;
		int index = 1;
		int fontSize = 80;
		String text = "";
		boolean edit = false;
		
		if (part != null) {
			type = part.getType();
			index = part.getIndex();
			fontSize = part.getFontSize();
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
		
		this.spnFontSize = new JSpinner(new SpinnerNumberModel(fontSize, 1, Integer.MAX_VALUE, 1));
		this.spnFontSize.setEnabled(edit);
		this.spnFontSize.addChangeListener(this);
		JTextField txtFontSize = ((DefaultEditor)this.spnFontSize.getEditor()).getTextField();
		txtFontSize.setColumns(3);
		txtFontSize.addFocusListener(new SelectTextFocusListener(txtFontSize));
		
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
						.addComponent(this.txtPartIndex, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.spnFontSize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addComponent(pneText, 100, 200, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(this.cmbPartTypes, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.txtPartIndex, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.spnFontSize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addComponent(pneText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
	}
	
	// part type
	
	/* (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == this.cmbPartTypes) {
			SongPartType type = (SongPartType)this.cmbPartTypes.getSelectedItem();
			if (this.part != null) {
				this.part.setType(type);
				this.notifySongPartListeners();
			}
		}
	}
	
	// part index
	
	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == this.txtPartIndex) {
			Object o = evt.getNewValue();
			if (o instanceof Number) {
				Number n = (Number)o;
				if (this.part != null) {
					this.part.setIndex(n.intValue());
					this.notifySongPartListeners();
				}
			}
		}
	}
	
	// song text
	
	/* (non-Javadoc)
	 * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
	 */
	@Override
	public void changedUpdate(DocumentEvent e) {
		this.updateSongPartText(this.txtPartText.getText());
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
	 */
	@Override
	public void insertUpdate(DocumentEvent e) {
		this.updateSongPartText(this.txtPartText.getText());
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
	 */
	@Override
	public void removeUpdate(DocumentEvent e) {
		this.updateSongPartText(this.txtPartText.getText());
	}
	
	/**
	 * Called when the song part text is modified.
	 * @param text the new text
	 */
	private void updateSongPartText(String text) {
		if (this.part != null) {
			this.part.setText(text);
			this.notifySongPartListeners();
		}
	}
	
	// font size
	
	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == this.spnFontSize) {
			Object value = this.spnFontSize.getValue();
			if (this.part != null && value != null && value instanceof Number) {
				this.part.setFontSize(((Number)value).intValue());
				this.notifySongPartListeners();
			}
		}
	}
	
	/**
	 * Returns the song part currently being edited.
	 * @return {@link SongPart}
	 */
	public SongPart getSongPart() {
		return this.part;
	}
	
	/**
	 * Sets the current {@link SongPart} to edit.
	 * @param part the song part; can be null
	 */
	public void setSongPart(SongPart part) {
		this.part = part;
		
		SongPartType type = SongPartType.CHORUS;
		int index = 1;
		int fontSize = 80;
		String text = "";
		boolean edit = false;
		
		if (part != null) {
			type = part.getType();
			index = part.getIndex();
			fontSize = part.getFontSize();
			text = part.getText();
			edit = true;
		}
		
		this.notificationsDisabled = true;
		
		this.cmbPartTypes.setSelectedItem(type);
		this.txtPartIndex.setValue(index);
		this.spnFontSize.setValue(fontSize);
		this.txtPartText.setText(text);
		this.txtPartText.setCaretPosition(0);
		
		this.cmbPartTypes.setEnabled(edit);
		this.txtPartIndex.setEnabled(edit);
		this.spnFontSize.setEnabled(edit);
		this.txtPartText.setEnabled(edit);
		
		this.notificationsDisabled = false;
	}
	
	/**
	 * Called when any song part data changes.
	 * <p>
	 * Note: this does not indicate that the song was saved.
	 */
	private void notifySongPartListeners() {
		if (!this.notificationsDisabled) {
			SongPartListener[] listeners = this.getListeners(SongPartListener.class);
			for (SongPartListener listener : listeners) {
				listener.songPartChanged(this.part);
			}
		}
	}
	
	/**
	 * Adds a song part listener to listen for song part changes.
	 * @param listener the listener
	 */
	public void addSongPartListener(SongPartListener listener) {
		this.listenerList.add(SongPartListener.class, listener);
	}
}
