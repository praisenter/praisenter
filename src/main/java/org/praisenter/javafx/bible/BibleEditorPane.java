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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.praisenter.Constants;
import org.praisenter.bible.Bible;
import org.praisenter.bible.Book;
import org.praisenter.bible.Chapter;
import org.praisenter.bible.Verse;
import org.praisenter.javafx.Alerts;
import org.praisenter.javafx.ApplicationAction;
import org.praisenter.javafx.ApplicationContextMenu;
import org.praisenter.javafx.ApplicationEvent;
import org.praisenter.javafx.ApplicationPane;
import org.praisenter.javafx.ApplicationPaneEvent;
import org.praisenter.javafx.DataFormats;
import org.praisenter.javafx.Option;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.configuration.Setting;
import org.praisenter.javafx.utility.Fx;
import org.praisenter.resources.translations.Translations;

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
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * A pane for editing {@link Bible}s.
 * @author William Bittle
 * @version 3.0.0
 */
public final class BibleEditorPane extends BorderPane implements ApplicationPane {
	/** The class level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** The font-awesome glyph-font pack */
	private static final GlyphFont FONT_AWESOME	= GlyphFontRegistry.font("FontAwesome");
	
	// data
	
	/** The praisenter context */
	private final PraisenterContext context;

	/** The bible being edited */
	private final ObjectProperty<Bible> bible = new SimpleObjectProperty<Bible>();
	
	// nodes
	
	/** The bible tree view */
	private final TreeView<TreeData> bibleTree;
	
	// state
	
	/** True when the bible property is being set */
	private boolean mutating = false;
	
	/** True if there is unsaved changes */
	private boolean unsavedChanges = false;
	
	/**
	 * Minimal constructor.
	 * @param context the context
	 */
	public BibleEditorPane(PraisenterContext context) {
		this.getStyleClass().add("bible-editor-pane");
		
		this.context = context;

		ObservableList<Option<Locale>> locales = FXCollections.observableArrayList();
		for (Locale locale : Locale.getAvailableLocales()) {
			locales.add(new Option<Locale>(locale.getDisplayName(), locale));
		}
		Collections.sort(locales);
		
		// bible
		Label lblName = new Label(Translations.get("bible.edit.name"));
		TextField txtName = new TextField();
		Label lblLanguage = new Label(Translations.get("bible.edit.language"));
		ComboBox<Option<Locale>> cmbLanguage = new ComboBox<Option<Locale>>(locales);
		cmbLanguage.setEditable(true);
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
		Label lblCopyright = new Label(Translations.get("bible.edit.copyright"));
		TextField txtCopyright = new TextField();
		Label lblNotes = new Label(Translations.get("bible.edit.notes"));
		TextArea txtNotes = new TextArea();
		txtNotes.setWrapText(true);
		txtNotes.setPrefHeight(250);
		
		Label lblEditMessage = new Label(Translations.get("bible.edit.message"), FONT_AWESOME.create(FontAwesome.Glyph.ARROW_LEFT));
		
		// book
		Label lblBookName = new Label(Translations.get("bible.edit.bookname"));
		TextField txtBookName = new TextField();
		Label lblBookNumber = new Label(Translations.get("bible.edit.booknumber"));
		Spinner<Integer> spnBookNumber = new Spinner<Integer>(1, Short.MAX_VALUE, 1, 1);
		spnBookNumber.setEditable(true);
		
		// chapter
		Label lblChapter = new Label(Translations.get("bible.edit.chapternumber"));
		Spinner<Integer> spnChapter = new Spinner<Integer>(1, Short.MAX_VALUE, 1, 1);
		spnChapter.setEditable(true);
		
		// verse
		Label lblVerse = new Label(Translations.get("bible.edit.versenumber"));
		Spinner<Integer> spnVerse = new Spinner<Integer>(1, Short.MAX_VALUE, 1, 1);
		spnVerse.setEditable(true);
		Label lblVerseText = new Label(Translations.get("bible.edit.versetext"));
		TextArea txtText = new TextArea();
		txtText.setWrapText(true);
		txtText.setPrefHeight(350);
		
		VBox editorFields = new VBox();
		editorFields.setSpacing(2);
		editorFields.setPadding(new Insets(10));
		editorFields.getChildren().addAll(
				lblEditMessage,
				lblName,
				txtName, 
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
        			manager.dragDropped(cell, e);
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
			this.mutating = true;
			bibleTree.getSelectionModel().clearSelection();
			if (nv != null) {
				// create the root node
				TreeItem<TreeData> root = this.forBible(nv);
				root.setExpanded(true);
				
				// set the tree
				bibleTree.setRoot(root);
				
				// set the editor fields
				txtName.setText(nv.getName());
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
				bibleTree.setRoot(null);
				
				txtName.setText(null);
				cmbLanguage.setValue(null);
				cmbLanguage.getEditor().setText(null);
				txtSource.setText(null);
				txtCopyright.setText(null);
				txtNotes.setText(null);
			}
			this.unsavedChanges = false;
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
			if (this.bibleTree.getSelectionModel().getSelectedIndices().size() == 1) {
				TreeData data = nv.getValue();
				if (data instanceof BibleTreeData) {
					lblName.setVisible(true);
					txtName.setVisible(true);
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
		
		txtName.textProperty().addListener((obs, ov, nv) -> {
			if (this.mutating) return;
			Bible bible = this.bible.get();
			if (bible != null) {
				this.unsavedChanges = true;
				bible.setName(nv);
				TreeItem<TreeData> root = this.bibleTree.getRoot();
				if (root != null) {
					TreeData data = root.getValue();
					if (data != null && data instanceof BibleTreeData) {
						((BibleTreeData)data).update();
					}
				}
			}
		});
		cmbLanguage.valueProperty().addListener((obs, ov, nv) -> {
			if (this.mutating) return;
			Bible bible = this.bible.get();
			if (bible != null) {
				String language = null;
				if (nv != null) {
					Locale locale = nv.getValue();
					language = locale != null ? locale.toLanguageTag() : nv.getName();
				}
				bible.setLanguage(language);
				this.unsavedChanges = true;
			}
		});
		txtSource.textProperty().addListener((obs, ov, nv) -> {
			if (this.mutating) return;
			Bible bible = this.bible.get();
			if (bible != null) {
				bible.setSource(nv);
				this.unsavedChanges = true;
			}
		});
		txtCopyright.textProperty().addListener((obs, ov, nv) -> {
			if (this.mutating) return;
			Bible bible = this.bible.get();
			if (bible != null) {
				bible.setCopyright(nv);
				this.unsavedChanges = true;
			}
		});
		txtNotes.textProperty().addListener((obs, ov, nv) -> {
			if (this.mutating) return;
			Bible bible = this.bible.get();
			if (bible != null) {
				bible.setNotes(nv);
				this.unsavedChanges = true;
			}
		});
		txtBookName.textProperty().addListener((obs, ov, nv) -> {
			if (this.mutating) return;
			TreeItem<TreeData> item = this.bibleTree.getSelectionModel().getSelectedItem();
			if (item != null) {
				TreeData data = item.getValue();
				if (data != null && data instanceof BookTreeData) {
					BookTreeData td = (BookTreeData)data;
					td.book.setName(nv);
					td.update();
					this.unsavedChanges = true;
				}
			}
		});
		spnBookNumber.valueProperty().addListener((obs, ov, nv) -> {
			if (this.mutating) return;
			TreeItem<TreeData> item = this.bibleTree.getSelectionModel().getSelectedItem();
			if (item != null) {
				TreeData data = item.getValue();
				if (data != null && data instanceof BookTreeData) {
					BookTreeData btd = (BookTreeData)data;
					btd.book.setNumber(nv.shortValue());
					btd.update();
					this.unsavedChanges = true;
				}
			}
		});
		spnChapter.valueProperty().addListener((obs, ov, nv) -> {
			if (this.mutating) return;
			TreeItem<TreeData> item = this.bibleTree.getSelectionModel().getSelectedItem();
			if (item != null) {
				TreeData data = item.getValue();
				if (data != null && data instanceof ChapterTreeData) {
					ChapterTreeData ctd = (ChapterTreeData)data;
					ctd.chapter.setNumber(nv.shortValue());
					ctd.update();
					this.unsavedChanges = true;
				}
			}
		});
		spnVerse.valueProperty().addListener((obs, ov, nv) -> {
			if (this.mutating) return;
			TreeItem<TreeData> item = this.bibleTree.getSelectionModel().getSelectedItem();
			if (item != null) {
				TreeData data = item.getValue();
				if (data != null && data instanceof VerseTreeData) {
					VerseTreeData vtd = (VerseTreeData)data;
					vtd.verse.setNumber(nv.shortValue());
					vtd.update();
					this.unsavedChanges = true;
				}
			}
		});
		txtText.textProperty().addListener((obs, ov, nv) -> {
			if (this.mutating) return;
			TreeItem<TreeData> item = this.bibleTree.getSelectionModel().getSelectedItem();
			if (item != null) {
				TreeData data = item.getValue();
				if (data != null && data instanceof VerseTreeData) {
					VerseTreeData vtd = (VerseTreeData)data;
					vtd.verse.setText(nv);
					vtd.update();
					this.unsavedChanges = true;
				}
			}
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
	 * Copies the selected items.
	 * @param cut true if they should be cut instead of copied
	 */
	private void copy(boolean cut) {
		// get the selection(s)
		List<TreeItem<TreeData>> items = new ArrayList<TreeItem<TreeData>>(this.bibleTree.getSelectionModel().getSelectedItems());
		
		if (items.size() > 0) {
			DataFormat format = null;
			StringBuilder text = new StringBuilder();
			
			List<Object> data = new ArrayList<Object>();
			for (TreeItem<TreeData> item : items) {
				TreeData td = item.getValue();
				if (td instanceof BookTreeData) {
					BookTreeData btd = (BookTreeData)td;
					Book book = btd.book;
					data.add(book);
					format = DataFormats.BOOKS;
					text.append(book.getName()).append(Constants.NEW_LINE);
					if (cut) {
						btd.bible.getBooks().remove(book);
					}
				} else if (td instanceof ChapterTreeData) {
					ChapterTreeData ctd = (ChapterTreeData)td;
					Chapter chapter = ctd.chapter;
					data.add(chapter);
					format = DataFormats.CHAPTERS;
					text.append(chapter.getNumber()).append(Constants.NEW_LINE);
					if (cut) {
						ctd.book.getChapters().remove(chapter);
					}
				} else if (td instanceof VerseTreeData) {
					VerseTreeData vtd = (VerseTreeData)td;
					Verse verse = vtd.verse;
					data.add(verse);
					format = DataFormats.VERSES;
					text.append(verse.getText()).append(Constants.NEW_LINE);
					if (cut) {
						vtd.chapter.getVerses().remove(verse);
					}
				}
			}
			
			for (TreeItem<TreeData> item : items) {
				if (cut) {
					// remove from the tree
					item.getParent().getChildren().remove(item);
				}
			}
			
			Clipboard cb = Clipboard.getSystemClipboard();
			ClipboardContent cc = new ClipboardContent();
			cc.put(DataFormat.PLAIN_TEXT, text.toString().trim());
			cc.put(format, data);
			cb.setContent(cc);
			
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
		
		Clipboard cb = Clipboard.getSystemClipboard();
		if (items.size() == 1) {
			TreeItem<TreeData> node = items.get(0);
			Class<?> type = node.getValue().getClass();
			if (type.equals(BibleTreeData.class)) {
				Bible bible = ((BibleTreeData)node.getValue()).bible;
				// then we can paste books
				Object data = cb.getContent(DataFormats.BOOKS);
				if (data != null && data instanceof List) {
					List<Book> books = (List<Book>)data;
					short max = bible.getMaxBookNumber();
					for (Book book : books) {
						Book copy = book.copy();
						copy.setNumber(++max);
						// add the book to the bible
						bible.getBooks().add(copy);
						// add the tree node
						node.getChildren().add(this.forBook(bible, copy));
					}
					this.unsavedChanges = true;
				}
			} else if (type.equals(BookTreeData.class)) {
				BookTreeData td = (BookTreeData)node.getValue();
				Bible bible = td.bible;
				Book book = td.book;
				// then we can paste books
				Object data = cb.getContent(DataFormats.CHAPTERS);
				if (data != null && data instanceof List) {
					List<Chapter> chapters = (List<Chapter>)data;
					for (Chapter chapter : chapters) {
						Chapter copy = chapter.copy();
						// add the book to the bible
						book.getChapters().add(copy);
						// add the tree node
						node.getChildren().add(this.forChapter(bible, book, copy));
					}
					this.unsavedChanges = true;
				}
			} else if (type.equals(ChapterTreeData.class)) {
				ChapterTreeData td = (ChapterTreeData)node.getValue();
				Bible bible = td.bible;
				Book book = td.book;
				Chapter chapter = td.chapter;
				// then we can paste books
				Object data = cb.getContent(DataFormats.VERSES);
				if (data != null && data instanceof List) {
					List<Verse> verses = (List<Verse>)data;
					for (Verse verse : verses) {
						Verse copy = verse.copy();
						// add the book to the bible
						chapter.getVerses().add(copy);
						// add the tree node
						node.getChildren().add(this.forVerse(bible, book, chapter, copy));
					}
					this.unsavedChanges = true;
				}
			} else if (type.equals(VerseTreeData.class)) {
				VerseTreeData td = (VerseTreeData)node.getValue();
				Bible bible = td.bible;
				Book book = td.book;
				Chapter chapter = td.chapter;
				// then we can paste books
				Object data = cb.getContent(DataFormats.VERSES);
				if (data != null && data instanceof List) {
					List<Verse> verses = (List<Verse>)data;
					for (Verse verse : verses) {
						Verse copy = verse.copy();
						// add the book to the bible
						chapter.getVerses().add(copy);
						// add the tree node
						node.getParent().getChildren().add(this.forVerse(bible, book, chapter, copy));
					}
					this.unsavedChanges = true;
				}
			}
		}
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
				for (TreeItem<TreeData> item : items) {
					// remove the data
					TreeData td = item.getValue();
					if (td instanceof BookTreeData) {
						BookTreeData btd = (BookTreeData)td;
						btd.bible.getBooks().remove(btd.book);
					} else if (td instanceof ChapterTreeData) {
						ChapterTreeData ctd = (ChapterTreeData)td;
						ctd.book.getChapters().remove(ctd.chapter);
					} else if (td instanceof VerseTreeData) {
						VerseTreeData vtd = (VerseTreeData)td;
						vtd.chapter.getVerses().remove(vtd.verse);
					}
					// remove the node
					item.getParent().getChildren().remove(item);
				}
				this.unsavedChanges = true;
			}
		}
	}
	
	/**
	 * Adds new verses, chapters, and books.
	 */
	private void add() {
		TreeItem<TreeData> item = this.bibleTree.getSelectionModel().getSelectedItem();
		
		if (item == null || item.getValue() == null) return;
		Class<?> type = item.getValue().getClass();
		TreeItem<TreeData> newItem = null;
		
		if (VerseTreeData.class.equals(type)) {
			// add new verse
			VerseTreeData vd = (VerseTreeData)item.getValue();
			short number = (short)(vd.verse.getNumber() + 1);
			Verse verse = new Verse(number, Translations.get("bible.edit.verse.default"));
			// add to data
			vd.chapter.getVerses().add(verse);
			// add to view
			int index = item.getParent().getChildren().indexOf(item);
			newItem = new TreeItem<TreeData>(new VerseTreeData(vd.bible, vd.book, vd.chapter, verse));
			item.getParent().getChildren().add(index + 1, newItem);
		} else if (ChapterTreeData.class.equals(type)) {
			// add new verse
			ChapterTreeData cd = (ChapterTreeData)item.getValue();
			short number = cd.chapter.getMaxVerseNumber();
			Verse verse = new Verse(++number, Translations.get("bible.edit.verse.default"));
			// add to data
			cd.chapter.getVerses().add(verse);
			// add to view
			newItem = new TreeItem<TreeData>(new VerseTreeData(cd.bible, cd.book, cd.chapter, verse));
			item.getChildren().add(newItem);
			item.setExpanded(true);
		} else if (BookTreeData.class.equals(type)) {
			// add new chapter
			BookTreeData bd = (BookTreeData)item.getValue();
			short number = bd.book.getMaxChapterNumber();
			Chapter chapter = new Chapter(++number);
			// add to data
			bd.book.getChapters().add(chapter);
			// add to view
			newItem = new TreeItem<TreeData>(new ChapterTreeData(bd.bible, bd.book, chapter));
			item.getChildren().add(newItem);
			item.setExpanded(true);
		} else if (BibleTreeData.class.equals(type)) {
			// add new book
			BibleTreeData bd = (BibleTreeData)item.getValue();
			short number = bd.bible.getMaxBookNumber();
			Book book = new Book(Translations.get("bible.edit.book.default"), ++number);
			// add to data
			bd.bible.getBooks().add(book);
			// add to view
			newItem = new TreeItem<TreeData>(new BookTreeData(bd.bible, book));
			item.getChildren().add(newItem);
			item.setExpanded(true);
		}
		
		// did we create an item?
		if (newItem != null) {
			this.unsavedChanges = true;
			// if so, then get it's index
			int index = this.bibleTree.getRow(newItem);
			if (index > 0) {
				// selected it
				this.bibleTree.getSelectionModel().clearAndSelect(index);
				final int offset = 10;
				// scroll to it (we'll close to it, we don't want it at the top)
				if (index - offset > 0) {
					this.bibleTree.scrollTo(index - offset);
				}
			}
		}
	}
	
	/**
	 * Saves the current bible.
	 */
	private void save() {
		this.context.getBibleLibrary().save(this.getBible(), b -> {
			this.unsavedChanges = false;
		}, (b, ex) -> {
			LOGGER.error("Failed to save bible " + b.getName() + " " + b.getId() + " due to: " + ex.getMessage(), ex);
			Alert alert = Alerts.exception(
					getScene().getWindow(),
					null, 
					null, 
					MessageFormat.format(Translations.get("bible.save.error.content"), bible.getName()), 
					ex);
			alert.show();
		});
	}
	
	/**
	 * Saves the current bible.
	 */
	private void promptSaveAs() {
		String old = this.getBible().getName();
		
    	TextInputDialog prompt = new TextInputDialog(old);
    	prompt.initOwner(getScene().getWindow());
    	prompt.initModality(Modality.WINDOW_MODAL);
    	prompt.setTitle(Translations.get("action.saveas"));
    	prompt.setHeaderText(Translations.get("saveas.header"));
    	prompt.setContentText(Translations.get("saveas.content"));
    	Optional<String> result = prompt.showAndWait();
    	
    	// check for the "OK" button
    	if (result.isPresent()) {
    		// actually rename it?
    		String name = result.get();
    		
        	// create a copy of the current bible
    		// with new id
    		Bible copy = this.getBible().copy(false);
    		// set the name
    		copy.setName(name);
    		// set the copy as the one we are editing now
    		this.bible.set(copy);
    		// then save
    		this.save();
    	}
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
							this.context.getConfiguration().setBoolean(Setting.BIBLE_SHOW_REORDER_WARNING, false);
						});
	
				result = alert.showAndWait();
			}
			
			if (result.get() == ButtonType.OK){
				// remove the data
				TreeData td = item.getValue();
				if (td instanceof BibleTreeData) {
					reorderBooks(item);
				} else if (td instanceof BookTreeData) {
					reorderChapters(item);
				} else if (td instanceof ChapterTreeData) {
					reorderVerses(item);
				} else if (td instanceof VerseTreeData) {
					reorderVerses(item.getParent());
				}
				this.unsavedChanges = true;
			}
		}
	}

	/**
	 * Reorders the books in the given bible.
	 * @param node the bible node
	 */
	private void reorderBooks(TreeItem<TreeData> node) {
		// sort the chapters in each book
		for (TreeItem<TreeData> item : node.getChildren()) {
			reorderChapters(item);
		}
		// sort the books
		Bible bible = ((BibleTreeData)node.getValue()).bible;
		Collections.sort(bible.getBooks());
		// sort the nodes
		node.getChildren().sort((TreeItem<TreeData> b1, TreeItem<TreeData> b2) -> {
			if (b1 == null) return 1;
			if (b2 == null) return -1;
			return b1.getValue().compareTo(b2.getValue());
		});
	}
	
	/**
	 * Reorders the chapters in the given book.
	 * @param node the book node.
	 */
	private void reorderChapters(TreeItem<TreeData> node) {
		for (TreeItem<TreeData> item : node.getChildren()) {
			reorderVerses(item);
		}
		// make sure the data is sorted the same way
		Collections.sort(((BookTreeData)node.getValue()).book.getChapters());
		// sort the nodes
		node.getChildren().sort((TreeItem<TreeData> b1, TreeItem<TreeData> b2) -> {
			if (b1 == null) return 1;
			if (b2 == null) return -1;
			return b1.getValue().compareTo(b2.getValue());
		});
	}
	
	/**
	 * Reorders the verses in the given chapter.
	 * @param node the chapter node
	 */
	private void reorderVerses(TreeItem<TreeData> node) {
		// make sure the data is sorted the same way
		Collections.sort(((ChapterTreeData)node.getValue()).chapter.getVerses());
		// sort the nodes
		node.getChildren().sort((TreeItem<TreeData> b1, TreeItem<TreeData> b2) -> {
			if (b1 == null) return 1;
			if (b2 == null) return -1;
			return b1.getValue().compareTo(b2.getValue());
		});
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
							this.context.getConfiguration().setBoolean(Setting.BIBLE_SHOW_RENUMBER_WARNING, false);
						});
	
				result = alert.showAndWait();
			}
			
			if (result.get() == ButtonType.OK){
				// remove the data
				TreeData td = item.getValue();
				if (td instanceof BibleTreeData) {
					renumberBible(item);
				} else if (td instanceof BookTreeData) {
					renumberBook(item);
				} else if (td instanceof ChapterTreeData) {
					renumberChapter(item);
				} else if (td instanceof VerseTreeData) {
					renumberChapter(item.getParent());
				}
				this.unsavedChanges = true;
			}
		}
	}
	
	/**
	 * Renumbers the books in the given bible.
	 * @param node the bible node
	 */
	private void renumberBible(TreeItem<TreeData> node) {
		short i = 1;
		for (TreeItem<TreeData> item : node.getChildren()) {
			BookTreeData td = (BookTreeData)item.getValue();
			// update the data
			td.book.setNumber(i++);
			// update the label
			td.update();
			// update children
			renumberBook(item);
		}
		// make sure the data is sorted the same way
		Collections.sort(((BibleTreeData)node.getValue()).bible.getBooks());
	}
	
	/**
	 * Renumbers the chapters in the given book.
	 * @param node the book node.
	 */
	private void renumberBook(TreeItem<TreeData> node) {
		short i = 1;
		for (TreeItem<TreeData> item : node.getChildren()) {
			ChapterTreeData td = (ChapterTreeData)item.getValue();
			// update the data
			td.chapter.setNumber(i++);
			// update the label
			td.update();
			// update children
			renumberChapter(item);
		}
		// make sure the data is sorted the same way
		Collections.sort(((BookTreeData)node.getValue()).book.getChapters());
	}
	
	/**
	 * Renumbers the verses in the given chapter.
	 * @param node the chapter node
	 */
	private void renumberChapter(TreeItem<TreeData> node) {
		short i = 1;
		for (TreeItem<TreeData> item : node.getChildren()) {
			VerseTreeData td = (VerseTreeData)item.getValue();
			// update the data
			td.verse.setNumber(i++);
			// update the label
			td.update();
		}
		// make sure the data is sorted the same way
		Collections.sort(((ChapterTreeData)node.getValue()).chapter.getVerses());
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
	    		// we only want to execute this if the current focus
				// is within the bibleTree
				if (Fx.isNodeInFocusChain(focused, this.bibleTree)) {
					add();
				}
	    		break;
			case NEW_CHAPTER:
				// we only want to execute this if the current focus
				// is within the bibleTree
				if (Fx.isNodeInFocusChain(focused, this.bibleTree)) {
					add();
				}
				break;
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
				if (this.unsavedChanges) {
					Alert alert = Alerts.yesNoCancel(
							getScene().getWindow(),
							Modality.WINDOW_MODAL,
							Translations.get("warning.modified.title"), 
							MessageFormat.format(Translations.get("warning.modified.header"), this.bible.get().getName()), 
							Translations.get("warning.modified.content"));
					Optional<ButtonType> result = alert.showAndWait();
					if (result.get() == ButtonType.YES) {
						this.save();
						this.fireEvent(new ApplicationEvent(this, this, ApplicationEvent.ALL, ApplicationAction.MANAGE_BIBLES));
					} else if (result.get() == ButtonType.NO) {
						this.fireEvent(new ApplicationEvent(this, this, ApplicationEvent.ALL, ApplicationAction.MANAGE_BIBLES));
					}
				} else {
					this.fireEvent(new ApplicationEvent(this, this, ApplicationEvent.ALL, ApplicationAction.MANAGE_BIBLES));
				}
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
