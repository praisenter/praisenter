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
package org.praisenter.application.bible.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxEditor;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;
import org.praisenter.animation.TransitionAnimator;
import org.praisenter.animation.easings.Easing;
import org.praisenter.animation.easings.Easings;
import org.praisenter.animation.transitions.Transition;
import org.praisenter.animation.transitions.TransitionType;
import org.praisenter.animation.transitions.Transitions;
import org.praisenter.application.errors.ui.ExceptionDialog;
import org.praisenter.application.icons.Icons;
import org.praisenter.application.preferences.BiblePreferences;
import org.praisenter.application.preferences.Preferences;
import org.praisenter.application.preferences.ui.PreferencesListener;
import org.praisenter.application.resources.Messages;
import org.praisenter.application.slide.ui.SlideLibraryListener;
import org.praisenter.application.slide.ui.SlideThumbnailComboBoxRenderer;
import org.praisenter.application.slide.ui.TransitionListCellRenderer;
import org.praisenter.application.slide.ui.preview.InlineSlidePreviewPanel;
import org.praisenter.application.ui.AutoCompleteComboBoxEditor;
import org.praisenter.application.ui.EmptyNumberFormatter;
import org.praisenter.application.ui.SelectTextFocusListener;
import org.praisenter.application.ui.WaterMark;
import org.praisenter.common.NotInitializedException;
import org.praisenter.common.utilities.StringUtilities;
import org.praisenter.common.utilities.WindowUtilities;
import org.praisenter.data.DataException;
import org.praisenter.data.bible.Bible;
import org.praisenter.data.bible.BibleSearchType;
import org.praisenter.data.bible.Bibles;
import org.praisenter.data.bible.Book;
import org.praisenter.data.bible.Verse;
import org.praisenter.presentation.ClearEvent;
import org.praisenter.presentation.PresentationEventConfiguration;
import org.praisenter.presentation.PresentationManager;
import org.praisenter.presentation.PresentationWindowType;
import org.praisenter.presentation.SendEvent;
import org.praisenter.slide.BibleSlide;
import org.praisenter.slide.BibleSlideTemplate;
import org.praisenter.slide.Slide;
import org.praisenter.slide.SlideFile;
import org.praisenter.slide.SlideLibrary;
import org.praisenter.slide.SlideLibraryException;
import org.praisenter.slide.SlideThumbnail;
import org.praisenter.slide.text.TextComponent;

/**
 * Panel for bible lookup and searching.
 * @author William Bittle
 * @version 2.0.0
 * @since 1.0.0
 */
public class BiblePanel extends JPanel implements ActionListener, ItemListener, PreferencesListener, SlideLibraryListener {
	/** The version id */
	private static final long serialVersionUID = 5706187704789309806L;

	/** Static logger */
	private static final Logger LOGGER = Logger.getLogger(BiblePanel.class);
	
	// normal bible lookup
	
	/** The primary bible combo box */
	private JComboBox<Bible> cmbBiblesPrimary;
	
	/** The secondary bible combo box */
	private JComboBox<Bible> cmbBiblesSecondary;
	
	/** The checkbox to use the secondary bible in addition to the first */
	private JCheckBox chkUseSecondaryBible;
	
	/** The combo box of books (for the selected bible) */
	private JComboBox<Book> cmbBooks;
	
	/** The chapter count label */
	private JLabel lblChapterCount;
	
	/** The text box for the chapter number */
	private JFormattedTextField txtChapter;
	
	/** The verse count label */
	private JLabel lblVerseCount;
	
	/** The text box for the verse number */
	private JFormattedTextField txtVerse;
	
	/** The found/not-found verse label */
	private JLabel lblFound;
	
	/** The table of queued verses */
	private JTable tblVerseQueue;

	/** The template combo box */
	private JComboBox<SlideThumbnail> cmbTemplates;
	
	/** The combo box of transitions for sending */
	private JComboBox<Transition> cmbSendTransitions;
	
	/** The text box of send transition duration */
	private JFormattedTextField txtSendTransitions;
	
	/** The combo box of transitions for clearing */
	private JComboBox<Transition> cmbClearTransitions;
	
	/** The text box of clear transition duration */
	private JFormattedTextField txtClearTransitions;
	
	// bible searching
	
	/** The bible searching thread */
	private BibleSearchThread bibleSearchThread;
	
	/** The bible searching text box */
	private JTextField txtBibleSearch;
	
	/** The bible searching type combo box */
	private JComboBox<BibleSearchType> cmbBibleSearchType;
	
	/** The bible search results label */
	private JLabel lblBibleSearchResults;
	
	/** The bible search results table */
	private JTable tblBibleSearchResults;
	
	// preview
	
	/** The preview panel */
	private InlineSlidePreviewPanel pnlPreview;
	
	// state
	
	/** True if the user has found a verse */
	private boolean verseFound;

	// preferences 
	
	/** A local reference to the preferences */
	private Preferences preferences = Preferences.getInstance();
	
	/** A local references to the bible preferences */
	private BiblePreferences bPreferences = this.preferences.getBiblePreferences();
	
	/**
	 * Default constructor.
	 */
	@SuppressWarnings("serial")
	public BiblePanel() {
		this.verseFound = false;
		
		// get the primary device
		GraphicsDevice device = this.preferences.getPrimaryOrDefaultDevice();
		
		// get the bible slide template
		BibleSlideTemplate template = this.getTemplate();
		
		// create the slides
		BibleSlide previous = template.createSlide();
		BibleSlide current = template.createSlide();
		BibleSlide next = template.createSlide();

		// set the initial text
		previous.setName(Messages.getString("panel.bible.preview.previous"));
		previous.getScriptureLocationComponent().setText(Messages.getString("slide.bible.location.default"));
		previous.getScriptureTextComponent().setText(Messages.getString("slide.bible.text.default"));
		current.setName(Messages.getString("panel.bible.preview.current"));
		current.getScriptureLocationComponent().setText(Messages.getString("slide.bible.location.default"));
		current.getScriptureTextComponent().setText(Messages.getString("slide.bible.text.default"));
		next.setName(Messages.getString("panel.bible.preview.next"));
		next.getScriptureLocationComponent().setText(Messages.getString("slide.bible.location.default"));
		next.getScriptureTextComponent().setText(Messages.getString("slide.bible.text.default"));
		
		SlideThumbnail[] thumbnails = this.getThumbnails();
		SlideThumbnail selected = this.getSelectedThumbnail(thumbnails);
		this.cmbTemplates = new JComboBox<SlideThumbnail>(thumbnails);
		if (selected != null) {
			this.cmbTemplates.setSelectedItem(selected);
		}
		this.cmbTemplates.setToolTipText(Messages.getString("panel.template"));
		this.cmbTemplates.setRenderer(new SlideThumbnailComboBoxRenderer());
		this.cmbTemplates.addItemListener(this);
		
		// create the preview panel
		this.pnlPreview = new InlineSlidePreviewPanel(10, 5);
		this.pnlPreview.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY), BorderFactory.createEmptyBorder(15, 15, 15, 15)));
		
		this.pnlPreview.addSlide(previous);
		this.pnlPreview.addSlide(current);
		this.pnlPreview.addSlide(next);
		
		this.pnlPreview.setMinimumSize(100);
		
		// normal bible lookup
		
		// get the bibles
		Bible[] bibles = null;
		try {
			bibles = Bibles.getBibles().toArray(new Bible[0]);
		} catch (DataException ex) {
			LOGGER.error("Bibles could not be retrieved:", ex);
		}
		
		// the bible combobox
		JLabel lblPrimaryBible = new JLabel(Messages.getString("panel.bible.primary"));
		if (bibles == null) {
			this.cmbBiblesPrimary = new JComboBox<Bible>();
		} else {
			this.cmbBiblesPrimary = new JComboBox<Bible>(bibles);
		}
		this.cmbBiblesPrimary.setToolTipText(Messages.getString("panel.bible.primary.tooltip"));
		this.cmbBiblesPrimary.setRenderer(new BibleListCellRenderer());
		this.cmbBiblesPrimary.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				// only perform the following when the event is a selected event
				if (e.getStateChange() == ItemEvent.SELECTED) {
					// look up the number of chapters in the book
					Bible bible = (Bible)cmbBiblesPrimary.getSelectedItem();
					if (bible != null) {
						// load up all the books
						try {
							boolean ia = bPreferences.isApocryphaIncluded();
							List<Book> books = Bibles.getBooks(bible, ia);
							Book selected = (Book)cmbBooks.getSelectedItem();
							int index = 0;
							int i = 0;
							cmbBooks.removeAllItems();
							for (Book book : books) {
								cmbBooks.addItem(book);
								// see if the previously selected book is in this
								// book listing
								if (selected != null && selected.isSameBook(book)) {
									index = i;
								}
								i++;
							}
							// select the first book
							if (books.size() > 0) {
								cmbBooks.setSelectedIndex(index);
							} else {
								cmbBooks.setSelectedItem(null);
							}
							updateLabels();
						} catch (DataException ex) {
							ExceptionDialog.show(
									BiblePanel.this, 
									Messages.getString("panel.bible.data.bookListing.exception.title"), 
									MessageFormat.format(Messages.getString("panel.bible.data.bookListing.exception.text"), bible.getName()), 
									ex);
						}
					}
				}
			}
		});
		
		// secondary bible
		JLabel lblSecondaryBible = new JLabel(Messages.getString("panel.bible.secondary"));
		if (bibles == null) {
			this.cmbBiblesSecondary = new JComboBox<Bible>();
		} else {
			this.cmbBiblesSecondary = new JComboBox<Bible>(bibles);
		}
		this.cmbBiblesSecondary.setToolTipText(Messages.getString("panel.bible.secondary.tooltip"));
		this.cmbBiblesSecondary.setRenderer(new BibleListCellRenderer());
		
		this.chkUseSecondaryBible = new JCheckBox(Messages.getString("panel.bible.secondary.use"));
		this.chkUseSecondaryBible.setToolTipText(Messages.getString("panel.bible.secondary.use.tooltip"));
		this.chkUseSecondaryBible.setSelected(bPreferences.isSecondaryTranslationEnabled());
		
		// get the books
		List<Book> books = new ArrayList<Book>();
		// get the default bible
		Bible bible = null;
		try {
			int id = bPreferences.getPrimaryTranslationId();
			if (id > 0) {
				bible = Bibles.getBible(id);
			}
			// the default bible wasn't found
			if (bible == null && bibles != null && bibles.length > 0) {
				bible = bibles[0];
			}
			
		} catch (DataException ex) {
			LOGGER.error("Default bible could not be retrieved:", ex);
		}
		
		if (bible != null) {
			try {
				boolean ia = bPreferences.isApocryphaIncluded();
				books = Bibles.getBooks(bible, ia);
			} catch (DataException ex) {
				LOGGER.error("An error occurred when trying to get the listing of books for the bible: " + bible.getName(), ex);
			}
		} else {
			LOGGER.error("The selected bible is null; index = 0");
		}
		
		// book combo box
		this.cmbBooks = new JComboBox<Book>(books.toArray(new Book[0])) {
			@Override
			public Object getSelectedItem() {
				Object value = super.getSelectedItem();
				if (value != null && value instanceof Book) {
					Book book = (Book)value;
					JTextComponent field = (JTextComponent)this.editor.getEditorComponent();
					if (book.getName().equals(field.getText())) {
						return book;
					}
				}
				return null;
			}
			
			@Override
			public void setSelectedItem(Object anObject) {
				super.setSelectedItem(anObject);
				if (anObject != null) {
					JTextComponent field = (JTextComponent)this.editor.getEditorComponent();
					field.setText(((Book)anObject).getName());
				}
				// if the value changes update the labels
				updateLabels();
			}
			
			@Override
			public void setSelectedIndex(int anIndex) {
				super.setSelectedIndex(anIndex);
				JTextComponent field = (JTextComponent)this.editor.getEditorComponent();
				Object item = this.getSelectedItem();
				if (item != null) {
					field.setText(((Book)item).getName());
				}
				// if the value changes update the labels
				updateLabels();
			}
		};
		this.cmbBooks.setEditable(true);
		this.cmbBooks.setRenderer(new BookListCellRenderer());
		this.cmbBooks.setEditor(new BookComboBoxEditor(cmbBooks.getEditor()));
		this.cmbBooks.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				// if the value changes update the labels
				updateLabels();
			}
		});
		
		// chapter text box
		this.txtChapter = new JFormattedTextField(new EmptyNumberFormatter(new DecimalFormat("0"))) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				// paint a watermark over the text box
				WaterMark.paintTextWaterMark(g, this, Messages.getString("panel.bible.chapter.watermark"));
			}
		};
		this.txtChapter.setColumns(3);
		this.txtChapter.addPropertyChangeListener("value", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				// update the labels when the value changes
				updateLabels();
			}
		});
		this.txtChapter.setHorizontalAlignment(JTextField.RIGHT);
		this.txtChapter.addFocusListener(new SelectTextFocusListener(this.txtChapter));
		
		// verse text box
		this.txtVerse = new JFormattedTextField(new EmptyNumberFormatter(new DecimalFormat("0"))) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				// paint a watermark over the text box
				WaterMark.paintTextWaterMark(g, this, Messages.getString("panel.bible.verse.watermark"));
			}
		};
		this.txtVerse.setColumns(3);
		this.txtVerse.addPropertyChangeListener("value", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				// update the labels when the value changes
				updateLabels();
			}
		});
		this.txtVerse.setHorizontalAlignment(JTextField.RIGHT);
		this.txtVerse.addFocusListener(new SelectTextFocusListener(this.txtVerse));
		
		// setup the labels
		this.lblChapterCount = new JLabel(MessageFormat.format(Messages.getString("panel.bible.chapterCount"), ""));
		this.lblChapterCount.setToolTipText(Messages.getString("panel.bible.chapterCount.tooltip"));
		this.lblChapterCount.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		this.lblVerseCount = new JLabel("");
		this.lblVerseCount.setToolTipText(Messages.getString("panel.bible.verseCount.tooltip"));
		this.lblVerseCount.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		this.lblFound = new JLabel("");
		this.lblFound.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		
		// setup the transition lists
		boolean transitionsSupported = Transitions.isTransitionSupportAvailable(device);
		
		this.cmbSendTransitions = new JComboBox<Transition>(Transitions.IN);
		this.cmbSendTransitions.setRenderer(new TransitionListCellRenderer());
		this.cmbSendTransitions.setSelectedItem(Transitions.getTransitionForId(bPreferences.getSendTransitionId(), TransitionType.IN));
		this.txtSendTransitions = new JFormattedTextField(new DecimalFormat("0"));
		this.txtSendTransitions.addFocusListener(new SelectTextFocusListener(this.txtSendTransitions));
		this.txtSendTransitions.setToolTipText(Messages.getString("transition.duration.tooltip"));
		this.txtSendTransitions.setValue(bPreferences.getSendTransitionDuration());
		this.txtSendTransitions.setColumns(3);
		
		this.cmbClearTransitions = new JComboBox<Transition>(Transitions.OUT);
		this.cmbClearTransitions.setRenderer(new TransitionListCellRenderer());
		this.cmbClearTransitions.setSelectedItem(Transitions.getTransitionForId(bPreferences.getClearTransitionId(), TransitionType.OUT));
		this.txtClearTransitions = new JFormattedTextField(new DecimalFormat("0"));
		this.txtClearTransitions.addFocusListener(new SelectTextFocusListener(this.txtClearTransitions));
		this.txtClearTransitions.setToolTipText(Messages.getString("transition.duration.tooltip"));
		this.txtClearTransitions.setValue(bPreferences.getClearTransitionDuration());
		this.txtClearTransitions.setColumns(3);
		
		if (!transitionsSupported) {
			this.cmbSendTransitions.setEnabled(false);
			this.txtSendTransitions.setEnabled(false);
			this.cmbClearTransitions.setEnabled(false);
			this.txtClearTransitions.setEnabled(false);
		}
		
		// setup the buttons
		JButton btnFind = new JButton(Messages.getString("panel.bible.preview"));
		btnFind.setToolTipText(Messages.getString("panel.bible.preview.tooltip"));
		btnFind.addActionListener(this);
		btnFind.setActionCommand("find");
		
		JButton btnAdd = new JButton(Messages.getString("panel.bible.add"));
		btnAdd.setToolTipText(Messages.getString("panel.bible.add.tooltip"));
		btnAdd.addActionListener(this);
		btnAdd.setActionCommand("add");

		JButton btnNext = new JButton(Messages.getString("panel.bible.next"));
		btnNext.setToolTipText(Messages.getString("panel.bible.next.tooltip"));
		btnNext.addActionListener(this);
		btnNext.setActionCommand("next");
		
		JButton btnPrev = new JButton(Messages.getString("panel.bible.previous"));
		btnPrev.setToolTipText(Messages.getString("panel.bible.previous.tooltip"));
		btnPrev.addActionListener(this);
		btnPrev.setActionCommand("prev");
		
		// create a panel/layout for the find/add/next/prev controls
		JPanel pnlLookupButtons = new JPanel();
		pnlLookupButtons.setLayout(new GridLayout(2, 2, 5, 5));
		pnlLookupButtons.add(btnFind);
		pnlLookupButtons.add(btnAdd);
		pnlLookupButtons.add(btnPrev);
		pnlLookupButtons.add(btnNext);
		
		// create the send/clear buttons
		
		JButton btnSend = new JButton(Messages.getString("panel.bible.send"));
		btnSend.setToolTipText(Messages.getString("panel.bible.send.tooltip"));
		btnSend.addActionListener(this);
		btnSend.setActionCommand("send");
		btnSend.setFont(btnSend.getFont().deriveFont(Font.BOLD, btnSend.getFont().getSize2D() + 3.0f));
		
		JButton btnClear = new JButton(Messages.getString("panel.bible.clear"));
		btnClear.setToolTipText(Messages.getString("panel.bible.clear.tooltip"));
		btnClear.addActionListener(this);
		btnClear.setActionCommand("clear");
		
		// create a panel and layout for the send/clear controls
		JPanel pnlSendClearButtons = new JPanel();
		GroupLayout subLayout = new GroupLayout(pnlSendClearButtons);
		pnlSendClearButtons.setLayout(subLayout);
		
		subLayout.setAutoCreateGaps(true);
		subLayout.setHorizontalGroup(subLayout.createParallelGroup()
				.addComponent(this.cmbTemplates)
				.addGroup(subLayout.createSequentialGroup()
						.addGroup(subLayout.createParallelGroup()
								.addGroup(subLayout.createSequentialGroup()
										.addComponent(this.cmbSendTransitions)
										.addComponent(this.txtSendTransitions))
								.addComponent(btnSend, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addGroup(subLayout.createParallelGroup()
								.addGroup(subLayout.createSequentialGroup()
										.addComponent(this.cmbClearTransitions)
										.addComponent(this.txtClearTransitions))
								.addComponent(btnClear, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))));
		subLayout.setVerticalGroup(subLayout.createSequentialGroup()
				.addComponent(this.cmbTemplates, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addGroup(subLayout.createParallelGroup()
						.addComponent(this.cmbSendTransitions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.txtSendTransitions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.cmbClearTransitions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.txtClearTransitions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(subLayout.createParallelGroup()
						.addComponent(btnSend, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(btnClear, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		
		// create a panel and layout for all the lookup controls
		JPanel pnlLookupPanel = new JPanel();
		pnlLookupPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		subLayout = new GroupLayout(pnlLookupPanel);
		pnlLookupPanel.setLayout(subLayout);
		
		subLayout.setAutoCreateGaps(true);
		subLayout.setHorizontalGroup(subLayout.createParallelGroup()
				.addGroup(subLayout.createSequentialGroup()
						.addGroup(subLayout.createParallelGroup()
								.addComponent(lblPrimaryBible)
								.addComponent(lblSecondaryBible))
						.addGroup(subLayout.createParallelGroup()
								.addComponent(this.cmbBiblesPrimary, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(this.cmbBiblesSecondary, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addComponent(this.chkUseSecondaryBible, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(subLayout.createSequentialGroup()
						.addGroup(subLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
								.addComponent(this.cmbBooks, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(this.lblChapterCount))
						.addGroup(subLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
								.addComponent(this.txtChapter, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(this.lblVerseCount))
						.addGroup(subLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
								.addComponent(this.txtVerse, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(this.lblFound))
						.addComponent(pnlLookupButtons, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(pnlSendClearButtons, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		subLayout.setVerticalGroup(subLayout.createParallelGroup()
						.addGroup(subLayout.createSequentialGroup()
								.addGroup(subLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
										.addComponent(lblPrimaryBible)
										.addComponent(this.cmbBiblesPrimary, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGroup(subLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
										.addComponent(lblSecondaryBible)
										.addComponent(this.cmbBiblesSecondary, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(this.chkUseSecondaryBible, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGroup(subLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
										.addGroup(subLayout.createSequentialGroup()
												.addGroup(subLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
													.addComponent(this.cmbBooks, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
													.addComponent(this.txtChapter, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
													.addComponent(this.txtVerse, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
												.addGroup(subLayout.createParallelGroup()
													.addComponent(this.lblChapterCount)
													.addComponent(this.lblVerseCount)
													.addComponent(this.lblFound)))
										.addComponent(pnlLookupButtons, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
						.addComponent(pnlSendClearButtons));
		
		// create the verse queue table
		this.tblVerseQueue = new JTable(new MutableVerseTableModel()) {
			@Override
			public String getToolTipText(MouseEvent event) {
				Point p = event.getPoint();
				int row = this.rowAtPoint(p);
				
				// get the text column value
				TableModel model = this.getModel();
				Object object = model.getValueAt(row, 5);
				if (object != null) {
					// get the verse text
					String text = object.toString();
					// split the lines by 50 characters
					return StringUtilities.addLineBreaksAtInterval(text, 50);
				}
				
				return super.getToolTipText(event);
			}
		};
		this.tblVerseQueue.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.tblVerseQueue.setColumnSelectionAllowed(false);
		this.tblVerseQueue.setCellSelectionEnabled(false);
		this.tblVerseQueue.setRowSelectionAllowed(true);
		this.tblVerseQueue.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// make sure its a double click
				if (e.getClickCount() >= 2 && e.getButton() == MouseEvent.BUTTON1) {
					// get the selected row
					int row = tblVerseQueue.rowAtPoint(e.getPoint());
					// get the data
					VerseTableModel model = (VerseTableModel)tblVerseQueue.getModel();
					Verse verse = model.getRow(row);
					// set the selection
					cmbBooks.setSelectedItem(verse.getBook());
					// set the numbers
					txtChapter.setValue(verse.getChapter());
					txtVerse.setValue(verse.getVerse());
					// update the labels
					updateLabels();
					// update the displays
					try {
						updateVerseDisplays(verse);
					} catch (DataException ex) {
						// just log this exception because the user
						// should still be able to click the preview button
						LOGGER.error("An error occurred while updating the verse displays from a queued verse: ", ex);
					}
				}
			}
		});
		this.tblVerseQueue.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		this.setVerseQueueTableWidths();
		
		// wrap the verse queue table in a scroll pane
		JScrollPane scrVerseQueue = new JScrollPane(this.tblVerseQueue);
		
		// need two buttons for the verse queue
		JButton btnRemoveSelected = new JButton(Messages.getString("panel.bible.verse.removeSelected"));
		btnRemoveSelected.setToolTipText(Messages.getString("panel.bible.verse.removeSelected.tooltip"));
		btnRemoveSelected.addActionListener(this);
		btnRemoveSelected.setActionCommand("remove-selected");
		
		JButton btnRemoveAll = new JButton(Messages.getString("panel.bible.verse.removeAll"));
		btnRemoveAll.setToolTipText(Messages.getString("panel.bible.verse.removeAll.tooltip"));
		btnRemoveAll.addActionListener(this);
		btnRemoveAll.setActionCommand("remove-all");
		
		// bible searching
		
		// create and start the bible search thread
		this.bibleSearchThread = new BibleSearchThread();
		this.bibleSearchThread.start();
		
		// the search text field
		this.txtBibleSearch = new JTextField() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				// paint a watermark on the text box
				WaterMark.paintTextWaterMark(g, this, Messages.getString("panel.bible.search.watermark"));
			}
		};
		this.txtBibleSearch.setActionCommand("search");
		this.txtBibleSearch.addActionListener(this);
		this.txtBibleSearch.addFocusListener(new SelectTextFocusListener(this.txtBibleSearch));
		
		this.cmbBibleSearchType = new JComboBox<BibleSearchType>(BibleSearchType.values());
		this.cmbBibleSearchType.setRenderer(new BibleSearchTypeListCellRenderer());
		this.cmbBibleSearchType.setSelectedItem(BibleSearchType.PHRASE);
		this.cmbBibleSearchType.setToolTipText(Messages.getString("panel.bible.search.type"));
		
		// create the search button
		JButton btnSearch = new JButton(Messages.getString("panel.bible.search"));
		btnSearch.setToolTipText(Messages.getString("panel.bible.search.tooltip"));
		btnSearch.addActionListener(this);
		btnSearch.setActionCommand("search");
		
		// create the search results label
		this.lblBibleSearchResults = new JLabel();
		this.lblBibleSearchResults.setHorizontalAlignment(SwingConstants.RIGHT);
		this.lblBibleSearchResults.setMinimumSize(new Dimension(200, 0));
		this.lblBibleSearchResults.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 5));
		
		// create the search results table
		this.tblBibleSearchResults = new JTable(new VerseTableModel()) {
			@Override
			public String getToolTipText(MouseEvent event) {
				Point p = event.getPoint();
				int row = this.rowAtPoint(p);
				
				// get the text column value
				TableModel model = this.getModel();
				Object object = model.getValueAt(row, 4);
				if (object != null) {
					// get the verse text
					String text = object.toString();
					// split the lines by 50 characters
					return StringUtilities.addLineBreaksAtInterval(text, 50);
				}
				
				return super.getToolTipText(event);
			}
		};
		this.tblBibleSearchResults.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.tblBibleSearchResults.setColumnSelectionAllowed(false);
		this.tblBibleSearchResults.setCellSelectionEnabled(false);
		this.tblBibleSearchResults.setRowSelectionAllowed(true);
		this.tblBibleSearchResults.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// make sure its a double click
				if (e.getClickCount() >= 2 && e.getButton() == MouseEvent.BUTTON1) {
					// get the selected row
					int row = tblBibleSearchResults.rowAtPoint(e.getPoint());
					// get the data
					VerseTableModel model = (VerseTableModel)tblBibleSearchResults.getModel();
					Verse verse = model.getRow(row);
					// set the selection
					cmbBooks.setSelectedItem(verse.getBook());
					// set the numbers
					txtChapter.setValue(verse.getChapter());
					txtVerse.setValue(verse.getVerse());
					// update the labels
					updateLabels();
					// update the displays
					try {
						updateVerseDisplays(verse);
					} catch (DataException ex) {
						// just log this exception because the user
						// should still be able to click the preview button
						LOGGER.error("An error occurred while updating the verse displays from a search result: ", ex);
					}
				}
			}
		});
		this.tblBibleSearchResults.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		this.setBibleSearchTableWidths();
		
		// wrap the search table in a scroll pane
		JScrollPane scrBibleSearchResults = new JScrollPane(this.tblBibleSearchResults);
		
		// create the verse queue/bible search panels
		JPanel pnlVerseQueue = new JPanel();
		GroupLayout svLayout = new GroupLayout(pnlVerseQueue);
		pnlVerseQueue.setLayout(svLayout);
		svLayout.setAutoCreateContainerGaps(true);
		svLayout.setAutoCreateGaps(true);
		svLayout.setHorizontalGroup(svLayout.createParallelGroup()
				.addGroup(svLayout.createSequentialGroup()
						.addComponent(btnRemoveSelected)
						.addComponent(btnRemoveAll))
				.addComponent(scrVerseQueue, 0, 400, Short.MAX_VALUE));
		svLayout.setVerticalGroup(svLayout.createSequentialGroup()
				.addGroup(svLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(btnRemoveSelected, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnRemoveAll, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addComponent(scrVerseQueue, 0, 150, Short.MAX_VALUE));
		
		JPanel pnlBibleSearch = new JPanel();
		GroupLayout bsLayout = new GroupLayout(pnlBibleSearch);
		pnlBibleSearch.setLayout(bsLayout);
		bsLayout.setAutoCreateContainerGaps(true);
		bsLayout.setAutoCreateGaps(true);
		bsLayout.setHorizontalGroup(bsLayout.createParallelGroup()
				.addGroup(bsLayout.createSequentialGroup()
						.addComponent(this.txtBibleSearch)
						.addComponent(this.cmbBibleSearchType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnSearch, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.lblBibleSearchResults))
				.addGroup(bsLayout.createSequentialGroup()
						.addComponent(scrBibleSearchResults, 0, 400, Short.MAX_VALUE)));
		bsLayout.setVerticalGroup(bsLayout.createSequentialGroup()
				.addGroup(bsLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.txtBibleSearch, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.cmbBibleSearchType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnSearch, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.lblBibleSearchResults, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addComponent(scrBibleSearchResults, 0, 150, Short.MAX_VALUE));
		
		JTabbedPane tableTabs = new JTabbedPane();
		tableTabs.addTab(Messages.getString("panel.bible.verseQueue"), pnlVerseQueue);
		tableTabs.addTab(Messages.getString("panel.bible.bibleSearch"), pnlBibleSearch);
		
		// default any fields
		if (bible != null) {
			this.cmbBiblesPrimary.setSelectedItem(bible);
		} else if (bibles != null && bibles.length > 0) {
			this.cmbBiblesPrimary.setSelectedItem(bibles[0]);
		}
		bible = null;
		try {
			bible = Bibles.getBible(bPreferences.getSecondaryTranslationId());
		} catch (DataException e) {
			LOGGER.error("Default secondary bible could not be retrieved: ", e);
		}
		if (bible != null) {
			this.cmbBiblesSecondary.setSelectedItem(bible);
		} else if (bibles != null && bibles.length > 0) {
			this.cmbBiblesSecondary.setSelectedItem(bibles[0]);
		}
		if (books != null && books.size() > 0) {
			this.cmbBooks.setSelectedItem(books.get(0));
		}
		
		// split the preview and controls with the table panels
		JPanel pnlTop = new JPanel();
		GroupLayout tLayout = new GroupLayout(pnlTop);
		pnlTop.setLayout(tLayout);
		tLayout.setAutoCreateGaps(true);
		tLayout.setHorizontalGroup(tLayout.createParallelGroup()
				.addComponent(this.pnlPreview)
				.addComponent(pnlLookupPanel));
		tLayout.setVerticalGroup(tLayout.createSequentialGroup()
				.addComponent(this.pnlPreview)
				.addComponent(pnlLookupPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));
		
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		split.setTopComponent(pnlTop);
		split.setBottomComponent(tableTabs);
		split.setResizeWeight(1.0);
		split.setOneTouchExpandable(true);
		
		// create the layout
		this.setLayout(new BorderLayout());
		this.add(split, BorderLayout.CENTER);
	}
	
	/**
	 * Returns the template to use from the preferences.
	 * @return {@link BibleSlideTemplate}
	 */
	private BibleSlideTemplate getTemplate() {
		// get the primary device size
		Dimension displaySize = this.preferences.getPrimaryOrDefaultDeviceResolution();
		
		// get the bible slide template
		BibleSlideTemplate template = null;
		String templatePath = this.bPreferences.getTemplate();
		try {
			SlideLibrary library = SlideLibrary.getInstance();
			if (templatePath != null && templatePath.trim().length() > 0) {
				try {
					template = library.getTemplate(templatePath, BibleSlideTemplate.class);
				} catch (SlideLibraryException e1) {
					LOGGER.error("Unable to load preferences bible template [" + templatePath + "]: ", e1);
				}
			}
		} catch (NotInitializedException e) {
			LOGGER.error(e);
		}
		
		if (template == null) {
			// if its still null, then use the default template
			template = BibleSlideTemplate.getDefaultTemplate(displaySize.width, displaySize.height);
		}
		
		// check the template size against the display size
		this.verifyTemplateDimensions(template, displaySize);
		
		return template;
	}
	
	/**
	 * Verifies the template is sized to the given size.
	 * <p>
	 * If not, the template is adjusted to fit.
	 * @param template the template
	 * @param size the size
	 */
	private void verifyTemplateDimensions(BibleSlideTemplate template, Dimension size) {
		// check the template size against the display size
		if (template.getWidth() != size.width || template.getHeight() != size.height) {
			// log a message and modify the template to fit
			LOGGER.warn("Template is not sized correctly for the primary display. Adjusing template.");
			template.adjustSize(size.width, size.height);
		}
	}
	
	/**
	 * Returns an array of {@link SlideThumbnail}s for {@link BibleSlideTemplate}s.
	 * @return {@link SlideThumbnail}[]
	 */
	private SlideThumbnail[] getThumbnails() {
		Dimension displaySize = this.preferences.getPrimaryOrDefaultDeviceResolution();
		
		List<SlideThumbnail> thumbs = null;
		try {
			thumbs = SlideLibrary.getInstance().getThumbnails(BibleSlideTemplate.class);
		} catch (NotInitializedException e) {
			// just ignore the error
			thumbs = new ArrayList<SlideThumbnail>();
		}
		
		// add in the default template
		BibleSlideTemplate dTemplate = BibleSlideTemplate.getDefaultTemplate(displaySize.width, displaySize.height);
		BufferedImage image = dTemplate.getThumbnail(SlideLibrary.THUMBNAIL_SIZE);
		SlideThumbnail temp = new SlideThumbnail(SlideFile.NOT_STORED, dTemplate.getName(), image);
		thumbs.add(temp);
		
		// sort them
		Collections.sort(thumbs);
		
		return thumbs.toArray(new SlideThumbnail[0]);
	}
	
	/**
	 * Returns the selected thumbnail for the selected {@link BibleSlideTemplate}
	 * given in the preferences.
	 * @param thumbnails the list of all slide thumbnails
	 * @return {@link SlideThumbnail}
	 */
	private SlideThumbnail getSelectedThumbnail(SlideThumbnail[] thumbnails) {
		for (SlideThumbnail thumb : thumbnails) {
			if (thumb.getFile() == SlideFile.NOT_STORED) {
				if (this.bPreferences.getTemplate() == null) {
					return thumb;
				}
			} else if (thumb.getFile().getRelativePath().equals(this.bPreferences.getTemplate())) {
				return thumb;
			}
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.preferences.ui.PreferencesListener#preferencesChanged()
	 */
	@Override
	public void preferencesChanged() {
		this.onPreferencesOrSlideLibraryChanged();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.SlideLibraryListener#slideLibraryChanged()
	 */
	@Override
	public void slideLibraryChanged() {
		this.onPreferencesOrSlideLibraryChanged();
	}
	
	/**
	 * Called when the preferences or the slide library changes.
	 * <p>
	 * The preferences can alter the selected template and the slide library
	 * can be changed from the preferences dialog. Because of this, we need
	 * to perform the same action for both events.
	 */
	private void onPreferencesOrSlideLibraryChanged() {
		// if the preferences or slide library changes we only want to make
		// sure that we are using the latest template (so if it was edited
		// we need to update the preview) and that we are using the latest
		// listing of other templates
		
		SlideThumbnail[] thumbnails = this.getThumbnails();
		
		// update the list of templates
		SlideThumbnail selected = (SlideThumbnail)this.cmbTemplates.getSelectedItem();
		if (selected == null) {
			selected = this.getSelectedThumbnail(thumbnails);
		}
		this.cmbTemplates.removeAllItems();
		for (SlideThumbnail thumb : thumbnails) {
			this.cmbTemplates.addItem(thumb);
		}
		
		// set the selected one
		// selecting the item in the combo box will update the template
		// and the preview panel
		if (selected != null) {
			this.cmbTemplates.setSelectedItem(selected);
		} else {
			this.cmbTemplates.setSelectedIndex(0);
		}
		
		// redraw the preview panel
		this.pnlPreview.repaint();
	}
	
	/**
	 * Updates the slide template in the preview.
	 * @param template the template
	 */
	private void updateSlideTemplate(BibleSlideTemplate template) {
		// create the slides
		BibleSlide previous = template.createSlide();
		BibleSlide current = template.createSlide();
		BibleSlide next = template.createSlide();

		BibleSlide s0 = (BibleSlide)this.pnlPreview.getSlide(0);
		BibleSlide s1 = (BibleSlide)this.pnlPreview.getSlide(1);
		BibleSlide s2 = (BibleSlide)this.pnlPreview.getSlide(2);
		this.pnlPreview.clearSlides();
		this.pnlPreview.addSlide(previous);
		this.pnlPreview.addSlide(current);
		this.pnlPreview.addSlide(next);
		
		// copy the text over from the current slides
		previous.setName(s0.getName());
		previous.getScriptureLocationComponent().setText(s0.getScriptureLocationComponent().getText());
		previous.getScriptureTextComponent().setText(s0.getScriptureTextComponent().getText());
		current.setName(s1.getName());
		current.getScriptureLocationComponent().setText(s1.getScriptureLocationComponent().getText());
		current.getScriptureTextComponent().setText(s1.getScriptureTextComponent().getText());
		next.setName(s2.getName());
		next.getScriptureLocationComponent().setText(s2.getScriptureLocationComponent().getText());
		next.getScriptureTextComponent().setText(s2.getScriptureTextComponent().getText());
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			Object source = e.getSource();
			if (source == this.cmbTemplates) {
				SlideThumbnail thumbnail = (SlideThumbnail)this.cmbTemplates.getSelectedItem();
				if (thumbnail != null) {
					try {
						SlideLibrary library = SlideLibrary.getInstance();
						BibleSlideTemplate template = null;
						Dimension size = this.preferences.getPrimaryOrDefaultDeviceResolution();
						if (thumbnail.getFile() == SlideFile.NOT_STORED) {
							template = BibleSlideTemplate.getDefaultTemplate(size.width, size.height);
						} else {
							try {
								template = library.getTemplate(thumbnail.getFile(), BibleSlideTemplate.class);
							} catch (SlideLibraryException ex) {
								// just log the error
								LOGGER.error("Failed to switch to template: [" + thumbnail.getFile().getRelativePath() + "]", ex);
								return;
							}
						}
						this.verifyTemplateDimensions(template, size);
						this.updateSlideTemplate(template);
						this.pnlPreview.repaint();
					} catch (NotInitializedException e1) {
						// ignore it
					}
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		Bible bible = (Bible)cmbBiblesPrimary.getSelectedItem();
		Object b = this.cmbBooks.getSelectedItem();
		Object c = this.txtChapter.getValue();
		Object v = this.txtVerse.getValue();
		boolean ia = this.bPreferences.isApocryphaIncluded();
		
		// dont bother with any of these actions unless we have what we need
		if (b != null && b instanceof Book &&
			c != null && c instanceof Number &&
			v != null && v instanceof Number) {
			
			// get the book, chapter, and verse
			Book book = (Book)b;
			int chapter = ((Number)c).intValue();
			int verse = ((Number)v).intValue();
			
			if ("find".equals(command)) {
				this.findVerseAction(bible, book, chapter, verse);
			} else if ("prev".equals(command)) {
				this.getPreviousVerseAction(bible, book, chapter, verse, ia);
			} else if ("next".equals(command)) {
				this.getNextVerseAction(bible, book, chapter, verse, ia);
			} else if ("add".equals(command)) {
				this.addVerseAction(bible, book, chapter, verse);
			} else if ("send".equals(command) && this.verseFound) {
				this.sendVerseAction();
			} else if ("clear".equals(command)) {
				this.clearVerseAction();
			}
		}
		
		// check for search
		if ("search".equals(command)) {
			// grab the text from the text box
			String text = this.txtBibleSearch.getText();
			// get the bible search type
			BibleSearchType type = (BibleSearchType)this.cmbBibleSearchType.getSelectedItem();
			if (text != null && text.length() > 0) {
				// execute the search in another thread
				// its possible that the search thread was interrupted or stopped
				// so make sure its still running
				if (!this.bibleSearchThread.isAlive()) {
					// if the current thread is no longer alive (running) then
					// create another and start it
					this.bibleSearchThread = new BibleSearchThread();
					this.bibleSearchThread.start();
				}
				
				// execute the search
				BibleSearch search = new BibleSearch(bible, text, ia, type, new BiblePanelSearchCallback());
				this.bibleSearchThread.queueSearch(search);
			}
		}
		// check for remove selected
		else if ("remove-selected".equals(command)) {
			MutableVerseTableModel model = (MutableVerseTableModel)this.tblVerseQueue.getModel();
			model.removeSelectedRows();
		}
		// check for remove all
		else if ("remove-all".equals(command)) {
			MutableVerseTableModel model = (MutableVerseTableModel)this.tblVerseQueue.getModel();
			model.removeAllRows();
		}
	}
	
	/**
	 * Finds the given verse.
	 * @param bible the bible
	 * @param book the book
	 * @param chapter the chapter
	 * @param verse the verse
	 */
	private void findVerseAction(Bible bible, Book book, int chapter, int verse) {
		try {
			// get the verse
			Verse text = Bibles.getVerse(bible, book.getCode(), chapter, verse);
			if (text != null) {
				// update the displays
				this.updateVerseDisplays(text);
			} else {
				this.verseFound = false;
			}
		} catch (DataException ex) {
			String message = MessageFormat.format(Messages.getString("panel.bible.data.find.exception.text"), bible.getName(), book.getName(), chapter, verse);
			ExceptionDialog.show(
					BiblePanel.this, 
					Messages.getString("panel.bible.data.find.exception.title"), 
					message, 
					ex);
			LOGGER.error(message, ex);
		}
	}
	
	/**
	 * Moves the preview to the previous verse.
	 * @param bible the bible
	 * @param book the book
	 * @param chapter the chapter
	 * @param verse the verse
	 * @param includeApocrypha true to include the apocrypha books
	 */
	private void getPreviousVerseAction(Bible bible, Book book, int chapter, int verse, boolean includeApocrypha) {
		// to be more efficient we can shift the verse displays right by one and only get
		// the current verse's previous-previous
		try {
			// get the current verse
			Verse curr = Bibles.getVerse(bible, book.getCode(), chapter, verse);
			if (curr == null) {
				this.verseFound = false;
				return;
			}
			// get the previous verse
			Verse prev = Bibles.getPreviousVerse(curr, includeApocrypha);
			// if prev is null (we are at the beginning, there is nothing to do)
			if (prev != null) {
				this.verseFound = true;
				// change fields to new verse data
				this.cmbBooks.setSelectedItem(prev.getBook());
				this.txtChapter.setValue(prev.getChapter());
				this.txtVerse.setValue(prev.getVerse());
				// get the previous-previous
				Verse prev2 = Bibles.getPreviousVerse(prev, includeApocrypha);
				this.shiftVerseDisplays(+1, prev2);
			}
		} catch (DataException ex) {
			String message = MessageFormat.format(Messages.getString("panel.bible.data.previous.exception.text"), bible.getName(), book.getName(), chapter, verse);
			ExceptionDialog.show(
					WindowUtilities.getParentWindow(BiblePanel.this), 
					Messages.getString("panel.bible.data.previous.exception.title"), 
					message, 
					ex);
			LOGGER.error(message, ex);
		}
	}
	
	/**
	 * Moves the preview to the next verse.
	 * @param bible the bible
	 * @param book the book
	 * @param chapter the chapter
	 * @param verse the verse
	 * @param includeApocrypha true to include the apocrypha books
	 */
	private void getNextVerseAction(Bible bible, Book book, int chapter, int verse, boolean includeApocrypha) {
		try {
			// get the current verse
			Verse curr = Bibles.getVerse(bible, book.getCode(), chapter, verse);
			if (curr == null) {
				this.verseFound = false;
				return;
			}
			// get the next verse
			Verse next = Bibles.getNextVerse(curr, includeApocrypha);
			if (next != null) {
				this.verseFound = true;
				// change fields to new verse data
				this.cmbBooks.setSelectedItem(next.getBook());
				this.txtChapter.setValue(next.getChapter());
				this.txtVerse.setValue(next.getVerse());
				// get the previous-previous
				Verse next2 = Bibles.getNextVerse(next, includeApocrypha);
				this.shiftVerseDisplays(-1, next2);
			}
		} catch (DataException ex) {
			String message = MessageFormat.format(Messages.getString("panel.bible.data.next.exception.text"), bible.getName(), book.getName(), chapter, verse);
			ExceptionDialog.show(
					WindowUtilities.getParentWindow(BiblePanel.this), 
					Messages.getString("panel.bible.data.next.exception.title"), 
					message, 
					ex);
			LOGGER.error(message, ex);
		}
	}
	
	/**
	 * Adds the current verse to the list of queued verses.
	 * @param bible the bible
	 * @param book the book
	 * @param chapter the chapter
	 * @param verse the verse
	 */
	private void addVerseAction(Bible bible, Book book, int chapter, int verse) {
		try {
			Verse text = Bibles.getVerse(bible, book.getCode(), chapter, verse);
			if (text != null) {
				// change fields to new verse data
				this.cmbBooks.setSelectedItem(text.getBook());
				this.txtChapter.setValue(text.getChapter());
				this.txtVerse.setValue(text.getVerse());
				// update the displays
				this.updateVerseDisplays(text);
				// add the verse to the queue
				MutableVerseTableModel model = (MutableVerseTableModel)this.tblVerseQueue.getModel();
				model.addRow(text);
			} else {
				this.verseFound = false;
			}
		} catch (DataException ex) {
			String message = MessageFormat.format(Messages.getString("panel.bible.data.next.exception.text"), bible.getName(), book.getName(), chapter, verse);
			ExceptionDialog.show(
					WindowUtilities.getParentWindow(BiblePanel.this), 
					Messages.getString("panel.bible.data.next.exception.title"), 
					message, 
					ex);
			LOGGER.error(message, ex);
		}
	}
	
	/**
	 * Sends the current verse display to the primary display.
	 */
	private void sendVerseAction() {
		// get the transition
		Transition transition = (Transition)this.cmbSendTransitions.getSelectedItem();
		int duration = ((Number)this.txtSendTransitions.getValue()).intValue();
		int delay = this.preferences.getTransitionDelay();
		Easing easing = Easings.getEasingForId(this.bPreferences.getSendTransitionEasingId());
		TransitionAnimator animator = new TransitionAnimator(transition, duration, delay, easing);
		Slide slide = this.pnlPreview.getSlide(1);
		slide = slide.copy();
		// get the configuration
		PresentationEventConfiguration configuration = this.preferences.getPresentationEventConfiguration(PresentationWindowType.FULLSCREEN);
		// execute a new send event to the primary fullscreen display
		PresentationManager.getInstance().execute(new SendEvent(configuration, animator, slide));
	}
	
	/**
	 * Clears the primary display.
	 */
	private void clearVerseAction() {
		// get the transition
		Transition transition = (Transition)this.cmbClearTransitions.getSelectedItem();
		int duration = ((Number)this.txtClearTransitions.getValue()).intValue();
		int delay = this.preferences.getTransitionDelay();
		Easing easing = Easings.getEasingForId(this.bPreferences.getClearTransitionEasingId());
		TransitionAnimator animator = new TransitionAnimator(transition, duration, delay, easing);
		// get the configuration
		PresentationEventConfiguration configuration = this.preferences.getPresentationEventConfiguration(PresentationWindowType.FULLSCREEN);
		// execute a new clear event to the primary fullscreen display
		PresentationManager.getInstance().execute(new ClearEvent(configuration, animator));
	}
	
	/**
	 * Sets the verse text and location of the given bible slide.
	 * @param verse the verse
	 * @param slide the slide
	 */
	private void setVerse(Verse verse, BibleSlide slide) {
		TextComponent l = slide.getScriptureLocationComponent();
		l.setText(verse.getBook().getName() + " " + verse.getChapter() + ":" + verse.getVerse());
		
		TextComponent t = slide.getScriptureTextComponent();
		t.setText(verse.getText());
	}
	
	/**
	 * Sets the verse text and location of the given bible slide.
	 * @param verse1 the first translation verse
	 * @param verse2 the second translation verse
	 * @param slide the slide
	 */
	private void setVerse(Verse verse1, Verse verse2, BibleSlide slide) {
		TextComponent l = slide.getScriptureLocationComponent();
		l.setText(verse1.getBook().getName() + " " + verse1.getChapter() + ":" + verse1.getVerse());
		
		TextComponent t = slide.getScriptureTextComponent();
		t.setText(verse1.getText() + "\n\n" + verse2.getText());
	}
	
	/**
	 * Clears the verse text and location of the given bible slide.
	 * @param slide the slide
	 */
	private void clearVerse(BibleSlide slide) {
		slide.getScriptureLocationComponent().setText("");
		slide.getScriptureTextComponent().setText("");
	}
	
	/**
	 * Updates the bible displays for the new current verse.
	 * @param verse the new current verse
	 * @throws DataException if an exception occurs while loading the next and previous verses
	 */
	private void updateVerseDisplays(Verse verse) throws DataException {
		this.verseFound = true;
		boolean ia = this.bPreferences.isApocryphaIncluded();
		// then get the previous and next verses as well
		Verse prev = Bibles.getPreviousVerse(verse, ia);
		Verse next = Bibles.getNextVerse(verse, ia);
		
		// get the displays to update
		BibleSlide sPrevious = (BibleSlide)this.pnlPreview.getSlide(0);
		BibleSlide sCurrent = (BibleSlide)this.pnlPreview.getSlide(1);
		BibleSlide sNext = (BibleSlide)this.pnlPreview.getSlide(2);
		
		// check the secondary bible
		if (this.chkUseSecondaryBible.isSelected()) {
			// get the secondary bible's text
			Bible bible = (Bible)this.cmbBiblesSecondary.getSelectedItem();
			// as long as they aren't the same bible
			if (bible != null && !bible.equals(verse.getBible())) {
				try {
					// get the secondary bible verses
					Verse v2 = Bibles.getVerse(bible, verse.getBook().getCode(), verse.getChapter(), verse.getVerse());
					Verse v2p = Bibles.getPreviousVerse(v2, ia);
					Verse v2n = Bibles.getNextVerse(v2, ia);
					// set the current verse text
					this.setVerse(verse, v2, sCurrent);
					// set the previous verse
					if (prev != null) {
						this.setVerse(prev, v2p, sPrevious);
					} else {
						this.clearVerse(sPrevious);
					}
					// set the next verse
					if (next != null) {
						this.setVerse(next, v2n, sNext);
					} else {
						this.clearVerse(sNext);
					}
					// repaint the preview
					this.pnlPreview.repaint();
					return;
				} catch (DataException e) {
					// the secondary bible isn't as important as the primary
					// we should just log the error if the secondary throws an excpetion
					LOGGER.error("An error occurred while retrieving the previous, current, and next verses from the secondary bible: ", e);
				}
			}
		}
		
		// set the current verse text
		this.setVerse(verse, sCurrent);
		// set the previous verse
		if (prev != null) {
			this.setVerse(prev, sPrevious);
		} else {
			this.clearVerse(sPrevious);
		}
		// set the next verse
		if (next != null) {
			this.setVerse(next, sNext);
		} else {
			this.clearVerse(sNext);
		}
		// repaint the preview
		this.pnlPreview.repaint();
	}
	
	/**
	 * Updates the bible displays for the new current verse.
	 * @param direction the direction to shift the displays; &lt; 0 to shift left; &gt; 0 to shift right
	 * @param verse the prev-prev or next-next verse
	 * @throws DataException if an exception occurs while loading the next and previous verses
	 */
	private void shiftVerseDisplays(int direction, Verse verse) throws DataException {
		BibleSlide slide = null;
		// shift the displays
		{
			// shift them and assign the one we will modify
			if (direction > 0) {
				// we are shifting everything left
				this.pnlPreview.shiftSlides(direction);
				slide = (BibleSlide)this.pnlPreview.getSlide(0);
			} else {
				// we are shifting everything right
				this.pnlPreview.shiftSlides(direction);
				slide = (BibleSlide)this.pnlPreview.getSlide(2);
			}
			
			// we need to make sure the slide labels remain stationary
			this.pnlPreview.getSlide(0).setName(Messages.getString("panel.bible.preview.previous"));
			this.pnlPreview.getSlide(1).setName(Messages.getString("panel.bible.preview.current"));
			this.pnlPreview.getSlide(2).setName(Messages.getString("panel.bible.preview.next"));
		}
		
		// check for null
		if (verse == null) {
			// clear the verse
			this.clearVerse(slide);
			// repaint the preview
			this.pnlPreview.repaint();
			// don't continue
			return;
		}
		
		// check the secondary bible
		if (this.chkUseSecondaryBible.isSelected()) {
			// get the secondary bible's text
			Bible bible = (Bible)this.cmbBiblesSecondary.getSelectedItem();
			// as long as they aren't the same bible
			if (bible != null && !bible.equals(verse.getBible())) {
				try {
					// get the secondary bible verses
					Verse v2 = Bibles.getVerse(bible, verse.getBook().getCode(), verse.getChapter(), verse.getVerse());
					if (v2 != null) {
						// set the verses
						this.setVerse(verse, v2, slide);
						// repaint the preview
						this.pnlPreview.repaint();
						return;
					}
				} catch (DataException e) {
					// the secondary bible isn't as important as the primary
					// we should just log the error if the secondary throws an excpetion
					LOGGER.error("An error occurred while retrieving the previous, current, and next verses from the secondary bible: ", e);
				}
			}
		}
		
		this.setVerse(verse, slide);
		// repaint the preview
		this.pnlPreview.repaint();
	}
	
	/**
	 * Updates the labels to show the current information.
	 */
	private void updateLabels() {
		// look up the number of verses in the chapter
		Bible bible = (Bible)this.cmbBiblesPrimary.getSelectedItem();
		Book book = (Book)this.cmbBooks.getSelectedItem();
		Object chap = this.txtChapter.getValue();
		Object vers = this.txtVerse.getValue();
		
		if (bible != null && book != null) {
			// show the number of chapters
			try {
				int count = Bibles.getChapterCount(bible, book.getCode());
				this.lblChapterCount.setText(MessageFormat.format(Messages.getString("panel.bible.chapterCount"), count));
			} catch (DataException ex) {
				LOGGER.error(MessageFormat.format(Messages.getString("panel.bible.data.chapterCount.exception.text"), bible.getName(), book.getName()), ex);
				this.lblChapterCount.setText("");
			}
			
			if (chap != null && chap instanceof Number) {
				// show the number of verses
				int chapter = ((Number) chap).intValue();
				try {
					int count = Bibles.getVerseCount(bible, book.getCode(), chapter);
					this.lblVerseCount.setText(MessageFormat.format(Messages.getString("panel.bible.verseCount"), count));
				} catch (DataException ex) {
					LOGGER.error(MessageFormat.format(Messages.getString("panel.bible.data.verseCount.exception.text"), bible.getName(), book.getName(), chapter), ex);
					this.lblVerseCount.setText("");
				}
				
				if (vers != null && vers instanceof Number) {
					// show the found/not found icon
					int verse = ((Number)vers).intValue();
					try {
						Verse v = Bibles.getVerse(bible, book.getCode(), chapter, verse);
						if (v != null) {
							this.lblFound.setIcon(Icons.FOUND);
							this.lblFound.setToolTipText(null);
						} else {
							this.lblFound.setIcon(Icons.NOT_FOUND);
							this.lblFound.setToolTipText(MessageFormat.format(Messages.getString("panel.bible.data.verseNotFound"), bible.getName(), book.getName(), chapter, verse));
						}
					} catch (DataException ex) {
						LOGGER.error(MessageFormat.format(Messages.getString("panel.bible.data.validate.exception.text"), bible.getName(), book.getName(), chapter, verse), ex);
						this.lblFound.setIcon(null);
						this.lblFound.setToolTipText(null);
					}
				} else {
					// clear labels if not enough info is given
					this.lblFound.setIcon(null);
					this.lblFound.setToolTipText(null);
				}
			} else {
				// clear labels if not enough info is given
				this.lblVerseCount.setText("");
				this.lblFound.setIcon(null);
				this.lblFound.setToolTipText(null);
			}
		} else {
			// clear labels if not enough info is given
			this.lblChapterCount.setText(MessageFormat.format(Messages.getString("panel.bible.chapterCount"), ""));
			this.lblVerseCount.setText("");
			this.lblFound.setIcon(null);
			this.lblFound.setToolTipText(null);
		}
	}
	
	/**
	 * Sets the table column widths for the bible search results table.
	 */
	private void setBibleSearchTableWidths() {
		this.tblBibleSearchResults.getColumnModel().getColumn(0).setMaxWidth(300);
		this.tblBibleSearchResults.getColumnModel().getColumn(0).setPreferredWidth(150);
		this.tblBibleSearchResults.getColumnModel().getColumn(1).setMaxWidth(150);
		this.tblBibleSearchResults.getColumnModel().getColumn(1).setPreferredWidth(110);
		this.tblBibleSearchResults.getColumnModel().getColumn(2).setMaxWidth(35);
		this.tblBibleSearchResults.getColumnModel().getColumn(2).setPreferredWidth(35);
		this.tblBibleSearchResults.getColumnModel().getColumn(3).setMaxWidth(35);
		this.tblBibleSearchResults.getColumnModel().getColumn(3).setPreferredWidth(35);
	}
	
	/**
	 * Sets the table column widths for the verse queue table.
	 */
	private void setVerseQueueTableWidths() {
		this.tblVerseQueue.getColumnModel().getColumn(0).setMaxWidth(35);
		this.tblVerseQueue.getColumnModel().getColumn(0).setPreferredWidth(35);
		this.tblVerseQueue.getColumnModel().getColumn(1).setMaxWidth(300);
		this.tblVerseQueue.getColumnModel().getColumn(1).setPreferredWidth(150);
		this.tblVerseQueue.getColumnModel().getColumn(2).setMaxWidth(150);
		this.tblVerseQueue.getColumnModel().getColumn(2).setPreferredWidth(110);
		this.tblVerseQueue.getColumnModel().getColumn(3).setMaxWidth(35);
		this.tblVerseQueue.getColumnModel().getColumn(3).setPreferredWidth(35);
		this.tblVerseQueue.getColumnModel().getColumn(4).setMaxWidth(35);
		this.tblVerseQueue.getColumnModel().getColumn(4).setPreferredWidth(35);
	}
	
	/**
	 * Called when a bible is imported.
	 */
	public void onBibleLibraryChanged() {
		// get the selected items
		Bible b1 = (Bible)this.cmbBiblesPrimary.getSelectedItem();
		Bible b2 = (Bible)this.cmbBiblesSecondary.getSelectedItem();
		// add the bibles back
		try {
			// get the new list of bibles
			List<Bible> bibles = Bibles.getBibles();
			// remove all the current items in the lists
			this.cmbBiblesPrimary.removeAllItems();
			this.cmbBiblesSecondary.removeAllItems();
			// add the bibles to the lists
			for (Bible bible : bibles) {
				this.cmbBiblesPrimary.addItem(bible);
				this.cmbBiblesSecondary.addItem(bible);
			}
			// reset the selected items
			this.cmbBiblesPrimary.setSelectedItem(b1);
			this.cmbBiblesSecondary.setSelectedItem(b2);
		} catch (DataException e) {
			ExceptionDialog.show(
					WindowUtilities.getParentWindow(this), 
					Messages.getString("bible.import.updateUI.title"), 
					Messages.getString("bible.import.updateUI.text"), 
					e);
			LOGGER.error("An error occurred while updating the bible dropdowns after a successful bible import: ", e);
		}
	}
	
	/**
	 * Custom editor decorator for the book combo box.
	 * @author William Bittle
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	private class BookComboBoxEditor extends AutoCompleteComboBoxEditor {
		/**
		 * Full constructor.
		 * @param comboBoxEditor the editor to decorate
		 */
		public BookComboBoxEditor(ComboBoxEditor comboBoxEditor) {
			super(comboBoxEditor);
		}
		
		/* (non-Javadoc)
		 * @see org.praisenter.control.AutoCompleteComboBoxEditor#match(java.lang.String)
		 */
		@Override
		public String match(String text) {
			if (text == null || text.length() == 0) {
				return null;
			}
			int n = cmbBooks.getItemCount();
			for (int i = 0; i < n; i++) {
				Book book = cmbBooks.getItemAt(i);
				String name = book.getName();
				if (!book.equals(text)) {
					if (name.toUpperCase().startsWith(text.toUpperCase())) {
						// then set the text and select the remaining text
						return name;
					}
				} else {
					return name;
				}
			}
			return null;
		}
		
		/* (non-Javadoc)
		 * @see org.praisenter.control.AutoCompleteComboBoxEditor#getValue(java.lang.Object)
		 */
		@Override
		public String getValue(Object o) {
			if (o == null) return null;
			if (o instanceof Book) {
				Book book = (Book)o;
				return book.getName();
			} else {
				return o.toString();
			}
		}
		
		/* (non-Javadoc)
		 * @see org.praisenter.control.AutoCompleteComboBoxEditor#getItem(java.lang.String)
		 */
		@Override
		public Object getItem(String text) {
			if (text == null || text.length() == 0) {
				return null;
			}
			int n = cmbBooks.getItemCount();
			for (int i = 0; i < n; i++) {
				Book book = cmbBooks.getItemAt(i);
				String name = book.getName();
				if (!book.equals(text)) {
					if (name.toUpperCase().startsWith(text.toUpperCase())) {
						// then set the text and select the remaining text
						return book;
					}
				} else {
					return book;
				}
			}
			return null;
		}
	}

	/**
	 * Callback for bible searching to update the table and results text.
	 * @author William Bittle
	 * @version 2.0.0
	 * @since 1.0.0
	 */
	private final class BiblePanelSearchCallback extends BibleSearchCallback {
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			List<Verse> verses = this.getResult();
			Exception ex = this.getException();
			BibleSearch search = this.getSearch();
			if (ex == null) {
				String message = Messages.getString("panel.bible.search.results.pattern");
				if (verses != null && verses.size() > 0) {
					tblBibleSearchResults.setModel(new VerseTableModel(verses));
					lblBibleSearchResults.setText(MessageFormat.format(message, verses.size()));
				} else {
					tblBibleSearchResults.setModel(new VerseTableModel());
					lblBibleSearchResults.setText(MessageFormat.format(message, 0));
				}
				setBibleSearchTableWidths();
			} else {
				String message = MessageFormat.format(Messages.getString("panel.bible.data.search.exception.text"), search.getText(), search.getBible().getName());
				ExceptionDialog.show(
						BiblePanel.this, 
						Messages.getString("panel.bible.data.search.exception.title"), 
						message, 
						ex);
				LOGGER.error(message, ex);
			}
		}
	}
}
