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
package org.praisenter.application.slide.ui.editor;

import java.awt.Graphics;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.praisenter.application.resources.Messages;
import org.praisenter.application.ui.SelectTextFocusListener;
import org.praisenter.application.ui.WaterMark;
import org.praisenter.slide.text.DateTimeComponent;

/**
 * Editor panel for {@link DateTimeComponent}s.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class DateTimeComponentEditorPanel extends TextComponentEditorPanel<DateTimeComponent> implements ItemListener, DocumentListener, ChangeListener {
	/** The version id */
	private static final long serialVersionUID = 7124306861519471498L;

	// controls
	
	/** The date time format label */
	private JLabel lblDateTimeFormat;
	
	/** The date time format text box */
	private JTextField txtDateTimeFormat;

	/** The date time update check box */
	private JCheckBox chkDateTimeUpdate;
	
	/** The common date time format list */
	private JComboBox<DateTimeFormat> cmbDateTimeFormats;
	
	/**
	 * Default constructor.
	 */
	public DateTimeComponentEditorPanel() {
		this(true);
	}
	
	/**
	 * Full constructor.
	 * @param layout true if this component should layout its controls
	 */
	@SuppressWarnings("serial")
	public DateTimeComponentEditorPanel(boolean layout) {
		// pass false down so that TextComponentEditorPanel doesn't build
		// the layout, we need this class to build its own layout
		super(false);
		
		this.lblDateTimeFormat = new JLabel(Messages.getString("panel.slide.editor.datetime.format"));
		this.txtDateTimeFormat = new JTextField() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				WaterMark.paintTextWaterMark(g, this, Messages.getString("panel.slide.editor.datetime.format"));
			}
		};
		this.txtDateTimeFormat.getDocument().addDocumentListener(this);
		this.txtDateTimeFormat.addFocusListener(new SelectTextFocusListener(this.txtDateTimeFormat));
		
		this.chkDateTimeUpdate = new JCheckBox(Messages.getString("panel.slide.editor.datetime.update"));
		this.chkDateTimeUpdate.setToolTipText(Messages.getString("panel.slide.editor.datetime.update.tooltip"));
		this.chkDateTimeUpdate.addChangeListener(this);
		
		this.cmbDateTimeFormats = new JComboBox<DateTimeFormat>(DateTimeFormat.values());
		this.cmbDateTimeFormats.addItemListener(this);
		this.cmbDateTimeFormats.setToolTipText(Messages.getString("panel.slide.editor.datetime.format00"));
		
		this.createLayout();
	}

	/**
	 * Creates the layout for an image media component.
	 */
	protected void createLayout() {
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblName)
						.addComponent(this.lblBackground)
						.addComponent(this.lblBorder)
						.addComponent(this.lblFontFamily)
						.addComponent(this.lblFontSize)
						.addComponent(this.lblLayout)
						.addComponent(this.lblOutline)
						.addComponent(this.lblText)
						.addComponent(this.lblDateTimeFormat))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.txtName)
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.btnBackgroundFill)
								.addComponent(this.chkBackgroundVisible))
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.btnBorderFill)
								.addComponent(this.btnBorderStyle)
								.addComponent(this.chkBorderVisible))
						// font row
						.addComponent(this.cmbFontFamilies)
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.pnlBoldItalic, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(this.btnFillEditor))
						// size row
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.spnFontSize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(this.cmbFontScaleType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						// layout row
						.addComponent(this.pnlHorizontalTextAlignment, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					    // layout row 2
						.addComponent(this.pnlVerticalTextAlignment, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						// layout row 3
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.spnPadding, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(this.chkWrapText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						// outline row
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.btnOutlineFillEditor)
								.addComponent(this.btnOutlineStyleEditor)
								.addComponent(this.chkOutlineVisible))
						// date/time row
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.chkTextVisible)
								.addComponent(this.chkDateTimeUpdate))
						.addComponent(this.cmbDateTimeFormats)
						.addComponent(this.txtDateTimeFormat)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblName)
						.addComponent(this.txtName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblBackground)
						.addComponent(this.btnBackgroundFill)
						.addComponent(this.chkBackgroundVisible))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblBorder)
						.addComponent(this.btnBorderFill)
						.addComponent(this.btnBorderStyle)
						.addComponent(this.chkBorderVisible))
				// font row
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblFontFamily)
						.addComponent(this.cmbFontFamilies, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.pnlBoldItalic, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.btnFillEditor))
				// size row
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblFontSize)
						.addComponent(this.spnFontSize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.cmbFontScaleType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				// layout row
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblLayout)
						.addComponent(this.pnlHorizontalTextAlignment, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				// layout row 2
				.addComponent(this.pnlVerticalTextAlignment, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				// layout row 3
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.spnPadding, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.chkWrapText))
				// outline row
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblOutline)
						.addComponent(this.btnOutlineFillEditor)
						.addComponent(this.btnOutlineStyleEditor)
						.addComponent(this.chkOutlineVisible))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblText)
						.addComponent(this.chkTextVisible)
						.addComponent(this.chkDateTimeUpdate))
				// date/time row
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblDateTimeFormat)
						.addComponent(this.cmbDateTimeFormats, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addComponent(this.txtDateTimeFormat, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public void itemStateChanged(ItemEvent event) {
		super.itemStateChanged(event);
		
		if (event.getSource() == this.cmbDateTimeFormats) {
			DateTimeFormat format = (DateTimeFormat)this.cmbDateTimeFormats.getSelectedItem();
			this.txtDateTimeFormat.setText(format.getFormat());
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent event) {
		// send to the super class first
		super.stateChanged(event);
		
		if (event.getSource() == this.chkDateTimeUpdate) {
			if (this.slideComponent != null) {
				this.slideComponent.setDateTimeUpdateEnabled(this.chkDateTimeUpdate.isSelected());
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.editor.TextComponentEditorPanel#insertUpdate(javax.swing.event.DocumentEvent)
	 */
	@Override
	public void insertUpdate(DocumentEvent e) {
		super.insertUpdate(e);
		
		if (e.getDocument() == this.txtDateTimeFormat.getDocument()) {
			if (this.slideComponent != null) {
				this.slideComponent.setDateTimeFormat(this.txtDateTimeFormat.getText());
				this.notifyEditorListeners();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.editor.TextComponentEditorPanel#removeUpdate(javax.swing.event.DocumentEvent)
	 */
	@Override
	public void removeUpdate(DocumentEvent e) {
		super.removeUpdate(e);
		
		if (e.getDocument() == this.txtDateTimeFormat.getDocument()) {
			if (this.slideComponent != null) {
				this.slideComponent.setDateTimeFormat(this.txtDateTimeFormat.getText());
				this.notifyEditorListeners();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.editor.TextComponentEditorPanel#setSlideComponent(org.praisenter.slide.text.TextComponent, boolean)
	 */
	@Override
	public void setSlideComponent(DateTimeComponent slideComponent, boolean isStatic) {
		super.setSlideComponent(slideComponent, isStatic);
		
		this.disableNotification();
		if (slideComponent != null) {
			this.txtDateTimeFormat.setText(slideComponent.getDateTimeFormat());
			this.chkDateTimeUpdate.setSelected(slideComponent.isDateTimeUpdateEnabled());
		} else {
			this.txtDateTimeFormat.setText("EEEE MMMM, d yyyy");
			this.chkDateTimeUpdate.setSelected(false);
		}
		this.enableNotification();
	}
}
