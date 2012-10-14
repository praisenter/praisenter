package org.praisenter.data.bible.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxEditor;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;
import org.praisenter.data.DataException;
import org.praisenter.data.bible.Bible;
import org.praisenter.data.bible.BibleSearchType;
import org.praisenter.data.bible.Bibles;
import org.praisenter.data.bible.Book;
import org.praisenter.data.bible.Verse;
import org.praisenter.data.errors.ui.ExceptionDialog;
import org.praisenter.display.BibleDisplay;
import org.praisenter.display.DisplayFactory;
import org.praisenter.display.TextComponent;
import org.praisenter.display.ui.DisplayWindows;
import org.praisenter.display.ui.StandardDisplayWindow;
import org.praisenter.icons.Icons;
import org.praisenter.resources.Messages;
import org.praisenter.settings.BibleSettings;
import org.praisenter.settings.GeneralSettings;
import org.praisenter.settings.SettingsListener;
import org.praisenter.transitions.Transition;
import org.praisenter.transitions.TransitionAnimator;
import org.praisenter.transitions.Transitions;
import org.praisenter.transitions.ui.TransitionListCellRenderer;
import org.praisenter.ui.AutoCompleteComboBoxEditor;
import org.praisenter.ui.EmptyNumberFormatter;
import org.praisenter.ui.SelectTextFocusListener;
import org.praisenter.ui.WaterMark;
import org.praisenter.utilities.StringUtilities;
import org.praisenter.utilities.WindowUtilities;

/**
 * Panel for bible lookup and searching.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class BiblePanel extends JPanel implements ActionListener, SettingsListener {
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
	private BibleDisplayPreviewPanel pnlPreview;
	
	// state
	
	/** True if the user has found a verse */
	private boolean verseFound;
	
	/**
	 * Default constructor.
	 */
	@SuppressWarnings("serial")
	public BiblePanel() {
		this.verseFound = false;
		
		// get the settings
		GeneralSettings gSettings = GeneralSettings.getInstance();
		BibleSettings bSettings = BibleSettings.getInstance();
		
		// get the display size
		Dimension displaySize = gSettings.getPrimaryDisplaySize();
		
		// create the preview panel
		this.pnlPreview = new BibleDisplayPreviewPanel();
		this.pnlPreview.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), BorderFactory.createTitledBorder("")));
		this.pnlPreview.setPreviousVerseDisplay(DisplayFactory.getDisplay(bSettings, displaySize));
		this.pnlPreview.setCurrentVerseDisplay(DisplayFactory.getDisplay(bSettings, displaySize));
		this.pnlPreview.setNextVerseDisplay(DisplayFactory.getDisplay(bSettings, displaySize));
		
		this.pnlPreview.setMinimumSize(250);
		
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
		lblPrimaryBible.setToolTipText(Messages.getString("panel.bible.primary.tooltip"));
		if (bibles == null) {
			this.cmbBiblesPrimary = new JComboBox<Bible>();
		} else {
			this.cmbBiblesPrimary = new JComboBox<Bible>(bibles);
		}
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
							boolean ia = BibleSettings.getInstance().isApocryphaIncluded();
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
		lblSecondaryBible.setToolTipText(Messages.getString("panel.bible.secondary.tooltip"));
		if (bibles == null) {
			this.cmbBiblesSecondary = new JComboBox<Bible>();
		} else {
			this.cmbBiblesSecondary = new JComboBox<Bible>(bibles);
		}
		this.cmbBiblesSecondary.setRenderer(new BibleListCellRenderer());
		
		this.chkUseSecondaryBible = new JCheckBox(Messages.getString("panel.bible.secondary.use"));
		this.chkUseSecondaryBible.setToolTipText(Messages.getString("panel.bible.secondary.use.tooltip"));
		this.chkUseSecondaryBible.setSelected(bSettings.isSecondaryBibleInUse());
		
		// get the books
		List<Book> books = new ArrayList<Book>();
		// get the default bible
		Bible bible = null;
		try {
			int id = bSettings.getDefaultPrimaryBibleId();
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
				boolean ia = bSettings.isApocryphaIncluded();
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
		this.txtChapter = new JFormattedTextField(new EmptyNumberFormatter(NumberFormat.getIntegerInstance())) {
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
		this.txtChapter.addFocusListener(new SelectTextFocusListener(this.txtChapter));
		
		// verse text box
		this.txtVerse = new JFormattedTextField(new EmptyNumberFormatter(NumberFormat.getIntegerInstance())) {
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
		this.txtVerse.addFocusListener(new SelectTextFocusListener(this.txtVerse));
		
		// setup the labels
		this.lblChapterCount = new JLabel(MessageFormat.format(Messages.getString("panel.bible.chapterCount"), ""));
		this.lblChapterCount.setToolTipText(Messages.getString("panel.bible.chapterCount.tooltip"));
		this.lblVerseCount = new JLabel("");
		this.lblVerseCount.setToolTipText(Messages.getString("panel.bible.verseCount.tooltip"));
		this.lblFound = new JLabel("");
		
		// setup the transition lists
		boolean transitionsSupported = Transitions.isTransitionSupportAvailable(gSettings.getPrimaryOrDefaultDisplay());
		
		this.cmbSendTransitions = new JComboBox<Transition>(Transitions.IN);
		this.cmbSendTransitions.setRenderer(new TransitionListCellRenderer());
		this.cmbSendTransitions.setSelectedItem(Transitions.getTransitionForId(bSettings.getDefaultSendTransition(), Transition.Type.IN));
		this.txtSendTransitions = new JFormattedTextField(NumberFormat.getIntegerInstance());
		this.txtSendTransitions.addFocusListener(new SelectTextFocusListener(this.txtSendTransitions));
		this.txtSendTransitions.setToolTipText(Messages.getString("transition.duration.tooltip"));
		this.txtSendTransitions.setValue(bSettings.getDefaultSendTransitionDuration());
		this.txtSendTransitions.setColumns(3);
		
		this.cmbClearTransitions = new JComboBox<Transition>(Transitions.OUT);
		this.cmbClearTransitions.setRenderer(new TransitionListCellRenderer());
		this.cmbClearTransitions.setSelectedItem(Transitions.getTransitionForId(bSettings.getDefaultClearTransition(), Transition.Type.OUT));
		this.txtClearTransitions = new JFormattedTextField(NumberFormat.getIntegerInstance());
		this.txtClearTransitions.addFocusListener(new SelectTextFocusListener(this.txtClearTransitions));
		this.txtClearTransitions.setToolTipText(Messages.getString("transition.duration.tooltip"));
		this.txtClearTransitions.setValue(bSettings.getDefaultClearTransitionDuration());
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
		subLayout.setHorizontalGroup(subLayout.createSequentialGroup()
				.addGroup(subLayout.createParallelGroup()
						.addGroup(subLayout.createSequentialGroup()
								.addComponent(this.cmbSendTransitions)
								.addComponent(this.txtSendTransitions))
						.addComponent(btnSend, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				.addGroup(subLayout.createParallelGroup()
						.addGroup(subLayout.createSequentialGroup()
								.addComponent(this.cmbClearTransitions)
								.addComponent(this.txtClearTransitions))
						.addComponent(btnClear, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		subLayout.setVerticalGroup(subLayout.createSequentialGroup()
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
						.addGroup(subLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(this.txtChapter, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(this.lblVerseCount))
						.addGroup(subLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(this.txtVerse, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(this.lblFound))
						.addComponent(pnlLookupButtons, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(pnlSendClearButtons, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		subLayout.setVerticalGroup(subLayout.createSequentialGroup()
				.addGroup(subLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
							.addComponent(lblPrimaryBible)
							.addComponent(this.cmbBiblesPrimary, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(this.chkUseSecondaryBible, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(subLayout.createParallelGroup()
						.addGroup(subLayout.createSequentialGroup()
								.addGroup(subLayout.createParallelGroup()
										.addComponent(lblSecondaryBible)
										.addComponent(this.cmbBiblesSecondary, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
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
						.addComponent(pnlSendClearButtons)));
		
		// create the verse queue table
		this.tblVerseQueue = new JTable(new MutableBibleTableModel()) {
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
		this.tblVerseQueue.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.tblVerseQueue.setColumnSelectionAllowed(false);
		this.tblVerseQueue.setCellSelectionEnabled(false);
		this.tblVerseQueue.setRowSelectionAllowed(true);
		this.tblVerseQueue.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// make sure its a double click
				if (e.getClickCount() == 2) {
					// get the selected row
					int row = tblVerseQueue.rowAtPoint(e.getPoint());
					// get the data
					BibleTableModel model = (BibleTableModel)tblVerseQueue.getModel();
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
		JButton btnRemoveSelected = new JButton(Messages.getString("panel.bible.removeSelected"));
		btnRemoveSelected.setToolTipText(Messages.getString("panel.bible.removeSelected.tooltip"));
		btnRemoveSelected.addActionListener(this);
		btnRemoveSelected.setActionCommand("remove-selected");
		
		JButton btnRemoveAll = new JButton(Messages.getString("panel.bible.removeAll"));
		btnRemoveAll.setToolTipText(Messages.getString("panel.bible.removeAll.tooltip"));
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
		this.tblBibleSearchResults = new JTable(new BibleTableModel()) {
			@Override
			public String getToolTipText(MouseEvent event) {
				Point p = event.getPoint();
				int row = this.rowAtPoint(p);
				
				// get the text column value
				TableModel model = this.getModel();
				Object object = model.getValueAt(row, 3);
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
				if (e.getClickCount() == 2) {
					// get the selected row
					int row = tblBibleSearchResults.rowAtPoint(e.getPoint());
					// get the data
					BibleTableModel model = (BibleTableModel)tblBibleSearchResults.getModel();
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
				.addComponent(scrVerseQueue, 400, 400, Short.MAX_VALUE));
		svLayout.setVerticalGroup(svLayout.createSequentialGroup()
				.addGroup(svLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(btnRemoveSelected, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnRemoveAll, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addComponent(scrVerseQueue, 150, 200, 400));
		
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
						.addComponent(scrBibleSearchResults, 400, 400, Short.MAX_VALUE)));
		bsLayout.setVerticalGroup(bsLayout.createSequentialGroup()
				.addGroup(bsLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.txtBibleSearch, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.cmbBibleSearchType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnSearch, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.lblBibleSearchResults, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addComponent(scrBibleSearchResults, 150, 200, 400));
		
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
			bible = Bibles.getBible(bSettings.getDefaultSecondaryBibleId());
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
		
		JSeparator sepTables = new JSeparator(JSeparator.HORIZONTAL);
		
		// create the layout
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateGaps(true);
		
		// setup the horizontal layout
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(this.pnlPreview)
				.addComponent(pnlLookupPanel)
				.addComponent(sepTables)
				.addComponent(tableTabs));
		
		// setup the vertical layout
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(this.pnlPreview)
				.addComponent(pnlLookupPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(sepTables, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(tableTabs, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.SettingsListener#settingsSaved()
	 */
	@Override
	public void settingsSaved() {
		// when the settings change, either the general or the bible settings
		// we need to update all the components and redraw them
		
		GeneralSettings gSettings = GeneralSettings.getInstance();
		// in the case of general settings we need to get the assigned device
		// and see if we need to update the display size
		
		// get the display size
		Dimension displaySize = gSettings.getPrimaryDisplaySize();
		
		BibleSettings bSettings = BibleSettings.getInstance();
		
		// create new displays using the new settings (this will also reset the render qualities)
		BibleDisplay pDisplay = DisplayFactory.getDisplay(bSettings, displaySize);
		BibleDisplay cDisplay = DisplayFactory.getDisplay(bSettings, displaySize);
		BibleDisplay nDisplay = DisplayFactory.getDisplay(bSettings, displaySize);
		
		// copy over the text values
		this.copyTextValues(this.pnlPreview.getPreviousVerseDisplay(), pDisplay);
		this.copyTextValues(this.pnlPreview.getCurrentVerseDisplay(), cDisplay);
		this.copyTextValues(this.pnlPreview.getNextVerseDisplay(), nDisplay);
		
		// set the new displays
		this.pnlPreview.setPreviousVerseDisplay(pDisplay);
		this.pnlPreview.setCurrentVerseDisplay(cDisplay);
		this.pnlPreview.setNextVerseDisplay(nDisplay);
		
		// resize the view using the current size's minimum dimension
		Dimension size = this.pnlPreview.getSize();
		if (size.width < size.height) {
			this.pnlPreview.setMinimumSize(size.width);
		} else {
			this.pnlPreview.setMinimumSize(size.height);
		}
		
		// redraw the preview panel
		this.pnlPreview.repaint();
	}
	
	/**
	 * Copies the text values from the source display to the destination display.
	 * @param source the source display
	 * @param destination the destination display
	 */
	private void copyTextValues(BibleDisplay source, BibleDisplay destination) {
		TextComponent sTitle = source.getScriptureTitleComponent();
		TextComponent sText = source.getScriptureTextComponent();
		
		TextComponent dTitle = destination.getScriptureTitleComponent();
		TextComponent dText = destination.getScriptureTextComponent();
		
		dTitle.setText(sTitle.getText());
		dText.setText(sText.getText());
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
		boolean ia = BibleSettings.getInstance().isApocryphaIncluded();
		
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
				BibleSearch search = new BibleSearch(bible, text, ia, type, new BibleSearchCallback());
				this.bibleSearchThread.queueSearch(search);
			}
		}
		// check for remove selected
		else if ("remove-selected".equals(command)) {
			MutableBibleTableModel model = (MutableBibleTableModel)this.tblVerseQueue.getModel();
			model.removeSelectedRows();
		}
		// check for remove all
		else if ("remove-all".equals(command)) {
			MutableBibleTableModel model = (MutableBibleTableModel)this.tblVerseQueue.getModel();
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
				this.shiftVerseDisplays(-1, prev2);
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
				this.shiftVerseDisplays(+1, next2);
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
				MutableBibleTableModel model = (MutableBibleTableModel)this.tblVerseQueue.getModel();
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
		TransitionAnimator ta = new TransitionAnimator(transition, duration);
		StandardDisplayWindow primary = DisplayWindows.getPrimaryDisplayWindow();
		if (primary != null) {
			primary.send(this.pnlPreview.getCurrentVerseDisplay(), ta);
		} else {
			// the device is no longer available
			LOGGER.warn("The primary display doesn't exist.");
			JOptionPane.showMessageDialog(
					this, 
					Messages.getString("dialog.device.primary.missing.text"), 
					Messages.getString("dialog.device.primary.missing.title"), 
					JOptionPane.WARNING_MESSAGE);
		}
	}
	
	/**
	 * Clears the primary display.
	 */
	private void clearVerseAction() {
		// get the transition
		Transition transition = (Transition)this.cmbClearTransitions.getSelectedItem();
		int duration = ((Number)this.txtClearTransitions.getValue()).intValue();
		TransitionAnimator ta = new TransitionAnimator(transition, duration);
		StandardDisplayWindow primary = DisplayWindows.getPrimaryDisplayWindow();
		if (primary != null) {
			primary.clear(ta);
		}
	}
	
	/**
	 * Updates the bible displays for the new current verse.
	 * @param verse the new current verse
	 * @throws DataException if an exception occurs while loading the next and previous verses
	 */
	private void updateVerseDisplays(Verse verse) throws DataException {
		this.verseFound = true;
		boolean ia = BibleSettings.getInstance().isApocryphaIncluded();
		// then get the previous and next verses as well
		Verse prev = Bibles.getPreviousVerse(verse, ia);
		Verse next = Bibles.getNextVerse(verse, ia);
		
		// get the displays to update
		BibleDisplay dPrev = this.pnlPreview.getPreviousVerseDisplay();
		BibleDisplay dCurr = this.pnlPreview.getCurrentVerseDisplay();
		BibleDisplay dNext = this.pnlPreview.getNextVerseDisplay();
		
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
					dCurr.setVerse(verse, v2);
					// set the previous verse
					if (prev != null) {
						dPrev.setVerse(prev, v2p);
					} else {
						dPrev.clearVerse();
					}
					// set the next verse
					if (next != null) {
						dNext.setVerse(next, v2n);
					} else {
						dNext.clearVerse();
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
		dCurr.setVerse(verse);
		// set the previous verse
		if (prev != null) {
			dPrev.setVerse(prev);
		} else {
			dPrev.clearVerse();
		}
		// set the next verse
		if (next != null) {
			dNext.setVerse(next);
		} else {
			dNext.clearVerse();
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
		BibleDisplay display = null;
		// shift the displays
		{
			// shift them and assign the one we will modify
			if (direction < 0) {
				display = this.pnlPreview.getNextVerseDisplay();
				this.pnlPreview.setNextVerseDisplay(this.pnlPreview.getCurrentVerseDisplay());
				this.pnlPreview.setCurrentVerseDisplay(this.pnlPreview.getPreviousVerseDisplay());
				this.pnlPreview.setPreviousVerseDisplay(display);
			} else {
				display = this.pnlPreview.getPreviousVerseDisplay();
				this.pnlPreview.setPreviousVerseDisplay(this.pnlPreview.getCurrentVerseDisplay());
				this.pnlPreview.setCurrentVerseDisplay(this.pnlPreview.getNextVerseDisplay());
				this.pnlPreview.setNextVerseDisplay(display);
			}
		}
		
		// check for null
		if (verse == null) {
			// clear the verse
			display.clearVerse();
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
						display.setVerse(verse, v2);
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
		
		display.setVerse(verse);
		// repaint the preview
		this.pnlPreview.repaint();
	}
	
	/**
	 * Updates the labels to show the current information.
	 */
	private void updateLabels() {
		// look up the number of verses in the chapter
		Bible bible = (Bible)cmbBiblesPrimary.getSelectedItem();
		Book book = (Book)cmbBooks.getSelectedItem();
		Object chap = txtChapter.getValue();
		Object vers = txtVerse.getValue();
		
		if (bible != null && book != null) {
			// show the number of chapters
			try {
				int count = Bibles.getChapterCount(bible, book.getCode());
				lblChapterCount.setText(MessageFormat.format(Messages.getString("panel.bible.chapterCount"), count));
			} catch (DataException ex) {
				LOGGER.error(MessageFormat.format(Messages.getString("panel.bible.data.chapterCount.exception.text"), bible.getName(), book.getName()), ex);
				lblChapterCount.setText("");
			}
			
			if (chap != null && chap instanceof Number) {
				// show the number of verses
				int chapter = ((Number) chap).intValue();
				try {
					int count = Bibles.getVerseCount(bible, book.getCode(), chapter);
					lblVerseCount.setText(MessageFormat.format(Messages.getString("panel.bible.verseCount"), count));
				} catch (DataException ex) {
					LOGGER.error(MessageFormat.format(Messages.getString("panel.bible.data.verseCount.exception.text"), bible.getName(), book.getName(), chapter), ex);
					lblVerseCount.setText("");
				}
				
				if (vers != null && vers instanceof Number) {
					// show the found/not found icon
					int verse = ((Number)vers).intValue();
					try {
						Verse v = Bibles.getVerse(bible, book.getCode(), chapter, verse);
						if (v != null) {
							lblFound.setIcon(Icons.FOUND);
							lblFound.setToolTipText(null);
						} else {
							lblFound.setIcon(Icons.NOT_FOUND);
							lblFound.setToolTipText(MessageFormat.format(Messages.getString("panel.bible.data.verseNotFound"), bible.getName(), book.getName(), chapter, verse));
						}
					} catch (DataException ex) {
						LOGGER.error(MessageFormat.format(Messages.getString("panel.bible.data.validate.exception.text"), bible.getName(), book.getName(), chapter, verse), ex);
						lblFound.setIcon(null);
						lblFound.setToolTipText(null);
					}
				} else {
					// clear labels if not enough info is given
					lblFound.setIcon(null);
					lblFound.setToolTipText(null);
				}
			} else {
				// clear labels if not enough info is given
				lblVerseCount.setText("");
				lblFound.setIcon(null);
				lblFound.setToolTipText(null);
			}
		} else {
			// clear labels if not enough info is given
			lblChapterCount.setText(MessageFormat.format(Messages.getString("panel.bible.chapterCount"), ""));
			lblVerseCount.setText("");
			lblFound.setIcon(null);
			lblFound.setToolTipText(null);
		}
	}
	
	/**
	 * Sets the table column widths for the bible search results table.
	 */
	private void setBibleSearchTableWidths() {
		this.tblBibleSearchResults.getColumnModel().getColumn(0).setMaxWidth(150);
		this.tblBibleSearchResults.getColumnModel().getColumn(0).setPreferredWidth(110);
		this.tblBibleSearchResults.getColumnModel().getColumn(1).setMaxWidth(35);
		this.tblBibleSearchResults.getColumnModel().getColumn(1).setPreferredWidth(35);
		this.tblBibleSearchResults.getColumnModel().getColumn(2).setMaxWidth(35);
		this.tblBibleSearchResults.getColumnModel().getColumn(2).setPreferredWidth(35);
	}
	
	/**
	 * Sets the table column widths for the verse queue table.
	 */
	private void setVerseQueueTableWidths() {
		this.tblVerseQueue.getColumnModel().getColumn(0).setMaxWidth(35);
		this.tblVerseQueue.getColumnModel().getColumn(0).setPreferredWidth(35);
		this.tblVerseQueue.getColumnModel().getColumn(1).setMaxWidth(150);
		this.tblVerseQueue.getColumnModel().getColumn(1).setPreferredWidth(110);
		this.tblVerseQueue.getColumnModel().getColumn(2).setMaxWidth(35);
		this.tblVerseQueue.getColumnModel().getColumn(2).setPreferredWidth(35);
		this.tblVerseQueue.getColumnModel().getColumn(3).setMaxWidth(35);
		this.tblVerseQueue.getColumnModel().getColumn(3).setPreferredWidth(35);
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
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	private class BibleSearchCallback extends BibleSearchThread.Callback {
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
					tblBibleSearchResults.setModel(new BibleTableModel(verses));
					lblBibleSearchResults.setText(MessageFormat.format(message, verses.size()));
				} else {
					tblBibleSearchResults.setModel(new BibleTableModel());
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
