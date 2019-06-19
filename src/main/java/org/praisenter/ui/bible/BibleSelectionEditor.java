package org.praisenter.ui.bible;

import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.Tag;
import org.praisenter.data.bible.Bible;
import org.praisenter.data.bible.Book;
import org.praisenter.data.bible.Chapter;
import org.praisenter.data.bible.Verse;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.controls.EditGridPane;
import org.praisenter.ui.controls.TagListView;
import org.praisenter.ui.controls.TextInputFieldEventFilter;
import org.praisenter.ui.document.DocumentContext;
import org.praisenter.ui.document.DocumentSelectionEditor;
import org.praisenter.ui.translations.Translations;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.geometry.Insets;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.VBox;

public final class BibleSelectionEditor extends VBox implements DocumentSelectionEditor<Bible> {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final GlobalContext context;
	private final ObjectProperty<DocumentContext<Bible>> documentContext;
	
	private final ObjectProperty<Bible> bible;
	private final ObjectProperty<Object> selectedItem;

	private final StringProperty name;
	private final StringProperty language;
	private final StringProperty source;
	private final StringProperty copyright;
	private final StringProperty notes;
	private final ObservableSet<Tag> tags;
	
	private final ObjectProperty<Book> selectedBook;
	private final StringProperty bookName;
	private final IntegerProperty bookNumber;
	private final ObjectProperty<Integer> bookNumber2;
	
	private final ObjectProperty<Chapter> selectedChapter;
	private final IntegerProperty chapterNumber;
	private final ObjectProperty<Integer> chapterNumber2;
	
	private final ObjectProperty<Verse> selectedVerse;
	private final StringProperty verseText;
	private final IntegerProperty verseNumber;
	private final ObjectProperty<Integer> verseNumber2;
	
	public BibleSelectionEditor(GlobalContext context) {
		this.getStyleClass().add("p-bible-selection-editor");
		
		this.context = context;
		this.documentContext = new SimpleObjectProperty<>();
		
		this.bible = new SimpleObjectProperty<>();
		this.selectedItem = new SimpleObjectProperty<>();
		
		this.name = new SimpleStringProperty();
		this.language = new SimpleStringProperty();
		this.source = new SimpleStringProperty();
		this.copyright = new SimpleStringProperty();
		this.notes = new SimpleStringProperty();
		this.tags = FXCollections.observableSet(new HashSet<>());
		
		this.documentContext.addListener((obs, ov, nv) -> {
			this.bible.unbind();
			this.selectedItem.unbind();
			if (nv != null) {
				this.bible.bind(nv.documentProperty());
				this.selectedItem.bind(nv.selectedItemProperty());
			} else {
				this.bible.set(null);
				this.selectedItem.set(null);
			}
		});
		
		this.bible.addListener((obs, ov, nv) -> {
			if (ov != null) {
				this.name.unbindBidirectional(ov.nameProperty());
				this.language.unbindBidirectional(ov.languageProperty());
				this.source.unbindBidirectional(ov.sourceProperty());
				this.copyright.unbindBidirectional(ov.copyrightProperty());
				this.notes.unbindBidirectional(ov.notesProperty());
				Bindings.unbindContentBidirectional(this.tags, ov.getTags());
				
				this.name.set(null);
				this.language.set(null);
				this.source.set(null);
				this.copyright.set(null);
				this.notes.set(null);
				this.tags.clear();
			}
			if (nv != null) {
				this.name.bindBidirectional(nv.nameProperty());
				this.language.bindBidirectional(nv.languageProperty());
				this.source.bindBidirectional(nv.sourceProperty());
				this.copyright.bindBidirectional(nv.copyrightProperty());
				this.notes.bindBidirectional(nv.notesProperty());
				Bindings.bindContentBidirectional(this.tags, nv.getTags());
			}
		});
		
		this.bookNumber = new SimpleIntegerProperty();
		this.bookName = new SimpleStringProperty();
		this.bookNumber2 = this.bookNumber.asObject();
		
		this.selectedBook = new SimpleObjectProperty<>();
		this.selectedBook.addListener((obs, ov, nv) -> {
			if (ov != null) {
				this.bookName.unbindBidirectional(ov.nameProperty());
				this.bookNumber.unbindBidirectional(ov.numberProperty());
				
				this.bookName.set(null);
				this.bookNumber.set(1);
			}
			if (nv != null) {
				this.bookName.bindBidirectional(nv.nameProperty());
				this.bookNumber.bindBidirectional(nv.numberProperty());
			}
		});
		
		this.chapterNumber = new SimpleIntegerProperty();
		this.chapterNumber2 = this.chapterNumber.asObject();
		
		this.selectedChapter = new SimpleObjectProperty<>();
		this.selectedChapter.addListener((obs, ov, nv) -> {
			if (ov != null) {
				this.chapterNumber.unbindBidirectional(ov.numberProperty());
				
				this.chapterNumber.set(0);
			}
			if (nv != null) {
				this.chapterNumber.bindBidirectional(nv.numberProperty());
			}
		});
		
		this.verseNumber = new SimpleIntegerProperty();
		this.verseText = new SimpleStringProperty();
		this.verseNumber2 = this.verseNumber.asObject();
		
		this.selectedVerse = new SimpleObjectProperty<>();
		this.selectedVerse.addListener((obs, ov, nv) -> {
			if (ov != null) {
				this.verseText.unbindBidirectional(ov.textProperty());
				this.verseNumber.unbindBidirectional(ov.numberProperty());
				
				this.verseText.set(null);
				this.verseNumber.set(0);
			}
			if (nv != null) {
				this.verseText.bindBidirectional(nv.textProperty());
				this.verseNumber.bindBidirectional(nv.numberProperty());
			}
		});
		
		this.selectedItem.addListener((obs, ov, nv) -> {
			// handle selection of a tree item
			this.selectedBook.set(null);
			this.selectedChapter.set(null);
			this.selectedVerse.set(null);

			if (nv == null || nv instanceof Bible) {
				// do nothing
			} else if (nv instanceof Book) {
				this.selectedBook.set((Book)nv);
			} else if (nv instanceof Chapter) {
				this.selectedChapter.set((Chapter)nv);
			} else if (nv instanceof Verse) {
				this.selectedVerse.set((Verse)nv);
			} else {
				LOGGER.warn("The selected item was not a recognized type {}", nv.getClass());
			}
		});
		
		// UI
		
		Label lblBibleName = new Label(Translations.get("item.name"));
		TextField txtBibleName = new TextField();
		txtBibleName.textProperty().bindBidirectional(this.name);

		Label lblBibleLanguage = new Label(Translations.get("bible.language"));
		TextField txtBibleLanguage = new TextField();
		txtBibleLanguage.textProperty().bindBidirectional(this.language);
		
		Label lblBibleSource = new Label(Translations.get("bible.source"));
		TextField txtBibleSource = new TextField();
		txtBibleSource.textProperty().bindBidirectional(this.source);
		
		Label lblBibleCopyright = new Label(Translations.get("bible.copyright"));
		TextField txtBibleCopyright = new TextField();
		txtBibleCopyright.textProperty().bindBidirectional(this.copyright);

		Label lblBibleNotes = new Label(Translations.get("bible.notes"));
		TextArea txtBibleNotes = new TextArea();
		txtBibleNotes.textProperty().bindBidirectional(this.notes);
		txtBibleNotes.setWrapText(true);
		
		TagListView viewTags = new TagListView(this.context.getDataManager().getTagsUmodifiable());
		Bindings.bindContentBidirectional(viewTags.getTags(), this.tags);
		
		Label lblBookName = new Label(Translations.get("item.name"));
		TextField txtBookName = new TextField();
		txtBookName.textProperty().bindBidirectional(this.bookName);
		
		Label lblBookNumber = new Label(Translations.get("bible.number"));
		Spinner<Integer> spnBookNumber = new Spinner<>(1, Integer.MAX_VALUE, 1);
		spnBookNumber.setEditable(true);
		spnBookNumber.getValueFactory().valueProperty().bindBidirectional(this.bookNumber2);
		spnBookNumber.setMaxWidth(Double.MAX_VALUE);
		
		Label lblChapterNumber = new Label(Translations.get("bible.number"));
		Spinner<Integer> spnChapterNumber = new Spinner<>(1, Integer.MAX_VALUE, 1);
		spnChapterNumber.setEditable(true);
		spnChapterNumber.getValueFactory().valueProperty().bindBidirectional(this.chapterNumber2);
		spnChapterNumber.setMaxWidth(Double.MAX_VALUE);
		
		Label lblVerseText = new Label(Translations.get("bible.text"));
		TextArea txtVerseText = new TextArea();
		txtVerseText.textProperty().bindBidirectional(this.verseText);
		txtVerseText.setWrapText(true);
		
		Label lblVerseNumber = new Label(Translations.get("bible.number"));
		Spinner<Integer> spnVerseNumber = new Spinner<>(1, Integer.MAX_VALUE, 1);
		spnVerseNumber.setEditable(true);
		spnVerseNumber.getValueFactory().valueProperty().bindBidirectional(this.verseNumber2);
		spnVerseNumber.setMaxWidth(Double.MAX_VALUE);
		
		TextInputFieldEventFilter.applyTextInputFieldEventFilter(
				txtBibleName,
				txtBibleLanguage,
				txtBibleSource,
				txtBibleCopyright,
				txtBibleNotes,
				txtBookName,
				spnBookNumber.getEditor(),
				spnChapterNumber.getEditor(),
				txtVerseText,
				spnVerseNumber.getEditor());
		
		int row = 0;
		EditGridPane bibleGrid = new EditGridPane();
		bibleGrid.add(lblBibleName, 0, row); bibleGrid.add(txtBibleName, 1, row++);
		bibleGrid.add(lblBibleLanguage, 0, row); bibleGrid.add(txtBibleLanguage, 1, row++);
		bibleGrid.add(lblBibleSource, 0, row); bibleGrid.add(txtBibleSource, 1, row++);
		bibleGrid.add(lblBibleCopyright, 0, row); bibleGrid.add(txtBibleCopyright, 1, row++);
		bibleGrid.add(lblBibleNotes, 0, row++, 2);
		bibleGrid.add(txtBibleNotes, 0, row++, 2);
		bibleGrid.add(viewTags, 0, row++, 2);
		bibleGrid.setPadding(new Insets(5));
		TitledPane ttlBible = new TitledPane(Translations.get("bible"), bibleGrid);
		ttlBible.setAnimated(false);
//		ttlBible.setCollapsible(false);
		
		row = 0;
		EditGridPane selectionGrid = new EditGridPane();
		selectionGrid.add(lblBookNumber, 0, row); selectionGrid.add(spnBookNumber, 1, row++);
		selectionGrid.add(lblBookName, 0, row); selectionGrid.add(txtBookName, 1, row++);
		selectionGrid.add(lblChapterNumber, 0, row); selectionGrid.add(spnChapterNumber, 1, row++);
		selectionGrid.add(lblVerseNumber, 0, row); selectionGrid.add(spnVerseNumber, 1, row++);
		selectionGrid.add(lblVerseText, 0, row++, 2);
		selectionGrid.add(txtVerseText, 0, row++, 2);
		selectionGrid.setPadding(new Insets(5));
		TitledPane ttlSelection = new TitledPane("", selectionGrid);
		ttlSelection.setAnimated(false);
//		ttlSelection.setCollapsible(false);
		ttlSelection.textProperty().bind(Bindings.createStringBinding(() -> {
			Object item = this.selectedItem.get();
			if (item == null || item instanceof Bible) {
				return "";
			} else if (item instanceof Book) {
				return Translations.get("bible.book");
			} else if (item instanceof Chapter) {
				return Translations.get("bible.chapter");
			} else if (item instanceof Verse) {
				return Translations.get("bible.verse");
			}
			return "";
		}, this.selectedItem));
		this.selectedItem.addListener((obs, ov, nv) -> {
//			selectionGrid.hideRows(0,1,2,3,4,5);
			if (nv == null || nv instanceof Bible) {
			} else if (nv instanceof Book) {
				selectionGrid.showRowsOnly(0,1);
			} else if (nv instanceof Chapter) {
				selectionGrid.showRowsOnly(2);
			} else if (nv instanceof Verse) {
				selectionGrid.showRowsOnly(3,4,5);
			}
		});
		
//		TitledPane ttlBook = new TitledPane(Translations.get("bible.book"), new VBox(
//				lblBookNumber, spnBookNumber,
//				lblBookName, txtBookName));
//
//		TitledPane ttlChapter = new TitledPane(Translations.get("bible.chapter"), new VBox(
//				lblChapterNumber, spnChapterNumber));
//		
//		TitledPane ttlVerse = new TitledPane(Translations.get("bible.verse"), new VBox(
//				lblVerseNumber, spnVerseNumber,
//				lblVerseText, txtVerseText));
		
		ScrollPane scroller = new ScrollPane(new VBox(
				ttlBible,
				ttlSelection));
		scroller.setHbarPolicy(ScrollBarPolicy.NEVER);
		scroller.setFitToWidth(true);
		
		this.getChildren().addAll(scroller);
		
		// hide/show
		
		BooleanBinding hasBible = this.documentContext.isNotNull();
		ttlBible.visibleProperty().bind(hasBible);
		ttlBible.managedProperty().bind(hasBible);
		
		BooleanBinding selectionEditingEnabled = Bindings.createBooleanBinding(() -> {
			Object item = this.selectedItem.get();
			if (item == null || item instanceof Bible) return false;
			return true;
		}, this.selectedItem);
		ttlSelection.visibleProperty().bind(selectionEditingEnabled);
		ttlSelection.managedProperty().bind(selectionEditingEnabled);
	}
	
	public DocumentContext<Bible> getDocumentContext() {
		return this.documentContext.get();
	}
	
	public void setDocumentContext(DocumentContext<Bible> ctx) {
		this.documentContext.set(ctx);
	}
	
	public ObjectProperty<DocumentContext<Bible>> documentContextProperty() {
		return this.documentContext;
	}
}