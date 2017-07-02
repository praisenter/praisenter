/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
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
package org.praisenter.javafx.bible;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Constants;
import org.praisenter.bible.Bible;
import org.praisenter.bible.Book;
import org.praisenter.bible.Chapter;
import org.praisenter.bible.Verse;
import org.praisenter.javafx.Alerts;
import org.praisenter.javafx.ApplicationAction;
import org.praisenter.javafx.ApplicationContextMenu;
import org.praisenter.javafx.ApplicationEditorPane;
import org.praisenter.javafx.ApplicationEvent;
import org.praisenter.javafx.ApplicationGlyphs;
import org.praisenter.javafx.ApplicationPane;
import org.praisenter.javafx.ApplicationPaneEvent;
import org.praisenter.javafx.DataFormats;
import org.praisenter.javafx.Option;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.PreventUndoRedoEventFilter;
import org.praisenter.javafx.async.AsyncTask;
import org.praisenter.javafx.bible.commands.AddBookEditCommand;
import org.praisenter.javafx.bible.commands.AddChapterEditCommand;
import org.praisenter.javafx.bible.commands.AddVerseEditCommand;
import org.praisenter.javafx.bible.commands.BibleCopyrightEditCommand;
import org.praisenter.javafx.bible.commands.BibleLanguageEditCommand;
import org.praisenter.javafx.bible.commands.BibleNameEditCommand;
import org.praisenter.javafx.bible.commands.BibleNotesEditCommand;
import org.praisenter.javafx.bible.commands.BibleSourceEditCommand;
import org.praisenter.javafx.bible.commands.BookNameEditCommand;
import org.praisenter.javafx.bible.commands.BookNumberEditCommand;
import org.praisenter.javafx.bible.commands.ChapterNumberEditCommand;
import org.praisenter.javafx.bible.commands.RemoveBookEditCommand;
import org.praisenter.javafx.bible.commands.RemoveChapterEditCommand;
import org.praisenter.javafx.bible.commands.RemoveVerseEditCommand;
import org.praisenter.javafx.bible.commands.RenumberEditCommand;
import org.praisenter.javafx.bible.commands.ReorderEditCommand;
import org.praisenter.javafx.bible.commands.VerseNumberEditCommand;
import org.praisenter.javafx.bible.commands.VerseTextEditCommand;
import org.praisenter.javafx.command.CommandFactory;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.command.EditManager;
import org.praisenter.javafx.command.RemoveEditCommand;
import org.praisenter.javafx.command.UndorderedCompositeEditCommand;
import org.praisenter.javafx.configuration.Setting;
import org.praisenter.javafx.themes.Styles;
import org.praisenter.javafx.utility.Fx;
import org.praisenter.resources.translations.Translations;
import org.praisenter.xml.XmlIO;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.util.Callback;
import javafx.util.StringConverter;

// FEATURE (L) Add glyphicons to nodes to help distinguish book & chapter
// FEATURE (L) Add ability to create N number of books, chapters, verses with default text

/**
 * A pane for editing {@link Bible}s.
 * @author William Bittle
 * @version 3.0.0
 */
public final class BibleEditorPane extends BorderPane implements ApplicationPane, ApplicationEditorPane {
	/** The class level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	// data
	
	/** The praisenter context */
	private final PraisenterContext context;

	/** The bible being edited */
	private final ObjectProperty<Bible> bible = new SimpleObjectProperty<Bible>();
	
	// nodes
	
	/** The bible tree view */
	private final TreeView<TreeData> bibleTree;
	
	/** The field for the bible name */
	private final TextField txtName;
	
	// state
	
	/** The editing manager */
	private final EditManager manager = new EditManager();
	
	/** True when the bible property is being set */
	private boolean mutating = false;
	
	/**
	 * Minimal constructor.
	 * @param context the context
	 */
	public BibleEditorPane(PraisenterContext context) {
		this.getStyleClass().add(Styles.BIBLE_EDITOR_PANE);
		
		this.context = context;

		ObservableList<Option<Locale>> locales = FXCollections.observableArrayList();
		for (Locale locale : Locale.getAvailableLocales()) {
			locales.add(new Option<Locale>(locale.getDisplayName(), locale));
		}
		Collections.sort(locales);
		
		// bible
		Label lblName = new Label(Translations.get("bible.edit.name"));
		this.txtName = new TextField();
		this.txtName.addEventFilter(KeyEvent.KEY_PRESSED, new PreventUndoRedoEventFilter(this));
		
		Label lblLanguage = new Label(Translations.get("bible.edit.language"));
		ComboBox<Option<Locale>> cmbLanguage = new ComboBox<Option<Locale>>(locales);
		cmbLanguage.setEditable(true);
		cmbLanguage.getEditor().addEventFilter(KeyEvent.KEY_PRESSED, new PreventUndoRedoEventFilter(this));
		cmbLanguage.setCellFactory(new Callback<ListView<Option<Locale>>, ListCell<Option<Locale>>>() {
			public ListCell<Option<Locale>> call(ListView<Option<Locale>> param) {
				ListCell<Option<Locale>> cell = new ListCell<Option<Locale>>() {
					@Override
					protected void updateItem(Option<Locale> item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setText(null);
						} else {
							Locale locale = item.getValue();
							setText(locale != null ? locale.getDisplayName() : item.getName());
						}
					}
				};
				return cell;
			}
		});
		cmbLanguage.setButtonCell(cmbLanguage.getCellFactory().call(null));
		cmbLanguage.setConverter(new StringConverter<Option<Locale>>() {
			@Override
			public String toString(Option<Locale> option) {
				if (option != null) {
					Locale locale = option.getValue();
					if (locale != null) {
						return locale.toLanguageTag();
					} else {
						return option.getName();
					}
				} else {
					return null;
				}
			}
			
			@Override
			public Option<Locale> fromString(String value) {
				Locale locale = value != null ? Locale.forLanguageTag(value) : null;
				return new Option<Locale>(locale != null ? locale.getDisplayName() : value, locale);
			}
		});
		cmbLanguage.setMaxWidth(Double.MAX_VALUE);
		
		Label lblSource = new Label(Translations.get("bible.edit.source"));
		TextField txtSource = new TextField();
		txtSource.addEventFilter(KeyEvent.KEY_PRESSED, new PreventUndoRedoEventFilter(this));
		
		Label lblCopyright = new Label(Translations.get("bible.edit.copyright"));
		TextField txtCopyright = new TextField();
		txtCopyright.addEventFilter(KeyEvent.KEY_PRESSED, new PreventUndoRedoEventFilter(this));
		
		Label lblNotes = new Label(Translations.get("bible.edit.notes"));
		TextArea txtNotes = new TextArea();
		txtNotes.setWrapText(true);
		txtNotes.setPrefHeight(250);
		txtNotes.addEventFilter(KeyEvent.KEY_PRESSED, new PreventUndoRedoEventFilter(this));
		
		Label lblEditMessage = new Label(Translations.get("bible.edit.message"), ApplicationGlyphs.BIBLE_EDIT_ARROW_LEFT.duplicate());
		
		// book
		Label lblBookName = new Label(Translations.get("bible.edit.bookname"));
		TextField txtBookName = new TextField();
		txtBookName.addEventFilter(KeyEvent.KEY_PRESSED, new PreventUndoRedoEventFilter(this));
		
		Label lblBookNumber = new Label(Translations.get("bible.edit.booknumber"));
		Spinner<Integer> spnBookNumber = new Spinner<Integer>(1, Short.MAX_VALUE, 1, 1);
		spnBookNumber.setEditable(true);
		spnBookNumber.getEditor().addEventFilter(KeyEvent.KEY_PRESSED, new PreventUndoRedoEventFilter(this));
		
		// chapter
		Label lblChapter = new Label(Translations.get("bible.edit.chapternumber"));
		Spinner<Integer> spnChapter = new Spinner<Integer>(1, Short.MAX_VALUE, 1, 1);
		spnChapter.setEditable(true);
		spnChapter.getEditor().addEventFilter(KeyEvent.KEY_PRESSED, new PreventUndoRedoEventFilter(this));
		
		// verse
		Label lblVerse = new Label(Translations.get("bible.edit.versenumber"));
		Spinner<Integer> spnVerse = new Spinner<Integer>(1, Short.MAX_VALUE, 1, 1);
		spnVerse.setEditable(true);
		spnVerse.getEditor().addEventFilter(KeyEvent.KEY_PRESSED, new PreventUndoRedoEventFilter(this));
		
		Label lblVerseText = new Label(Translations.get("bible.edit.versetext"));
		TextArea txtText = new TextArea();
		txtText.setWrapText(true);
		txtText.setPrefHeight(350);
		txtText.addEventFilter(KeyEvent.KEY_PRESSED, new PreventUndoRedoEventFilter(this));
		
		VBox editorFields = new VBox();
		editorFields.setSpacing(2);
		editorFields.setPadding(new Insets(10));
		editorFields.getChildren().addAll(
				lblEditMessage,
				lblName,
				this.txtName, 
				lblLanguage,
				cmbLanguage,
				lblSource,
				txtSource,
				lblCopyright,
				txtCopyright,
				lblNotes,
				txtNotes,
				lblBookName,
				txtBookName, 
				lblBookNumber,
				spnBookNumber,
				lblChapter,
				spnChapter,
				lblVerse,
				spnVerse,
				lblVerseText,
				txtText);
		VBox.setVgrow(txtText, Priority.ALWAYS);
		VBox.setVgrow(txtNotes, Priority.ALWAYS);
		
		for (Node node : editorFields.getChildren()) {
			node.managedProperty().bind(node.visibleProperty());
			node.setVisible(false);
		}
		lblEditMessage.setVisible(true);
		
		TitledPane ttlOther = new TitledPane(Translations.get("bible.edit.title"), editorFields);
		ttlOther.setCollapsible(false);
		
		BibleEditorDragDropManager manager = new BibleEditorDragDropManager();
		
		this.bibleTree = new TreeView<TreeData>();
		this.bibleTree.setEditable(true);
		this.bibleTree.setShowRoot(true);
		// allow multiple selection
		this.bibleTree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		this.bibleTree.setCellFactory(new Callback<TreeView<TreeData>, TreeCell<TreeData>>(){
            @Override
            public TreeCell<TreeData> call(TreeView<TreeData> itm) {
            	BibleTreeCell cell = new BibleTreeCell();
            	// wire up events
            	cell.setOnDragDetected(e -> {
        			manager.dragDetected(cell, e);
        		});
            	cell.setOnDragExited(e -> {
        			manager.dragExited(cell, e);
        		});
            	cell.setOnDragEntered(e -> {
        			manager.dragEntered(cell, e);
        		});
            	cell.setOnDragOver(e -> {
        			manager.dragOver(cell, e);
        		});
            	cell.setOnDragDropped(e -> {
        			EditCommand command = manager.dragDropped(cell, e);
        			applyCommand(command);
        		});
            	cell.setOnDragDone(e -> {
            		manager.dragDone(cell, e);
            	});
            	return cell;
            }
        });
		
		// context menu
		ApplicationContextMenu menu = new ApplicationContextMenu(this);
		menu.getItems().addAll(
				menu.createMenuItem(ApplicationAction.NEW_BOOK),
				menu.createMenuItem(ApplicationAction.NEW_CHAPTER),
				menu.createMenuItem(ApplicationAction.NEW_VERSE),
				new SeparatorMenuItem(),
				menu.createMenuItem(ApplicationAction.COPY),
				menu.createMenuItem(ApplicationAction.CUT),
				menu.createMenuItem(ApplicationAction.PASTE),
				new SeparatorMenuItem(),
				menu.createMenuItem(ApplicationAction.REORDER),
				menu.createMenuItem(ApplicationAction.RENUMBER),
				new SeparatorMenuItem(),
				menu.createMenuItem(ApplicationAction.DELETE));
		this.bibleTree.setContextMenu(menu);

		// TOOLBAR
		
		Button btnSave = this.createToolbarButton(ApplicationAction.SAVE);
		Button btnSaveAs = this.createToolbarButton(ApplicationAction.SAVE_AS);
		Button btnClose = this.createToolbarButton(ApplicationAction.CLOSE);
		ToolBar toolbar = new ToolBar(btnSave, btnSaveAs, btnClose);
		
		// LAYOUT
		
		BorderPane left = new BorderPane(this.bibleTree);
		
		VBox mid = new VBox(ttlOther);
		mid.setMinWidth(300);
		mid.setPrefWidth(300);
		
		ttlOther.prefHeightProperty().bind(mid.heightProperty());
		
		SplitPane split = new SplitPane(left, mid);
		split.setDividerPositions(0.75);
		split.setBackground(null);
		split.setPadding(new Insets(0));
		
		this.setTop(toolbar);
		this.setCenter(split);
		
		// EVENTS & BINDINGS
		this.bible.addListener((obs, ov, nv) -> {
			this.bibleTree.getSelectionModel().clearSelection();
			this.mutating = true;
			this.manager.reset();
			
			if (nv != null) {
				// create the root node
				TreeItem<TreeData> root = this.forBible(nv);
				root.setExpanded(true);
				
				// set the tree
				this.bibleTree.setRoot(root);
				
				// set the editor fields
				this.txtName.setText(nv.getName());
				if (nv.getLanguage() != null) {
					Locale locale = Locale.forLanguageTag(nv.getLanguage());
					if (locale != null) {
						cmbLanguage.setValue(new Option<Locale>(locale.getDisplayName(), locale));
					} else {
						cmbLanguage.setValue(null);
						cmbLanguage.getEditor().setText(nv.getLanguage());
					}
				} else {
					cmbLanguage.setValue(null);
					cmbLanguage.getEditor().setText(null);
				}
				txtSource.setText(nv.getSource());
				txtCopyright.setText(nv.getCopyright());
				txtNotes.setText(nv.getNotes());
			} else {
				this.bibleTree.setRoot(null);
				
				this.txtName.setText(null);
				cmbLanguage.setValue(null);
				cmbLanguage.getEditor().setText(null);
				txtSource.setText(null);
				txtCopyright.setText(null);
				txtNotes.setText(null);
			}
			this.mutating = false;
		});
		
		this.bibleTree.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<TreeItem<TreeData>>() {
			@Override
			public void onChanged(ListChangeListener.Change<? extends TreeItem<TreeData>> change) {
				// update state
				stateChanged(ApplicationPaneEvent.REASON_SELECTION_CHANGED);
			}
		});
		
		this.bibleTree.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
			this.mutating = true;
			for (Node node : editorFields.getChildren()) {
				node.setVisible(false);
			}
			if (nv != null && this.bibleTree.getSelectionModel().getSelectedIndices().size() == 1) {
				TreeData data = nv.getValue();
				if (data instanceof BibleTreeData) {
					lblName.setVisible(true);
					this.txtName.setVisible(true);
					lblLanguage.setVisible(true);
					cmbLanguage.setVisible(true);
					lblSource.setVisible(true);
					txtSource.setVisible(true);
					lblCopyright.setVisible(true);
					txtCopyright.setVisible(true);
					lblNotes.setVisible(true);
					txtNotes.setVisible(true);
				} else if (data instanceof BookTreeData) {
					// show book name
					lblBookName.setVisible(true);
					txtBookName.setVisible(true);
					txtBookName.setText(((BookTreeData)data).book.getName());
					lblBookNumber.setVisible(true);
					spnBookNumber.setVisible(true);
					spnBookNumber.getValueFactory().setValue((int)((BookTreeData)data).book.getNumber());
				} else if (data instanceof ChapterTreeData) {
					// show book name
					lblChapter.setVisible(true);
					spnChapter.setVisible(true);
					spnChapter.getValueFactory().setValue((int)((ChapterTreeData)data).chapter.getNumber());
				} else if (data instanceof VerseTreeData) {
					// show book name
					lblVerse.setVisible(true);
					spnVerse.setVisible(true);
					spnVerse.getValueFactory().setValue((int)((VerseTreeData)data).verse.getNumber());
					lblVerseText.setVisible(true);
					txtText.setVisible(true);
					txtText.setText(((VerseTreeData)data).verse.getText());
				}
			} else {
				lblEditMessage.setVisible(true);
			}
			this.mutating = false;
		});
		
		// editing events
		
		// bible name
		this.txtName.textProperty().addListener((obs, ov, nv) -> {
			if (this.mutating) return;
			TreeItem<TreeData> item = this.bibleTree.getRoot();
			applyCommand(new BibleNameEditCommand(
					item,
					CommandFactory.changed(ov, nv),
					CommandFactory.select(this.bibleTree, item),
					CommandFactory.text(this.txtName)));
		});
		
		// bible language
		cmbLanguage.valueProperty().addListener((obs, ov, nv) -> {
			if (this.mutating) return;
			TreeItem<TreeData> item = this.bibleTree.getRoot();
			applyCommand(new BibleLanguageEditCommand(
					item,
					CommandFactory.changed(ov, nv),
					CommandFactory.select(this.bibleTree, item),
					CommandFactory.combo(cmbLanguage)));
		});
		
		// bible source
		txtSource.textProperty().addListener((obs, ov, nv) -> {
			if (this.mutating) return;
			TreeItem<TreeData> item = this.bibleTree.getRoot();
			applyCommand(new BibleSourceEditCommand(
					item,
					CommandFactory.changed(ov, nv),
					CommandFactory.select(this.bibleTree, item),
					CommandFactory.text(txtSource)));
		});
		
		// bible copyright
		txtCopyright.textProperty().addListener((obs, ov, nv) -> {
			if (this.mutating) return;
			TreeItem<TreeData> item = this.bibleTree.getRoot();
			applyCommand(new BibleCopyrightEditCommand(
					item,
					CommandFactory.changed(ov, nv),
					CommandFactory.select(this.bibleTree, item),
					CommandFactory.text(txtCopyright)));
		});
		
		// bible notes
		txtNotes.textProperty().addListener((obs, ov, nv) -> {
			if (this.mutating) return;
			TreeItem<TreeData> item = this.bibleTree.getRoot();
			applyCommand(new BibleNotesEditCommand(
					item,
					CommandFactory.changed(ov, nv),
					CommandFactory.select(this.bibleTree, item),
					CommandFactory.text(txtNotes)));
		});
		
		// book name
		txtBookName.textProperty().addListener((obs, ov, nv) -> {
			if (this.mutating) return;
			TreeItem<TreeData> item = this.bibleTree.getSelectionModel().getSelectedItem();
			applyCommand(new BookNameEditCommand(
					item,
					CommandFactory.changed(ov, nv),
					CommandFactory.select(this.bibleTree, item),
					CommandFactory.text(txtBookName)));
		});
		
		// book number
		spnBookNumber.valueProperty().addListener((obs, ov, nv) -> {
			if (this.mutating) return;
			TreeItem<TreeData> item = this.bibleTree.getSelectionModel().getSelectedItem();
			applyCommand(new BookNumberEditCommand(
					item,
					CommandFactory.changed(ov, nv),
					CommandFactory.select(this.bibleTree, item),
					CommandFactory.spinner(spnBookNumber)));
		});
		
		// chapter number
		spnChapter.valueProperty().addListener((obs, ov, nv) -> {
			if (this.mutating) return;
			TreeItem<TreeData> item = this.bibleTree.getSelectionModel().getSelectedItem();
			applyCommand(new ChapterNumberEditCommand(
					item,
					CommandFactory.changed(ov, nv),
					CommandFactory.select(this.bibleTree, item),
					CommandFactory.spinner(spnChapter)));
		});
		
		// verse number
		spnVerse.valueProperty().addListener((obs, ov, nv) -> {
			if (this.mutating) return;
			TreeItem<TreeData> item = this.bibleTree.getSelectionModel().getSelectedItem();
			applyCommand(new VerseNumberEditCommand(
					item,
					CommandFactory.changed(ov, nv),
					CommandFactory.select(this.bibleTree, item),
					CommandFactory.spinner(spnVerse)));
		});
		
		// verse text
		txtText.textProperty().addListener((obs, ov, nv) -> {
			if (this.mutating) return;
			TreeItem<TreeData> item = this.bibleTree.getSelectionModel().getSelectedItem();
			applyCommand(new VerseTextEditCommand(
					item,
					CommandFactory.changed(ov, nv),
					CommandFactory.select(this.bibleTree, item),
					CommandFactory.text(txtText)));
		});
		
		// listen for application events
		this.addEventHandler(ApplicationEvent.ALL, this::onApplicationEvent);
	}

	private Button createToolbarButton(ApplicationAction action) {
		Button button = action.toButton();
		button.setOnAction(e -> {
			this.fireEvent(new ApplicationEvent(button, button, ApplicationEvent.ALL, action));
		});
		return button;
	}
	
	// NODE GENERATION
	
	private TreeItem<TreeData> forBible(Bible bible) {
		TreeItem<TreeData> root = new TreeItem<TreeData>(new BibleTreeData(bible));
		for (Book book : bible.getBooks()) {
			if (book != null) {
				TreeItem<TreeData> bi = this.forBook(bible, book);
				root.getChildren().add(bi);
			}
		}
		return root;
	}
	
	private TreeItem<TreeData> forBook(Bible bible, Book book) {
		TreeItem<TreeData> bi = new TreeItem<TreeData>(new BookTreeData(bible, book));
		for (Chapter chapter : book.getChapters()) {
			TreeItem<TreeData> ch = this.forChapter(bible, book, chapter);
			bi.getChildren().add(ch);
		}
		return bi;
	}
	
	private TreeItem<TreeData> forChapter(Bible bible, Book book, Chapter chapter) {
		TreeItem<TreeData> ch = new TreeItem<TreeData>(new ChapterTreeData(bible, book, chapter));
		for (Verse verse : chapter.getVerses()) {
			TreeItem<TreeData> vi = this.forVerse(bible, book, chapter, verse);
			ch.getChildren().add(vi);
		}
		return ch;
	}
	
	private TreeItem<TreeData> forVerse(Bible bible, Book book, Chapter chapter, Verse verse) {
		return new TreeItem<TreeData>(new VerseTreeData(bible, book, chapter, verse));
	}
	
	// METHODS
	
	/**
	 * Applies the given edit command.
	 * @param command the command
	 */
	private void applyCommand(EditCommand command) {
		this.manager.execute(command);
		stateChanged(ApplicationPaneEvent.REASON_UNDO_REDO_STATE_CHANGED);
	}
	
	/**
	 * Copies the selected items.
	 * @param cut true if they should be cut instead of copied
	 */
	private void copy(boolean cut) {
		// get the selection(s)
		List<TreeItem<TreeData>> items = new ArrayList<TreeItem<TreeData>>(this.bibleTree.getSelectionModel().getSelectedItems());
		
		// make sure the selections are all the same type (all books or all chapters or all verses)
		Class<?> type = null;
		if (!items.isEmpty()) {
			TreeItem<TreeData> item = items.get(0);
			if (item != null) {
				type = item.getValue().getClass();
				for (TreeItem<TreeData> other : items) {
					if (!other.getValue().getClass().equals(type)) {
						// different types, so exit
						// TODO warn user with alert
						LOGGER.warn("Selections were of different types. Copy/Cut action cancelled.");
						return;
					}
				}
			}
		}
		
		List<RemoveEditCommand> commands = new ArrayList<RemoveEditCommand>();
		if (!items.isEmpty()) {
			DataFormat format = null;
			StringBuilder text = new StringBuilder();
			
			List<String> data = new ArrayList<String>();
			for (TreeItem<TreeData> item : items) {
				TreeData td = item.getValue();
				if (td instanceof BookTreeData) {
					BookTreeData btd = (BookTreeData)td;
					Book book = btd.book;
					try {
						data.add(XmlIO.save(book));
						format = DataFormats.BOOKS;
						text.append(book.getName()).append(Constants.NEW_LINE);
						if (cut) {
							commands.add(new RemoveBookEditCommand(item));
						}
					} catch (Exception ex) {
						LOGGER.warn("Failed to add the book '" + book.getName() + "' to the clipboard.", ex);
					}
				} else if (td instanceof ChapterTreeData) {
					ChapterTreeData ctd = (ChapterTreeData)td;
					Chapter chapter = ctd.chapter;
					try {
						data.add(XmlIO.save(chapter));
						format = DataFormats.CHAPTERS;
						text.append(chapter.getNumber()).append(Constants.NEW_LINE);
						if (cut) {
							commands.add(new RemoveChapterEditCommand(item));
						}
					} catch (Exception ex) {
						LOGGER.warn("Failed to add the chapter '" + chapter.getNumber() + "' to the clipboard.", ex);
					}
				} else if (td instanceof VerseTreeData) {
					VerseTreeData vtd = (VerseTreeData)td;
					Verse verse = vtd.verse;
					try {
						data.add(XmlIO.save(verse));
						format = DataFormats.VERSES;
						text.append(verse.getText()).append(Constants.NEW_LINE);
						if (cut) {
							commands.add(new RemoveVerseEditCommand(item));
						}
					} catch (Exception ex) {
						LOGGER.warn("Failed to add the verse '" + verse.getText() + "' to the clipboard.", ex);
					}
				}
			}
			
			Clipboard cb = Clipboard.getSystemClipboard();
			ClipboardContent cc = new ClipboardContent();
			cc.putString(text.toString().trim());
			cc.put(format, data);
			cb.setContent(cc);
			
			// was anything removed? (a cut command)
			if (!commands.isEmpty()) {
				// build the command
				EditCommand command = CommandFactory.chain(
						// first perform the removes
						CommandFactory.sequence(commands),
						// then do some UI stuff
						CommandFactory.chain(
								// focus the TreeView
								CommandFactory.focus(this.bibleTree),
								// select the appropriate TreeItem
								commands.size() == 1
									? CommandFactory.selectRemoved(this.bibleTree, items.get(0))
									: CommandFactory.select(this.bibleTree, this.bibleTree.getRoot())));
				// apply it
				applyCommand(command);
			}
			
			// notify we changed
			this.stateChanged(ApplicationPaneEvent.REASON_DATA_COPIED);
		}
	}
	
	/**
	 * Pastes the copied items.
	 */
	@SuppressWarnings("unchecked")
	private void paste() {
		// get the selection(s)
		ObservableList<TreeItem<TreeData>> items = this.bibleTree.getSelectionModel().getSelectedItems();
		List<EditCommand> commands = new ArrayList<EditCommand>();
		
		Clipboard cb = Clipboard.getSystemClipboard();
		if (items.size() == 1) {
			TreeItem<TreeData> node = items.get(0);
			Class<?> type = node.getValue().getClass();
			if (type.equals(BibleTreeData.class)) {
				// then we can paste books
				Object data = cb.getContent(DataFormats.BOOKS);
				if (data != null && data instanceof List) {
					List<String> books = (List<String>)data;
					for (String book : books) {
						try {
							Book copy = XmlIO.read(book, Book.class);
							commands.add(new AddBookEditCommand(this.bibleTree, node, copy));
						} catch (Exception ex) {
							LOGGER.warn("Failed to paste the book '" + book + "'.", ex);
						}
					}
				}
			} else if (type.equals(BookTreeData.class)) {
				// then we can paste chapters
				Object data = cb.getContent(DataFormats.CHAPTERS);
				if (data != null && data instanceof List) {
					List<String> chapters = (List<String>)data;
					for (String chapter : chapters) {
						try {
							Chapter copy = XmlIO.read(chapter, Chapter.class);
							commands.add(new AddChapterEditCommand(this.bibleTree, node, copy));
						} catch (Exception ex) {
							LOGGER.warn("Failed to paste the chapter '" + chapter + "'.", ex);
						}
					}
				}
			} else if (type.equals(ChapterTreeData.class)) {
				// then we can paste verses
				Object data = cb.getContent(DataFormats.VERSES);
				if (data != null && data instanceof List) {
					List<String> verses = (List<String>)data;
					for (String verse : verses) {
						try {
							Verse copy = XmlIO.read(verse, Verse.class);
							commands.add(new AddVerseEditCommand(this.bibleTree, node, copy));
						} catch (Exception ex) {
							LOGGER.warn("Failed to paste the verse '" + verse + "'.", ex);
						}
					}
				}
			} else if (type.equals(VerseTreeData.class)) {
				// then we can paste books
				Object data = cb.getContent(DataFormats.VERSES);
				if (data != null && data instanceof List) {
					List<String> verses = (List<String>)data;
					for (String verse : verses) {
						try {
							Verse copy = XmlIO.read(verse, Verse.class);
							commands.add(new AddVerseEditCommand(this.bibleTree, node, copy));
						} catch (Exception ex) {
							LOGGER.warn("Failed to paste the verse '" + verse + "'.", ex);
						}
					}
				}
			}
		}
		
		applyCommand(new UndorderedCompositeEditCommand(commands));
	}
	
	/**
	 * Deletes the selected items.
	 */
	private void promptDelete() {
		List<TreeItem<TreeData>> items = new ArrayList<TreeItem<TreeData>>(this.bibleTree.getSelectionModel().getSelectedItems());
		
		if (items.size() > 0) {
			Alert alert = Alerts.confirm(
					getScene().getWindow(),
					Modality.WINDOW_MODAL,
					Translations.get("action.delete"), 
					Translations.get("bible.edit.delete.header"), 
					Translations.get("bible.edit.delete.content"));
			
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				List<RemoveEditCommand> commands = new ArrayList<RemoveEditCommand>();
				for (TreeItem<TreeData> item : items) {
					TreeData td = item.getValue();
					if (td instanceof BookTreeData) {
						commands.add(new RemoveBookEditCommand(item));
					} else if (td instanceof ChapterTreeData) {
						commands.add(new RemoveChapterEditCommand(item));
					} else if (td instanceof VerseTreeData) {
						commands.add(new RemoveVerseEditCommand(item));
					}
				}
				
				// was anything removed? (a cut command)
				if (!commands.isEmpty()) {
					// build the command
					EditCommand command = CommandFactory.chain(
							// first perform the removes
							CommandFactory.sequence(commands),
							// then do some UI stuff
							CommandFactory.chain(
									// focus the TreeView
									CommandFactory.focus(this.bibleTree),
									// select the appropriate TreeItem
									commands.size() == 1
										? CommandFactory.selectRemoved(this.bibleTree, items.get(0))
										: CommandFactory.select(this.bibleTree, this.bibleTree.getRoot())));
					// apply it
					applyCommand(command);
				}
			}
		}
	}
	
	/**
	 * Adds new verses, chapters, and books.
	 */
	private void add() {
		TreeItem<TreeData> item = this.bibleTree.getSelectionModel().getSelectedItem();
		
		if (item == null || item.getValue() == null) return;
		
		TreeData data = item.getValue();
		if (data instanceof VerseTreeData) {
			applyCommand(new AddVerseEditCommand(this.bibleTree, item));
		} else if (data instanceof ChapterTreeData) {
			applyCommand(new AddVerseEditCommand(this.bibleTree, item));
		} else if (data instanceof BookTreeData) {
			applyCommand(new AddChapterEditCommand(this.bibleTree, item));
		} else if (data instanceof BibleTreeData) {
			applyCommand(new AddBookEditCommand(this.bibleTree, item));
		}
	}
	
	/**
	 * Saves the current bible.
	 */
	private void save() {
		this.manager.mark();
		AsyncTask<Bible> task = BibleActions.bibleSave(
			this.context.getBibleLibrary(), 
			this.getScene().getWindow(), 
			this.getBible());
		task.addCancelledOrFailedHandler(e -> {
			this.manager.unmark();
		}).execute(this.context.getExecutorService());
	}
	
	/**
	 * Saves the current bible.
	 */
	private void promptSaveAs() {
		this.manager.mark();
		AsyncTask<Bible> task = BibleActions.biblePromptSaveAs(
			this.context.getBibleLibrary(), 
			this.getScene().getWindow(), 
			this.getBible());
		task.addSuccessHandler(e -> {
			Bible saved = task.getValue();
			// we need to update the bible that is currently
			// being edited to make sure that if the name changed
			// that the name and other metadata is updated so when
			// subsequent changes are saved, they are saved to the
			// appropriate place.  It's also possible that the 
			// bible has been changed since the save completed
			this.bible.get().as(saved);
			this.mutating = true;
			// manually update the name field
			this.txtName.setText(saved.getName());
			this.mutating = false;
		}).addCancelledOrFailedHandler(e -> {
			this.manager.unmark();
		}).execute(this.context.getExecutorService());
	}
	
	/**
	 * Reorders the selected node.
	 */
	private void promptReorder() {
		TreeItem<TreeData> item = this.bibleTree.getSelectionModel().getSelectedItem();
		// need to determine what is selected
		// renumber depth first
		if (item != null) {
			Optional<ButtonType> result = Optional.of(ButtonType.OK);
			
			// see if we should prompt the user to verify
			boolean verify = this.context.getConfiguration().getBoolean(Setting.BIBLE_SHOW_REORDER_WARNING, true);
			if (verify) {
				Alert alert = Alerts.optOut(
					getScene().getWindow(),
					Modality.WINDOW_MODAL,
					AlertType.CONFIRMATION, 
					Translations.get("action.bible.reorder"), 
					Translations.get("bible.edit.reorder.header"), 
					Translations.get("bible.edit.reorder.content"), 
					Translations.get("optout"), 
					(d) -> {
						this.context.getConfiguration()
							.setBoolean(Setting.BIBLE_SHOW_REORDER_WARNING, false)
							.execute(this.context.getExecutorService());
					});
	
				result = alert.showAndWait();
			}
			
			if (result.get() == ButtonType.OK){
				applyCommand(CommandFactory.chain(
					new ReorderEditCommand(this.bibleTree, item),
					CommandFactory.chain(
						CommandFactory.focus(this.bibleTree),
						CommandFactory.select(this.bibleTree, item))));
			}
		}
	}

	/**
	 * Renumbers the selected node.
	 */
	private void promptRenumber() {
		TreeItem<TreeData> item = this.bibleTree.getSelectionModel().getSelectedItem();
		// need to determine what is selected
		// renumber depth first
		if (item != null) {
			Optional<ButtonType> result = Optional.of(ButtonType.OK);
			
			// see if we should prompt the user to verify
			boolean verify = this.context.getConfiguration().getBoolean(Setting.BIBLE_SHOW_RENUMBER_WARNING, true);
			if (verify) {
				Alert alert = Alerts.optOut(
						getScene().getWindow(),
						Modality.WINDOW_MODAL,
						AlertType.CONFIRMATION, 
						Translations.get("action.bible.renumber"), 
						Translations.get("bible.edit.renumber.header"), 
						Translations.get("bible.edit.renumber.content"), 
						Translations.get("optout"), 
						(d) -> {
							this.context.getConfiguration()
								.setBoolean(Setting.BIBLE_SHOW_RENUMBER_WARNING, false)
								.execute(this.context.getExecutorService());
						});
	
				result = alert.showAndWait();
			}
			
			if (result.get() == ButtonType.OK){
				applyCommand(CommandFactory.chain(
						new RenumberEditCommand(this.bibleTree, item),
						CommandFactory.chain(
							CommandFactory.focus(this.bibleTree),
							CommandFactory.select(this.bibleTree, item))));
			}
		}
	}
	
    /**
     * Event handler for application events.
     * @param event the event
     */
    private final void onApplicationEvent(ApplicationEvent event) {
    	Node focused = this.getScene().getFocusOwner();
    	ApplicationAction action = event.getAction();
    	switch (action) {
	    	case NEW_BOOK:
	    	case NEW_CHAPTER:
	    	case NEW_VERSE:
	    		// we only want to execute this if the current focus
				// is within the bibleTree
				if (Fx.isNodeInFocusChain(focused, this.bibleTree)) {
					add();
				}
				break;
			case COPY:
				// we only want to execute this if the current focus
				// is within the bibleTree
				if (Fx.isNodeInFocusChain(focused, this.bibleTree)) {
					this.copy(false);
				}
				break;
			case CUT:
				// we only want to execute this if the current focus
				// is within the bibleTree
				if (Fx.isNodeInFocusChain(focused, this.bibleTree)) {
					this.copy(true);
				}
				break;
			case PASTE:
				// we only want to execute this if the current focus
				// is within the bibleTree
				if (Fx.isNodeInFocusChain(focused, this.bibleTree)) {
					this.paste();
				}
				break;
			case DELETE:
				// we only want to execute this if the current focus
				// is within the bibleTree
				if (Fx.isNodeInFocusChain(focused, this.bibleTree)) {
					this.promptDelete();
				}
				break;
			case SAVE:
				this.save();
				break;
			case SAVE_AS:
				this.promptSaveAs();
				break;
			case CLOSE:
				// go back to the bible library
				this.fireEvent(new ApplicationEvent(this, this, ApplicationEvent.ALL, ApplicationAction.MANAGE_BIBLES));
				break;
			case RENUMBER:
				// we only want to execute this if the current focus
				// is within the bibleTree
				if (Fx.isNodeInFocusChain(focused, this.bibleTree)) {
					this.promptRenumber();
				}
				break;
			case REORDER:
				// we only want to execute this if the current focus
				// is within the bibleTree
				if (Fx.isNodeInFocusChain(focused, this.bibleTree)) {
					this.promptReorder();
				}
				break;
			case UNDO:
				this.manager.undo();
				this.stateChanged(ApplicationPaneEvent.REASON_UNDO_REDO_STATE_CHANGED);
				break;
			case REDO:
				this.manager.redo();
				this.stateChanged(ApplicationPaneEvent.REASON_UNDO_REDO_STATE_CHANGED);
				break;
    		default:
    			break;
    	}
    }
    
    /**
     * Called when the state of this pane changes.
     * @param reason the reason
     */
    private final void stateChanged(String reason) {
    	Scene scene = this.getScene();
    	// don't bother if there's no place to send the event to
    	if (scene != null) {
    		fireEvent(new ApplicationPaneEvent(this.bibleTree, BibleEditorPane.this, ApplicationPaneEvent.STATE_CHANGED, BibleEditorPane.this, reason));
    	}
    }
    
    /* (non-Javadoc)
     * @see org.praisenter.javafx.ApplicationPane#setDefaultFocus()
     */
    @Override
    public void setDefaultFocus() {
    	this.requestFocus();
    }
    
    /* (non-Javadoc)
     * @see org.praisenter.javafx.ApplicationPane#isApplicationActionEnabled(org.praisenter.javafx.ApplicationAction)
     */
	@Override
	public boolean isApplicationActionEnabled(ApplicationAction action) {
		Node focused = this.getScene().getFocusOwner();
		
		// how much is selected?
		ObservableList<TreeItem<TreeData>> items = this.bibleTree.getSelectionModel().getSelectedItems();
		int count = items.size();
		
		// are all the selections the same type?
		boolean sameType = true;
		Class<?> type = null;
		if (count > 0) {
			TreeItem<TreeData> first = items.get(0);
			if (first != null) {
				type = first.getValue().getClass();
				for (TreeItem<TreeData> item : items) {
					if (!item.getValue().getClass().equals(type)) {
						sameType = false;
						break;
					}
				}
			}
		}
		
    	switch (action) {
    		case NEW_BOOK:
    			if (Fx.isNodeInFocusChain(focused, this.bibleTree)) { 
    				return count == 1 && BibleTreeData.class.equals(type);
    			}
    			return false;
    		case NEW_CHAPTER:
    			if (Fx.isNodeInFocusChain(focused, this.bibleTree)) {
    				return count == 1 && BookTreeData.class.equals(type);
    			}
    			return false;
    		case NEW_VERSE:
    			if (Fx.isNodeInFocusChain(focused, this.bibleTree)) {
    				return count == 1 && (ChapterTreeData.class.equals(type) || VerseTreeData.class.equals(type));
    			}
    			return false;
			case COPY:
			case CUT:
				if (Fx.isNodeInFocusChain(focused, this.bibleTree)) {
					return sameType && count > 0;
				}
				return false;
			case PASTE:
				Clipboard cb = Clipboard.getSystemClipboard();
				if (Fx.isNodeInFocusChain(focused, this.bibleTree)) {
					if (sameType && count == 1) {
						if (BibleTreeData.class.equals(type)) {
							// then we can paste books
							return cb.hasContent(DataFormats.BOOKS);
						} else if (BookTreeData.class.equals(type)) {
							return cb.hasContent(DataFormats.CHAPTERS);
						} else if (ChapterTreeData.class.equals(type)) {
							return cb.hasContent(DataFormats.VERSES);
						} else if (VerseTreeData.class.equals(type)) {
							return cb.hasContent(DataFormats.VERSES);
						}
					}
				}
				return false;
			case DELETE:
				// check for focused text input first
				if (Fx.isNodeInFocusChain(focused, this.bibleTree)) {
					return count > 0;
				}
				return false;
			case RENUMBER:
				if (Fx.isNodeInFocusChain(focused, this.bibleTree)) {
					return count == 1;
				}
				return false;
			case REORDER:
				if (Fx.isNodeInFocusChain(focused, this.bibleTree)) {
					return count == 1;
				}
				return false;
			case SAVE:
			case SAVE_AS:
			case CLOSE:
				return true;
			case UNDO:
				return this.manager.isUndoAvailable();
			case REDO:
				return this.manager.isRedoAvailable();
			default:
				break;
		}
    	return false;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.ApplicationPane#isApplicationActionVisible(org.praisenter.javafx.ApplicationAction)
	 */
	@Override
	public boolean isApplicationActionVisible(ApplicationAction action) {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.ApplicationPane#cleanup()
	 */
	@Override
	public void cleanup() {
		this.bible.set(null);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.ApplicationEditorPane#hasUnsavedChanges()
	 */
	@Override
	public boolean hasUnsavedChanges() {
		return !this.manager.isTopMarked();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.ApplicationEditorPane#saveChanges()
	 */
	@Override
	public void saveChanges() {
		this.save();
	}

	/* (non-Javadoc)
	 * @see org.praisenter.javafx.ApplicationEditorPane#getTargetName()
	 */
	@Override
	public String getEditTargetName() {
		return this.bible.get().getName();
	}
	
	/**
	 * Returns the bible being edited.
	 * @return {@link Bible}
	 */
	public Bible getBible() {
		return this.bible.get();
	}
	
	/**
	 * Sets the bible to be edited.
	 * <p>
	 * This should always be given an exact copy of the desired bible to edit
	 * to ensure that unsaved changes are not reflected in the rest of the
	 * application.
	 * @param bible the bible
	 */
	public void setBible(Bible bible) {
		this.bible.set(bible);
	}
	
	/**
	 * The bible property.
	 * @return ObjectProperty&lt;{@link Bible}&gt;
	 */
	public ObjectProperty<Bible> bibleProperty() {
		return this.bible;
	}
}
