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

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.praisenter.application.resources.Messages;
import org.praisenter.application.slide.ui.preview.SingleSlidePreviewPanel;
import org.praisenter.application.ui.SelectTextFocusListener;
import org.praisenter.data.song.Song;
import org.praisenter.data.song.SongPart;
import org.praisenter.data.song.SongPartType;
import org.praisenter.slide.SongSlide;
import org.praisenter.slide.SongSlideTemplate;
import org.praisenter.slide.text.TextComponent;

/**
 * Sub panel for viewing/editing song parts.
 * @author William Bittle
 * @version 2.0.1
 * @since 1.0.0
 */
public class EditSongPartPanel extends JPanel implements ItemListener, DocumentListener, ChangeListener {
	/** The version id */
	private static final long serialVersionUID = -6670959052726030508L;
	
	// data
	
	/** The song containing the song part */
	private Song song;
	
	/** The song part being edited */
	private SongPart part;
	
	// controls
	
	/** Checkbox for being selected */
	private JCheckBox chkSelected;
	
	/** The combo box of part types */
	private JComboBox<SongPartType> cmbPartTypes;
	
	/** The part index */
	private JSpinner spnPartIndex;
	
	/** The font size spinner */
	private JSpinner spnFontSize;
	
	/** The part text */
	private JTextArea txtPartText;
	
	// preview
	
	/** The template to use for the preview */
	private SongSlideTemplate template;
	
	/** The preview panel */
	private SingleSlidePreviewPanel pnlPreview;
	
	// temp
	
	/** True if notifications should be sent */
	private boolean notificationsDisabled;
	
	/**
	 * Minimal constructor.
	 * @param template the initial song slide template
	 */
	public EditSongPartPanel(SongSlideTemplate template) {
		this(false, template);
	}
	
	/**
	 * Full constructor.
	 * @param selectable true if this panel is selectable; if true, this will show a checkbox to the left of the part type
	 * @param template the initial song slide template
	 */
	public EditSongPartPanel(boolean selectable, SongSlideTemplate template) {
		this.song = null;
		this.part = null;
		this.template = template;
		this.notificationsDisabled = true;
		
		this.pnlPreview = new SingleSlidePreviewPanel();
		// the maximum dimension (200 on the height)
		final int h = 100;
		// it should be ok to use the template width and height at this
		// time since it should already be adjusted for the primary display
		double s = (double)h / (double)template.getHeight();
		int w = (int)Math.floor(template.getWidth() * s);
		Dimension size = new Dimension(w, h);
		this.pnlPreview.setPreferredSize(size);
		
		SongPartType type = SongPartType.CHORUS;
		int index = 1;
		int fontSize = 80;
		String text = "";
		int[] takenIndices = null;
		boolean edit = false;
		
		this.chkSelected = new JCheckBox();
		this.chkSelected.setSelected(false);
		
		this.cmbPartTypes = new JComboBox<SongPartType>(SongPartType.values());
		this.cmbPartTypes.setToolTipText(Messages.getString("panel.song.type.tooltip"));
		this.cmbPartTypes.setRenderer(new SongPartTypeCellRenderer());
		this.cmbPartTypes.setSelectedItem(type);
		this.cmbPartTypes.setEnabled(edit);
		this.cmbPartTypes.addItemListener(this);
		
		this.spnPartIndex = new JSpinner(new SongPartIndexSpinnerModel(index, takenIndices));
		this.spnPartIndex.setToolTipText(Messages.getString("panel.song.index.tooltip"));
		this.spnPartIndex.setEnabled(edit);
		this.spnPartIndex.addChangeListener(this);
		this.spnPartIndex.setEditor(new JSpinner.NumberEditor(this.spnPartIndex, "0"));
		JFormattedTextField txtPartIndex = ((DefaultEditor)this.spnPartIndex.getEditor()).getTextField();
		txtPartIndex.setColumns(2);
		txtPartIndex.addFocusListener(new SelectTextFocusListener(txtPartIndex));
		
		this.spnFontSize = new JSpinner(new SpinnerNumberModel(fontSize, 1, Integer.MAX_VALUE, 1));
		this.spnFontSize.setToolTipText(Messages.getString("panel.song.fontSize.tooltip"));
		this.spnFontSize.setEnabled(edit);
		this.spnFontSize.addChangeListener(this);
		this.spnFontSize.setEditor(new JSpinner.NumberEditor(this.spnFontSize, "0"));
		JTextField txtFontSize = ((DefaultEditor)this.spnFontSize.getEditor()).getTextField();
		txtFontSize.setColumns(3);
		txtFontSize.addFocusListener(new SelectTextFocusListener(txtFontSize));
		
		this.txtPartText = new JTextArea(text);
		this.txtPartText.setRows(2);
		this.txtPartText.setLineWrap(true);
		this.txtPartText.setWrapStyleWord(true);
		this.txtPartText.setEnabled(edit);
		this.txtPartText.getDocument().addDocumentListener(this);
		JScrollPane pneText = new JScrollPane(this.txtPartText);
		
		this.notificationsDisabled = false;
		
		JPanel pnlConfigure = new JPanel();
		GroupLayout layout = new GroupLayout(pnlConfigure);
		pnlConfigure.setLayout(layout);
		
		SequentialGroup hGroup = layout.createSequentialGroup();
		ParallelGroup vGroup = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
		if (selectable) {
			hGroup.addComponent(this.chkSelected);
			vGroup.addComponent(this.chkSelected);
		}
		hGroup.addComponent(this.cmbPartTypes);
		hGroup.addComponent(this.spnPartIndex, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		hGroup.addComponent(this.spnFontSize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		vGroup.addComponent(this.cmbPartTypes, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		vGroup.addComponent(this.spnPartIndex, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		vGroup.addComponent(this.spnFontSize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addGroup(hGroup)
						.addComponent(pneText, 100, 200, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
						.addGroup(vGroup)
						.addComponent(pneText, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		
		layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addComponent(pnlConfigure)
				.addComponent(this.pnlPreview));
		layout.setVerticalGroup(layout.createParallelGroup()
				.addComponent(pnlConfigure)
				.addComponent(this.pnlPreview));
	}
	
	// part type
	
	/* (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == this.cmbPartTypes) {
			SongPartType type = (SongPartType)this.cmbPartTypes.getSelectedItem();
			if (this.song != null && this.part != null) {
				this.part.setType(type);
				// we need to also update the song index model with the new restricted indexes
				SongPartIndexSpinnerModel model = (SongPartIndexSpinnerModel)this.spnPartIndex.getModel();
				model.setExcludedIndices(this.getTakenIndices(type));
				this.notifySongPartListeners();
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
			this.updatePreview(this.part);
			this.notifySongPartListeners();
		}
	}
	
	// part index / font size
	
	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == this.spnFontSize) {
			Object value = this.spnFontSize.getValue();
			if (this.part != null && value != null && value instanceof Number) {
				this.part.setFontSize(((Number)value).intValue());
				this.updatePreview(this.part);
				this.notifySongPartListeners();
			}
		} else if (e.getSource() == this.spnPartIndex) {
			Object value = this.spnPartIndex.getValue();
			if (this.part != null && value != null && value instanceof Number) {
				this.part.setIndex(((Number)value).intValue());
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
	 * @param song the song containing the part; can be null
	 * @param part the song part; can be null
	 */
	public void setSongPart(Song song, SongPart part) {
		this.song = song;
		this.part = part;
		
		SongPartType type = SongPartType.CHORUS;
		int index = 1;
		int fontSize = 80;
		String text = "";
		int[] takenIndices = null;
		boolean edit = false;
		
		if (song != null && part != null) {
			type = part.getType();
			index = part.getIndex();
			fontSize = part.getFontSize();
			text = part.getText();
			takenIndices = this.getTakenIndices(part.getType());
			edit = true;
			this.updatePreview(part);
		} else {
			this.pnlPreview.setSlide(null);
			this.pnlPreview.repaint();
		}
		
		this.notificationsDisabled = true;
		
		this.cmbPartTypes.setSelectedItem(type);
		// get the model and update it (creating a new model makes the spinner recreate some
		// stuff like the textbox)
		SongPartIndexSpinnerModel model = (SongPartIndexSpinnerModel)this.spnPartIndex.getModel();
		model.setExcludedIndices(takenIndices);
		model.setValue(index);
		this.spnFontSize.setValue(fontSize);
		this.txtPartText.setText(text);
		this.txtPartText.setCaretPosition(0);
		
		this.cmbPartTypes.setEnabled(edit);
		this.spnPartIndex.setEnabled(edit);
		this.spnFontSize.setEnabled(edit);
		this.txtPartText.setEnabled(edit);
		
		this.notificationsDisabled = false;
	}
	
	/**
	 * Sets the previewing template for the song part.
	 * @param template the template
	 */
	public void setTemplate(SongSlideTemplate template) {
		this.template = template;
		this.pnlPreview.setSlide(null);
		if (this.part != null) {
			this.updatePreview(this.part);
		}
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

	/**
	 * Returns the part indices that are already taken by other parts.
	 * @param type the song part type
	 * @return int[]
	 */
	private int[] getTakenIndices(SongPartType type) {
		Song song = this.song;
		// loop over the song parts
		List<Integer> indices = new ArrayList<>(); 
		for (SongPart p : song.getParts()) {
			if (p != this.part && p.getType() == type) {
				indices.add(p.getIndex());
			}
		}
		int[] taken = new int[indices.size()];
		for (int i = 0; i < indices.size(); i++) {
			taken[i] = indices.get(i);
		}
		return taken;
	}
	
	/**
	 * Updates the preview with the given part.
	 * @param part the part
	 */
	private void updatePreview(SongPart part) {
		SongSlide slide = (SongSlide)pnlPreview.getSlide();
		if (slide == null) {
			slide = template.createSlide();
			pnlPreview.setSlide(slide);
		}
		TextComponent text = slide.getTextComponent();
		// update the text and font size
		text.setText(part.getText());
		text.setTextFont(text.getTextFont().deriveFont((float)part.getFontSize()));
		
		// update the slides (this method should not
		// do anything that should normally be done on the EDT)
		pnlPreview.setSlide(slide);
		pnlPreview.repaint();
	}
	
	/**
	 * Returns true if this part is selected.
	 * @return boolean
	 */
	public boolean isSelected() {
		return this.chkSelected.isSelected();
	}
}
